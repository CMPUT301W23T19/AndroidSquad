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

public class HistoryActivity extends AppCompatActivity {
    ImageButton imageView;
    Button bb;
    ImageButton back;
    private String username;
    HistoryAdapter adapter;
    ListView historyList;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_list);
        back = findViewById(R.id.back);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        //username="y";
        db = FirebaseFirestore.getInstance();
        // Testing history list
        historyList = findViewById(R.id.history_list);
        ListView listView = findViewById(R.id.history_list);

        // custom array adapter
        adapter= new HistoryAdapter(this,getIntent());
        listView.setAdapter(adapter);


        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openScannedQRCodeProfile(adapter.getItem(position).getName(),adapter.getItem(position).getScore(),position,adapter.getItem(position).getLocation(),adapter.getItem(position).getCount());
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    // Get data from child activity (PreviouslyScannedQRCodeActivity)
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
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

    public void openScannedQRCodeProfile(String name, Long score,Integer position,String location, Long playerCount){
        // push the history model
        Intent intent = new Intent(this, PreviouslyScannedQRCodeActivity.class);
        intent.putExtra("qrCodeName", name);
        intent.putExtra("username", username);
        intent.putExtra("qrscore",score);
        intent.putExtra("location",location);
        //startActivity(intent);
        intent.putExtra("PlayerCount",playerCount);
        intent.putExtra("position", position);
        startForResult.launch(intent);
    }

    public void commentActivity(){
        Intent intent = new Intent(this, CommentActivity.class);
        startActivity(intent);
    }


}
