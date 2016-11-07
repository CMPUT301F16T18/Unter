package ca.ualberta.cs.unter.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ca.ualberta.cs.unter.R;

public class DriverAcceptRequestActivity extends AppCompatActivity implements View.OnClickListener {
    private Button cancelButton;
    private Button acceptRequestButton;

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
