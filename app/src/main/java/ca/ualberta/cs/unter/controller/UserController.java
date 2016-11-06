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

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import ca.ualberta.cs.unter.exception.UserException;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.User;

public class UserController {
    OnAsyncTaskCompleted listener;

    public UserController(OnAsyncTaskCompleted listener) {
        this.listener = listener;
    }

    public void addUser(User user) throws UserException{
        String query = String.format(
                "{\n" +
                        "    \"query\": {\n" +
                        "       \"term\" : { \"userName\" : \"%s\" }\n" +
                        "    }\n" +
                        "}", user.getUserName());

        User.CreateUserTask task = new User.CreateUserTask(listener);
        User.SearchUserExistTask checkTask = new User.SearchUserExistTask();
        checkTask.execute(query);

        try {
            if (checkTask.get()) {
                throw new UserException("Username has been taken");
            } else {
                user.setID(UUID.randomUUID().toString());
                task.execute(user);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user, String oldUserName) throws UserException{
        String query = String.format(
                "{\n" +
                        "    \"query\": {\n" +
                        "       \"term\" : { \"userName\" : \"%s\" }\n" +
                        "    }\n" +
                        "}", user.getUserName());
        Log.i("Debug", user.getID());
        User.UpdateUserTask task = new User.UpdateUserTask(listener);
        User.SearchUserExistTask checkTask = new User.SearchUserExistTask();
        checkTask.execute(query);

        try {
            if (checkTask.get() && !oldUserName.equals(user.getUserName())) {
                throw new UserException("Username has been taken");
            } else {
                task.execute(user);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrive user profile from the server
     * @param username the username to search
     * @return
     */
    public User getUser(String username) {
        User user = new User();
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
