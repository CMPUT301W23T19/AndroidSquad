package com.example.myapplication;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for MainActivity.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * Tests if CameraActivty starts from MainActivity
     */
    @Test
    public void checkCameraActivity() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.camera));

        solo.assertCurrentActivity("Wrong Activity", CameraActivity.class);
        solo.clickOnView(solo.getView(R.id.exitCam));
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

    }

    /**
     * Tests if HistoryActivty starts from MainActivity
     */
    @Test
    public void checkHistoryActivity() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.history));
        solo.assertCurrentActivity("Wrong Activity", HistoryActivity.class);
    }

    /**
     * Tests if PreviouslyScannedQRCodeActivity starts from HistoryActivity
     */
    @Test
    public void checkPreviouslyScannedQRCodeActivity() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.history));
        solo.assertCurrentActivity("Wrong Activity", HistoryActivity.class);

        ListView qrCodeList =  (ListView) solo.getView(R.id.history_list);
        solo.clickOnView(qrCodeList);

        solo.assertCurrentActivity("Wrong Activity", PreviouslyScannedQRCodeActivity.class);

    }


    /**
     * Tests if LeaderboardActivity starts from MainActivity
     */
    @Test
    public void checkLeaderboardActivity() throws InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.view_more));
        solo.assertCurrentActivity("Wrong Activity", LeaderboardActivity.class);

        solo.clickOnView(solo.getView(R.id.highest_scoring));
        solo.assertCurrentActivity("Wrong Activity", LeaderboardActivity.class);
        solo.waitForActivity(LeaderboardActivity.class, 10);

        solo.clickOnView(solo.getView(R.id.greatest_sum));
        solo.waitForActivity(LeaderboardActivity.class, 10);
        solo.assertCurrentActivity("Wrong Activity", LeaderboardActivity.class);
    }

    /**
     * Tests if SearchActivity starts from MainActivity
     */
    @Test
    public void checkSearchActivity() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.search_button));

        solo.assertCurrentActivity("Wrong Activity", SearchActivity.class);
        solo.clickOnView(solo.getView(R.id.search_bar));
        solo.enterText(0, "micheal");

    }

    /**
     * Tests if OtherUserProfileActivity starts from SearchActivity
     */
    @Test
    public void checkOtherUserProfileActivity() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.search_button));

        solo.assertCurrentActivity("Wrong Activity", SearchActivity.class);

        ListView results =  (ListView) solo.getView(R.id.result_list);
        solo.clickOnView(results);
        solo.assertCurrentActivity("Wrong Activity", OtherUserProfileActivity.class);
    }

    /**
     * Tests if MapActivity starts from MainActivity
     */
    @Test
    public void checkMapActivity() throws InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.map));
        solo.waitForActivity(MapActivity.class);
        solo.assertCurrentActivity("Wrong Activity", MapActivity.class);
    }

    /**
     * Tests if ProfileActivity starts from MainActivity
     */
    @Test
    public void checkProfileActivity() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        String username = solo.getString(R.id.name);
        Log.e("TAG", username);
        solo.clickOnView(solo.getView(R.id.profile));

        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        Log.e("TAG", username + solo.getString(R.id.username3));

        assertEquals(username, solo.getString(R.id.username3));
    }


}