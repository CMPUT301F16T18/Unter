package ca.ualberta.cs.unter.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.ualberta.cs.unter.R;

public class EditUserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText usernameText;
    private EditText emailText;
    private EditText mobileText;

    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        usernameText = (EditText) findViewById(R.id.username_name);
        emailText = (EditText) findViewById(R.id.email_address);
        mobileText = (EditText) findViewById(R.id.mobile_number);

        // TODO get the user, use the user to setText
        usernameText.setText("test username");   // setText(user.getName)
        emailText.setText("test email");      // setText(user.getEmail)
        mobileText.setText("test mobile");     // setText(user.getMobile)

        saveButton = (Button) findViewById(R.id.save_button);
        assert saveButton != null;
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        if (view == saveButton ) {
            editProfile();
        }
    }

    public void editProfile(){
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
                // TODO check duplicate user name
                // TODO change user info to elastic search

                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
