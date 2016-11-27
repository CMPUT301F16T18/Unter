/*
 * Copyright (C) 2016 CMPUT301F16T18 - Alan(Xutong) Zhao, Michael(Zichun) Lin, Stephen Larsen, Yu Zhu, Zhenzhe Xu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.ualberta.cs.unter.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.appyvet.rangebar.RangeBar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.novoda.merlin.registerable.bind.Bindable;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;

import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Iterator;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.controller.RequestController;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.OnAsyncTaskFailure;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.model.request.Request;
import ca.ualberta.cs.unter.util.FileIOUtil;
import ca.ualberta.cs.unter.util.OSMapUtil;
import ca.ualberta.cs.unter.util.RequestUtil;
import cz.msebera.android.httpclient.Header;

/**
 * Activity that driver can search for request and browse the map and accept it
 */
public class DriverSearchRequestActivity extends AppCompatActivity
        implements View.OnClickListener, Connectable, Disconnectable, Bindable {

    private Spinner searchOptionSpinner;
    private ArrayAdapter<CharSequence> searchOptionAdapter;

    private EditText searchContextEditText;
    private Button searchButton;
    private Button filterButton;

    // set default value
    private double priceRangeMin = 0.00;
    private double priceRangeMax = 300.00;
    private double pricePerKMRangeMin = 0.00;
    private double pricePerKMRangeMax = 10.00;

    private ListView searchRequestListView;
    private ArrayAdapter<Request> searchRequestAdapter;
    private ArrayList<Request> searchRequestList = new ArrayList<>();

    private int searchOption;
    private User driver;

    protected Merlin merlin;

    // Request controller for searching result
    private RequestController requestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            // Cast
            searchRequestList = (ArrayList<Request>) o;
            updateRequest();
        }
    });

    // Request controller for confirming request
    // TODO data consistent
    private RequestController confirmedRequestController = new RequestController(
            new OnAsyncTaskCompleted() {
                @Override
                public void onTaskCompleted(Object o) {
                    Request req = (Request) o;
                    FileIOUtil.saveRequestInFile(req, RequestUtil.generateDriverRequestFileName(req),
                            getApplicationContext());
                    searchRequestList.remove(req);
                    updateRequest();
                }
            },
            new OnAsyncTaskFailure() {
                @Override
                public void onTaskFailed(Object o) {
                    Request req = (Request) o;
                    FileIOUtil.saveRequestInFile(req, RequestUtil.generateAcceptedReqestFileName(req),
                            getApplicationContext());
                    searchRequestList.remove(req);
                    updateRequest();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_search_request);

        // merline stuff
        merlin = new Merlin.Builder().withConnectableCallbacks().withDisconnectableCallbacks().withBindableCallbacks().build(this);
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);
        merlin.registerBindable(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchContextEditText = (EditText) findViewById(R.id.editText_searchRequest_DriverSearchRequestActivity);

        // https://developer.android.com/guide/topics/ui/controls/spinner.html#Populate
        searchOptionSpinner = (Spinner) findViewById(R.id.spinner_searchOption_DriverSearchRequestActivity);
        searchOptionAdapter = ArrayAdapter.createFromResource(this,
                R.array.search_option, android.R.layout.simple_spinner_item);

        searchOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchOptionSpinner.setAdapter(searchOptionAdapter);

        searchOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchOption = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchButton = (Button) findViewById(R.id.button_search_DriverSearchRequestActivity);
        assert searchButton != null;
        searchButton.setOnClickListener(this);

        filterButton = (Button) findViewById(R.id.button_filter_driversearchrequestactivity);
        assert filterButton != null;
        filterButton.setOnClickListener(this);

        searchRequestListView = (ListView) findViewById(R.id.listView_searchList_DriverSearchRequestActivity);
        searchRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Open dialog
                openRequestInfoDialog(searchRequestList.get(position));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        driver = FileIOUtil.loadUserFromFile(getApplicationContext());
        searchRequestAdapter = new ArrayAdapter<>(this, R.layout.request_list_item, searchRequestList);
        searchRequestListView.setAdapter(searchRequestAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        merlin.bind();
    }

    @Override
    protected void onPause() {
        super.onPause();
        merlin.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, DriverMainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == searchButton) {
            String address = searchContextEditText.getText().toString();
            if (TextUtils.isEmpty(address)) {
                searchContextEditText.setError("Address cannot be empty");
                return;
            }
            if (searchOption == 0) {
                // If search by geolocation
                OSMapUtil.GeoCoding(searchContextEditText.getText().toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        GeoPoint point = OSMapUtil.pharseGeoJson(response);
                        requestController.searchRequestByGeoLocation(point, driver.getUserName());
                    }
                });
            } else if (searchOption == 1) {
                // If search by keyword
                requestController.searchRequestByKeyword(searchContextEditText.getText().toString(), driver.getUserName());
            }
        } else if (view == filterButton) {
            if (searchRequestList == null || searchRequestList.isEmpty()) {
                // if there is no search result, cannot be filtered
                searchContextEditText.setError("Just search something");
            } else if (!searchRequestList.isEmpty()) {
                // open the filter dialog
                openFilterRequestDialog();
            }
        }
    }

    /**
     * A dialog that allow user to view request info
     * @param request the request
     */
    private void openRequestInfoDialog(final Request request) {
        // TODO get estimated fare price and description of the request
        String estimatedFare = request.getEstimatedFare().toString();   // replace 100 with estimated price
        String description = request.getRequestDescription();   // replace hello with actual request description

        AlertDialog.Builder builder = new AlertDialog.Builder(DriverSearchRequestActivity.this);
        builder.setTitle("Request Information")
                .setMessage("Estimated Fare: " + estimatedFare + "\n" + "Description: " + description)
                .setNeutralButton(R.string.dialog_view_map_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Serialize the request to pass it over the intent
                        Intent intentDriverMain = new Intent(DriverSearchRequestActivity.this, BrowseRequestRouteActivity.class);
                        // http://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
                        intentDriverMain.putExtra("request", RequestUtil.serializer(request));   // TODO replace testRequest with actuall request object
                        startActivity(intentDriverMain);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    // Driver confirms request
                    public void onClick(DialogInterface dialog, int which) {
                        // Confirm request task
                        confirmedRequestController.driverConfirmRequest(request, driver.getUserName());
                        // Then go to browse request activity
                        Intent intent = new Intent(DriverSearchRequestActivity.this, DriverBrowseRequestActivity.class);
                        startActivity(intent);
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * A dialog that allow user to filter the request
     */
    private void openFilterRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverSearchRequestActivity.this);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.driver_filter_request_dialog, null);

        RangeBar priceRangeBar = (RangeBar) promptView.findViewById(R.id.rangebar__priceRange_DriverSearchRequestActivity);
        priceRangeBar.setTickStart(0);
        priceRangeBar.setTickEnd(300);
        priceRangeBar.setTickInterval(300 / 100.0f);
        // Sets the display values of the indices
        priceRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                // use priceRangeMin, priceRangeMax later for filtering search result
                priceRangeMin = Float.parseFloat(leftPinValue);
                priceRangeMax = Float.parseFloat(rightPinValue);
            }
        });

        RangeBar pricePerKMRangeBar = (RangeBar) promptView.findViewById(R.id.rangebar_priceperkmrange_driversearchrequestactivity);
        pricePerKMRangeBar.setTickStart(0);
        pricePerKMRangeBar.setTickEnd(10);
        pricePerKMRangeBar.setTickInterval(10 / 20.0f);
        // Sets the display values of the indices
        pricePerKMRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                // use pricePerKMRangeMin, pricePerKMRangeMin later for filtering search result
                pricePerKMRangeMin = Float.parseFloat(leftPinValue);
                pricePerKMRangeMax = Float.parseFloat(rightPinValue);
            }
        });

        builder.setTitle("Filter Request")
                .setView(promptView)
                .setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        filterRequestList(priceRangeMin, priceRangeMax, pricePerKMRangeMin, pricePerKMRangeMax);
                    }
                }).setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        // Create & Show the FilterDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Filter the request list
     * @param minPrice the min price
     * @param maxPrice the max price
     * @param minPricePerKM the min price per kilometer
     * @param maxPricePerKM the max price per kilometer
     */
    private void filterRequestList(double minPrice, double maxPrice, double minPricePerKM, double maxPricePerKM) {
        Iterator<Request> ite = searchRequestList.iterator();
        while (ite.hasNext()) {
            Request r = ite.next();
            double fare = r.getEstimatedFare();
            double farePerKM = fare / r.getDistance();
            // remove item that does not fit the range
            if (!(fare >= minPrice && fare <= maxPrice && farePerKM >= minPricePerKM && farePerKM <= maxPricePerKM)) {
                ite.remove();
            }
        }
        // update
        updateRequest();
    }

    // Update method
    private void updateRequest() {
        searchRequestAdapter.clear();
        searchRequestAdapter.addAll(searchRequestList);
        searchRequestAdapter.notifyDataSetChanged();
    }

    /**
     * Once the device is oneline, try to update the request to the
     * server
     */
    protected void updateOfflineRequest() {
        confirmedRequestController.updateDriverOfflineRequest(driver.getUserName(), this);
    }

    @Override
    public void onBind(NetworkStatus networkStatus) {
        if (networkStatus.isAvailable()) {
            onConnect();
        } else if (!networkStatus.isAvailable()) {
            onDisconnect();
        }
    }

    @Override
    public void onConnect() {
        updateOfflineRequest();
    }

    @Override
    public void onDisconnect() {

    }
}
