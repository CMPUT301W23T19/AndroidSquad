package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class CameraControllerActivity extends AppCompatActivity {

    private CameraController cameraController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        ImageButton backButton = findViewById(R.id.back_button_camera);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize the CameraController and the ActivityResultLauncher
        ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(
                new ScanContract(),
                result -> {
                    if (result != null) {
                        // Handle the scan result here
                        String contents = result.getContents();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String username = "yourUsername"; // Replace with the actual username
                        cameraController.handleScanResult(contents, db, CameraControllerActivity.this, username);
                    }
                });

        cameraController = new CameraController(this, barLauncher);
        cameraController.scanCode();

    }

}
