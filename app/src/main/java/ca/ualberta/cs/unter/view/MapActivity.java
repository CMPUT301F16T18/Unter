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

package ca.ualberta.cs.unter.view;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.util.ArrayList;
import java.util.List;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.model.request.Request;
import ca.ualberta.cs.unter.util.RequestIntentUtil;

// Code adapted from:
// http://stackoverflow.com/questions/38539637/osmbonuspack-roadmanager-networkonmainthreadexception
// accessed on October 27th, 2016
// author: yubaraj poudel

public class MapActivity extends Activity {

	private Activity ourActivity = this;
	private MapView map;
	private Road[] mRoads;
	private double distance;
	private Request request;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		map = (MapView) findViewById(R.id.map);
		map.setTileSource(TileSourceFactory.MAPNIK);
		map.setBuiltInZoomControls(true);
		map.setMultiTouchControls(true);

		String requestStr = getIntent().getStringExtra("request");
		request = RequestIntentUtil.deserializer(requestStr);

		GeoPoint startPoint = request.getOriginCoordinate();  // Start point
		IMapController mapController = map.getController();
		mapController.setZoom(9);
		mapController.setCenter(startPoint);

		Marker startMarker = new Marker(map);
		startMarker.setPosition(startPoint);
		startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); // sets map to centre here
		startMarker.setTitle("Start Point");
		map.getOverlays().add(startMarker);


		GeoPoint destinationPoint = request.getDestinationCoordinate();  // End point
		Marker endMarker = new Marker(map);
		endMarker.setPosition(destinationPoint);
		endMarker.setTitle("End Point");
		map.getOverlays().add(endMarker);

		ArrayList<OverlayItem> overlayItemArray;
		overlayItemArray = new ArrayList<>();

		overlayItemArray.add(new OverlayItem("Starting Point", "This is the starting point", startPoint));
		overlayItemArray.add(new OverlayItem("Destination", "This is the destination point", destinationPoint));
		getRoadAsync(startPoint, destinationPoint);

		request.calculateEstimatedFare(distance);
		request.setDistance(distance);
	}

	public void getRoadAsync(GeoPoint startPoint, GeoPoint destinationPoint) {
		mRoads = null;

		ArrayList<GeoPoint> wayPoints = new ArrayList<GeoPoint>(2);
		wayPoints.add(startPoint);
		wayPoints.add(destinationPoint);
		new UpdateRoadTask().execute(wayPoints);
	}

	private class UpdateRoadTask extends AsyncTask<Object, Void, Road[]> {  // where the magic begins

		protected Road[] doInBackground(Object... params) {
			@SuppressWarnings("unchecked")
			ArrayList<GeoPoint> wayPoints = (ArrayList<GeoPoint>) params[0];
			RoadManager roadManager = new MapQuestRoadManager("jYSs7vV0XMAMdCTXhFlnFAHzc6EAXGyi");  // MapQuest key
			return roadManager.getRoads(wayPoints);
		}

		@Override
		protected void onPostExecute(Road[] roads) {
			mRoads = roads;
			if (roads == null)
				return;
			if (roads[0].mStatus == Road.STATUS_TECHNICAL_ISSUE)
				Toast.makeText(map.getContext(), "Technical issue when getting the route", Toast.LENGTH_SHORT).show();
			else if (roads[0].mStatus > Road.STATUS_TECHNICAL_ISSUE) //functional issues
				Toast.makeText(map.getContext(), "No possible route here", Toast.LENGTH_SHORT).show();
			Polyline[] mRoadOverlays = new Polyline[roads.length];
			List<Overlay> mapOverlays = map.getOverlays();
			for (int i = 0; i < roads.length; i++) {
				Polyline roadPolyline = RoadManager.buildRoadOverlay(roads[i]);
				mRoadOverlays[i] = roadPolyline;
				String routeDesc = roads[i].getLengthDurationText(ourActivity, -1);
				roadPolyline.setTitle(getString(R.string.app_name) + " - " + routeDesc);
				roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
				roadPolyline.setRelatedObject(i);
				mapOverlays.add(1, roadPolyline);

				// all this shizz works
				Toast.makeText(ourActivity, "distance="+roads[i].mLength,Toast.LENGTH_LONG).show();
				Toast.makeText(ourActivity, "durée="+roads[i].mDuration,Toast.LENGTH_LONG).show();

				distance = roads[i].mLength;
				double price = distance * 0.50;
				Toast.makeText(ourActivity, "price="+price, Toast.LENGTH_LONG).show();
				//we insert the road overlays at the "bottom", just above the MapEventsOverlay,
				//to avoid covering the other overlays.
			}
		}
	}
}
