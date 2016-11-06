package ca.ualberta.cs.unter.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class EditUserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText usernameText;
    private EditText emailText;
    private EditText mobileText;

    private Button saveButton;
    private User user;
    private String id;

    private UserController uc = new UserController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            User user = (User) o;
            FileIOUtil.saveUserInFile(user, getApplicationContext());
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        usernameText = (EditText) findViewById(R.id.username_name);
        emailText = (EditText) findViewById(R.id.email_address);
        mobileText = (EditText) findViewById(R.id.mobile_number);
        user = FileIOUtil.loadUserFromFile(getApplicationContext());
        // TODO get the user, use the user to setText
        usernameText.setText(user.getUserName());   // setText(user.getName)
        emailText.setText(user.getEmailAddress());      // setText(user.getEmail)
        mobileText.setText(user.getMobileNumber());     // setText(user.getMobile)
        this.id = user.getID();
        Log.i("Debug", user.getID());

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

        String oldUserName = user.getUserName();

        user.setUserName(username);
        user.setEmailAddress(email);
        user.setMobileNumber(mobile);
        Log.i("Debug", user.getID());

        boolean validUsername = !(username.isEmpty() || username.trim().isEmpty());
        boolean validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean validMobile = Patterns.PHONE.matcher(mobile).matches();

        if ( !(validUsername && validEmail && validMobile) ){
            Toast.makeText(this, "Username/Email/Mobile is not valid.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                Log.i("Debug", user.getID());
                uc.updateUser(user, oldUserName);
                finish();
            } catch (UserException e) {
                // if the username has been taken
                Toast.makeText(this, "Username has been taken.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
