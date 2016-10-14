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
 *
 */

package ca.ualberta.cs.unter.controller;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

import ca.ualberta.cs.unter.model.Request;

/**
 * Class handles all the save to/load from file (file.sav).
 */
// TODO
	// currently hollow for testing purposes
public class FileSystemController {

	private String FILE_NAME = "file.sav";

	public FileSystemController() {} // hollow constructor

	public void saveInFile() {
		//TODO save requestList in file
		// hollow method until implemented
	}

	public void loadFromFile() {
		//TODO load requestList from file
		// hollow method until implemented
	}
}
