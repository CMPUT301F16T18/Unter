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

package ca.ualberta.cs.unter.controller;

import java.util.ArrayList;

import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.request.Request;


/**
 * This class contains all functionalities of request list
 */
public class RequestController {
    private ArrayList<Request> requestList;

    /**
     * Gets request list.
     *
     * @return the request list
     */
    public ArrayList<Request> getRequestList() {
        if (requestList == null) {
            requestList = new ArrayList<>();
        }
        return requestList;
    }

    public void createRequest(Request request, OnAsyncTaskCompleted listener) {
        Request.CreateRequestTask task = new Request.CreateRequestTask(listener);
        task.execute(request);
    }

    public void updateRequest(Request request) {

    }

    public void deleteRequest(Request request) {

    }

    /**
     * Gets index of request.
     *
     * @param request the request
     * @return the index of request
     */
    public int getIndexOfRequest(Request request) {
        return requestList.indexOf(request);
    }

    /**
     * Gets request by index.
     *
     * @param index the index
     * @return the request by index
     */
    public Request getRequestByIndex(int index) {
        return requestList.get(index);
    }

    /**
     * Driver confirm request.
     *
     * @param index          the index
     * @param driverUserName the driver user name
     */
    public void driverConfirmRequest(int index, String driverUserName) {
        requestList.get(index).driverAcceptRequest(driverUserName);
    }

    /**
     * Rider confirm request complete.
     *
     * @param index the index
     */
    public void riderConfirmRequestComplete(int index) {
        requestList.get(index).riderConfirmRequestComplete();
    }

    /**
     * Rider confirm driver.
     *
     * @param index          the index
     * @param driverUserName the driver user name
     */
    public void riderConfirmDriver(int index, String driverUserName) {
        requestList.get(index).riderConfirmDriver(driverUserName);
    }

    /**
     * Add ride request.
     *
     * @param newRequest the new request
     */
    public void addRideRequest(Request newRequest) {
        getRequestList().add(newRequest);
    }

    /**
     * Add all ride request.
     *
     * @param requestList the request list
     */
    public void addAllRideRequest(ArrayList<Request> requestList) {
        this.requestList = requestList;
    }

    /**
     * Cancel ride request.
     *
     * @param oldRequest the request to cancel
     */
    public void cancelRideRequest(Request oldRequest) {
        getRequestList().remove(oldRequest);
    }

    /**
     * Check if the request is existed in the request list.
     *
     * @param request the request to check
     * @return if the request exist
     */
    public Boolean contains(Request request) {
        if (getRequestList().contains(request)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
