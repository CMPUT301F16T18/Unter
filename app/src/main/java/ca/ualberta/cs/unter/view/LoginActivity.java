package ca.ualberta.cs.unter.view;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import ca.ualberta.cs.unter.R;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameText;
    // private EditText passwordText;
    private RadioGroup rolesGroup;
    private RadioButton riderRadio;
    private RadioButton driverRadio;
    private Button loginButton;
    private String roleSel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = (EditText) findViewById(R.id.username);
        // passwordText = (EditText) findViewById(R.id.password);

        riderRadio = (RadioButton) findViewById(R.id.radio_rider);
        driverRadio = (RadioButton) findViewById(R.id.radio_driver);

        loginButton = (Button) findViewById(R.id.login_button);

        // http://stackoverflow.com/questions/8323778/how-to-set-on-click-listener-on-the-radio-button-in-android

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // https://developer.android.com/guide/topics/ui/controls/radiobutton.html
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_rider:
                if (checked)
                    riderRadio.setTypeface(null, Typeface.BOLD_ITALIC);
                    // will login as rider
                    roleSel = "R";
                    break;
            case R.id.radio_driver:
                if (checked)
                    driverRadio.setTypeface(null, Typeface.BOLD_ITALIC);
                    // will login as driver
                    roleSel = "D";
                    break;
        }
    }

//    @Override
//    public void onClick(View view) {
//        if (view == loginButton ) {
//            login();
//        }
//    }

    // TODO create a login() function, refer to addHabit in as1
    protected void login(){
        boolean validUsername = true;
        // boolean validPassword = true;

        String username = usernameText.getText().toString();
        // String password = passwordText.getText().toString();

        // TODO maybe also use controller to check user's validity
        if (username.isEmpty() || username.trim().isEmpty()){
            validUsername = false;
            Toast.makeText(this, "Username is not valid.", Toast.LENGTH_SHORT).show();
        }

        if (validUsername) {
            try {
                if (roleSel == "R") {
                    // intent to RiderMainActivity

                } else if (roleSel == "D") {
                    // intent to DriverMainActivity

                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }

        }
    }
}
