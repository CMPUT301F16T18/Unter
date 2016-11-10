package ca.ualberta.cs.unter.view;

import android.Manifest;
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

import java.util.ArrayList;

import ca.ualberta.cs.unter.R;

public class RiderRequestDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView acceptanceListView;
    private ArrayAdapter<String> acceptanceAdapter;
    private ArrayList<String> acceptanceList = new ArrayList<>();

    private TextView startingLocationTextView;
    private TextView endingLocationTextView;

    private Button cancelRequestButton;
    private Button completeRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_request_detail);

        startingLocationTextView = (TextView) findViewById(R.id.textView_startingLocation_RiderRequestDetailActivity);
        endingLocationTextView = (TextView) findViewById(R.id.textView_endingLocation_RiderRequestDetailActivity);

        // TODO set text with actual starting and ending location
        startingLocationTextView.setText("UOFA HUB");
        endingLocationTextView.setText("UOFA CAB");

        acceptanceListView = (ListView) findViewById(R.id.listView_acceptance_RiderRequestDetailActivity);
        acceptanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // intent RiderChooseAcceptanceDialog
                openRiderChooseAcceptanceDialog();
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
        acceptanceAdapter = new ArrayAdapter<>(this, R.layout.request_list_item, acceptanceList);
        acceptanceListView.setAdapter(acceptanceAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view == cancelRequestButton ) {
            // TODO cancel this request
        } else if (view == completeRequestButton) {
            // TODO complete this request
            // use this button temporarily to test the dialog
            // openRiderChooseAcceptanceDialog();
        }
    }

    private void openRiderChooseAcceptanceDialog() {
        // TODO get the driver of this acceptance
        String driverName = "yuzhu";    // replace it with actual driver's name
        final String driverMobile = "tel:7803407914";   // replace it with actual driver's mobile
        String driverEmail = "yz6@ua.ca";   // replace it with actual driver's email

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
                // TODO modify acceptanceList so that only this driver's acceptance will appear on acceptanceListView
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


}
