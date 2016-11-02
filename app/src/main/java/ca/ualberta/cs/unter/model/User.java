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

package ca.ualberta.cs.unter.model;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import ca.ualberta.cs.unter.UnterConstant;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * This a abstract base class for for all user model, including Driver and Rider.
 */
public abstract class User {
    private String userName;
    private String mobileNumber;
    private String emailAddress;

    private String ID;

    private transient static JestDroidClient client;

    public User() {

    }

    /**
     * Instantiates a new User.
     *
     * @param userName     the user name
     * @param mobileNumber the mobile number
     * @param emailAddress the email address
     */
    public User(String userName, String mobileNumber, String emailAddress) {
        this.userName = userName;
        this.mobileNumber = mobileNumber;
        this.emailAddress = emailAddress;
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

            Index index = new Index.Builder(user).index("unter").type("user").build();

            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    // do something
                }
                else {
                    Log.i("Error", "Elastic search was not able to add the update user.");
                }
            }
            catch (Exception e) {
                Log.i("Uhoh", "We failed to update user profile to elastic search!");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // do something to notify succeed
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
     * Gets user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets user name.
     *
     * @param userName the user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets mobile number.
     *
     * @return the mobile number
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * Sets mobile number.
     *
     * @param mobileNumber the mobile number
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * Gets email addr.
     *
     * @return the email addr
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets email addr.
     *
     * @param emailAddress the email addr
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public String toString() {
        return userName;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {

        return ID;
    }
}
