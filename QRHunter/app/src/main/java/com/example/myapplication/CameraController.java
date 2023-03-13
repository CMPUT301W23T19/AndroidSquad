package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class CameraController{
    private final Context context;
    private final ActivityResultLauncher<ScanOptions> barLauncher;
    FirebaseFirestore db;



    public CameraController(Context context, ActivityResultLauncher<ScanOptions> barLauncher,FirebaseFirestore db) {
        this.context = context;
        this.barLauncher = barLauncher;
        this.db=db;
        //db= FirebaseFirestore.getInstance();

    }

    public void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("volume up to flash on!!");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);

    }

    public ActivityResultLauncher<ScanOptions> getBarLauncher() {
        return barLauncher;
    }

    public void handleScanResult(String contents) {
        String serialNumber = extractSerialNumber(contents);
        //QRCodeController qrController = new QRCodeController("wawawawa", "anna46", db);
        //qrController.validateAndAdd();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("result");
        builder.setMessage(serialNumber);
        builder.setMessage(contents);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private String extractSerialNumber(String qrData) {
        String serialNumber = null;
        if (qrData != null) {
            // Assuming the QR code contains the serial number as a string followed by a separator character
            String[] parts = qrData.split(":"); // Replace ":" with your separator character
            if (parts.length > 0) {
                serialNumber = parts[0];
            }
        }
        return serialNumber;
    }
}
