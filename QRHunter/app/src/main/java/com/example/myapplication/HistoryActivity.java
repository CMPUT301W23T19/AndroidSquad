package com.example.myapplication;

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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    ImageButton imageView;
    Button bb;
    ImageButton back;
    private String username;

    ListView historyList;
    private FirebaseFirestore db;
    ArrayList<HistoryModel> HistoryAdapterlist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_list);
        back = findViewById(R.id.back);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        db = FirebaseFirestore.getInstance();
        // Testing history list
        historyList = findViewById(R.id.history_list);
        ListView listView = findViewById(R.id.history_list);


        // custom array adapter
        HistoryAdapter adapter= new HistoryAdapter(this,getIntent());
        listView.setAdapter(adapter);


        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //openScannedQRCodeProfile(qrNames.get(position));    // for testing purposes
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
