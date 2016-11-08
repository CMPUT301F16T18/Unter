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
 */

package ca.ualberta.cs.unter.ControllerTest;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import ca.ualberta.cs.unter.UnterConstant;
import ca.ualberta.cs.unter.model.User;

public class UserControllerTest extends ApplicationTestCase<Application> {
    private static JestDroidClient client;
    User user;

    public UserControllerTest() {
        super(Application.class);
    }

    public void testAddUser() {
        verifySettings();
//        user = new Driver("Test Driver", "780-716-4073", "test@cs.ualberta.ca");
//        Index index = new Index.Builder(user).index("unter").type("user").build();
//
//        try {
//            DocumentResult result = client.execute(index);
//            assertTrue(result.isSucceeded());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Search search = new Search.Builder("Test Driver")
//                .addIndex("unter")
//                .addType("user")
//                .build();
//        try {
//            SearchResult result = client.execute(search);
//            if (result.isSucceeded()) {
//                User testUser = result.getSourceAsObject(User.class);
//                assertTrue(user.equals(testUser));
//            }
//        }
//        catch (Exception e) {
//            Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
//        }

    }

    // TODO
    public void testUpdateUser() {

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
}
