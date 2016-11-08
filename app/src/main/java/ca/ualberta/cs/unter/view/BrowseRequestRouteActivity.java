package ca.ualberta.cs.unter.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.controller.RequestController;
import ca.ualberta.cs.unter.model.OnAsyncTaskCompleted;
import ca.ualberta.cs.unter.model.request.Request;
import ca.ualberta.cs.unter.util.RequestIntentUtil;

/**
 * Activity that allow driver to view the rider's request on map
 * and decided to accept it or not.
 */
public class BrowseRequestRouteActivity extends AppCompatActivity implements View.OnClickListener {
    private Button cancelButton;
    private Button acceptRequestButton;
    private Request request;
    private RequestController requestController = new RequestController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {

        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_accept_request);

        cancelButton = (Button) findViewById(R.id.button_cancel_DriverAcceptRequestActivity);
        assert cancelButton != null;
        cancelButton.setOnClickListener(this);

        acceptRequestButton = (Button) findViewById(R.id.button_accept_DriverAcceptRequestActivity);
        assert acceptRequestButton != null;
        acceptRequestButton.setOnClickListener(this);

        Intent intent = getIntent();
        // TODO display the map
        // Retrieve the intent, and deserialize it into a request object
        String requestStr = intent.getStringExtra("request");
        request = RequestIntentUtil.deserializer(requestStr);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        if (view == cancelButton ) {
            finish();
        } else if (view == acceptRequestButton){
            // TODO accept a request
        }
    }
}
