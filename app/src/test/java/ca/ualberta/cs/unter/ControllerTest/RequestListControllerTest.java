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

import junit.framework.TestCase;

import java.util.ArrayList;

import ca.ualberta.cs.unter.controller.RequestListController;
import ca.ualberta.cs.unter.model.Request;


/**
 * Test cases of RequestListController class
 *
 * @see RequestListController
 */
public class RequestListControllerTest extends TestCase {
    /**
     * The R 1.
     */
    Request r1 = new Request("John Doe", "112, 201", "112, 201");
    /**
     * The R 2.
     */
    Request r2 = new Request("John", "White", "112, 201", "292, 293", 200.00);
    /**
     * The Rlc.
     */
    RequestListController rlc = new RequestListController();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rlc.addRideRequest(r1);
        rlc.addRideRequest(r2);
    }

    /**
     * Test current request, see if it exists.
     */
    public void testCurrentRequest() {
        ArrayList<Request> requestList = rlc.getRequestList();
        for (Request r : requestList) {
            assertTrue(r.toString() != null);
        }
    }

    /**
     * Test add ride request.
     */
    public void testAddRideRequest() {
        assertEquals(rlc.contains(r1), Boolean.TRUE);
        assertEquals(rlc.contains(r2), Boolean.TRUE);
    }

    /**
     * Test notify rider request accept.
     */
    //TODO
    public void testNotifyRiderRequestAccept() {
        rlc.driverConfirmRequest(1, "XXX");
        // haven't work on the activity yet
        // so cannot really implement this method
    }

    /**
     * Test cancel request.
     */
    public void testCancelRequest() {
        rlc.cancelRideRequest(r1);
        assertFalse(rlc.contains(r1));
    }

    /**
     * Test contact driver.
     */
    public void testContactDriver() {
        assertTrue(rlc.getRequestByIndex(1).getDriverUserName() != null);
        // the get user profile method is implement and tested by ESC
    }

    /**
     * Test estimated fare.
     */
    public void testEstimatedFare() {
        ArrayList<Request> requestList = rlc.getRequestList();
        for (Request r : requestList) {
            assertTrue(r.getEstimatedFare() != null);
        }
    }

    /**
     * Test rider confirm request complete.
     */
    public void testRiderConfirmRequestComplete() {
        rlc.riderConfirmRequestComplete(0);
        assertTrue(rlc.getRequestByIndex(0).getCompleted());
    }

    /**
     * Test rider confirm driver.
     */
    public void testRiderConfirmDriver() {
        rlc.riderConfirmDriver(0, "white");
        assertEquals(rlc.getRequestByIndex(0).getDriverUserName(), "white");
    }

    /**
     * Test see request status.
     */
    public void testSeeRequestStatus() {
        assertTrue(rlc.getRequestByIndex(0) != null);
    }
}
