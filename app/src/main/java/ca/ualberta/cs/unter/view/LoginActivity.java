package ca.ualberta.cs.unter.view;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.controller.UserController;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.util.FileIOUtil;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText usernameText;

    private RadioButton riderRadio;
    private RadioButton driverRadio;
    private Button loginButton;
    private Button signupButton;
    private String roleSel;

    private UserController uc = new UserController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            User user = (User) o;
            FileIOUtil.saveUserInFile(user, getApplicationContext());
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = (EditText) findViewById(R.id.editText_userName_LoginActivity);

        riderRadio = (RadioButton) findViewById(R.id.radioButton_rider_LoginActivity);
        driverRadio = (RadioButton) findViewById(R.id.radioButton_driver_LoginActivity);

        loginButton = (Button) findViewById(R.id.button_login_LoginActivity);
        assert loginButton != null;
        loginButton.setOnClickListener(this);

        signupButton = (Button) findViewById(R.id.button_signup_LoginActivity);
        assert signupButton != null;
        signupButton.setOnClickListener(this);

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
            case R.id.radioButton_rider_LoginActivity:
                if (checked)
                    riderRadio.setTypeface(null, Typeface.BOLD);
                    driverRadio.setTypeface(null, Typeface.NORMAL);
                    // will login as rider
                    roleSel = "R";
                    break;
            case R.id.radioButton_driver_LoginActivity:
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
        } else if (view == signupButton){
            // intent to SignupActivity
            Intent intentSignup = new Intent(this, SignupActivity.class);
            startActivity(intentSignup);
        }
    }

    public void login(){
        String username = usernameText.getText().toString();

        // TODO check user validity, replace following line with elastic search
        boolean validUsername = !(username.isEmpty() || username.trim().isEmpty());

        if ( !(validUsername) ){
            Toast.makeText(this, "Username entered is incorrect.", Toast.LENGTH_SHORT).show();
        }

        User user = uc.getUser(username);

        if (user == null) {
            Toast.makeText(this, "User does not exist, please signup", Toast.LENGTH_SHORT).show();
        } else {
            try {
                if (roleSel.equals("R")) {
                    // intent to RiderMainActivity
                    Intent intentRiderMain = new Intent(this, RiderMainActivity.class);
                    startActivity(intentRiderMain);
                } else if (roleSel.equals("D")) {
                    // intent to DriverMainActivity
                    Intent intentDriverMain = new Intent(this, DriverMainActivity.class);
                    startActivity(intentDriverMain);
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
