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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.controller.RequestController;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.model.request.Request;
import ca.ualberta.cs.unter.util.FileIOUtil;
import ca.ualberta.cs.unter.util.OSMapUtil;
import ca.ualberta.cs.unter.util.RequestIntentUtil;

/**
 * Activity that driver can search for request
 * and browse the map and accept it
 */
public class DriverSearchRequestActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner searchOptionSpinner;
    private ArrayAdapter<CharSequence> searchOptionAdapter;

    EditText searchContextEditText;
    private Button searchButton;

    private ListView searchRequestListView;
    private ArrayAdapter<Request> searchRequestAdapter;
    private ArrayList<Request> searchRequestList = new ArrayList<>();

    private int searchOption;
    private User driver;

    // Requestcontroller for searching result
    private RequestController requestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            // Cast
            searchRequestList = (ArrayList<Request>) o;
            for (Request r : searchRequestList) {
                // If the request has been confirmed by rider
                // or the request has been confirmed by the current driver
                Log.i("Debug", r.getRequestDescription());
                if (r.getDriverUserName() != null || r.getDriverList().contains(driver.getUserName())) {
                    searchRequestList.remove(r);
                }
            }
            // Notify the adapter things is changed
            searchRequestAdapter.clear();
            searchRequestAdapter.addAll(searchRequestList);
            searchRequestAdapter.notifyDataSetChanged();
        }
    });

    // Request controller for confirming request
    // TODO data consistent
    private RequestController confirmedRequestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            Request request = (Request) o;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_search_request);

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
        searchRequestAdapter = new ArrayAdapter<>(this, R.layout.list_item, searchRequestList);
        searchRequestListView.setAdapter(searchRequestAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view == searchButton) {
            if (searchOption == 0) {
                // If search by geolocation
                OSMapUtil.GeocoderTask task = new OSMapUtil.GeocoderTask(getApplicationContext());
                task.execute(searchContextEditText.getText().toString());

                try {
                    requestController.searchRequestByGeoLocation(task.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else if (searchOption == 1) {
                // If search by keyword
                requestController.searchRequestByKeyword(searchContextEditText.getText().toString());
            }
        }
    }

    private void openRequestInfoDialog(final Request request) {
        // TODO get estimated fare price and description of the request
        String estimatedFare = request.getEstimatedFare().toString();   // replace 100 with estimated price
        String description = request.getRequestDescription();   // replace hello with actual request description

        AlertDialog.Builder builder = new AlertDialog.Builder(DriverSearchRequestActivity.this);
        builder.setTitle("Request Information")
                .setMessage("Estimated Fare: " + estimatedFare + "\\n" + "Description" + description)
                .setNeutralButton(R.string.dialog_view_map_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Serialize the request to pass it over the intent
                        Intent intentDriverMain = new Intent(DriverSearchRequestActivity.this, BrowseRequestRouteActivity.class);
                        // http://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
                        intentDriverMain.putExtra("request", RequestIntentUtil.serializer(request));   // TODO replace testRequest with actuall request object
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

}
