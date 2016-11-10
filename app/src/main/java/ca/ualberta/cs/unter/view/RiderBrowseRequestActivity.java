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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import ca.ualberta.cs.unter.util.RequestIntentUtil;

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

        inProgressRequestListView = (ListView) findViewById(R.id.listview_inprogress_riderbrowserequestactivity);
        inProgressRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // intent RiderRequestDetailActivity
                Intent intent = new Intent(RiderBrowseRequestActivity.this, RiderRequestDetailActivity.class);
                intent.putExtra("request", RequestIntentUtil.serializer(inProgressRequestList.get(position)));
                startActivity(intent);
            }
        });

        completedRequestListView = (ListView) findViewById(R.id.listview_inprogress_riderbrowserequestactivity);
        completedRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO customize dialog to show info
            }
        });

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

    /**
     * Update view
     */
    protected void updateRequest() {
        Log.i("Debug", rider.getUserName());
        inProgressRequestController.getRiderInProgressRequest(rider.getUserName());
        completedRequestController.getRiderCompletedRequest(rider.getUserName());
    }
}
