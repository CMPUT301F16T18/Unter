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

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.view.LoginActivity;

/**
 * Unit test cases for user model
 */
public class UserTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo solo;

    /**
     * Mock callback method
     */
    OnAsyncTaskCompleted mockTask = new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {

        }
    };

    public UserTest() {
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

    /*
    This method is only here for making
    Junit test method work on robotinum
    http://stackoverflow.com/questions/11390276/android-junit-tests-not-detecting-in-robotium
    Author: BlackHatSamurai
     */
    public void testClickButton() {
        solo.enterText((EditText) solo.getView(R.id.editText_userName_LoginActivity), "balabl");
        solo.clickOnButton("Rider");
        solo.clickOnButton("Login");
        assertTrue(solo.waitForText("User does not exist, please signup"));
    }

    // http://stackoverflow.com/questions/7588584/android-asynctask-check-status

    /**
     * Test case for creating new user
     * and get user profile method
     */
    public void testCreateUser() {
        String userName = new RandomNameGenerator(new Random().nextInt(100)).next();
        String mobileNumber = "100-1000-2000";
        String emailAddress = userName + "@cs.ualberta.ca";
        User user = new User(userName, mobileNumber, emailAddress);
        user.setID(UUID.randomUUID().toString());

        User.CreateUserTask createUserTask = new User.CreateUserTask(mockTask);
        createUserTask.execute(user);
        // Hang around till is done
        AsyncTask.Status taskStatus;
        do {
            taskStatus = createUserTask.getStatus();
        } while (taskStatus != AsyncTask.Status.FINISHED);

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String query = String.format(
                        "{\n" +
                        "    \"query\": {\n" +
                        "       \"term\" : { \"userName\" : \"%s\" }\n" +
                        "    }\n" +
                        "}", userName);
        Log.d("Debug", query);

        User.GetUserProfileTask getUserTask = new User.GetUserProfileTask(mockTask);
        getUserTask.execute(query);
        User getUser = new User();
        // Hang around till is done
        AsyncTask.Status anotherStatus;
        do {
            anotherStatus = getUserTask.getStatus();
        } while (anotherStatus != AsyncTask.Status.FINISHED);

        try {
            getUser = getUserTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertEquals(user.getID(), getUser.getID());
        assertEquals(user, getUser);
    }

    /**
     * Test cases for search user exist task
     */
    public void testSearchUserExist() {
        String query = String.format(
                "{\n" +
                        "    \"query\": {\n" +
                        "       \"term\" : { \"userName\" : \"%s\" }\n" +
                        "    }\n" +
                        "}", "zhuyu");
        User.SearchUserExistTask task = new User.SearchUserExistTask();
        task.execute(query);
        // Hang around till it's done
        AsyncTask.Status taskStatus;
        do {
            taskStatus = task.getStatus();
        } while (taskStatus != AsyncTask.Status.FINISHED);

        try {
            assertTrue(task.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
