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

package ca.ualberta.cs.unter.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import ca.ualberta.cs.unter.UnterConstant;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;

/**
 * Utility class that provide helper method for
 * OSM that handle all kind of stuffs
 */
public class OSMapUtil {
    /**
     * Get the road between two location
     * @param startPoint the start location coordinate
     * @param destinationPoint the end location coordinate
     * @param listener the task to do after this
     */
    public static void getRoad(GeoPoint startPoint, GeoPoint destinationPoint, OnAsyncTaskCompleted listener) {
        ArrayList<GeoPoint> wayPoints = new ArrayList<GeoPoint>(2);
        wayPoints.add(startPoint);
        wayPoints.add(destinationPoint);
        new UpdateRoadTask(listener).execute(wayPoints);
    }

    /**
     * The async method to interact with
     * MapQuest
     */
    private static class UpdateRoadTask extends AsyncTask<Object, Void, Road[]> {  // where the magic begins
        public UpdateRoadTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }

        OnAsyncTaskCompleted listener;
        protected Road[] doInBackground(Object... params) {
            @SuppressWarnings("unchecked")
            ArrayList<GeoPoint> wayPoints = (ArrayList<GeoPoint>) params[0];
            RoadManager roadManager = new MapQuestRoadManager(UnterConstant.MAP_QUEST_KEY);  // MapQuest key
            return roadManager.getRoads(wayPoints);
        }

        @Override
        protected void onPostExecute(Road[] roads) {
            listener.onTaskCompleted(roads);
        }
    }

    /**
     * Static class for geocoding a street address
     */
    public static class GeocoderTask extends AsyncTask<String, Void, GeoPoint> {

        private Context context;

        public GeocoderTask(Context context) {
            this.context = context;
        }

        /**
         * Converts a street address to a set of coordinates
         * @param strAddress the street address to be converted
         * @return the coordinates of the street address as a GeoPoint
         */
        @Override
        protected GeoPoint doInBackground(String... strAddress) {
            Geocoder coder = new Geocoder(context);
            List<Address> address;
            GeoPoint p1 = null;

            try {
                address = coder.getFromLocationName(strAddress[0], 5);
                if (address == null) {
                    return null;
                }
                Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();

                p1 = new GeoPoint(location.getLatitude(), location.getLongitude());

            } catch (Exception ex) {

                ex.printStackTrace();
            }

            return p1;
        }
    }
}
