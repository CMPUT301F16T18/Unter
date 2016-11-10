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
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.UnterConstant;
import ca.ualberta.cs.unter.controller.RequestController;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.Route;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.model.request.PendingRequest;
import ca.ualberta.cs.unter.model.request.Request;
import ca.ualberta.cs.unter.util.FileIOUtil;
import ca.ualberta.cs.unter.util.OSMapUtil;

public class RiderMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private EditText searchDepartureLocationEditText;
    private EditText searchDestinationLocationEditText;
    private Button searchDepartureButton;
    private Button searchDestinationButton;
    protected GeoPoint departureLocation;
    protected GeoPoint destinationLocation;
	private MapView map;
    private User rider;
	private Marker startMarker;
	private Marker endMarker;

    private RequestController requestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {

        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_main);

		// map stuff

		map = (MapView) findViewById(R.id.map);
		map.setTileSource(TileSourceFactory.MAPNIK);
		map.setBuiltInZoomControls(true);
		map.setMultiTouchControls(true);

		final IMapController mapController = map.getController();
		mapController.setZoom(20);
		mapController.setCenter(UnterConstant.UALBERTA_COORDS);

        searchDepartureLocationEditText = (EditText) findViewById(R.id.editDeparture);
        assert searchDepartureLocationEditText != null;
        //searchDepartureLocationEditText.setOnClickListener(this);

        searchDestinationLocationEditText = (EditText) findViewById(R.id.editDestination);
        assert searchDestinationLocationEditText != null;
        //searchDestinationLocationEditText.setOnClickListener(this);

        // The search button for departure location
        // TODO geocoder is broken on the Galaxy Note 3
        // Implement with the google map api instead, someday
        searchDepartureButton = (Button) findViewById(R.id.buttonSearchDep);
        searchDepartureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OSMapUtil.GeocoderTask task = new OSMapUtil.GeocoderTask(getApplicationContext(), new OnAsyncTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Object o) {
                        // Call back method after the coordinate is obtained
                        // TODO drop a marker on the map once the location is obtain // done
                        departureLocation = (GeoPoint) o;

						startMarker = createMarker(departureLocation, "Pick-Up");  // hard-coded string for now
						mapController.setCenter(departureLocation);

					}
                });

                task.execute(searchDepartureLocationEditText.getText().toString());
			}
        });

        // The search button for destination location
        searchDestinationButton = (Button) findViewById(R.id.buttonSearchDest);
        searchDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OSMapUtil.GeocoderTask task = new OSMapUtil.GeocoderTask(getApplicationContext(), new OnAsyncTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Object o) {
                        // Call back method after the coordinate is obtained
                        // TODO drop a marker on the map once the location is obtained
                        // also the route
                        destinationLocation = (GeoPoint) o;
						endMarker = createMarker(departureLocation, "Drop-Off");  // hard-coded string for now
						mapController.setCenter(destinationLocation);
					}
                });
                task.execute(searchDestinationLocationEditText.getText().toString());
			}
        });

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
        rider = FileIOUtil.loadUserFromFile(getApplicationContext());
        // Set text
        username.setText(rider.getUserName());
        email.setText(rider.getEmailAddress());
    }

    @Override
    public void onClick(View view) {
        if (view == searchDepartureLocationEditText) {
            Intent intentRiderEnterLocation = new Intent(this, RiderEnterLocationActivity.class);
            startActivityForResult(intentRiderEnterLocation, 1);
        } else if (view == searchDestinationLocationEditText) {
            Intent intentRiderEnterLocation = new Intent(this, RiderEnterLocationActivity.class);
            startActivity(intentRiderEnterLocation);
            startActivityForResult(intentRiderEnterLocation, 2);
        }
    }

    // http://stackoverflow.com/questions/14292398/how-to-pass-data-from-2nd-activity-to-1st-activity-when-pressed-back-android
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String strEditText = data.getStringExtra("edittextvalue");
                searchDepartureLocationEditText.setText(strEditText);
            }
        } else if (requestCode == 2) {
            if(resultCode == RESULT_OK){
                String strEditText = data.getStringExtra("edittextvalue");
                searchDestinationLocationEditText.setText(strEditText);
            }
        }
    }

    private void openRiderSendRequestDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(RiderMainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View promptView = inflater.inflate(R.layout.rider_send_request_dialog, null);

        // TODO
        // Set the default fare
        final Request request = new PendingRequest(rider.getUserName(), new Route(departureLocation, destinationLocation));
        requestController.calculateEstimatedFare(request);

        final EditText fareEditText = (EditText) promptView.findViewById(R.id.edittext_fare_ridermainactivity);
        final EditText descriptionEditText = (EditText) promptView.findViewById(R.id.edittext_description_ridermainactivity);
        fareEditText.setText(request.getEstimatedFare().toString());

        builder.setTitle("Send Request")
                .setView(promptView)
                .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setPositiveButton(R.string.dialog_send_request_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO send request to drivers
                        String searchStartLocation = searchDepartureLocationEditText.getText().toString().trim();
                        String searchEndLocation = searchDestinationLocationEditText.getText().toString().trim();
                        String description = descriptionEditText.getText().toString();
                        double fare = Double.parseDouble(fareEditText.getText().toString());
                        if (searchStartLocation.isEmpty() || searchEndLocation.isEmpty()) {
                            Toast.makeText(RiderMainActivity.this,
                                    "Starting/Ending Location is empty", Toast.LENGTH_SHORT).show();
                        } else if (fare == 0) {
                           fareEditText.setError("Fare cannot be empty");
                        } else if (description.isEmpty()) {
                            descriptionEditText.setError("Description cannot be empty");
                        } else {
                            Request req = new PendingRequest(rider.getUserName(),new Route(departureLocation, destinationLocation));
                            Log.i("Debug", String.format("%.2f", fare));
                            req.setEstimatedFare(fare);
                            req.setRequestDescription(description);
                            requestController.createRequest(req);
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

	public Marker createMarker(GeoPoint geoPoint, String title) {
		Marker marker = new Marker(map);
		marker.setPosition(geoPoint);
		marker.setTitle(title);
		marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
		map.getOverlays().add(marker);
		return marker;
	}

}
