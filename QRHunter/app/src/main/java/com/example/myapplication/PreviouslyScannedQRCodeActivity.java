package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;

/** Activity class that displays comprehensive information on selected QR code */
public class PreviouslyScannedQRCodeActivity extends AppCompatActivity {
    private Button delete;
    private String qrName;
    private Long qrScore;
    private Long playerCount;
    private ImageButton back;
    private TextView name;
    private TextView score;

    private TextView qrlocation;
    private TextView playernumber;

    private Button commentlistb;
    private Button commentb;

    private QRCodeControllerDB qrCodeControllerDB;
    private PlayerController playerController;
    private String username;
    FirebaseFirestore db;
    //int qrScore;
    private int position;
    private String location;
    private ArrayList<String> features;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previously_scanned_qr_code);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        qrName = intent.getStringExtra("qrCodeName");
        location = intent.getStringExtra("location");
        qrScore = intent.getLongExtra("qrscore",0);
        position = intent.getIntExtra("position", -1);
        playerCount=intent.getLongExtra("PlayerCount",0);
        features = intent.getStringArrayListExtra("features");

        playerController = new PlayerController(null, null, null,username, db);
        qrCodeControllerDB = new QRCodeControllerDB(null, username, db);

        commentb = (Button) findViewById(R.id.comment);
        commentlistb = (Button) findViewById(R.id.open_comment);
        name = findViewById(R.id.qr_code_name);
        name.setText(qrName);
        back = findViewById(R.id.back);
        delete = findViewById(R.id.delete);
        score = findViewById(R.id.qr_code_score);
        qrlocation=findViewById(R.id.qr_code_location);
        playernumber = findViewById(R.id.count_players_scanned_qr_code);

        score.setText(qrScore.toString());
        qrlocation.setText(location);
        playernumber.setText(playerCount.toString()+" other player scanned this QR Code");

        //Get avatar list
        HashMap<Integer, Integer[]> faces = new HashMap<>();
        faces.put(0, new Integer[]{R.id.face1, R.id.face2});
        faces.put(1, new Integer[]{R.id.eyebrow1, R.id.eyebrow2});
        faces.put(2, new Integer[]{R.id.eye1, R.id.eye2});
        faces.put(3, new Integer[]{R.id.nose1, R.id.nose2});
        faces.put(4, new Integer[]{R.id.mouth1, R.id.mouth2});

        for (int i = 0; i < features.size(); i++) {
            ImageView feature;
            if (features.get(i).compareTo("0") == 0) {
                feature = findViewById(faces.get(i)[0]);
            } else {
                feature = findViewById(faces.get(i)[1]);
            }
            feature.setVisibility(View.VISIBLE);
        }

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
                        DocumentReference playerDocRef =  db.collection("Player").document(username);
                        playerController.deleteQRFromHistory(qrName);
                        playerController.updateScore(-1, qrName);
                        qrCodeControllerDB.deleteUser(qrName);
                        playerDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if ((long)task.getResult().get("highestScore") == (long) qrScore) {       // deleted Qr code is the highest scoring
                                            playerDocRef.update("highestScore", 0);
                                        } else if ((long)task.getResult().get("lowestScore") == (long) qrScore) {
                                            playerDocRef.update("lowestScore", (long)task.getResult().get("highestScore"));
                                        } else {
                                            Log.e("Updating High/Low Score", "No need to update");
                                        }
                                        playerController.deleteUpdateHighLowScore();
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
        commentb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(PreviouslyScannedQRCodeActivity.this, CommentActivity.class);
                startActivity(intent);

            }
        });
        commentlistb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(PreviouslyScannedQRCodeActivity.this, CommentListViewActivity.class);
                startActivity(intent);

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
