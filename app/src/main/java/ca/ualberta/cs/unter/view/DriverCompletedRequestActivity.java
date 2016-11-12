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
 * Activity that driver would be able to
 * browse every past request
 */
public class DriverCompletedRequestActivity extends AppCompatActivity {
    private ListView completedRequestListView;
    private ArrayAdapter<Request> completedRequestAdapter;
    private ArrayList<Request> completedRequestList = new ArrayList<>();

    private User driver;

    private RequestController requestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            completedRequestList = (ArrayList<Request>) o; // cast
            // Update the list view
            completedRequestAdapter.clear();
            completedRequestAdapter.addAll(completedRequestList);
            completedRequestAdapter.notifyDataSetChanged();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_completed_request);

        // Back button on action bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        completedRequestListView = (ListView) findViewById(R.id.listView_completedRequest_DriverCompletedRequestActivity);
        completedRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open request info dialog
                openRequestInfoDialog(completedRequestList.get(position));
            }
        });
        driver = FileIOUtil.loadUserFromFile(getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        completedRequestAdapter = new ArrayAdapter<>(this, R.layout.request_list_item, completedRequestList);
        completedRequestListView.setAdapter(completedRequestAdapter);
        requestController.getDriverCompletedRequest(driver.getUserName());
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

    private void openRequestInfoDialog(final Request request) {
        // TODO get estimated fare price and description of the request
        String actualFare = request.getEstimatedFare().toString();   // replace 100 with actual price
        String description = request.getRequestDescription();   // replace hello with actual request description

        AlertDialog.Builder builder = new AlertDialog.Builder(DriverCompletedRequestActivity.this);
        builder.setTitle("Request Information")
                .setMessage("Actual Fare: " + actualFare + "\n" + "Description:" + description)
                .setNeutralButton(R.string.dialog_view_map_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Take a look at the route
                        Intent intent = new Intent(DriverCompletedRequestActivity.this, BrowseRequestRouteActivity.class);
                        // http://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
                        intent.putExtra("request", RequestUtil.serializer(request));   // TODO replace testRequest with actuall request object
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
}
