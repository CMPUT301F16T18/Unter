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

import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.novoda.merlin.registerable.bind.Bindable;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;

import java.util.ArrayList;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.controller.RequestController;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.model.request.Request;
import ca.ualberta.cs.unter.util.FileIOUtil;
import ca.ualberta.cs.unter.util.RequestUtil;

/**
 * Activity that driver could browse for current
 * evolved request
 */
public class DriverBrowseRequestActivity extends AppCompatActivity
        implements Connectable, Disconnectable, Bindable {

    private User driver;

    protected Merlin merlin;

    private ListView acceptedRequestListView;
    private ListView pendingRequestListView;

    private ArrayAdapter<Request> acceptedRequestAdapter;
    private ArrayAdapter<Request> pendingRequestAdapter;

    private ArrayList<Request> acceptedRequestList = new ArrayList<>();
    private ArrayList<Request> pendingRequestList = new ArrayList<>();

    private RequestController acceptedRequestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            acceptedRequestList = (ArrayList<Request>) o;
            acceptedRequestAdapter.clear();
            acceptedRequestAdapter.addAll(acceptedRequestList);
            acceptedRequestAdapter.notifyDataSetChanged();
        }
    });

    private RequestController pendingRequestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            pendingRequestList = (ArrayList<Request>) o;
            pendingRequestAdapter.clear();
            pendingRequestAdapter.addAll(pendingRequestList);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_browse_request);

        // monitor network connectivity
        merlin = new Merlin.Builder().withConnectableCallbacks().withDisconnectableCallbacks().withBindableCallbacks().build(this);
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);
        merlin.registerBindable(this);

        // Back button on action bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        acceptedRequestListView = (ListView) findViewById(R.id.listView_acceptedRequest_DriverBrowseRequestActivity);
        acceptedRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open request info dialog
                openRequestInfoDialog(acceptedRequestList.get(position));
            }
        });

        pendingRequestListView = (ListView) findViewById(R.id.listView_pendingRequest_DriverBrowseRequestActivity);
        pendingRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open request info dialog
                openRequestInfoDialog(pendingRequestList.get(position));
            }
        });

        driver = FileIOUtil.loadUserFromFile(getApplicationContext());

        acceptedRequestAdapter = new ArrayAdapter<>(this, R.layout.request_list_item, acceptedRequestList);
        pendingRequestAdapter = new ArrayAdapter<>(this, R.layout.request_list_item, pendingRequestList);
        acceptedRequestListView.setAdapter(acceptedRequestAdapter);
        pendingRequestListView.setAdapter(pendingRequestAdapter);
        updateRequestList();
    }

    @Override
    public void onStart() {
        super.onStart();
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

    private void openRequestInfoDialog(final Request request) {
        // get request info, and show it on the dialog
        String estimatedFare = request.getEstimatedFare().toString();   // replace 100 with estimated price
        String description = request.getRequestDescription();   // replace hello with actual request description

        AlertDialog.Builder builder = new AlertDialog.Builder(DriverBrowseRequestActivity.this);
        builder.setTitle("Request Information")
                .setMessage("Estimated Fare: " + estimatedFare + "\n" + "Description" + description)
                .setNeutralButton(R.string.dialog_view_map_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intentDriverMain = new Intent(DriverBrowseRequestActivity.this, DriverMainActivity.class);
                        // http://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
                        // Serialize the request object and pass it over through the intent
                        intentDriverMain.putExtra("request", RequestUtil.serializer(request));
                        startActivity(intentDriverMain);
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

    private void updateRequestList() {
        acceptedRequestController.getDriverAcceptedRequest(driver.getUserName());
        pendingRequestController.getDriverPendingRequest(driver.getUserName());
    }

    /**
     * Once the device went offline, try to get request list
     * from internal storage
     */
    protected void offlineHandler() {
        pendingRequestController.getDriverOfflinePendingRequest(driver.getUserName(), this);
        acceptedRequestController.getDriverOfflineAcceptedRequest(driver.getUserName(), this);
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
        // try to update offline accepted request
        pendingRequestController.updateDriverOfflineRequest(driver.getUserName(), this);
        updateRequestList();
    }

    @Override
    public void onDisconnect() {
        offlineHandler();
    }
}
