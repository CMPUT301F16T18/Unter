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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.controller.RequestController;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.model.request.Request;
import ca.ualberta.cs.unter.util.FileIOUtil;
import ca.ualberta.cs.unter.util.RequestUtil;

/**
 * Activity that allows rider to browse request
 * including completed request and the reuqest that
 * the rider currently envolve
 */
public class RiderBrowseRequestActivity extends AppCompatActivity {

    private ListView inProgressRequestListView;
    private ListView completedRequestListView;

    private ArrayAdapter<Request> inProgressRequestAdapter;
    private ArrayAdapter<Request> completedRequestAdapter;

    private ArrayList<Request> inProgressRequestList = new ArrayList<>();
    private ArrayList<Request> completedRequestList = new ArrayList<>();

    private RequestController inProgressRequestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            inProgressRequestList = (ArrayList<Request>) o;
            inProgressRequestAdapter.clear();
            inProgressRequestAdapter.addAll(inProgressRequestList);
            inProgressRequestAdapter.notifyDataSetChanged();
        }
    });

    private RequestController confirmRequestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            updateRequest();
        }
    });

    private RequestController completedRequestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            completedRequestList = (ArrayList<Request>) o;
            completedRequestAdapter.clear();
            completedRequestAdapter.addAll(completedRequestList);
            completedRequestAdapter.notifyDataSetChanged();
        }
    });

    private User rider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_browse_request);

        // Back button on action bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inProgressRequestListView = (ListView) findViewById(R.id.listview_inprogress_riderbrowserequestactivity);
        inProgressRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Request req = inProgressRequestList.get(position);
                if (req.getDriverUserName() == null || TextUtils.isEmpty(req.getDriverUserName())) {
                    // If the request has not been confirmed by the rider
                    // Direct to the activity that allow rider to select driver
                    Intent intent = new Intent(RiderBrowseRequestActivity.this, RiderRequestDetailActivity.class);
                    intent.putExtra("request", RequestUtil.serializer(req));
                    startActivity(intent);
                } else {
                    // If the request has been confirmed by the rider
                    openInProgressRequestInfoDialog(req);
                }
            }
        });

        completedRequestListView = (ListView) findViewById(R.id.listview_completed_riderwrowseqequestactivity);
        completedRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openCompletedRequestInfodialog(completedRequestList.get(position));
            }
        });

        // Get user info from the internal storage
        rider = FileIOUtil.loadUserFromFile(getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        inProgressRequestAdapter = new ArrayAdapter<>(this, R.layout.request_list_item, inProgressRequestList);
        completedRequestAdapter = new ArrayAdapter<>(this, R.layout.request_list_item, completedRequestList);
        inProgressRequestListView.setAdapter(inProgressRequestAdapter);
        completedRequestListView.setAdapter(completedRequestAdapter);
        updateRequest();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, RiderMainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // TODO
    private void openInProgressRequestInfoDialog(final Request request) {
        String actualFare = request.getEstimatedFare().toString();   // replace 100 with actual price
        String description = request.getRequestDescription();   // replace hello with actual request description

        AlertDialog.Builder builder = new AlertDialog.Builder(RiderBrowseRequestActivity.this);
        builder.setTitle("Your request has been accepted")
                .setMessage("Actual Fare: " + actualFare + "\n" + "Description:" + description)
                .setNeutralButton(R.string.dialog_view_map_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Take a look at the route
                        Intent intent = new Intent(RiderBrowseRequestActivity.this, BrowseRequestRouteActivity.class);
                        // http://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
                        intent.putExtra("request", RequestUtil.serializer(request));
                        startActivity(intent);
                    }
                })
                .setPositiveButton(R.string.dialog_confirm_completion_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmRequestController.riderConfirmRequestComplete(request);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // cutomize the dialog to display more information (driver info)
    private void openCompletedRequestInfodialog(final Request request) {
        String actualFare = request.getEstimatedFare().toString();   // replace 100 with actual price
        String description = request.getRequestDescription();   // replace hello with actual request description

        AlertDialog.Builder builder = new AlertDialog.Builder(RiderBrowseRequestActivity.this);
        builder.setTitle("Request Information")
                .setMessage("Actual Fare: " + actualFare + "\n" + "Description:" + description)
                .setNeutralButton(R.string.dialog_view_map_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Take a look at the route
                        Intent intent = new Intent(RiderBrowseRequestActivity.this, BrowseRequestRouteActivity.class);
                        // http://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
                        intent.putExtra("request", RequestUtil.serializer(request));
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Update view
     */
    protected void updateRequest() {
        Log.i("Debug", rider.getUserName());
        inProgressRequestController.getRiderInProgressRequest(rider.getUserName());
        completedRequestController.getRiderCompletedRequest(rider.getUserName());
    }
}
