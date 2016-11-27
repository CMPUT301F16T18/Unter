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

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.view.LoginActivity;
import ca.ualberta.cs.unter.view.RiderMainActivity;

/**
 * Suite for testing intents on RiderMainActivity. -- incomplete
 */
public class RiderMainActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

	private Solo solo;

	/**
	 * Instantiates a new Rider main activity test.
	 */
	public RiderMainActivityTest() {
		super(ca.ualberta.cs.unter.view.LoginActivity.class);
	}

	/**
	 * Test start.
	 *
	 * @throws Exception the exception
	 */
	public void testStart() throws Exception {
		Activity activity = getActivity();
	}

	@Override
	public void setUp() throws Exception {
		Log.d("TAG1", "setUp()");
		solo = new Solo(getInstrumentation(),getActivity());
        // Enter username
        solo.clearEditText((EditText) solo.getView(R.id.editText_userName_LoginActivity));
        solo.enterText((EditText) solo.getView(R.id.editText_userName_LoginActivity), "heyue");
        solo.clickOnButton("Rider");
        solo.clickOnButton("Login");

	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	/**
	 * Test invalid start location.
	 */
	public void testInvalidStartLocation() {
		solo.assertCurrentActivity("Wrong Activity", RiderMainActivity.class);
		solo.enterText((EditText) solo.getView(R.id.editDeparture), "");
		solo.clickOnButton("Search");
		assertTrue(solo.waitForText("Please enter an address")); // test for invalid address
	}

	/**
	 * Test invalid end location.
	 */
	public void testInvalidEndLocation() {
		solo.assertCurrentActivity("Wrong Activity", RiderMainActivity.class);
		solo.enterText((EditText) solo.getView(R.id.editDestination), "");
		solo.clickOnButton("Search");
		assertTrue(solo.waitForText("Please enter an address")); // test for invalid address
	}


	/**
	 * Test notify rider when request has been accepted by the driver
	 */
	public void testNotifyRider() {
		solo.assertCurrentActivity("Wrong Activity", RiderMainActivity.class);
		assertFalse("Cannot find dialog", solo.searchText("Request has been accepted"));
	}
}
