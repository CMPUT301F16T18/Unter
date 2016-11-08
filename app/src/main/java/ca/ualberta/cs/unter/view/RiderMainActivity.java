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
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.controller.RequestController;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.util.FileIOUtil;

public class RiderMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private EditText searchStartLocationEditText;
    private EditText searchEndLocationEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_main);

        searchStartLocationEditText = (EditText) findViewById(R.id.editText_searchStartLocation_RiderMainActivity);
        assert searchStartLocationEditText != null;
        searchStartLocationEditText.setOnClickListener(this);

        searchEndLocationEditText = (EditText) findViewById(R.id.editText_searchEndLocation_RiderMainActivity);
        assert searchEndLocationEditText != null;
        searchEndLocationEditText.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRiderSendRequestDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        View navHeader = navigationView.getHeaderView(0);

        TextView username = (TextView) navHeader.findViewById(R.id.nav_drawer_rider_username);
        TextView email = (TextView) navHeader.findViewById(R.id.nav_drawer_rider_email);

        // Get user profile
        User rider = FileIOUtil.loadUserFromFile(getApplicationContext());
        // Set text
        username.setText(rider.getUserName());
        email.setText(rider.getEmailAddress());
    }

    @Override
    public void onClick(View view) {
        if (view == searchStartLocationEditText) {
            Intent intentRiderEnterLocation = new Intent(this, RiderEnterLocationActivity.class);
            startActivity(intentRiderEnterLocation);
            // TODO after returning from RiderEnterLocationActivity, set location result string to searchStartLocationEditText
        } else if (view == searchEndLocationEditText) {
            Intent intentRiderEnterLocation = new Intent(this, RiderEnterLocationActivity.class);
            startActivity(intentRiderEnterLocation);
            // TODO after returning from RiderEnterLocationActivity, set location result string to searchEndLocationEditText
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_user_profile) {
            Intent intentUserProfile = new Intent(this, EditUserProfileActivity.class);
            startActivity(intentUserProfile);
        } else if (id == R.id.nav_request) {
            Intent intentRiderBrowseRequest = new Intent(this, RiderBrowseRequestActivity.class);
            startActivity(intentRiderBrowseRequest);
        } else if (id == R.id.nav_logout) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openRiderSendRequestDialog() {
        // TODO get estimated fare price and description of the request
        String estimatedFare = Integer.toString(100);   // replace 100 with estimated price
        String description = "hello";   // replace hello with actual request description

        AlertDialog.Builder builder = new AlertDialog.Builder(RiderMainActivity.this);
        builder.setTitle("Send Request")
                .setMessage("Estimated Fare: " + estimatedFare + "\n" + "Description" + description)
                .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNeutralButton(R.string.dialog_send_request_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO send request to drivers
                        String searchStartLocation = searchStartLocationEditText.getText().toString().trim();
                        String searchEndLocation = searchEndLocationEditText.getText().toString().trim();
                        if (searchStartLocation.isEmpty() || searchEndLocation.isEmpty()) {
                            Toast.makeText(RiderMainActivity.this,
                                    "Starting/Ending Location is empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // TODO call openRiderNotifiedRequestDialog() when a request is accepted by a driver

    // pops up on RiderMainActivity when a request is accepted by a driver
    private void openRiderNotifiedRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RiderMainActivity.this);
        builder.setTitle("Request Status Message")
                .setMessage("Request XX is Accepted by a Driver.!\n " +
                        "Click on View Request Button to View Request Details.")  // TODO replace XX with actual request ID
                .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNeutralButton(R.string.dialog_view_request_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // intent RiderRequestDetailActivity
                        Intent intentRiderRequestDetail = new Intent(RiderMainActivity.this, RiderRequestDetailActivity.class);
                        startActivity(intentRiderRequestDetail);
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
