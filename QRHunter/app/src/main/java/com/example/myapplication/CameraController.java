package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class CameraController{
    private final Context context;
    private final ActivityResultLauncher<ScanOptions> barLauncher;

    public CameraController(Context context, ActivityResultLauncher<ScanOptions> barLauncher) {
        this.context = context;
        this.barLauncher = barLauncher;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("result");
        builder.setMessage(contents);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }
}
