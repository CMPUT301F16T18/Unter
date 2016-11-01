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

package ca.ualberta.cs.unter.model.request;

/**
 * Reuqest that has been sent by the rider,
 * and accepted by one or more driver. Also,
 * it has been confirmed by the rider as well.
 * @see Request
 */
public class ConfirmedRequest extends Request{
    public ConfirmedRequest(String riderUserName, String driverUserName, double[] originCoordinate, double[] destinationCoordinate, Double estimatedFare) {
        super(riderUserName, driverUserName, originCoordinate, destinationCoordinate, estimatedFare);
    }
}
