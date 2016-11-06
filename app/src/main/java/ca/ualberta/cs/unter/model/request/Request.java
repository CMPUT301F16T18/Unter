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

package ca.ualberta.cs.unter.model.request;

import com.google.gson.Gson;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.ualberta.cs.unter.UnterConstant;
import ca.ualberta.cs.unter.exception.RequestException;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.Route;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;

/**
 * This is a class that contains all attributes a Request should have.
 */
public abstract class Request implements FareCalculator{
    private String riderUserName;
    private String driverUserName;
    private ArrayList<String> driverList;

    private Route route;

    private double estimatedFare;
    private String requestDescription;

    private Boolean isCompleted;

    private String ID;

    private transient static JestDroidClient client;

    /**
     * Empty constructor for fun :)
     */
    public Request() {

    }

    /**
     * Constructor for a new request.
     *
     * @param riderUserName  the rider user name
     * @param route the path from pickup location to destination
     */
    public Request(String riderUserName, Route route) {
        this.riderUserName = riderUserName;
        this.route = route;
        this.estimatedFare = calculateEstimatedFare();
    }

    /**
     * Constructor for ConfirmedRequest or CompletedRequest.
     *
     * @param riderUserName         the rider user name
     * @param driverUserName        the driver user name
     * @param estimatedFare         the estimated fare
     * @param route the path from pickup location to destination
     */
    public Request(String riderUserName, String driverUserName, Route route, double estimatedFare) {
        this.riderUserName = riderUserName;
        this.driverUserName = driverUserName;
        this.route = route;
        this.estimatedFare = estimatedFare;
    }

    /**
     * Constructor for AcceptedRequest
     *
     * @param riderUserName the rider user name
     * @param driverList the list of drivers username who accept the request
     * @param estimatedFare the estimated fare
     */
    public Request(String riderUserName, ArrayList<String> driverList, Route route, double estimatedFare) {
        this.riderUserName = riderUserName;
        this.driverList = driverList;
        this.route = route;
        this.estimatedFare = estimatedFare;
    }

    /**
     * Calculate an estimated fare base on location
     * @return the estimated fare
     */
    @Override
    public double calculateEstimatedFare() {
        return 100;
    }

    /**
     * Driver accepts the request.
     *
     * @param driverUserName the driver user name who accepts the ride request
     */
    public void driverAcceptRequest(String driverUserName) { // changed from driverConfirmRequest
        if (this.driverUserName == null) {
            // If the request has not been accepted
            this.driverUserName = driverUserName;
        } else if (driverList == null && !this.driverUserName.isEmpty()) {
            // If the request has been confirmed by only one driver
            driverList = new ArrayList<>();
            // add existing accepted driver username first
            driverList.add(this.driverUserName);
            // add the new accepted driver
            driverList.add(driverUserName);
        } else if (driverList != null && !driverList.isEmpty()) {
            // If the request has been accepted by more than one driver
            driverList.add(driverUserName);
        }
    }

    /**
     * Rider confirm driver.
     *
     * @param driverUserName the driver user name
     * @exception RequestException raise exception when request has not been confirmed
     */
    public void riderConfirmDriver(String driverUserName) {
        try {
            if (this.driverUserName.isEmpty() || driverList.isEmpty()) {
                // If the request has not been accpeted yet
                throw new RequestException("This request has not been accepted by any driver yet");
            } else {
                // Confirmed driver
                this.driverUserName = driverUserName;
            }
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /**
     * Static class that adds the request
     */
    public static class CreateRequestTask extends AsyncTask<Request, Void, Request> {
        public OnAsyncTaskCompleted listener;
        // http://stackoverflow.com/questions/9963691/android-asynctask-sending-callbacks-to-ui
        // Author: Dmitry Zaitsev
        /**
         * Constructor for CreateRequestTask class
         * @param listener the customize job after the async task is done
         */
        public CreateRequestTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }

        /**
         * Update the request when user accept, reject, require a ride
         * @param requests the request object to be create
         */
        @Override
        protected Request doInBackground(Request... requests) {
            verifySettings();
            Request request = new PendingRequest();
            for (Request req : requests) {
                Index index = new Index.Builder(req).index("unter").type("request").id(req.getID()).build();
                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        // set ID
                        req.setID(result.getId());
                        request = req;
                        Log.i("Debug", "Successful create request");
                    } else {
                        Log.i("Debug", "Elastic search was not able to add the request.");
                    }
                } catch (Exception e) {
                    Log.i("Debug", "We failed to add a request to elastic search!");
                    e.printStackTrace();
                }
            }
            return request;
        }

