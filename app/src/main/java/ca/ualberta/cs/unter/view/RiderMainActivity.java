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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.novoda.merlin.registerable.bind.Bindable;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;

import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.UnterConstant;
import ca.ualberta.cs.unter.controller.RequestController;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.OnAsyncTaskFailure;
import ca.ualberta.cs.unter.model.Route;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.model.request.PendingRequest;
import ca.ualberta.cs.unter.model.request.Request;
import ca.ualberta.cs.unter.util.FileIOUtil;
import ca.ualberta.cs.unter.util.OSMapUtil;
import ca.ualberta.cs.unter.util.RequestUtil;
import cz.msebera.android.httpclient.Header;


/**
 * Main activity of rider that could browse locatoin on map, search location, and send request
 *
 * Issue: it may possible break when the route is way too long for OSM to render.
 */
public class RiderMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Connectable, Disconnectable, Bindable {

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
    private Road[] mRoads;
    private double distance;

    protected Merlin merlin;

    private ArrayList<Request> offlineRequestList = new ArrayList<>();

    private RequestController requestController = new RequestController(
            new OnAsyncTaskCompleted() {
                @Override
                public void onTaskCompleted(Object o) {
                    FileIOUtil.saveRiderRequestInFile((Request) o, getApplicationContext());
                }
            },
            new OnAsyncTaskFailure() {
                @Override
                public void onTaskFailed(Object o) {
                    Toast.makeText(getApplication(), "Device offline", Toast.LENGTH_SHORT).show();
                    offlineRequestList.add((Request) o);
                    FileIOUtil.saveOfflineRequestInFile((Request) o, getApplicationContext());
                }
            });

