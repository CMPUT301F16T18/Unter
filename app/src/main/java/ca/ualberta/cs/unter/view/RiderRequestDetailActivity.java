package ca.ualberta.cs.unter.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.controller.RequestController;
import ca.ualberta.cs.unter.controller.UserController;
import ca.ualberta.cs.unter.exception.RequestException;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.model.request.Request;
import ca.ualberta.cs.unter.util.OSMapUtil;
import ca.ualberta.cs.unter.util.RequestIntentUtil;
import cz.msebera.android.httpclient.Header;

public class RiderRequestDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView acceptanceListView;
    private ArrayAdapter<String> acceptedDriverAdapter;
    private ArrayList<String> acceptedDriverList = new ArrayList<>();

    private TextView originalLocationTextView;
    private TextView destinationLocationTextView;

    private Button cancelRequestButton;
    private Button completeRequestButton;

    private Request request;
    private ArrayList<User> driverList;

    protected Activity activity = this;

    private RequestController requestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {

        }
    });

    private UserController userController = new UserController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {

        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_request_detail);

        request = RequestIntentUtil.deserializer(getIntent().getStringExtra("request"));

        originalLocationTextView = (TextView) findViewById(R.id.textView_startingLocation_RiderRequestDetailActivity);
        destinationLocationTextView = (TextView) findViewById(R.id.textView_endingLocation_RiderRequestDetailActivity);

        getAddress();

        acceptanceListView = (ListView) findViewById(R.id.listView_acceptance_RiderRequestDetailActivity);
        acceptanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // intent RiderChooseAcceptanceDialog
                openRiderChooseAcceptanceDialog(acceptedDriverList.get(position));
            }
        });

        cancelRequestButton = (Button) findViewById(R.id.button__cancelRequest_RiderRequestDetailActivity);
        assert cancelRequestButton != null;
        cancelRequestButton.setOnClickListener(this);

        completeRequestButton = (Button) findViewById(R.id.button__completeRequest_RiderRequestDetailActivity);
        assert completeRequestButton != null;
        completeRequestButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        acceptedDriverAdapter = new ArrayAdapter<>(this, R.layout.request_list_item, acceptedDriverList);
        acceptanceListView.setAdapter(acceptedDriverAdapter);
        updateDriverList();
    }

    @Override
    public void onClick(View view) {
        if (view == cancelRequestButton ) {
            // TODO cancel this request
            requestController.deleteRequest(request);
        } else if (view == completeRequestButton) {
            // TODO complete this request
            requestController.riderConfirmRequestComplete(request);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDriverList();
    }

    /**
     * Reverse geocoding the lat and lon
     */
    private void getAddress() {
        OSMapUtil.ReverseGeoCoding(request.getOriginCoordinate(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    JSONArray jsonArr = response.getJSONArray("results");
                    String address = jsonArr.getJSONObject(0).getString("formatted_address");
                    originalLocationTextView.setText(address);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
            }
        });

        OSMapUtil.ReverseGeoCoding(request.getDestinationCoordinate(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    JSONArray jsonArr = response.getJSONArray("results");
                    String address = jsonArr.getJSONObject(0).getString("formatted_address");
                    destinationLocationTextView.setText(address);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
            }
        });
    }

    private void openRiderChooseAcceptanceDialog(final String driverUserName) {
        User driver = userController.getUser(driverUserName);

        // TODO get the driver of this acceptance
        String driverName = driver.getUserName();    // replace it with actual driver's name
        final String driverMobile = driver.getMobileNumber();   // replace it with actual driver's mobile
        String driverEmail = driver.getEmailAddress();   // replace it with actual driver's email

        AlertDialog.Builder builder = new AlertDialog.Builder(RiderRequestDetailActivity.this);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.rider_choose_acceptance_dialog, null);
        Button mobileButton = (Button) promptView.findViewById(R.id.button__callMobile_RiderRequestDetailActivity);
        Button emailButton = (Button) promptView.findViewById(R.id.button__sendEmail_RiderRequestDetailActivity);
        Button acceptButton = (Button) promptView.findViewById(R.id.button__confirmAcceptance_RiderRequestDetailActivity);
        Button cancelButton = (Button) promptView.findViewById(R.id.button__cancel_RiderRequestDetailActivity);

        // click to make a phone call
        // http://stackoverflow.com/questions/4816683/how-to-make-a-phone-call-programatically
        mobileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intentCall = new Intent(Intent.ACTION_CALL);
                intentCall.setData(Uri.parse(driverMobile));
                if (ActivityCompat.checkSelfPermission(RiderRequestDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intentCall);
            }
        });

        // click to send an email
        // http://stackoverflow.com/questions/3935009/how-to-open-gmail-compose-when-a-button-is-clicked-in-android-app
        emailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                startActivity(emailIntent);
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TODO confirm driver's acceptance, driver will be notified
                // TODO modify acceptedDriverList so that only this driver's acceptance will appear on acceptanceListView
                try {
                    requestController.riderConfirmDriver(request, driverUserName);
                } catch (RequestException e) {
                    Toast.makeText(activity, "The request has not been confirmed by any driver", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setTitle("Confirm Acceptance")
                .setMessage("Driver's Information\n"
                        + "Name: " + driverName)
                .setView(promptView);

        // Create & Show the AlertDialog
        final AlertDialog dialog = builder.create();

        // http://stackoverflow.com/questions/4053395/android-dialog-cancel-button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    protected void updateDriverList() {
        if (request.getDriverList() == null) return;
        acceptedDriverList = request.getDriverList();
        acceptedDriverAdapter.clear();
        acceptedDriverAdapter.addAll(acceptedDriverList);
        acceptedDriverAdapter.notifyDataSetChanged();
    }

}
