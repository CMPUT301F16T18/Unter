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

import java.util.ArrayList;

import ca.ualberta.cs.unter.model.Route;

/**
 * Created by Michael on 16-11-04.
 */
public class NormalRequest extends Request {
    public NormalRequest() {
    }

    public NormalRequest(String riderUserName, Route route) {
        super(riderUserName, route);
    }

    public NormalRequest(String riderUserName, String driverUserName, Route route, double estimatedFare) {
        super(riderUserName, driverUserName, route, estimatedFare);
    }

    public NormalRequest(String riderUserName, ArrayList<String> driverList, Route route, double estimatedFare) {
        super(riderUserName, driverList, route, estimatedFare);
    }
}
