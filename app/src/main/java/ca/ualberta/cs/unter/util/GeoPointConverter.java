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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.osmdroid.util.GeoPoint;

import java.lang.reflect.Type;

// http://mybrainoncode.com/blog/2012/03/05/a-geopoint-converter-for-gson/
/**
 * Serializes and deserializes a GeoPoint
 *
 * It gets serialized to [lon, lat] (e.g. [-67.834062, 46.141129] )
 *
 * It also expects this format when deserializing.
 * */
public class GeoPointConverter implements JsonSerializer<GeoPoint>,
        JsonDeserializer<GeoPoint> {

    @Override
    public JsonElement serialize(GeoPoint src, Type srcType,
                                 JsonSerializationContext context) {

        JsonArray array = new JsonArray();

        array.add(
                context.serialize(src.getLongitudeE6() / 1E6, Double.class));
        array.add(
                context.serialize(src.getLatitudeE6() / 1E6, Double.class));

        return array;
    }

    @Override
    public GeoPoint deserialize(JsonElement json, Type type,
                                JsonDeserializationContext context)
            throws JsonParseException {

        final JsonArray array = json.getAsJsonArray();
        final JsonElement lonElement = array.get(0);
        final JsonElement latElement = array.get(1);
        final Double lon = lonElement.getAsDouble();
        final Double lat = latElement.getAsDouble();

        return new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6));
    }
}

