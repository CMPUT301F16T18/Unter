package ca.ualberta.cs.unter.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.novoda.merlin.NetworkStatus;
import com.novoda.merlin.registerable.bind.Bindable;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.util.ArrayList;
import java.util.List;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.UnterConstant;
import ca.ualberta.cs.unter.controller.RequestController;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.OnAsyncTaskFailure;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.model.request.Request;
import ca.ualberta.cs.unter.util.FileIOUtil;
import ca.ualberta.cs.unter.util.OSMapUtil;
import ca.ualberta.cs.unter.util.RequestUtil;

public class DriverMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Connectable, Disconnectable, Bindable {

    private Request request;
    private Activity ourActivity = this;
    private MapView map;
    private Road[] mRoads;

    private User driver;

    private RequestController requestController = new RequestController(
            new OnAsyncTaskCompleted() {
                @Override
                public void onTaskCompleted(Object o) {
                    Request req = (Request) o;
                    FileIOUtil.saveRequestInFile(req, RequestUtil.generateDriverRequestFileName(req),
                            getApplicationContext());
                }
            },
            new OnAsyncTaskFailure() {
                @Override
                public void onTaskFailed(Object o) {
                    Request req = (Request) o;
                    FileIOUtil.saveRequestInFile(req, RequestUtil.generateAcceptedReqestFileName(req),
                            getApplicationContext());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        readyMap();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        TextView username = (TextView) navHeader.findViewById(R.id.nav_drawer_driver_username);
        TextView email = (TextView) navHeader.findViewById(R.id.nav_drawer_driver_email);

        // Get user profile
        User driver = FileIOUtil.loadUserFromFile(getApplicationContext());

        // Set drawer text
        username.setText(driver.getUserName());
        email.setText(driver.getEmailAddress());

        // Get intent
        String requestStr = getIntent().getStringExtra("request");
        if (requestStr != null) {
            // If intent exist
            request = RequestUtil.deserializer(requestStr);
            drawRoute();
        }

        driver = FileIOUtil.loadUserFromFile(this);
    }

    @Override
    public void onStart() {
        super.onStart();
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

    /**
     * Init the map
     */
    protected void readyMap() {
        // map stuff
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMaxZoomLevel(16);
        map.setMultiTouchControls(true);

        final IMapController mapController = map.getController();
        mapController.setZoom(15);
        mapController.setCenter(UnterConstant.UALBERTA_COORDS);
    }

    /**
     * Draw out the route if there is a intent
     */
    protected void drawRoute() {
        GeoPoint startPoint = request.getOriginCoordinate();  // Start point
        IMapController mapController = map.getController();
        mapController.setZoom(9);
        mapController.setCenter(startPoint);  // sets map to centre here

        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("Start Point");
        map.getOverlays().add(startMarker);


        GeoPoint destinationPoint = request.getDestinationCoordinate();  // End point
        Marker endMarker = new Marker(map);
        endMarker.setPosition(destinationPoint);
        endMarker.setTitle("End Point");
        map.getOverlays().add(endMarker);

        // TODO - see if this code is ever used
        // (I don't think it is)
        ArrayList<OverlayItem> overlayItemArray;  //
        overlayItemArray = new ArrayList<>();  //

        overlayItemArray.add(new OverlayItem("Starting Point", "This is the starting point", startPoint));  //
        overlayItemArray.add(new OverlayItem("Destination", "This is the destination point", destinationPoint));  //

        // Get the route
        OSMapUtil.getRoad(startPoint, destinationPoint, updateMap);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // TODO handle all cases
        if (id == R.id.nav_user_profile) {
            Intent intentUserProfile = new Intent(this, EditUserProfileActivity.class);
            startActivity(intentUserProfile);
        } else if (id == R.id.nav_car_info) {
            Intent intent = new Intent(this, DriverCarInfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_request) {
            Intent intentDriverBrowseRequest = new Intent(this, DriverBrowseRequestActivity.class);
            startActivity(intentDriverBrowseRequest);
        } else if (id == R.id.nav_complated_request) {
            Intent intentDriverCompletedRequest = new Intent(this, DriverCompletedRequestActivity.class);
            startActivity(intentDriverCompletedRequest);
        } else if (id == R.id.nav_search) {
            Intent intentDriverSearchRequest = new Intent(this, DriverSearchRequestActivity.class);
            startActivity(intentDriverSearchRequest);
        } else if (id == R.id.nav_logout) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // TODO call openDriverNotifyAcceptedDialog() when a request is accepted by a rider

    private void openDriverNotifyAcceptedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverMainActivity.this);
        builder.setTitle("Rider Acceptance Notification")
                .setMessage("Request XX is Accepted!")  // TODO replace XX with actual request ID
                .setNeutralButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // the update method
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
                String routeDesc = mRoads[i].getLengthDurationText(ourActivity, -1);
                roadPolyline.setTitle(getString(R.string.app_name) + " - " + routeDesc);
                roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
                roadPolyline.setRelatedObject(i);
                mapOverlays.add(1, roadPolyline);

                // all this shizz works
                Toast.makeText(ourActivity, "distance="+mRoads[i].mLength,Toast.LENGTH_LONG).show(); //
                Toast.makeText(ourActivity, "durée="+mRoads[i].mDuration,Toast.LENGTH_LONG).show(); //
            }
        }
    };

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
        requestController.updateDriverOfflineRequest(driver.getUserName(), this);
    }

    @Override
    public void onDisconnect() {

    }
}
