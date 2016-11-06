package ca.ualberta.cs.unter;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

import com.robotium.solo.Solo;

import org.kohsuke.randname.RandomNameGenerator;

import ca.ualberta.cs.unter.view.LoginActivity;
import ca.ualberta.cs.unter.view.RiderMainActivity;
import ca.ualberta.cs.unter.view.SignupActivity;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo solo;

    public LoginActivityTest() {
        super(ca.ualberta.cs.unter.view.LoginActivity.class);
    }

    public void testStart() throws Exception {
        Activity activity = getActivity();
    }

    @Override
    public void setUp() throws Exception {
        Log.d("TAG1", "setUp()");
        solo = new Solo(getInstrumentation(),getActivity());

    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testLogin() {
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        // Enter username
        solo.enterText((EditText) solo.getView(R.id.editText_userName_LoginActivity), "balabl");
        solo.clickOnButton("Rider");
        solo.clickOnButton("Login");
        assertTrue(solo.waitForText("User does not exist, please signup"));
        // Enter username
        solo.clearEditText((EditText) solo.getView(R.id.editText_userName_LoginActivity));
        solo.enterText((EditText) solo.getView(R.id.editText_userName_LoginActivity), "zhuyu");
        solo.clickOnButton("Rider");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", RiderMainActivity.class);
    }

    public void testSignup() {
        LoginActivity activity = (LoginActivity) solo.getCurrentActivity();

        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        // Go to Signup activity
        solo.clickOnButton("Sign Up");
        solo.assertCurrentActivity("Wrong Activity", SignupActivity.class);
        // Enter user profile
        solo.enterText((EditText) solo.getView(R.id.editText_userName_SignupActivity),
                new RandomNameGenerator(0).next());
        solo.enterText((EditText) solo.getView(R.id.editText_email_SignupActivity), "test@test.ca");
        solo.enterText((EditText) solo.getView(R.id.editText_mobile_SignupActivity), "911");
        // Signup
        solo.clickOnButton("Sign Up");
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }
}