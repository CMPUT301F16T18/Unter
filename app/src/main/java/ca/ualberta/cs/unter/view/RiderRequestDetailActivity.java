package ca.ualberta.cs.unter.view;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import ca.ualberta.cs.unter.util.RequestUtil;
import cz.msebera.android.httpclient.Header;

public class RiderRequestDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView acceptanceListView;
    private ArrayAdapter<String> acceptedDriverAdapter;
    private ArrayList<String> acceptedDriverList = new ArrayList<>();

    private TextView originalLocationTextView;
    private TextView destinationLocationTextView;

    private Button cancelRequestButton;

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
            openRiderChooseAcceptanceDialog((User) o);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_request_detail);

        request = RequestUtil.deserializer(getIntent().getStringExtra("request"));

        originalLocationTextView = (TextView) findViewById(R.id.textView_startingLocation_RiderRequestDetailActivity);
        destinationLocationTextView = (TextView) findViewById(R.id.textView_endingLocation_RiderRequestDetailActivity);

        getAddress();

        acceptanceListView = (ListView) findViewById(R.id.listView_acceptance_RiderRequestDetailActivity);
        acceptanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // intent RiderChooseAcceptanceDialog
                userController.getUser(acceptedDriverList.get(position));
                //openRiderChooseAcceptanceDialog(acceptedDriverList.get(position));
            }
        });

        cancelRequestButton = (Button) findViewById(R.id.button__cancelRequest_RiderRequestDetailActivity);
        assert cancelRequestButton != null;
        cancelRequestButton.setOnClickListener(this);
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

    private void openRiderChooseAcceptanceDialog(final User driver) {
        //User driver = userController.getUser(driverUserName);
        final String driverUserName = driver.getUserName();
        // TODO get the driver of this acceptance
        String driverName = driver.getUserName();    // replace it with actual driver's name
        final String driverMobile = driver.getMobileNumber();   // replace it with actual driver's mobile
        final String driverEmail = driver.getEmailAddress();   // replace it with actual driver's email

        AlertDialog.Builder builder = new AlertDialog.Builder(RiderRequestDetailActivity.this);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.rider_choose_acceptance_dialog, null);
        Button mobileButton = (Button) promptView.findViewById(R.id.button__callMobile_RiderRequestDetailActivity);
        Button emailButton = (Button) promptView.findViewById(R.id.button__sendEmail_RiderRequestDetailActivity);

        // click to make a phone call
        // http://stackoverflow.com/questions/4816683/how-to-make-a-phone-call-programatically
        mobileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intentCall = new Intent(Intent.ACTION_CALL);
                intentCall.setData(Uri.parse("tel:" + Uri.parse(driverMobile)));
                if (ActivityCompat.checkSelfPermission(RiderRequestDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(Intent.createChooser(intentCall, "Select Phone Call App :"));
            }
        });

        // click to send an email
        // http://stackoverflow.com/questions/3935009/how-to-open-gmail-compose-when-a-button-is-clicked-in-android-app
        emailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{driverEmail});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hello" + driverUserName);
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Select Email Sending App :"));
            }
        });

        builder.setTitle("Confirm Acceptance")
                .setMessage("Driver's Information\n"
                        + "Name: " + driverName)
                .setView(promptView)
                .setPositiveButton(R.string.dialog_confirm_acceptance_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO confirm driver's acceptance, driver will be notified
                        // TODO modify acceptedDriverList so that only this driver's acceptance will appear on acceptanceListView
                        try {
                            requestController.riderConfirmDriver(request, driverUserName);
                        } catch (RequestException e) {
                            Toast.makeText(activity, "The request has not been confirmed by any driver", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
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
