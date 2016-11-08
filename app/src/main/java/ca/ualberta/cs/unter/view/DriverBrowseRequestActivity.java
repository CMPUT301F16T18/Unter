package ca.ualberta.cs.unter.view;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.model.request.Request;

public class DriverBrowseRequestActivity extends AppCompatActivity {

    private ListView acceptedRequestListView;
    private ListView pendingRequestListView;

    private ArrayAdapter<Request> acceptedRequestAdapter;
    private ArrayAdapter<Request> pendingRequestAdapter;

    private ArrayList<Request> acceptedRequestList = new ArrayList<>();
    private ArrayList<Request> pendingRequestList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_browse_request);

        acceptedRequestListView = (ListView) findViewById(R.id.listView_acceptedRequest_DriverBrowseRequestActivity);
        acceptedRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open request info dialog
                openRequestInfoDialog();
            }
        });

        pendingRequestListView = (ListView) findViewById(R.id.listView_pendingRequest_DriverBrowseRequestActivity);
        pendingRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open request info dialog
                openRequestInfoDialog();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        acceptedRequestAdapter = new ArrayAdapter<>(this, R.layout.list_item, acceptedRequestList);
        pendingRequestAdapter = new ArrayAdapter<>(this, R.layout.list_item, pendingRequestList);
        acceptedRequestListView.setAdapter(acceptedRequestAdapter);
        pendingRequestListView.setAdapter(pendingRequestAdapter);
    }

    private void openRequestInfoDialog() {
        // TODO get estimated fare price and description of the request
        String estimatedFare = Integer.toString(100);   // replace 100 with estimated price
        String description = "hello";   // replace hello with actual request description

        AlertDialog.Builder builder = new AlertDialog.Builder(DriverBrowseRequestActivity.this);
        builder.setTitle("Request Information")
                .setMessage("Estimated Fare: " + estimatedFare + "\\n" + "Description" + description)
                .setNeutralButton(R.string.dialog_view_map_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO intent to MainActivity/BrowseRequestRouteActivity, send request
                        // TODO display route on one of two above activities
                        // TODO new MainActivity is BrowseRequestRouteActivity without cancel and ok buttons
                        Intent intentDriverMain = new Intent(DriverBrowseRequestActivity.this, DriverMainActivity.class);
                        // http://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
                        intentDriverMain.putExtra("request", "testRequest");   // TODO replace testRequest with actuall request object
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


}
