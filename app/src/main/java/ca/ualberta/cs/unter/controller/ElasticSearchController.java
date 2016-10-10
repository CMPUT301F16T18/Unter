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

import ca.ualberta.cs.unter.model.Driver;
import ca.ualberta.cs.unter.model.User;

/**
 * This class provide interaction with elastic search engine. TODO
 */
public class ElasticSearchController {

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
     * Update user int.
     *
     * @param user the user object to be update
     * @return the int
     */
    public int updateUser(User user) {
        // check https status code
        // hardcode for now
        // add exception later
        if (true) {
            return 1;
        } else {
            return -1;
        }
    }
}
