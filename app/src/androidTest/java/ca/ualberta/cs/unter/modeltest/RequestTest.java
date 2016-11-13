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

package ca.ualberta.cs.unter.modeltest;

import android.os.AsyncTask;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

import com.robotium.solo.Solo;

import org.kohsuke.randname.RandomNameGenerator;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.exception.RequestException;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.Route;
import ca.ualberta.cs.unter.model.request.NormalRequest;
import ca.ualberta.cs.unter.model.request.PendingRequest;
import ca.ualberta.cs.unter.model.request.Request;
import ca.ualberta.cs.unter.view.LoginActivity;

/**
 * Unit test cases for request model Since the controller class are designed to be as thick as
 * possible, all business logic are inside the model class, which fits the MVC pattern. Therefore,
 * it's pretty much no need to test controller class.
 */
public class RequestTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo solo;
    private Route route = new Route(new GeoPoint(3.33, 2.22), new GeoPoint(2.22, 3.33));

    /**
     * Mock callback method
     */
    OnAsyncTaskCompleted mockTask = new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {

        }
    };

    /**
     * Instantiates a new Request test.
     */
    public RequestTest() {
        super("ca.ualberta.cs.unter.view", LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        Log.d("TAG1", "setUp()");
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * This method is only here for making Junit test method work on robotinum
     * http://stackoverflow.com/questions/11390276/android-junit-tests-not-detecting-in-robotium
     * Author: BlackHatSamurai
     */
    public void testClickButton() {
        solo.enterText((EditText) solo.getView(R.id.editText_userName_LoginActivity), "balabl");
        solo.clickOnButton("Rider");
        solo.clickOnButton("Login");
        assertTrue(solo.waitForText("User does not exist, please signup"));
    }

    /**
     * Test update request. Once the request is modified,
     * created, this method would be called to update request
     * to the server. This test covers all update method
     * for the rest of tests. In other word, no need to implement
     * UpdateRequestTask in the rest of the test cases.
     * Generally, the interaction with databae or server should be mocked
     * up in the unittest (waste of resource, and possbility to mess up
     * the production environment). Also, this test is not guarantee to pass.
     */
    public void testUpdateRequest() {
        Request.CreateRequestTask createRequestTask = new Request.CreateRequestTask(null);
        String riderUserName = new RandomNameGenerator(new Random().nextInt(100)).next();
        Request request = new PendingRequest(riderUserName, route);
        request.setID(UUID.randomUUID().toString());
        createRequestTask.execute(request);
        AsyncTask.Status taskStatus;
        do {
            taskStatus = createRequestTask.getStatus();
        } while (taskStatus != AsyncTask.Status.FINISHED);

        // Wait for task to finished
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String query = String.format(
                        "{\n" +
                        "    \"filter\": {\n" +
                        "       \"bool\" : {\n" +
                        "           \"must\" : [\n " +
                        "               { \"term\": {\"riderUserName\": \"%s\"} },\n" +
                        "           ]\n" +
                        "       }\n" +
                        "    }\n" +
                        "}", riderUserName);

        Request.GetRequestsListTask getRequestsListTask = new Request.GetRequestsListTask(null);
        getRequestsListTask.execute(query);
        AsyncTask.Status anotherStatus;
        do {
            anotherStatus = getRequestsListTask.getStatus();
        } while (anotherStatus != AsyncTask.Status.FINISHED);

        ArrayList<NormalRequest> getRequests = new ArrayList<>();
        try {
            getRequests = getRequestsListTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertEquals(request.getID(), getRequests.get(0).getID());
        assertEquals(request, getRequests.get(0));
    }

    /**
     * Test estimated fare.
     */
    public void testEstimatedFare() {
        Request request = new PendingRequest("Rider", route);
        request.setEstimatedFare(4.44);
        assertEquals(request.getEstimatedFare(), 4.44);
    }

    /**
     * Test rider confirm request's completion.
     */
    public void testRiderConfirmRequestComplete() {
        Request request = new PendingRequest("Rider", route);
        request.riderConfirmRequestComplete();
        assertTrue(request.getCompleted());
    }

    /**
     * Test rider confirm driver.
     */
    public void testRiderConfirmDriver() {
        Request request = new PendingRequest("Rider", route);
        request.driverAcceptRequest("Driver1");
        request.driverAcceptRequest("Driver2");
        try {
            request.riderConfirmDriver("Driver1");
        } catch (RequestException e) {
            e.printStackTrace();
        }
        assertEquals(request.getDriverUserName(), "Driver1");
        assertNull(request.getDriverList());
    }

    /**
     * Test driver accept request.
     */
    public void testDriverAcceptRequest() {
        Request request = new PendingRequest("Rider", route);
        request.driverAcceptRequest("Driver1");
        assertTrue(request.getDriverList().contains("Driver1"));
    }
}
