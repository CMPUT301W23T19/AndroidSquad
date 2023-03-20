package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import org.checkerframework.checker.units.qual.A;

public class HistoryActivity extends AppCompatActivity {
    ImageButton imageView;
    Button bb;
    Button back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main);
        // initialize imageView
        // with method findViewById()
        imageView = (ImageButton) findViewById(R.id.imageView4);
        back = findViewById(R.id.back_from_history);
        // Apply OnClickListener  to imageView to
        // switch from one activity to another
        imageView.setOnClickListener((v) -> {openActivity2();});
        bb = (Button) findViewById(R.id.button5);
        bb.setOnClickListener(v -> {activity();});

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void openActivity2(){
        Intent intent = new Intent(this, HistoryClickActivity.class);
        startActivity(intent);
    }
    public void activity(){
        Intent intent = new Intent(this, CommentActivity.class);
        startActivity(intent);

    }

}
