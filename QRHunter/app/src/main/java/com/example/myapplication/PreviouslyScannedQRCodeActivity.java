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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.mutation.ArrayTransformOperation;

import java.util.ArrayList;

/** Activity class that displays comprehensive information on selected QR code */
public class PreviouslyScannedQRCodeActivity extends AppCompatActivity {
    private Button delete;
    private String qrName;
    private Long qrScore;
    private ImageButton back;
    private TextView name;
    private TextView score;
    private QRCodeControllerDB qrCodeControllerDB;
    private PlayerController playerController;
    private String username;
    FirebaseFirestore db;
    //int qrScore;
    int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previously_scanned_qr_code);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        qrName = intent.getStringExtra("qrCodeName");
        qrScore = intent.getLongExtra("qrscore",0);
        position = intent.getIntExtra("position", -1);
//        score = intent.getIntExtra("score", 0);
        //qrScore = 107;     // TODO: replace with above code
        playerController = new PlayerController(null, null, null,username, db);
        qrCodeControllerDB = new QRCodeControllerDB(null, username, db);

        name = findViewById(R.id.qr_code_name);
        name.setText(qrName);
        back = findViewById(R.id.back);
        delete = findViewById(R.id.delete);
        score = findViewById(R.id.qr_code_score);
        score.setText(qrScore.toString());

        boolean d = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(PreviouslyScannedQRCodeActivity.this);     // Creates window telling user they have already scanned it



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
                        playerController.updateScore((int)(-1*qrScore));       // TODO: Pass in qr_score from historyActivity

                        DocumentReference playerDocRef =  db.collection("Player").document(username);
                        playerDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if ((long)task.getResult().get("highestScore") == (long) qrScore) {       // deleted Qr code is the highest scoring
                                            playerDocRef.update("highestScore", 0);
                                            playerController.deleteUpdateHighLowScore("high");
                                        } else if ((long)task.getResult().get("lowestScore") == (long) qrScore) {
                                            playerDocRef.update("lowestScore", (long)task.getResult().get("highestScore"));
                                            playerController.deleteUpdateHighLowScore("low");
                                        } else {
                                            Log.e("Updating High/Low Score", "No need to update");
                                        }
                                    }
                                });

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
