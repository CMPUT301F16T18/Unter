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
import com.google.gson.GsonBuilder;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import ca.ualberta.cs.unter.UnterConstant;
import ca.ualberta.cs.unter.exception.RequestException;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.OnAsyncTaskFailure;
import ca.ualberta.cs.unter.model.Route;
import ca.ualberta.cs.unter.util.GeoPointConverter;
import ca.ualberta.cs.unter.util.RequestUtil;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * This is a class that contains all attributes a Request should have.
 */
public abstract class Request {
    private String riderUserName;
    private String driverUserName;
    private ArrayList<String> driverList = new ArrayList<>();

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
     * @param riderUserName the rider user name
     * @param route         the path from pickup location to destination
     */
    public Request(String riderUserName, Route route) {
        this.riderUserName = riderUserName;
        this.route = route;
    }

    /**
     * Constructor for ConfirmedRequest or CompletedRequest.
     *
     * @param riderUserName  the rider user name
     * @param driverUserName the driver user name
     * @param route          the path from pickup location to destination
     * @param estimatedFare  the estimated fare
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
     * @param driverList    the list of drivers username who accept the request
     * @param route         the route
     * @param estimatedFare the estimated fare
     */
    public Request(String riderUserName, ArrayList<String> driverList, Route route, double estimatedFare) {
        this.riderUserName = riderUserName;
        this.driverList = driverList;
        this.route = route;
		this.estimatedFare = estimatedFare;
    }

    /**
     * Driver accepts the request.
     *
     * @param driverUserName the driver user name who accepts the ride request
     */
    public void driverAcceptRequest(String driverUserName) { // changed from driverConfirmRequest
        driverList.add(driverUserName);
    }

    /**
     * Rider confirm driver.
     *
     * @param driverUserName the driver user name
     * @throws RequestException the request exception
     * @throws RequestException raise exception when request has not been confirmed
     */
    public void riderConfirmDriver(String driverUserName) throws RequestException {
        if (driverList == null || driverList.isEmpty()) {
            // If the request has not been accepted yet
            throw new RequestException("This request has not been accepted by any driver yet");
        } else {
            // Confirmed driver
            this.driverUserName = driverUserName;
            driverList.clear();
        }
    }

    /**
     * Static class that adds the request
     */
    public static class CreateRequestTask extends AsyncTask<Request, Void, Request> {
        /**
         * The Listener.
         */
        public OnAsyncTaskCompleted listener;
        public OnAsyncTaskFailure offlineHandler;
        // http://stackoverflow.com/questions/9963691/android-asynctask-sending-callbacks-to-ui
        // Author: Dmitry Zaitsev
        private RequestException requestException;

        /**
         * Constructor for CreateRequestTask class
         *
         * @param listener the customize job after the async task is done
         */
        public CreateRequestTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }

