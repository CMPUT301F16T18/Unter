package ca.ualberta.cs.unter.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.model.request.Request;

public class RiderRequestDetailActivity extends AppCompatActivity {

    private ListView acceptanceListView;
    private ArrayAdapter<String> acceptanceAdapter;
    private ArrayList<String> acceptanceList = new ArrayList<>();

    private TextView startingLocationTextView;
    private TextView endingLocationTextView;

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
    }

    @Override
    public void onStart() {
        super.onStart();
        acceptanceAdapter = new ArrayAdapter<>(this, R.layout.list_item, acceptanceList);
        acceptanceListView.setAdapter(acceptanceAdapter);
    }

    private void openRiderChooseAcceptanceDialog() {
        // TODO get the driver of this acceptance
        String driverName = "yuzhu";    // replace it with actual driver's name
        final String driverMobile = "tel:7801234567";   // replace it with actual driver's mobile
        String driverEmail = "yz6@ua.ca";   // replace it with actual driver's email

        AlertDialog.Builder builder = new AlertDialog.Builder(RiderRequestDetailActivity.this);
        builder.setTitle("Confirm Acceptance")
                .setMessage("Driver's Information\n"
                        + "Name: " + driverName + "\n")
                // click to make a phone call
                // http://stackoverflow.com/questions/4816683/how-to-make-a-phone-call-programatically
                .setNeutralButton(R.string.dialog_call_mobile_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intentCall = new Intent(Intent.ACTION_CALL);
                        intentCall.setData(Uri.parse(driverMobile));
                        if (ActivityCompat.checkSelfPermission(RiderRequestDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(intentCall);
                    }
                })
                // click to send an email
                // http://stackoverflow.com/questions/3935009/how-to-open-gmail-compose-when-a-button-is-clicked-in-android-app
                .setNeutralButton(R.string.dialog_send_email_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("text/plain");
                        startActivity(emailIntent);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNeutralButton(R.string.dialog_confirm_acceptance_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO confirm driver's acceptance, driver will be notified
                        // TODO modify acceptanceList so that only this driver's acceptance will appear on acceptanceListView
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
