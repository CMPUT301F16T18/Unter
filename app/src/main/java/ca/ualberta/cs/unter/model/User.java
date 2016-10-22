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

/**
 * This a abstract base class for for all user model, including Driver and Rider.
 */
public abstract class User {
    private String userName;
    private String mobileNumber;
    private String emailAddress;

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
}
