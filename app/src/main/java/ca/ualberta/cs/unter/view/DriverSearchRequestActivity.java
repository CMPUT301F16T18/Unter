package ca.ualberta.cs.unter.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.model.request.Request;

public class DriverSearchRequestActivity extends AppCompatActivity {

    private Spinner searchOptionSpinner;
    private ArrayAdapter<CharSequence> searchOptionAdapter;

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

        searchRequestListView = (ListView) findViewById(R.id.listView_searchList_DriverSearchRequestActivity);
        searchRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO intent to DriverAcceptRequestActivity
//                Intent intentDriverAcceptRequest = new Intent(this, DriverAcceptRequestActivity.class);
//                startActivity(intentDriverAcceptRequest);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        searchRequestAdapter = new ArrayAdapter<>(this, R.layout.driver_accepted_list_item, searchRequestList);
        searchRequestListView.setAdapter(searchRequestAdapter);
    }

}
