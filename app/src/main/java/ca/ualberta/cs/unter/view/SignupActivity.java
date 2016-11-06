package ca.ualberta.cs.unter.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.controller.UserController;
import ca.ualberta.cs.unter.exception.UserException;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.util.FileIOUtil;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameText;
    private EditText emailText;
    private EditText mobileText;

    private Button signupButton;

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
        setContentView(R.layout.activity_signup);

        usernameText = (EditText) findViewById(R.id.username);
        emailText = (EditText) findViewById(R.id.email);
        mobileText = (EditText) findViewById(R.id.mobile);

        signupButton = (Button) findViewById(R.id.signup_button);
        assert signupButton != null;
        signupButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        if (view == signupButton) {
            signup();
        }
    }


    public void signup() {
        String username = usernameText.getText().toString();
        String email = emailText.getText().toString();
        String mobile = mobileText.getText().toString();

        boolean validUsername = !(username.isEmpty() || username.trim().isEmpty());
        boolean validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean validMobile = Patterns.PHONE.matcher(mobile).matches();

        if (!(validUsername && validEmail && validMobile)) {
            Toast.makeText(this, "Username/Email/Mobile is not valid.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                User user = new User(username, mobile, email);
                // create user
                uc.addUser(user);
                //finish();
            } catch (UserException e) {
                // if the username has been taken
                Toast.makeText(this, "Username has been taken.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
