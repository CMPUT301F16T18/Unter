package ca.ualberta.cs.unter.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import ca.ualberta.cs.unter.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText usernameText;
    private EditText emailText;
    private EditText mobileText;

    private RadioButton riderRadio;
    private RadioButton driverRadio;
    private Button loginButton;
    private String roleSel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = (EditText) findViewById(R.id.username);
        emailText = (EditText) findViewById(R.id.email);
        mobileText = (EditText) findViewById(R.id.mobile);

        riderRadio = (RadioButton) findViewById(R.id.radio_rider);
        driverRadio = (RadioButton) findViewById(R.id.radio_driver);

        loginButton = (Button) findViewById(R.id.login_button);
        assert loginButton != null;
        loginButton.setOnClickListener(this);

        // http://stackoverflow.com/questions/8323778/how-to-set-on-click-listener-on-the-radio-button-in-android

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // https://developer.android.com/guide/topics/ui/controls/radiobutton.html
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_rider:
                if (checked)
                    riderRadio.setTypeface(null, Typeface.BOLD);
                    driverRadio.setTypeface(null, Typeface.NORMAL);
                    // will login as rider
                    roleSel = "R";
                    break;
            case R.id.radio_driver:
                if (checked)
                    driverRadio.setTypeface(null, Typeface.BOLD);
                    riderRadio.setTypeface(null, Typeface.NORMAL);
                    // will login as driver
                    roleSel = "D";
                    break;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == loginButton ) {
            login();
        }
    }

    // TODO create a login() function, refer to addHabit in as1
    // TODO maybe also use controller to check user's validity
    public void login(){
        String username = usernameText.getText().toString();
        String email = emailText.getText().toString();
        String mobile = mobileText.getText().toString();

        boolean validUsername = !(username.isEmpty() || username.trim().isEmpty());
        boolean validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean validMobile = Patterns.PHONE.matcher(mobile).matches();

        if ( !(validUsername && validEmail && validMobile) ){
            Toast.makeText(this, "Username/Email/Mobile is not valid.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                if (roleSel.equals("R")) {
                    // intent to RiderMainActivity
//                    Intent intentRiderMain = new Intent(this, RiderMainActivity.class);
//                    startActivity(intentRiderMain);
                } else if (roleSel.equals("D")) {
                    // intent to DriverMainActivity
//                    Intent intentDriverMain = new Intent(this, DriverMainActivity.class);
//                    startActivity(intentDriverMain);
                }
            } catch (Exception e) {
                openSelRoleDialog();
                e.printStackTrace();
            }

        }
    }

    private void openSelRoleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Please Specify Your Role (Rider/Driver).")
                .setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
