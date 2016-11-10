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

import java.io.IOException;

import ca.ualberta.cs.unter.UnterConstant;
import io.searchbox.client.JestResult;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;


/**
 * This a abstract base class for for all user model, including Driver and Rider.
 */
public class User {
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
     * Static class that create user profile
     */
    public static class CreateUserTask extends AsyncTask<User, Void, User> {
        public OnAsyncTaskCompleted listener;

        public CreateUserTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }

        /**
         * Update the user profile to the server
         * @param user the user object to be updated
         * @return user
         */
        @Override
        protected User doInBackground(User... user) {
            verifySettings();
            User newUser = new User();
            for (User u : user) {
                Index index = new Index.Builder(u)
                        .index("unter")
                        .type("user")
                        .id(u.getID())
                        .build();
                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        u.setID(result.getId());
                        newUser = u;
                        Log.i("Debug", "Successful create user");
                    } else {
                        Log.i("Debug", "Elastic search was not able to add the update user.");
                    }
                } catch (Exception e) {
                    Log.i("Error", "We failed to add user profile to elastic search!");
                    e.printStackTrace();
                }
            }
            return newUser;
        }

        @Override
        protected void onPostExecute(User user) {
            listener.onTaskCompleted(user);
        }
    }

    /**
     * Static class that update user profile
     */
    public static class UpdateUserTask extends AsyncTask<User, Void, User> {
        public OnAsyncTaskCompleted listener;

        public UpdateUserTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }
        @Override
        protected User doInBackground(User... users) {
            verifySettings();

            String query = String.format(
                            "{\n" +
                            "    \"userName\": \"%s\"     ,\n" +
                            "    \"mobileNumber\": \"%s\" ,\n" +
                            "    \"emailAddress\": \"%s\" ,\n" +
                            "    \"ID\": \"%s\" \n" +
                            "}",
                    users[0].getUserName(), users[0].getMobileNumber(),
                    users[0].getEmailAddress(), users[0].getID());
            Log.i("Debug", query);
            Index index = new Index.Builder(query)
                    .index("unter").type("user").id(users[0].getID()).build();

            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    Log.i("Debug", "Successful update user profile");
                } else {
                    Log.i("Error", "We failed to update user profile to elastic search!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return users[0];
        }

        @Override
        protected void onPostExecute(User user) {
            listener.onTaskCompleted(user);
        }
    }

    /**
     *  Static class that get user profile
     */
    public static class GetUserProfileTask extends AsyncTask<String, Void, User> {
        public OnAsyncTaskCompleted listener;

        public GetUserProfileTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }
        /**
         * Get the user profile from the server
         * @param query the username to be searched
         * @return the mathed user obejct
         */
        @Override
        protected User doInBackground(String... query) {
            verifySettings();

            User user = new User();
            Search search = new Search.Builder(query[0])
                    .addIndex("unter")
                    .addType("user")
                    .build();
            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    User getUser = result.getSourceAsObject(User.class);
                    user = getUser;
                    Log.i("Debug", "Successful get user profile");
                    if (user == null) {
                        Log.i("Debug", "fail to deserilize");
                    }
                } else {
                    Log.i("Error", "The search query failed to find any user that matched.");
                }
            } catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elastic search server!");
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            listener.onTaskCompleted(user);
        }
    }

    /**
     *  Static class that check user profile
     */
    public static class SearchUserExistTask extends AsyncTask<String, Void, Boolean> {

        /**
         * Check if username has been taken
         * @param query the username to be searched
         * @return True or Flase
         */
        @Override
        protected Boolean doInBackground(String... query) {
            verifySettings();

            User user = new User();
            Search search = new Search.Builder(query[0])
                    .addIndex("unter")
                    .addType("user")
                    .build();
            try {
                JestResult result = client.execute(search);
                if (result.isSucceeded()) {
                    user = result.getSourceAsObject(User.class);
                    if (user != null) {
                        Log.i("Debug", "Username has been taken");
                        return true;
                    }
                    Log.i("Debug", "Successful");
                } else {
                    Log.i("Debug", "The search query failed to find any user that matched.");
                }
            } catch (Exception e) {
                Log.i("Debug", "Something went wrong when we tried to communicate with the elastic search server!");
            }
            return false;
        }
    }

    /**
     * Set up the connection with server
     */
    private static void verifySettings() {
        // if the client hasn't been initialized then we should make it!
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig
                    .Builder(UnterConstant.ELASTIC_SEARCH_URL);

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
