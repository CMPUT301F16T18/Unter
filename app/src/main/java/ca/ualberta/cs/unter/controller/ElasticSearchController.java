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

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.util.ArrayList;
import java.util.List;

import ca.ualberta.cs.unter.model.Driver;
import ca.ualberta.cs.unter.model.Request;
import ca.ualberta.cs.unter.model.Rider;
import ca.ualberta.cs.unter.model.User;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * This class provide interaction with elastic search engine. TODO
 */
public class ElasticSearchController {
    private static JestDroidClient client;

    /**
     * Find user by user name user.
     *
     * @param userName the user name
     * @return the user
     */
    public User findUserByUserName(String userName) {
        // hardcode for now
        // for testing ony during project part 2
        Driver driver = new Driver("t1", "110-110-110", "test@ualberta.ca");
        return driver;
    }

    /**
     * Search request by location array list.
     *
     * @param lat the lat
     * @param lon the lon
     * @return the array list
     */
    public ArrayList<Request> searchRequestByLocation(int lat, int lon) {
        ArrayList<Request> matchedRequest = new ArrayList<>();
        // magic
        return matchedRequest;
    }

    /**
     * Search request by keyword array list.
     *
     * @param keyword the keyword
     * @return the array list
     */
    public ArrayList<Request> searchRequestByKeyword(String keyword) {
        ArrayList<Request> matchedRequest = new ArrayList<>();
        // magic
        return matchedRequest;
    }

    /**
     * Static class that update user profile
     */
    public static class UpdateUserProfileTask extends AsyncTask<User, Void, Void> {

        /**
         * Update the user profile to the server
         * @param user the user object to be updated
         * @return
         */
        @Override
        protected Void doInBackground(User... user) {
            verifySettings();

            Index index = new Index.Builder(user).index("Unter").type("user").build();

            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    // do something
                }
                else {
                    Log.i("Error", "Elastic search was not able to add the tweet.");
                }
            }
            catch (Exception e) {
                Log.i("Uhoh", "We failed to add a tweet to elastic search!");
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     *  Static class that get user profile
     */
    public static class GetUserProfileTask extends AsyncTask<String, Void, User> {

        /**
         * Get the user profile from the server
         * @param search_parameters the username to be searched
         * @return the mathed user obejct
         */
        @Override
        protected User doInBackground(String... search_parameters) {
            verifySettings();

            User user = new Driver();

            // assume that search_parameters[0] is the only search term we are interested in using
            Search search = new Search.Builder(search_parameters[0])
                    .addIndex("unter")
                    .addType("user")
                    .build();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    user = result.getSourceAsObject(User.class);
                }
                else {
                    Log.i("Error", "The search query failed to find any tweets that matched.");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return user;
        }
    }

    /**
     * Static class that update the request
     */
    public static class UpdateRequestTask extends AsyncTask<Request, Void, Void> {

        /**
         * Update the request when user accept, reject, require a ride
         * @param request the request object to be updated
         */
        @Override
        protected Void doInBackground(Request... request) {
            verifySettings();

            Index index = new Index.Builder(request).index("unter").type("user").build();

            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    // do something
                }
                else {
                    Log.i("Error", "Elastic search was not able to add the tweet.");
                }
            }
            catch (Exception e) {
                Log.i("Uhoh", "We failed to add a tweet to elastic search!");
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Static class that fetch request from server
     */
    public static class GetRequestsTask extends AsyncTask<String, Void, ArrayList<Request>> {

        /**
         * Fetch request list that matched the parameters, by keyword, geo-location, and all requests
         * @param search_parameters the parameter to search
         * @return a arraylist of requests
         */
        @Override
        protected ArrayList<Request> doInBackground(String... search_parameters) {
            verifySettings();

            ArrayList<Request> requests = new ArrayList<Request>();

            // assume that search_parameters[0] is the only search term we are interested in using
            Search search = new Search.Builder(search_parameters[0])
                    .addIndex("unter")
                    .addType("request")
                    .build();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<Request> findRequest = result.getSourceAsObjectList(Request.class);
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
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            //DroidClientConfig.Builder builder = new DroidClientConfig.Builder("https://api.vfree.org");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
}
