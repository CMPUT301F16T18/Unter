package ca.ualberta.cs.unter.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.controller.RequestController;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.request.Request;

public class DriverSearchRequestActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner searchOptionSpinner;
    private ArrayAdapter<CharSequence> searchOptionAdapter;

    EditText searchContextEditText;
    private Button searchButton;

    private ListView searchRequestListView;
    private ArrayAdapter<Request> searchRequestAdapter;
    private ArrayList<Request> searchRequestList = new ArrayList<>();

    private RequestController requestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            // Cast
            searchRequestList = (ArrayList<Request>) o;
            for (Request r : searchRequestList) {
                Log.i("Debug", r.toString());
            }
            // Notify the adapter things is changed
            searchRequestAdapter.clear();
            searchRequestAdapter.addAll(searchRequestList);
            searchRequestAdapter.notifyDataSetChanged();
        }
    });

    private int searchOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_search_request);

        searchContextEditText = (EditText) findViewById(R.id.editText_searchRequest_DriverSearchRequestActivity);

        // https://developer.android.com/guide/topics/ui/controls/spinner.html#Populate
        searchOptionSpinner = (Spinner) findViewById(R.id.spinner_searchOption_DriverSearchRequestActivity);
        searchOptionAdapter = ArrayAdapter.createFromResource(this,
                R.array.search_option, android.R.layout.simple_spinner_item);

        searchOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchOptionSpinner.setAdapter(searchOptionAdapter);

        searchOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchOption = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchButton = (Button) findViewById(R.id.button_search_DriverSearchRequestActivity);
        assert searchButton != null;
        searchButton.setOnClickListener(this);

        searchRequestListView = (ListView) findViewById(R.id.listView_searchList_DriverSearchRequestActivity);
        searchRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentDriverAcceptRequest = new Intent(DriverSearchRequestActivity.this, DriverAcceptRequestActivity.class);
                startActivity(intentDriverAcceptRequest);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        searchRequestAdapter = new ArrayAdapter<>(this, R.layout.driver_search_list_item, searchRequestList);
        searchRequestListView.setAdapter(searchRequestAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view == searchButton ) {
            if (searchOption == 0) {
                //TODO geo-filter
            } else if (searchOption == 1) {
                requestController.searchRequestByKeyword(searchContextEditText.getText().toString());
            }
        }
    }
}
