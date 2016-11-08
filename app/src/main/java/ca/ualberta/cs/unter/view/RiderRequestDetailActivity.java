package ca.ualberta.cs.unter.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.model.request.Request;

public class RiderRequestDetailActivity extends AppCompatActivity {

    private ListView acceptanceListView;
    private ArrayAdapter<Request> acceptanceAdapter;
    private ArrayList<Request> acceptanceList = new ArrayList<>();

    private TextView startingLocationTextView;
    private TextView endingLocationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_request_detail);

        startingLocationTextView = (TextView)findViewById(R.id.textView_startingLocation_RiderRequestDetailActivity);
        endingLocationTextView = (TextView)findViewById(R.id.textView_endingLocation_RiderRequestDetailActivity);

        // TODO set text with actual starting and ending locatoin
        startingLocationTextView.setText("UOFA HUB");
        endingLocationTextView.setText("UOFA CAB");

        acceptanceListView = (ListView) findViewById(R.id.listView_acceptance_RiderRequestDetailActivity);
        acceptanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO intent RiderChooseAcceptanceDialog
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        acceptanceAdapter = new ArrayAdapter<>(this, R.layout.list_item, acceptanceList);
        acceptanceListView.setAdapter(acceptanceAdapter);
    }
}
