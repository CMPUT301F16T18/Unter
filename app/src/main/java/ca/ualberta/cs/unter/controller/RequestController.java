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

    public void createRequest(Request request) {
        Request.CreateRequestTask task = new Request.CreateRequestTask(listener);
        try {
            request.setID(UUID.randomUUID().toString());
            task.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateRequest(Request request) {
        Request.UpdateRequestTask task = new Request.UpdateRequestTask(listener);
        task.execute(request);
    }

    public void deleteRequest(Request request) {
        Request.DeleteRequestTask task = new Request.DeleteRequestTask(listener);
        task.execute(request);
    }

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
    public void riderConfirmDriver(Request request, String driverUserName) {
        request.riderConfirmDriver(driverUserName);
        updateRequest(request);
    }
}
