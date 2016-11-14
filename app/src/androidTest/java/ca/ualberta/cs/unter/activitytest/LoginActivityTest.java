/*
 * Copyright (C) 2016 CMPUT301F16T18 - Alan(Xutong) Zhao, Michael(Zichun) Lin, Stephen Larsen, Yu Zhu, Zhenzhe Xu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.ualberta.cs.unter.activitytest;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

import com.robotium.solo.Solo;

import org.kohsuke.randname.RandomNameGenerator;

import java.util.Random;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.view.LoginActivity;
import ca.ualberta.cs.unter.view.RiderMainActivity;
import ca.ualberta.cs.unter.view.SignupActivity;

/**
 * Application test for LoginActivity
 */
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

    /**
     * Test login functionality
     */
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

    /**
     * Tst signup functionaliry
     */
    public void testSignup() {
        LoginActivity activity = (LoginActivity) solo.getCurrentActivity();

        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        // Go to Signup activity
        solo.clickOnButton("Sign Up");
        solo.assertCurrentActivity("Wrong Activity", SignupActivity.class);
        // Enter user profile
        solo.enterText((EditText) solo.getView(R.id.editText_userName_SignupActivity),
                new RandomNameGenerator(new Random().nextInt(100)).next());
        solo.enterText((EditText) solo.getView(R.id.editText_email_SignupActivity), "test@test.ca");
        solo.enterText((EditText) solo.getView(R.id.editText_mobile_SignupActivity), "911");
        // Signup
        solo.clickOnButton("Sign Up");
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }
}