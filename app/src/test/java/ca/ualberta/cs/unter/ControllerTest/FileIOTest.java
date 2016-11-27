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

package ca.ualberta.cs.unter.ControllerTest;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import ca.ualberta.cs.unter.model.Route;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.model.request.AcceptedRequest;
import ca.ualberta.cs.unter.model.request.CompletedRequest;
import ca.ualberta.cs.unter.model.request.ConfirmedRequest;
import ca.ualberta.cs.unter.model.request.PendingRequest;
import ca.ualberta.cs.unter.model.request.Request;
import ca.ualberta.cs.unter.util.FileIOUtil;

/**
 * Testing suite for the FileIO system
 */
public class FileIOTest extends ApplicationTestCase<Application> {

	/**
	 * The Signal.
	 */
	CountDownLatch signal = null;

	/**
	 * Instantiates a new File io test.
	 */
	public FileIOTest() {
		super(Application.class);
	}

	@Override
	public void setUp() throws Exception {
		signal = new CountDownLatch(1);
	}

	@Override
	public void tearDown() throws Exception {
		signal.countDown();
	}

	public void testSaveUserInFile() {
		User testUser = new User("test", "000", "test@test.com");
		try {
			FileIOUtil.saveUserInFile(testUser, getContext());
		} catch (Exception e) {
			throw new RuntimeException("Failed to save user");
		}
	}

	public void testLoadUserFromFile() {
		User testUser = new User("test", "000", "test@test.com");
		FileIOUtil.saveUserInFile(testUser, getContext());

		User user;
		user = FileIOUtil.loadUserFromFile(getContext());
		assertFalse(user.equals(null));
	}

	public void testSaveRequestInFile() {
		Route route = new Route(new GeoPoint(-127.00, 32), new GeoPoint(-128.11, 36));
		PendingRequest test = new PendingRequest("test", route);

		try {
			FileIOUtil.saveRequestInFile(test, "test.json", getContext());
		} catch (Exception e) {
			throw new RuntimeException("Failed to save request");
		}

	}

	public void testSaveOfflineRequestInFile() {
		Route route = new Route(new GeoPoint(-127.00, 32), new GeoPoint(-128.11, 36));
		PendingRequest test = new PendingRequest("test1", route);

		try {
			FileIOUtil.saveOfflineRequestInFile(test, getContext());
		} catch (Exception e) {
			throw new RuntimeException("Failed to save request");

		}
	}

	public void testSaveRiderRequestInFile() {
		Route route = new Route(new GeoPoint(-127.00, 32), new GeoPoint(-128.11, 36));
		PendingRequest test = new PendingRequest("test2", route);

		try {
			FileIOUtil.saveRiderRequestInFile(test, getContext());
		} catch (Exception e) {
			throw new RuntimeException("Failed to save request");
		}
	}

	public void testLoadRequestFromFile() {
		ArrayList<Request> testArray;
		ArrayList<String> strArray = new ArrayList<>();
		strArray.add("test.json");

		testArray = FileIOUtil.loadRequestFromFile(getContext(),strArray);
		assertFalse(testArray.isEmpty());
	}

	public void testLoadSingleRequestFromFile() {
		Request test;

		test = FileIOUtil.loadSingleRequestFromFile("test.json", getContext());
		assertFalse(test.equals(null));
	}
}
