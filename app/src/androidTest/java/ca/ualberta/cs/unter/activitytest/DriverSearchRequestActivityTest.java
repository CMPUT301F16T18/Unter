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
import android.widget.Button;
import android.widget.EditText;

import com.robotium.solo.Solo;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.view.DriverSearchRequestActivity;

/**
 * Test driversearchrequestactivity's search functionality
 */
public class DriverSearchRequestActivityTest extends ActivityInstrumentationTestCase2<DriverSearchRequestActivity> {

    private Solo solo;

    /**
     * Instantiates a new Driver search request activity test.
     */
    public DriverSearchRequestActivityTest() {
        super(ca.ualberta.cs.unter.view.DriverSearchRequestActivity.class);
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
     * Test search request by keyword.
     */
    public void testSearchRequestByKeyword() {
        solo.assertCurrentActivity("Wrong Activity", DriverSearchRequestActivity.class);
        solo.enterText((EditText) solo.getView(R.id.editText_searchRequest_DriverSearchRequestActivity), "u of a");
        solo.clickOnButton("Search");
        solo.clickInList(0);
        assertTrue("Cannot find dialog", solo.searchText("Request Information"));
    }

    /**
     * Test search request by location.
     */
    public void testSearchRequestByLocation() {
        solo.assertCurrentActivity("Wrong Activity", DriverSearchRequestActivity.class);
        solo.enterText((EditText) solo.getView(R.id.editText_searchRequest_DriverSearchRequestActivity), "university of alberta");
        solo.clickOnButton("Search");
        solo.clickInList(0);
        assertTrue("Cannot find dialog", solo.searchText("Request Information"));
    }

    /**
     * Test the filter method
     * since there is no way to actually test the customize
     * range bar, we just see if the dialog pop up or not
     */
    public void testFilterByPrice() {
        solo.clickLongOnView((Button) solo.getView(R.id.button_filter_driversearchrequestactivity));
        assertTrue(solo.searchText("Filter Request"));
    }
}
