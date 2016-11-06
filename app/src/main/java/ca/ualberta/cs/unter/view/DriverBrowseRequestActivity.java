package ca.ualberta.cs.unter.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.model.request.Request;

public class DriverBrowseRequestActivity extends AppCompatActivity {

    private ListView acceptedRequestListView;
    private ListView pendingRequestListView;

    private ArrayAdapter<Request> acceptedRequestAdapter;
    private ArrayAdapter<Request> pendingRequestAdapter;

    private ArrayList<Request> acceptedRequestList = new ArrayList<>();
    private ArrayList<Request> pendingRequestList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_browse_request);

        acceptedRequestListView = (ListView) findViewById(R.id.accepted_request_list);
        acceptedRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO open request info dialog
            }
        });

        pendingRequestListView = (ListView) findViewById(R.id.pending_request_list);
        pendingRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO open request info dialog
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        acceptedRequestAdapter = new ArrayAdapter<>(this, R.layout.driver_accepted_list_item, acceptedRequestList);
        pendingRequestAdapter = new ArrayAdapter<>(this, R.layout.driver_pending_list_item, pendingRequestList);
        acceptedRequestListView.setAdapter(acceptedRequestAdapter);
        pendingRequestListView.setAdapter(pendingRequestAdapter);
    }


}
