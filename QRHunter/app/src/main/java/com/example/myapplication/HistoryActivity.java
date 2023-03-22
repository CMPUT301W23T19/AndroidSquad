package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
//    ImageButton imageView;
//    Button bb;
    ImageButton back;
    private String username;
    ArrayAdapter<String> arrayAdapter;
    ListView historyList;
    ArrayList<String> qrNames;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_list);
        back = findViewById(R.id.back);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        // Testing history list
        historyList = findViewById(R.id.history_list);
        qrNames = new ArrayList<>();
        qrNames.add("Poker");
        qrNames.add("SolarFloGalMegaSonicSupernova");
        arrayAdapter = new ArrayAdapter(this, R.layout.history_list_contents, R.id.scanned_name, qrNames);
        historyList.setAdapter(arrayAdapter);

        // initialize imageView
        // with method findViewById()
//        imageView = (ImageButton) findViewById(R.id.imageView4);
//        back = findViewById(R.id.back_from_history);
        // Apply OnClickListener  to imageView to
        // switch from one activity to another
//        imageView.setOnClickListener((v) -> {openActivity2();});
//        bb = (Button) findViewById(R.id.button5);
//        bb.setOnClickListener(v -> {activity();});

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openScannedQRCodeProfile(qrNames.get(position), position);    // for testing purposes
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
                    if (result.getResultCode() > -1) {      // delete qr code
                        // delete from qr codes list
                        Log.e("HistoryActivity","Position of item to be deleted: " + String.valueOf(result.getResultCode()));
                        qrNames.remove(result.getResultCode());
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            });

    public void openScannedQRCodeProfile(String name, int position){
        Intent intent = new Intent(this, PreviouslyScannedQRCodeActivity.class);
        intent.putExtra("qrCodeName", name);
        intent.putExtra("username", username);
        intent.putExtra("position", position);
        startForResult.launch(intent);
    }

    public void commentActivity(){
        Intent intent = new Intent(this, CommentActivity.class);
        startActivity(intent);

    }

}
