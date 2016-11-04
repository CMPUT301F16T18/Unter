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

package ca.ualberta.cs.unter.controller;

import android.util.Log;

import ca.ualberta.cs.unter.model.Driver;
import ca.ualberta.cs.unter.model.Rider;
import ca.ualberta.cs.unter.model.User;

public class UserController {
    User user;
    public void addUser(String username, String phoneNumber, String emailAddr, String role) {
        if (role.equals("Driver")) {
            user = new Driver(username, phoneNumber, emailAddr);
        } else if (role.equals("Rider")) {
            user = new Rider(username, phoneNumber, emailAddr);
        }
        User.UpdateUserProfileTask task = new User.UpdateUserProfileTask();
        task.execute(user);
    }

    /**
     * Retrive user profile from the server
     * @param username the username to search
     * @return
     */
    public User getUser(String username) {
        String query = String.format(
                "{\n" +
                "    \"query\": {\n" +
                "        \"filtered\" : {\n" +
                "            \"filter\" : {\n" +
                "                \"term\" : { \"userName\" : \"%s\" }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}", username);
        User.GetUserProfileTask task = new User.GetUserProfileTask();
        task.execute(query);

        try {
            Log.i("Error", "Searching");
            user = task.get();
        } catch (Exception e) {
            Log.i("Error", "Fail to get");
        }
        return user;
    }
}
