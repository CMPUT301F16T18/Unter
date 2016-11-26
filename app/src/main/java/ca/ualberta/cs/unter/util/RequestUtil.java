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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import ca.ualberta.cs.unter.model.request.NormalRequest;
import ca.ualberta.cs.unter.model.request.Request;

/**
 * Utility class to help pass request object through intent
 * or update to the server
 */
public class RequestUtil {
    public static String serializer(Request request) {
        Gson gson = new GsonBuilder().registerTypeAdapter(GeoPoint.class, new GeoPointConverter()).create();
        return gson.toJson(request);
    }

    public static Request deserializer(String string) {
        Gson gson = new GsonBuilder().registerTypeAdapter(GeoPoint.class, new GeoPointConverter()).create();
        return gson.fromJson(string, NormalRequest.class);
    }

    public static Gson customGsonBuilder() {
        return new GsonBuilder().registerTypeAdapter(GeoPoint.class, new GeoPointConverter()).create();
    }

    public static String generateOfflineRequestFileName(Request request) {
        return "offline-" + request.getID() + ".json";
    }

    public static String generateAcceptedReqestFileName(Request request) {
        return "accepted-" + request.getID() + ".json";
    }

    public static String generateRiderRequestFileName(Request request) {
        return "rider-" + request.getID() + ".json";
    }

    public static String generateDriverRequestFileName(Request request) {
        return "driver-" + request.getID() + ".json";
    }

    public static ArrayList<String> getOfflineRequestList(Context context) {
        String[] fileList = context.fileList();
        ArrayList<String> offlineRequestFileList = new ArrayList<>();
        for (String f : fileList) {
            if (f != null && f.startsWith("offline-")) {
                offlineRequestFileList.add(f);
            }
        }
        return offlineRequestFileList;
    }

    public static ArrayList<String> getRiderRequestList(Context context) {
        String[] fileList = context.fileList();
        ArrayList<String> offlineAcceptedRequestFileList = new ArrayList<>();
        for (String f : fileList) {
            if (f != null && f.startsWith("rider-")) {
                Log.i("Debug", f);
                offlineAcceptedRequestFileList.add(f);
            }
        }
        return offlineAcceptedRequestFileList;
    }

    public static ArrayList<String> getDriverRequestList(Context context) {
        String[] fileList = context.fileList();
        ArrayList<String> offlineAcceptedRequestFileList = new ArrayList<>();
        for (String f : fileList) {
            if (f != null && f.startsWith("driver-")) {
                Log.i("Debug", f);
                offlineAcceptedRequestFileList.add(f);
            }
        }
        return offlineAcceptedRequestFileList;
    }

    public static ArrayList<String> getAcceptedRequestList(Context context) {
        String[] fileList = context.fileList();
        ArrayList<String> offlineAcceptedRequestFileList = new ArrayList<>();
        for (String f : fileList) {
            if (f != null && f.startsWith("accepted-")) {
                Log.i("Debug", f);
                offlineAcceptedRequestFileList.add(f);
            }
        }
        return offlineAcceptedRequestFileList;
    }
}
