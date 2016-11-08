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

package ca.ualberta.cs.unter.model;


import org.osmdroid.util.GeoPoint;


/**
 * The type Route.
 */
public class Route {
    private GeoPoint origin;
    private GeoPoint destination;
	private double distance;
	private double estimatedFare;

    /**
     * Instantiates a new Route.
     *
     * @param origin      the origin coordinate
     * @param destination the destination coordinate
     */
    public Route(GeoPoint origin, GeoPoint destination) {
        this.origin = origin;
        this.destination = destination;
    }

    /**
     * Gets origin.
     *
     * @return the origin coordinate
     */
    public GeoPoint getOrigin() {
        return origin;
    }

    /**
     * Gets destination.
     *
     * @return the destination coordinate
     */
    public GeoPoint getDestination() {
        return destination;
    }

	/**
	 * Gets distance between star/end points
	 * @return the distance as a double
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Set the distance between start/end points.
	 * @param distance
	 */
	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public double getEstimatedFare() {
		return estimatedFare;
	}

	public void setEstimatedFare(Double estimatedFare) {
		this.estimatedFare = estimatedFare;
	}

	public void calculateFare(Double distance) {
		setEstimatedFare(distance * 0.50);
	}

}
