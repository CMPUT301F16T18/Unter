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
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import ca.ualberta.cs.unter.exception.RequestException;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.request.NormalRequest;
import ca.ualberta.cs.unter.model.request.Request;


/**
 * This class contains all functionalities of request list
 */
public class RequestController {

    public OnAsyncTaskCompleted listener;

    public RequestController(OnAsyncTaskCompleted listener) {
        this.listener = listener;
    }

    /**
     * Create a new request and send it to the server
     * @param request The request to be created
     */
    public void createRequest(Request request) {
        Request.CreateRequestTask task = new Request.CreateRequestTask(listener);
        try {
            request.setID(UUID.randomUUID().toString());
            task.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Update a a request
     * @param request The request to be updated
     */
    public void updateRequest(Request request) {
        Request.UpdateRequestTask task = new Request.UpdateRequestTask(listener);
        task.execute(request);
    }

    /**
     * Cancle a request
     * @param request The request to be deleted
     */
    public void deleteRequest(Request request) {
        Request.DeleteRequestTask task = new Request.DeleteRequestTask(listener);
        task.execute(request);
    }

    /**
     * Get a list of all request
     * @return An ArrayList of requests
     */
    public ArrayList<NormalRequest> getAllRequest() {
        Request.GetRequestsListTask task = new Request.GetRequestsListTask(listener);
        task.execute("");

        ArrayList<NormalRequest> requests = new ArrayList<>();

        try {
            requests =  task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return requests;
    }

    /**
     * Get a list of request that match the keyword
     * @param keyword The keyword
     * @return An arraylist of matching request.
     */
    public ArrayList<Request> searchRequestByKeyword(String keyword) {
        String query = String.format(
                        "{\n" +
                        "    \"query\": {\n" +
                        "       \"match\" : {\n" +
                        "           \"requestDescription\" : \"%s\" \n" +
                        "       }\n" +
                        "    }\n" +
                        "}", keyword);

        ArrayList<Request> requestList = new ArrayList<>();
        Request.GetRequestsListTask task = new Request.GetRequestsListTask(listener);
        task.execute(query);
        try {
            ArrayList<NormalRequest> getRequest = task.get();
            for (NormalRequest r : getRequest) {
                requestList.add(r);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return requestList;
    }

    /**
     * Driver confirm request.
     *
     * @param driverUserName the driver user name
     */
    public void driverConfirmRequest(Request request, String driverUserName) {
        request.driverAcceptRequest(driverUserName);
        updateRequest(request);
    }

    /**
     * Rider confirm request complete.
     *
     * @param request the request to be confirmed completed
     */
    public void riderConfirmRequestComplete(Request request) {
        request.riderConfirmRequestComplete();
        updateRequest(request);
    }

    /**
     * Rider confirm driver.
     *
     * @param request the request to be confirmed by the rider
     * @param driverUserName the driver user name
     */
    public void riderConfirmDriver(Request request, String driverUserName) throws RequestException {
        request.riderConfirmDriver(driverUserName);

        updateRequest(request);
    }
}
