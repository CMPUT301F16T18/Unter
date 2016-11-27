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

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

import com.robotium.solo.Solo;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.view.DriverBrowseRequestActivity;
import ca.ualberta.cs.unter.view.DriverCarInfoActivity;
import ca.ualberta.cs.unter.view.DriverMainActivity;

/**
 * Applicatoin test for DriverCarInfoActivityTest
 */
public class DriverCarInfoActivityTest extends ActivityInstrumentationTestCase2<DriverCarInfoActivity> {

    private Solo solo;

    public DriverCarInfoActivityTest() {
        super(DriverCarInfoActivity.class);
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
     * Tst see if the request can be seen or not
     */
    public void testCarInfo() {

        solo.assertCurrentActivity("Wrong Activity", DriverCarInfoActivity.class);

        solo.enterText((EditText) solo.getView(R.id.edittext_vehcilename_drivercarinfoactivity), "benz");
        solo.enterText((EditText) solo.getView(R.id.edittext_platenumber_drivercarinfoactivity), "4848448");

        solo.clickOnButton("Save");
    }
}
