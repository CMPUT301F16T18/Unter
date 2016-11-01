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
 *
 */

package ca.ualberta.cs.unter.ControllerTest;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;


import java.util.concurrent.CountDownLatch;

import ca.ualberta.cs.unter.controller.ElasticSearchController;
import ca.ualberta.cs.unter.model.Driver;
import ca.ualberta.cs.unter.model.Rider;
import ca.ualberta.cs.unter.model.User;
import io.searchbox.core.Update;

/**
 * The type Elastic search controller test.
 */
public class ElasticSearchControllerTest extends ApplicationTestCase<Application>{

    CountDownLatch signal = null;

    public ElasticSearchControllerTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        signal = new CountDownLatch(1);
    }

    @Override
    public void tearDown() throws Exception {
        signal.countDown();
    }

    /**
     * The Esc.
     */
    ElasticSearchController esc = new ElasticSearchController();

    /**
     * Test show user profile.
     */
    public void testShowUserProfile() {
        assertTrue(esc.findUserByUserName("xxx") != null);
    }

    /**
     * Test edit user profile.
     */
    public void testEditUserProfile() throws InterruptedException {
        Rider rider = new Rider("test", "7807163939", "test@ualberta.ca");


        signal.await();

        User test = new Driver();
        ElasticSearchController.GetUserProfileTask getUserProfileTask =
                new ElasticSearchController.GetUserProfileTask();
        getUserProfileTask.execute("test");
        try {
            test = getUserProfileTask.get();
        } catch (Exception e) {
            Log.i("Error", "Cannot get user");
        }
        signal.await();
        assertTrue(rider.equals(test));
    }

    /**
     * Test search request by location.
     */
    public void testSearchRequestByLocation() {
        assertTrue(esc.searchRequestByLocation(100, 100) != null);
    }

    /**
     * Test search request by keyword.
     */
    public void testSearchRequestByKeyword() {
        assertTrue(esc.searchRequestByKeyword("test") != null);
    }

}
