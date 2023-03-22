package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.mutation.ArrayTransformOperation;

import java.util.ArrayList;

/** Activity class that displays comprehensive information on selected QR code */
public class PreviouslyScannedQRCodeActivity extends AppCompatActivity {
    private Button delete;
    private String qrName;
    private ImageButton back;
    private TextView name;
    private QRCodeControllerDB qrCodeControllerDB;
    private PlayerController playerController;
    private String username;
    FirebaseFirestore db;
    int score;
    int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previously_scanned_qr_code);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        qrName = intent.getStringExtra("qrCodeName");
        position = intent.getIntExtra("position", -1);
//        score = intent.getIntExtra("score", 0);
        score = 10;     // TODO: replace with above code
        playerController = new PlayerController(null, null, null,username, db);
        qrCodeControllerDB = new QRCodeControllerDB(null, username, db);

        name = findViewById(R.id.qr_code_name);
        name.setText(qrName);
        back = findViewById(R.id.back);
        delete = findViewById(R.id.delete);



        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PreviouslyScannedQRCodeActivity.this);     // Creates window telling user they have already scanned it
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete this QR Code?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        qrCodeControllerDB.deleteUser(qrName);
                        playerController.deleteQRFromHistory(qrName);
                        playerController.updateScore(-1*score);       // TODO: Pass in qr_score from historyActivity

                        // check lowest and highest and update if necessary

                        Intent intent = new Intent();
                        setResult(position, intent);
                        dialogInterface.dismiss();
                        finish();      // return to HistoryActivity
                    }
                }).show();
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(-1, intent);      // result code of -1 means user did not delete QR code
                finish();
            }
        });
    }

}
