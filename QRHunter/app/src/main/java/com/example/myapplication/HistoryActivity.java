package com.example.myapplication;

/**
 * Resource(s):
 * Passing and retrieving data from child activity to parent:
 * --From: www.stackoverflow.com
 * --URL: https://stackoverflow.com/q/62671106
 * --Author: https://stackoverflow.com/users/12256844/abhijeet
 * --License: CC BY-SA
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;

/**
 * Activity class that displays QR codes that have been previously scanned by current user
 * Starts PreviouslyScannedQRCodeActivity when user selects on a QR Code
 * @authors: Randy, Angela, Jessie
 */
public class HistoryActivity extends AppCompatActivity {
    ImageButton back;
    private String username;
    HistoryAdapter adapter;
    ListView historyList;
    private FirebaseFirestore db;

    /**
     * Gets views associated with history_list layout and populates them with QR Code information
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_list);
        back = findViewById(R.id.back);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        db = FirebaseFirestore.getInstance();
        historyList = findViewById(R.id.history_list);
        ListView listView = findViewById(R.id.history_list);

        // custom array adapter
        adapter= new HistoryAdapter(this,getIntent());
        listView.setAdapter(adapter);


        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Handles event when a QR Code in the ListView has been selected.
             * Starts PreviouslyScannedQRCodeActivity and passes information of selected
             * QR Code.
             * @param parent The AdapterView where the click happened.
             * @param view The view within the AdapterView that was clicked (this
             *            will be a view provided by the adapter)
             * @param position The position of the view in the adapter.
             * @param id The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openScannedQRCodeProfile(adapter.getItem(position).getName(),
                        adapter.getItem(position).getScore(),
                        position,adapter.getItem(position).getLocation(),
                        adapter.getItem(position).getCount(),
                        adapter.getItem(position).getFeatures());
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the event when BACK button is clicked.
             * Returns to Home page (Main Activity)
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }


    // Get data from child activity (PreviouslyScannedQRCodeActivity)
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                /**
                 * Deletes QR Code from list and ListView if delete event occurred in
                 * PreviouslyScannedQRCodeActivity
                 * @param result - ActivityResult result containing the position of the QR Code to be
                 *               deleted. Position is -1 if no delete event occurred.
                 */
                @Override
                public void onActivityResult(ActivityResult result) {
                    int position = result.getResultCode();
                    if (position > -1) {      // delete qr code
                        // delete from qr codes list
                        Log.e("HistoryActivity","Position of item to be deleted: " + String.valueOf(position));
                        adapter.updateDataList(position);
                    }
                }
            });

    /**
     * Starts the PreviouslyScannedQRCodeActivity class
     * @param name - name of the selected QR Code
     * @param score - score of the selected QR Code
     * @param position - position of item selected
     * @param location - location of selected QR Code
     * @param playerCount - count of players who have scanned the QR Code
     * @param features - array of features belonging to the selected QR Code
     */

    public void openScannedQRCodeProfile(String name, Long score,Integer position,String location, Long playerCount, ArrayList<String> features){
        // push the history model
        Intent intent = new Intent(this, PreviouslyScannedQRCodeActivity.class);
        intent.putExtra("qrCodeName", name);
        intent.putExtra("username", username);
        intent.putExtra("qrscore",score);
        intent.putExtra("location",location);
        //startActivity(intent);
        intent.putExtra("PlayerCount",playerCount);
        intent.putExtra("position", position);
        intent.putExtra("features", features);
        startForResult.launch(intent);
    }
}
