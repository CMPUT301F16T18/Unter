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

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import ca.ualberta.cs.unter.exception.RequestException;
import ca.ualberta.cs.unter.model.Route;

/**
 * This is a class that contains all attributes a Request should have.
 */
public abstract class Request implements FareCalculator{
    private String riderUserName;
    private String driverUserName;
    private ArrayList<String> driverList;

    private Route route;

    private double estimatedFare;
    private String requestDescription;

    private Boolean isCompleted;
    private Boolean isDriverAccepted;

    private String ID;

    /**
     * Constructor for a new request.
     *
     * @param riderUserName  the rider user name
     * @param route the path from pickup location to destination
     */
    public Request(String riderUserName, Route route) {
        this.riderUserName = riderUserName;
        this.route = route;
        this.estimatedFare = calculateEstimatedFare();
    }

    /**
     * Constructor for ConfirmedRequest or CompletedRequest.
     *
     * @param riderUserName         the rider user name
     * @param driverUserName        the driver user name
     * @param estimatedFare         the estimated fare
     * @param route the path from pickup location to destination
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
     * @param driverList the list of drivers username who accept the request
     * @param estimatedFare the estimated fare
     */
    public Request(String riderUserName, ArrayList<String> driverList, Route route, double estimatedFare) {
        this.riderUserName = riderUserName;
        this.driverList = driverList;
        this.route = route;
        this.estimatedFare = estimatedFare;
    }

    /**
     * Calculate an estimated fare base on location
     * @return the estimated fare
     */
    @Override
    public double calculateEstimatedFare() {
        return 100;
    }

    /**
     * Driver accepts the request.
     *
     * @param driverUserName the driver user name who accepts the ride request
     */
    public void driverAcceptRequest(String driverUserName) { // changed from driverConfirmRequest
        if (this.driverUserName.isEmpty()) {
            // If the request has not been accepted
            this.driverUserName = driverUserName;
        } else if (driverList == null && this.driverUserName != null) {
            // If the request has been confirmed by only one driver
            driverList = new ArrayList<>();
            // add existing accepted driver username first
            driverList.add(this.driverUserName);
            // add the new accepted driver
            driverList.add(driverUserName);
        } else if (driverList != null && !driverList.isEmpty()) {
            // If the request has been accepted by more than one driver
            driverList.add(driverUserName);
        }
    }

    /**
     * Rider confirm driver.
     *
     * @param driverUserName the driver user name
     * @exception RequestException raise exception when request has not been confirmed
     */
    public void riderConfirmDriver(String driverUserName) {
        try {
            if (this.driverUserName.isEmpty() || driverList.isEmpty()) {
                // If the request has not been accpeted yet
                throw new RequestException("This request has not been accepted by any driver yet");
            } else {
                // Confirmed driver
                this.driverUserName = driverUserName;
            }
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rider complete request and pay.
     */
    public void riderCompleteRequest() {
        isCompleted = true;
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
        return riderUserName;
    }

    /**
     * Gets driver user name.
     *
     * @return the driver user name
     */
    public String getDriverUserName() {
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
        return isCompleted;
    }

    /**
     * Gets estimated fare.
     *
     * @return the estimated fare
     */
    public Double getEstimatedFare() {
        return estimatedFare;
    }

    public ArrayList<String> getDriverList() {
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
     * Sets estimated fare.
     *
     * @param estimatedFare the estimated fare
     */
    public void setEstimatedFare(Double estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRequestDescription() {
        return requestDescription;
    }

    public Route getRoute() {
        return route;
    }
}
