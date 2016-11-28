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

import com.robotium.solo.Solo;

import ca.ualberta.cs.unter.view.RiderBrowseRequestActivity;
import ca.ualberta.cs.unter.view.RiderRequestDetailActivity;


/**
 * The type Rider browse request activity test.
 *
 * This test may need to run seperately in order to pass
 */
public class RiderBrowseRequestActivityTest extends ActivityInstrumentationTestCase2<RiderBrowseRequestActivity> {

    private Solo solo;

    /**
     * Instantiates a new Rider browse request activity test.
     *
     */
    public RiderBrowseRequestActivityTest() {
        super(RiderBrowseRequestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        Log.d("TAG1", "setUp()");
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * Test request list can be viewed.
     */
    public void testRequestList() {
        solo.assertCurrentActivity("Wrong Activity", RiderBrowseRequestActivity.class);
        solo.clickInList(0);
        solo.assertCurrentActivity("Wrong", RiderRequestDetailActivity.class);
        solo.clickInList(0);
        assertTrue(solo.searchText("Vehicle")); // Find car info
        assertTrue("Cannot find dialog", solo.searchText("Confirm Acceptance")); // check acceptes
    }
}
