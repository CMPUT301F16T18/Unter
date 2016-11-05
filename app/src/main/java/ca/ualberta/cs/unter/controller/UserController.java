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

import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.User;

public class UserController {
    OnAsyncTaskCompleted listener;

    public UserController(OnAsyncTaskCompleted listener) {
        this.listener = listener;
    }

    public void addUser(User user) {
        User.CreateUserTask task = new User.CreateUserTask(listener);
        task.execute(user);
    }

    public void updateUser(User user) {
        String query = String.format(
                        "{\n" +
                        "    \"userName\": \"$s\"     " +
                        "    \"mobileNumber\": \"$s\" " +
                        "    \"emailAddress\": \"$s\" " +
                        "}",
                user.getUserName(), user.getMobileNumber(), user.getEmailAddress());
        User.UpdateUserTask task = new User.UpdateUserTask(listener);
        task.execute(user);
    }

    /** TODO, Query is not working
     * Retrive user profile from the server
     * @param username the username to search
     * @return
     */
    public User getUser(String username) {
        User user = new User();
//        String query = String.format(
//                "{\n" +
//                "    \"query\": {\n" +
//                "        \"filtered\" : {\n" +
//                "            \"filter\" : {\n" +
//                "                \"term\" : { \"userName\" : \"%s\" }\n" +
//                "            }\n" +
//                "        }\n" +
//                "    }\n" +
//                "}", username);
        String query = String.format(
                        "{\n" +
                        "    \"query\": {\n" +
                        "       \"term\" : { \"userName\" : \"%s\" }\n" +
                        "    }\n" +
                        "}", username);
        User.GetUserProfileTask task = new User.GetUserProfileTask(listener);
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
