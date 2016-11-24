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

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ca.ualberta.cs.unter.UnterConstant;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.model.request.NormalRequest;
import ca.ualberta.cs.unter.model.request.Request;

/**
 * The type File io util.
 */
public class FileIOUtil {
	/**
	 * An utility method that save user profile locally
	 *
	 * @param user    the user object to be saved
	 * @param context an android activity component
	 */
	public static void saveUserInFile(User user, Context context) {
        try {
            Gson gson = new Gson();
            String jsonStr = gson.toJson(user);
            // write json string into corresponding file
            Log.i("Debug", jsonStr);
            FileOutputStream fos = context.openFileOutput(UnterConstant.USER_PROFILE_FILENAME, 0);
            fos.write(jsonStr.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

	/**
	 * An utility method that retrieve user profile locally
	 *
	 * @param context an android activity component
	 * @return an User object
	 */
	public static User loadUserFromFile(Context context) {
        User user = new User();
        try {
            Gson gson = new Gson();
            FileInputStream fis = context.openFileInput(UnterConstant.USER_PROFILE_FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            user = gson.fromJson(in, User.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

	/**
	 * Save request in file.
	 *
	 * @param request the  requests
	 * @param context the context
	 */
	public static void saveOfflineRequestInFile(Request request, Context context) {
		try {
			Gson gson = RequestUtil.customGsonBuilder();
			String jsonStr = gson.toJson(request);
			String fileName = RequestUtil.generateOfflineRequestFileName(request);
			FileOutputStream fos = context.openFileOutput(fileName, 0);
			if (fos == null) {
				Log.i("Debug", "null fos in save request");
			}
			try {
				fos.write(jsonStr.getBytes());
			} catch (NullPointerException e) {
				Log.i("Debug", "getBytes() threw null pointer exception");
			}
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	/**
	 * Load request from file array list.
	 *
	 * @param context the context
	 * @return the array list of requests
	 */
	public static ArrayList<Request> loadRequestFromFile(Context context, ArrayList<String> fileList) {
		ArrayList<Request> requestList = new ArrayList<>();
        Gson gson = RequestUtil.customGsonBuilder();
		for (String f : fileList) {
            FileInputStream fis = null;
            try {
                fis = context.openFileInput(f);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                NormalRequest req = gson.fromJson(in, NormalRequest.class);
                requestList.add(req);
                context.deleteFile(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
		}
		return requestList;
	}
}