        /**
         * Excute after async task is finished
         * Stuff like notify arrayadapter the data set is changed
         * @param request the request
         */
        @Override
        protected void onPostExecute(Request request) {
            listener.onTaskCompleted(request);
        }
    }

    /**
     * Static class that update the request
     */
    public static class UpdateRequestTask extends AsyncTask<Request, Void, Request> {
        public OnAsyncTaskCompleted listener;

        /**
         * Constructor for updaterequesttask class
         * @param listener the customize job after the async task is done
         */
        public UpdateRequestTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }

        /**
         * Update the request when user accept, reject, require a ride
         * @param requests the request object to be updated
         */
        @Override
        protected Request doInBackground(Request... requests) {
            verifySettings();
            // Constructs json string
            Gson gson = new Gson();
            String query = String.format(gson.toJson(requests[0]));
            Log.i("Debug", query);
            Index index = new Index.Builder(query)
                    .index("unter")
                    .type("request")
                    .id(requests[0].getID()).build();
            try {
                DocumentResult result = client.execute(index);

                if (result.isSucceeded()) {
                    Log.i("Debug", "Successful update the request");
                } else {
                    Log.i("Debug", "Elastic search was not able to add the request.");
                }
            } catch (Exception e) {
                Log.i("Debug", "We failed to add a request to elastic search!");
                e.printStackTrace();
                }
            return requests[0];
        }

        /**
         * Excute after async task is finished
         * Stuff like notify arrayadapter the data set is changed
         * @param request the request
         */
        @Override
        protected void onPostExecute(Request request) {
            listener.onTaskCompleted(request);
        }
    }

    /**TODO
     * Static class that cancel the request
     */
    public static class DeleteRequestTask extends AsyncTask<Request, Void, Request> {
        public OnAsyncTaskCompleted listener;

        /**
         * Constructor for DeleteRequestTask class
         * @param listener the customize job after the async task is done
         */
        public DeleteRequestTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }

        /**
         * Cancel the request
         * @param requests the request object to be canceled
         */
        @Override
        protected Request doInBackground(Request... requests) {
            verifySettings();

            for (Request req : requests) {
                Delete delete = new Delete.Builder(req.getID()).index("unter").type("request").build();
                try {
                    DocumentResult result = client.execute(delete);
                    if (result.isSucceeded()) {
                        Log.i("Debug", "Successful delete request");
                    } else {
                        Log.i("Error", "Elastic search was not able to add the request.");
                    }
                } catch (Exception e) {
                    Log.i("Error", "We failed to add a request to elastic search!");
                    e.printStackTrace();
                }
            }
            return requests[0];
        }

        /**
         * Excute after async task is finished
         * Stuff like notify arrayadapter the data set is changed
         * @param request nothing
         */
        @Override
        protected void onPostExecute(Request request) {
            listener.onTaskCompleted(request);
        }
    }

    /**
     * Static class that fetch request from server
     */
    public static class GetRequestsListTask extends AsyncTask<String, Void, ArrayList<NormalRequest>> {
        public OnAsyncTaskCompleted listener;

        public GetRequestsListTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }

        /**
         * Fetch request list that matched the parameters, by keyword, geo-location, and all requests
         * @param search_parameters the parameter to search
         * @return a arraylist of requests
         */
        @Override
        protected ArrayList<NormalRequest> doInBackground(String... search_parameters) {
            verifySettings();

            ArrayList<NormalRequest> requests = new ArrayList<>();

            // assume that search_parameters[0] is the only search term we are interested in using
            Search search = new Search.Builder(search_parameters[0])
                    .addIndex("unter")
                    .addType("request")
                    .build();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<NormalRequest> findRequest = result.getSourceAsObjectList(NormalRequest.class);
                    requests.addAll(findRequest);
                }
                else {
                    Log.i("Error", "The search query failed to find any tweets that matched.");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }
            return requests;
        }
        
    }

    /**
     * Set up the connection with server
     */
    private static void verifySettings() {
        // if the client hasn't been initialized then we should make it!
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder(UnterConstant.ELASTIC_SEARCH_URL);
            //DroidClientConfig.Builder builder = new DroidClientConfig.Builder("https://api.vfree.org");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }

    /**
     * Rider confirm request complete.
     */
    public void riderConfirmRequestComplete() {
        this.isCompleted = true;
    }

    /**
     * Gets rider user name.
     *
     * @return the rider user name
     */
    public String getRiderUserName() {
        return riderUserName;
    }

    /**
     * Gets driver user name.
     *
     * @return the driver user name
     */
    public String getDriverUserName() {
        return driverUserName;
    }

    /**
     * Gets origin coordinate.
     *
     * @return the origin coordinate
     */
    public GeoPoint getOriginCoordinate() {
        return route.getOrigin();
    }

    /**
     * Gets destination coordinate.
     *
     * @return the destination coordinate
     */
    public GeoPoint getDestinationCoordinate() {
        return route.getDestination();
    }

    /**
     * Gets completed.
     *
     * @return the completed
     */
    public Boolean getCompleted() {
        return isCompleted;
    }

    /**
     * Gets estimated fare.
     *
     * @return the estimated fare
     */
    public Double getEstimatedFare() {
        return estimatedFare;
    }

    public ArrayList<String> getDriverList() {
        return driverList;
    }

    /**
     * Sets driver user name.
     *
     * @param driverUserName the driver user name
     */
    public void setDriverUserName(String driverUserName) {
        this.driverUserName = driverUserName;
    }

    /**
     * Sets estimated fare.
     *
     * @param estimatedFare the estimated fare
     */
    public void setEstimatedFare(Double estimatedFare) {
        this.estimatedFare = estimatedFare;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRequestDescription() {
        return requestDescription;
    }

    public Route getRoute() {
        return route;
    }
}
