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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.controller.UserController;
import ca.ualberta.cs.unter.model.Car;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.User;
import ca.ualberta.cs.unter.util.FileIOUtil;

public class DriverCarInfoActivity extends AppCompatActivity {

    private EditText vehicleNameEditText;
    private EditText plateNumberEditText;

    private Button saveButton;

    private User driver;

    UserController userController = new UserController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            FileIOUtil.saveUserInFile((User) o, getApplicationContext());
            finish();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_car_info);

        driver = FileIOUtil.loadUserFromFile(getApplicationContext());

        vehicleNameEditText = (EditText) findViewById(R.id.edittext_vehcilename_drivercarinfoactivity);
        plateNumberEditText = (EditText) findViewById(R.id.edittext_platenumber_drivercarinfoactivity);

        saveButton = (Button) findViewById(R.id.button_save_drivercarinfoactivity);
        assert saveButton != null;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCarInfo();
            }
        });

        if (driver.getCar() != null) {
            vehicleNameEditText.setText(driver.getVehicleName());
            plateNumberEditText.setText(driver.getPlateNumber());
        }
    }

    protected void saveCarInfo() {
        String vehicleName = vehicleNameEditText.getText().toString();
        String plateNumber = plateNumberEditText.getText().toString();

        if (TextUtils.isEmpty(vehicleName)) {
            vehicleNameEditText.setError("Vehicle name cannot be empty!");
        } else if (TextUtils.isEmpty(plateNumber)) {
            plateNumberEditText.setError("Plate number cannot be empty!");
        } else if (!TextUtils.isEmpty(vehicleName) && !TextUtils.isEmpty(plateNumber)) {
            Car car = new Car(vehicleName, plateNumber);
            driver.setCar(car);
            userController.updateCar(driver);
        }
    }
}