        /**
         * Constructor for create request async task
         * @param listener the customize job after the async task is done
         * @param offlineHandler the customize job after the async task is fail
         */
        public CreateRequestTask(OnAsyncTaskCompleted listener, OnAsyncTaskFailure offlineHandler) {
            this.listener = listener;
            this.offlineHandler = offlineHandler;
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
                    requestException = new RequestException("Application lost connection to the server");
                    Log.i("Debug", "We failed to add a request to elastic search!");
                    e.printStackTrace();
                }
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
            if (listener != null && requestException == null) {
                listener.onTaskCompleted(request);
            } else if (offlineHandler != null && requestException != null) {
                Log.i("Debug", "Fail to upload");
                offlineHandler.onTaskFailed(request);
            }
        }
    }

    /**
     * Static class that update the request
     */
    public static class UpdateRequestTask extends AsyncTask<Request, Void, Request> {
        /**
         * The Listener.
         */
        public OnAsyncTaskCompleted listener;
        public OnAsyncTaskFailure offlineHandler;
        private RequestException requestException;

        /**
         * Constructor for updaterequesttask class
         *
         * @param listener the customize job after the async task is done
         */
        public UpdateRequestTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }

        /**
         * Constructor for updaterequesttask async task
         * @param listener the customize job after the async task is done
         * @param offlineHandler the customize job after the async task is fail
         */
        public UpdateRequestTask(OnAsyncTaskCompleted listener, OnAsyncTaskFailure offlineHandler) {
            this.listener = listener;
            this.offlineHandler = offlineHandler;
        }

        /**
         * Update the request when user accept, reject, require a ride
         * @param requests the request object to be updated
         */
        @Override
        protected Request doInBackground(Request... requests) {
            verifySettings();
            // Constructs json string
            Gson gson = new GsonBuilder().registerTypeAdapter(GeoPoint.class, new GeoPointConverter()).create();
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
                requestException = new RequestException("Application lost connection to the server");
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
            if (listener != null && requestException == null) {
                listener.onTaskCompleted(request);
            } else if (offlineHandler != null && requestException != null) {
                Log.i("Debug", "Fail to upload");
                offlineHandler.onTaskFailed(request);
            }
        }
    }

    /**
     * TODO Static class that cancel the request
     */
    public static class DeleteRequestTask extends AsyncTask<Request, Void, Request> {
        /**
         * The Listener.
         */
        public OnAsyncTaskCompleted listener;

        /**
         * Constructor for DeleteRequestTask class
         *
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
            if (listener != null) {
                listener.onTaskCompleted(request);
            }
        }
    }

    /**
     * Static class that fetch request from server
     */
    public static class GetRequestsListTask extends AsyncTask<String, Void, ArrayList<NormalRequest>> {
        /**
         * The Listener.
         */
        public OnAsyncTaskCompleted listener;

        /**
         * Instantiates a new Get requests list task.
         *
         * @param listener the listener
         */
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
                    Log.i("Debug", "Successful get the request list");
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

        @Override
        protected void onPostExecute(ArrayList<NormalRequest> normalRequests) {
            if (listener != null) {
                ArrayList<Request> requestsList = new ArrayList<>();
                for (NormalRequest r : normalRequests) {
                    requestsList.add(r);
                }
                listener.onTaskCompleted(requestsList);
            }
        }
    }

    /**
     * Set up the connection with server
     */
    private static void verifySettings() {
        // if the client hasn't been initialized then we should make it!
        if (client == null) {
            // Custom gson Serializer and JsonDeserializer
            Gson gson = RequestUtil.customGsonBuilder();
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder(UnterConstant.ELASTIC_SEARCH_URL).gson(gson);
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }

    /**
     * Overide toString method
     * @return the descitipion
     */
    @Override
    public String toString() {
        if (requestDescription == null) return null;
        return requestDescription;
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
        if (riderUserName == null) return null;
        return riderUserName;
    }

    /**
     * Gets driver user name.
     *
     * @return the driver user name
     */
    public String getDriverUserName() {
        if (driverUserName == null) return null;
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
        if (isCompleted != null) {
            return isCompleted;
        }
        return false;
    }

    /**
     * Gets estimated fare.
     *
     * @return the estimated fare
     */
    public Double getEstimatedFare() {
        return estimatedFare;
    }

    /**
     * Gets the estimated fare rounded to 2 decimal places.
     *
     * @return the rounded fare
     */
    public String getRoundedFare() {
		String fare = String.format("%.2f", estimatedFare);
		return fare;
	}

    /**
     * Sets the estimated fare.
     *
     * @param fare the fare
     */
    public void setEstimatedFare(double fare) {
		estimatedFare = fare;
	}

    /**
     * Sets the distance of the route.
     *
     * @param distance the distance
     */
    public void setDistance(double distance) {
		route.setDistance(distance);
	}

    /**
     * Gets the distance of the route.
     *
     * @return distance
     */
    public double getDistance() {
		return route.getDistance();
	}

    /**
     * Gets driver list.
     *
     * @return the driver list
     */
    public ArrayList<String> getDriverList() {
        if (driverList.isEmpty()) {
            return null;
        }
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
     * Gets id.
     *
     * @return the id
     */
    public String getID() {
        return ID;
    }

    /**
     * Sets id.
     *
     * @param ID the id
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Gets request description.
     *
     * @return the request description
     */
    public String getRequestDescription() {
        return requestDescription;
    }

    /**
     * Sets request description.
     *
     * @param requestDescription the request description
     */
    public void setRequestDescription(String requestDescription) {
        this.requestDescription = requestDescription;
    }

    /**
     * Gets route.
     *
     * @return the route
     */
    public Route getRoute() {
        return route;
    }
}
