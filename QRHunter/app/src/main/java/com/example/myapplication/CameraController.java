package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class CameraController {
    private final Context context;
    private final ActivityResultLauncher<ScanOptions> barLauncher;
    private QRCodeControllerDB qrControllerDB;
    private QRCodeControllerDB qrCodeControllerDB;

    public CameraController(Context context, ActivityResultLauncher<ScanOptions> barLauncher) {
        this.context = context;
        this.barLauncher = barLauncher;
    }
    public void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("volume up to flash on!!");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CameraActivity.class);
        barLauncher.launch(options);

    }

    public ActivityResultLauncher<ScanOptions> getBarLauncher() {
        return barLauncher;
    }

    public void handleScanResult(String contents, FirebaseFirestore db, Context context) {
        qrCodeControllerDB = new QRCodeControllerDB(contents, "emily9", db);
        qrCodeControllerDB.validateAndAdd();
        Intent intent = new Intent(context, ScannedQRCodeActivity.class);
        intent.putExtra("contents", contents);
        context.startActivity(intent);
    }
}
