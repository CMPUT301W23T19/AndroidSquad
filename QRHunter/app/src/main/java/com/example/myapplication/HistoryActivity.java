package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.checkerframework.checker.units.qual.A;

public class HistoryActivity extends AppCompatActivity {
    ImageButton imageView;
    Button bb;
    ImageButton back;
    private String username;
    ArrayAdapter<String> arrayAdapter;
    ListView historyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_list);
        back = findViewById(R.id.back);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        // Testing history list
        historyList = findViewById(R.id.history_list);
        String[] qrNames = {"SolarGloStelMegaSonicTitan", "SolarFloGalMegaSonicSupernova"};
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
                openScannedQRCodeProfile(qrNames[position]);    // for testing purposes
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void openScannedQRCodeProfile(String name){
        Intent intent = new Intent(this, PreviouslyScannedQRCodeActivity.class);
        intent.putExtra("qrCodeName", name);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void activity(){
        Intent intent = new Intent(this, CommentActivity.class);
        startActivity(intent);

    }

}
