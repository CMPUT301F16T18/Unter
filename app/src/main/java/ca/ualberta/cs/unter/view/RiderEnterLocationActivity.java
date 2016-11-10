package ca.ualberta.cs.unter.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.model.request.Request;

public class RiderEnterLocationActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText searchLocationEditText;

    private ListView searchLocationListView;
    private ArrayAdapter<String> searchLocationAdapter;
    private ArrayList<String> searchLocationList = new ArrayList<>();

    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_enter_location);

        searchLocationEditText = (EditText) findViewById(R.id.editText_searchLocation_RiderEnterLocationActivity);
        assert searchLocationEditText != null;
        searchLocationEditText.setOnClickListener(this);

        searchButton = (Button) findViewById(R.id.button_search_RiderEnterLocationActivity);
        assert searchButton != null;
        searchButton.setOnClickListener(this);

        searchLocationListView = (ListView) findViewById(R.id.listView_searchLocationList_RiderEnterLocationActivity);
        searchLocationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO set the text of this search result item to searchLocationEditText
                String text = "test location item";
                searchLocationEditText.setText(text);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        searchLocationAdapter = new ArrayAdapter<>(this, R.layout.list_item, searchLocationList);
        searchLocationListView.setAdapter(searchLocationAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("edittextvalue",searchLocationEditText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == searchButton ) {
            // TODO start search locations
        }
    }
}