    private RequestController acceptedRequestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            ArrayList<Request> acceptedRequestList = (ArrayList<Request>) o;
            checkAccepted(acceptedRequestList);
        }
    }, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_main);

        // merline stuff
        merlin = new Merlin.Builder().withConnectableCallbacks().withDisconnectableCallbacks().withBindableCallbacks().build(this);
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);
        merlin.registerBindable(this);

        // map stuff
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMaxZoomLevel(16);
        map.setMultiTouchControls(true);

        final IMapController mapController = map.getController();
        mapController.setZoom(15);
        mapController.setCenter(UnterConstant.UALBERTA_COORDS);

        searchDepartureLocationEditText = (EditText) findViewById(R.id.editDeparture);
        assert searchDepartureLocationEditText != null;

        searchDestinationLocationEditText = (EditText) findViewById(R.id.editDestination);
        assert searchDestinationLocationEditText != null;

        // Implement geocoding with the google map api
        searchDepartureButton = (Button) findViewById(R.id.buttonSearchDep);
        searchDepartureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String departureStr = searchDepartureLocationEditText.getText().toString();
                if (TextUtils.isEmpty(departureStr)) {
                    searchDepartureLocationEditText.setError("Please enter an address");
                    return;
                }
                OSMapUtil.GeoCoding(departureStr,
                        new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                // phrase http reponse into geopoint
                                departureLocation = OSMapUtil.pharseGeoJson(response);
                                if (map.getOverlays().contains(startMarker)) {
									map.getOverlays().remove(startMarker);
									startMarker = createMarker(departureLocation, "Pick-Up");
                                    map.getOverlays().add(startMarker);
                                    mapController.setCenter(startMarker.getPosition());
                                } else {
									startMarker = createMarker(departureLocation, "Pick-Up");
									map.getOverlays().add(startMarker);
									mapController.setCenter(startMarker.getPosition());
                                }
								// set startMarker to draggable + init listener
								startMarker.setDraggable(true);
								startMarker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
									@Override
									public void onMarkerDrag(Marker marker) {

									}

									@Override
									public void onMarkerDragEnd(Marker marker) {
										startMarker.setPosition(marker.getPosition());
										mapController.setCenter(startMarker.getPosition());
										map.invalidate();
										//getRoadAsync(startPoint, testMarker.getPosition());
									}

									@Override
									public void onMarkerDragStart(Marker marker) {

									}
								});

                            }
                        });
            }
        });

        // The search button for destination location
        searchDestinationButton = (Button) findViewById(R.id.buttonSearchDest);
        searchDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destinationStr = searchDestinationLocationEditText.getText().toString();
                if (TextUtils.isEmpty(destinationStr)) {
                    searchDestinationLocationEditText.setError("Please enter an address");
                    return;
                }
                OSMapUtil.GeoCoding(destinationStr,
                        new JsonHttpResponseHandler() {
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                // phares https reponse into geopoint
                                destinationLocation = OSMapUtil.pharseGeoJson(response);
                                endMarker = createMarker(destinationLocation, "Drop-Off");
                                map.getOverlays().add(endMarker);
                                mapController.setCenter(destinationLocation);
                                mapController.setZoom(15);
                                // open a dialog to confirm the location before getting the route
                                openRiderConfirmPathDialog();
                            }
                        });
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
        // Set text on drawer
        View navHeader = navigationView.getHeaderView(0);
        TextView username = (TextView) navHeader.findViewById(R.id.nav_drawer_rider_username);
        TextView email = (TextView) navHeader.findViewById(R.id.nav_drawer_rider_email);
        // Get user profile
        rider = FileIOUtil.loadUserFromFile(getApplicationContext());
        // Set text
        username.setText(rider.getUserName());
        email.setText(rider.getEmailAddress());

        acceptedRequestController.getRiderInProgressRequest(rider.getUserName());
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        merlin.bind();
    }

    @Override
    protected void onPause() {
        super.onPause();
        merlin.unbind();
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
            startActivity(new Intent(this, LoginActivity.class));
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
            if (resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("edittextvalue");
                searchDepartureLocationEditText.setText(strEditText);
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("edittextvalue");
                searchDestinationLocationEditText.setText(strEditText);
            }
        }
    }

    // A dialog allow user to confirm the location he enters
    private void openRiderConfirmPathDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RiderMainActivity.this);

        builder.setTitle("Confirm location")
                .setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO - this breaks the code if you zoom in too far
                        OSMapUtil.getRoad(startMarker.getPosition(), destinationLocation, updateMap);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openRiderSendRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RiderMainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View promptView = inflater.inflate(R.layout.rider_send_request_dialog, null);

        // Set the default fare
        final Request request = new PendingRequest(rider.getUserName(), new Route(departureLocation, destinationLocation));
        requestController.setDistance(request, distance);  // set the distance before estimating fare
        requestController.calculateEstimatedFare(request);

        final EditText fareEditText = (EditText) promptView.findViewById(R.id.edittext_fare_ridermainactivity);
        final EditText descriptionEditText = (EditText) promptView.findViewById(R.id.edittext_description_ridermainactivity);
        fareEditText.setText(request.getRoundedFare());  // shows fare rounded to 2 dec places

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
                        String description = descriptionEditText.getText().toString();
                        double fare = Double.parseDouble(fareEditText.getText().toString());
                        if (fare == 0) {
                            fareEditText.setError("Fare cannot be empty");
                        } else if (description.isEmpty()) {
                            descriptionEditText.setError("Description cannot be empty");
                        } else {
                            Route route = new Route(departureLocation, destinationLocation);
                            route.setDistance(distance);
                            Request req = new PendingRequest(rider.getUserName(), route);
                            req.setEstimatedFare(fare);
                            req.setRequestDescription(description);
                            req.setID(UUID.randomUUID().toString());
                            requestController.createRequest(req);
                            // clean up filed after it's done
                            searchDepartureLocationEditText.setText("");
                            searchDestinationLocationEditText.setText("");
                        }
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // TODO call openRiderNotifiedRequestDialog() when a request is accepted by a driver

    // pops up on RiderMainActivity when a request is accepted by a driver
    private void openRiderNotifiedRequestDialog(final Request request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RiderMainActivity.this);
        builder.setTitle("Request Status Message")
                .setMessage("Request has been accepted by a Driver!\n" +
                        "Click on View Request button to view request details.")  // TODO replace XX with actual request ID
                .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNeutralButton(R.string.dialog_view_request_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // intent RiderRequestDetailActivity
                        Intent intent = new Intent(RiderMainActivity.this, RiderRequestDetailActivity.class);
                        intent.putExtra("request", RequestUtil.serializer(request));
                        startActivity(intent);
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Create a point marker on the map
     *
     * @param geoPoint the geolocatoin of the point
     * @param title the title to display
     * @return the marker
     */
    public Marker createMarker(GeoPoint geoPoint, String title) {
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);
        marker.setTitle(title);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        return marker;
    }

    /**
     * An async task to draw road on map
     */
    private OnAsyncTaskCompleted updateMap = new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            mRoads = (Road[]) o;  // not sure if this needs to be here, but it works
            if (mRoads == null)
                return;
            if (mRoads[0].mStatus == Road.STATUS_TECHNICAL_ISSUE)
                Toast.makeText(map.getContext(), "Technical issue when getting the route", Toast.LENGTH_SHORT).show();
            else if (mRoads[0].mStatus > Road.STATUS_TECHNICAL_ISSUE) //functional issues
                Toast.makeText(map.getContext(), "No possible route here", Toast.LENGTH_SHORT).show();
            Polyline[] mRoadOverlays = new Polyline[mRoads.length];
            List<Overlay> mapOverlays = map.getOverlays();
            for (int i = 0; i < mRoads.length; i++) {
                Polyline roadPolyline = RoadManager.buildRoadOverlay(mRoads[i]);
                mRoadOverlays[i] = roadPolyline;
                String routeDesc = mRoads[i].getLengthDurationText(getApplication(), -1);
                roadPolyline.setTitle(getString(R.string.app_name) + " - " + routeDesc);
                roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
                roadPolyline.setRelatedObject(i);
                mapOverlays.add(1, roadPolyline);

                // displays route distance on map overlay
                Toast.makeText(map.getContext(), "Distance =" + mRoads[i].mLength, Toast.LENGTH_LONG).show();
                distance = mRoads[i].mLength;
				map.invalidate();
            }
        }
    };

    protected void updateOfflineRequest() {
        ArrayList<String> offlineList = RequestUtil.getOfflineRequestList(getApplicationContext());
        if (offlineList == null) return;
        offlineRequestList = FileIOUtil.loadRequestFromFile(getApplicationContext(), offlineList);
        for (Request r : offlineRequestList) {
            if (r.getRiderUserName().equals(rider.getUserName())) {
                requestController.createRequest(r);
                deleteFile(RequestUtil.generateOfflineRequestFileName(r));
            }
        }
    }

    protected void checkAccepted(ArrayList<Request> requestsList) {
        ArrayList<String> fileList = RequestUtil.getRiderRequestList(this);
        if (fileList == null) return;
        for (Request r : requestsList) {
            // if request has been confirmed by a driver
            if (r.getDriverList() != null && r.getDriverUserName() == null) {
                Request req = FileIOUtil.loadSingleRequestFromFile(RequestUtil.generateRiderRequestFileName(r), this);
                if (!req.equals(r)) {
                    openRiderNotifiedRequestDialog(r);
                }
            }
        }
    }

    @Override
    public void onBind(NetworkStatus networkStatus) {
        if (!networkStatus.isAvailable()) {
            onDisconnect();
        } else if (networkStatus.isAvailable()) {
            onConnect();
        }
    }

    @Override
    public void onConnect() {
        Log.i("Debug", "Connected");
        updateOfflineRequest();
    }

    @Override
    public void onDisconnect() {
        Log.i("Debug", "Disconnect");
    }
}
