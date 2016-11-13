package ca.ualberta.cs.unter.view;

import android.test.ActivityInstrumentationTestCase2;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.robotium.solo.Solo;
import org.kohsuke.randname.RandomNameGenerator;
import java.util.Random;
import ca.ualberta.cs.unter.R;
import ca.ualberta.cs.unter.view.LoginActivity;
import ca.ualberta.cs.unter.view.RiderMainActivity;
import ca.ualberta.cs.unter.view.SignupActivity;
//import static org.junit.Assert.*;

/**
 * Created by TongTong on 12/11/2016.
 */
public class RiderMainActivityTest extends ActivityInstrumentationTestCase2<RiderMainActivity>{
    private Solo solo;

    public RiderMainActivityTest() {
        super(ca.ualberta.cs.unter.view.RiderMainActivity.class);
    }

    public void testStart() throws Exception {
        Activity activity = getActivity();
    }

    @Override
    public void setUp() throws Exception {
        // super.setUp();
        Log.d("TAG1", "setUp()");
        solo = new Solo(getInstrumentation(),getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        // super.tearDown();
        solo.finishOpenedActivities();
    }

    public void testSendRequest() {
        solo.assertCurrentActivity("Wrong Activity", RiderMainActivity.class);
        // Enter Departure location
        solo.enterText((EditText) solo.getView(R.id.editDeparture), "university of alberta");
        solo.clickOnButton("search");
        // Enter Destination location
        solo.enterText((EditText) solo.getView(R.id.editDestination), "edmonton house");
        solo.clickOnButton("Search");

        // click on fab to open the dialog
        View fab = getActivity().findViewById(R.id.fab);
        solo.clickOnView(fab);

        if (solo.waitForDialogToOpen(1000)) {
            assertTrue("Could not find the dialog!", solo.searchText("Send Request"));
        }

        // enter price
        solo.clearEditText ((EditText) solo.getView(R.id.edittext_fare_ridermainactivity));
        solo.enterText((EditText) solo.getView(R.id.edittext_fare_ridermainactivity), "66");

        solo.enterText((EditText) solo.getView(R.id.edittext_description_ridermainactivity),
                                                            "test request with robotium");
        // send request
        solo.clickOnButton("Send Request");

        if (solo.waitForDialogToClose(1000)) {
            solo.assertCurrentActivity("Wrong Activity", RiderMainActivity.class);
        }
    }
}