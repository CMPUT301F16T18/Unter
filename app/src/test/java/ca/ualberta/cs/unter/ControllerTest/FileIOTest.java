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
import android.test.ApplicationTestCase;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import ca.ualberta.cs.unter.model.Route;
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

	/**
	 * Test save request in file.
	 */
	public void testSaveRequestInFile() {
		// TODO - test does not pass until save properly implemented
		GeoPoint gPoint1 = new GeoPoint(-137.8826, 58.6698);
		GeoPoint gPoint2 = new GeoPoint(-137.4444, 62.4444);
		Route route = new Route(gPoint1, gPoint2);
		ArrayList<Request> requestList = new ArrayList<>();

		PendingRequest pending = new PendingRequest("john", route);
		requestList.add(pending);

		assertFalse(requestList.isEmpty());

		try {
			FileIOUtil.saveRequestInFile(requestList, getContext());
		} catch (Exception e) {
			Log.i("Error", "Did not save correctly!!");
			throw new RuntimeException();
		}

	}

	/**
	 * Test load request from file.
	 */
	public void testLoadRequestFromFile() {
		// TODO - does not pass until load from file implemented fully
		GeoPoint gPoint1 = new GeoPoint(-137.8826, 58.6698);
		GeoPoint gPoint2 = new GeoPoint(-137.4444, 62.4444);
		Route route = new Route(gPoint1, gPoint2);
		ArrayList<Request> requestList = new ArrayList<>();

		PendingRequest pending = new PendingRequest("john", route);
		requestList.add(pending);

		assertFalse(requestList.isEmpty());

		FileIOUtil.saveRequestInFile(requestList, getContext());

		ArrayList<Request> newList;
		try {
			newList = FileIOUtil.loadRequestFromFile(getContext());
		} catch (Exception e) {
			Log.i("Error", "Did not load correctly.");
			throw new RuntimeException();
		}
		assertFalse(newList.isEmpty());
	}
}
