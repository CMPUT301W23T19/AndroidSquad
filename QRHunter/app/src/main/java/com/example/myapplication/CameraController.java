package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class CameraController {
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private final Context context;
    private final ActivityResultLauncher<ScanOptions> barLauncher;
    private QRCodeControllerDB qrCodeControllerDB;

    public CameraController(Context context, ActivityResultLauncher<ScanOptions> barLauncher ) {
        this.context = context;
        this.barLauncher = barLauncher;

    }

    public void scanCode() {
        // Check if location permission is granted before launching the barcode scanner
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            launchBarcodeScanner();
        } else {
            // Location permission is not granted, request for it
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    // Called after requesting for location permission
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, launch barcode scanner
                launchBarcodeScanner();
            }
        }
    }

    // Helper method to launch the barcode scanner with options
    private void launchBarcodeScanner() {
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
    public void handleScanResult(String contents, FirebaseFirestore db, Context context, String username) {
        qrCodeControllerDB = new QRCodeControllerDB(contents, username, db);
        qrCodeControllerDB.validateAndAdd(context);
    }


    }




