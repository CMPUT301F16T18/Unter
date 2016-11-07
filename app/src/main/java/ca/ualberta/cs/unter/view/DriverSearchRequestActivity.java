package ca.ualberta.cs.unter.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.model.request.Request;

public class DriverSearchRequestActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner searchOptionSpinner;
    private ArrayAdapter<CharSequence> searchOptionAdapter;

    private Button searchButton;

    private ListView searchRequestListView;
    private ArrayAdapter<Request> searchRequestAdapter;
    private ArrayList<Request> searchRequestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_search_request);

        // https://developer.android.com/guide/topics/ui/controls/spinner.html#Populate
        searchOptionSpinner = (Spinner) findViewById(R.id.spinner_searchOption_DriverSearchRequestActivity);
        searchOptionAdapter = ArrayAdapter.createFromResource(this,
                R.array.search_option, android.R.layout.simple_spinner_item);
        searchOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchOptionSpinner.setAdapter(searchOptionAdapter);

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
            // TODO start search
        }
    }
}
