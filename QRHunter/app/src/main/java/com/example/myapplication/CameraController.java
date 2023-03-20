package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;

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

    /**
     * Handles scanning QR code event
     * @param contents QR code contents in string format
     * @param db FirestoreFirebase where data is being stored, added and modified
     * @param context Context context of previous Activity
     */
    public void handleScanResult(String contents, FirebaseFirestore db, Context context) {
        qrCodeControllerDB = new QRCodeControllerDB(contents, "michealscott", db);
        qrCodeControllerDB.validateAndAdd(context);

    }
}
