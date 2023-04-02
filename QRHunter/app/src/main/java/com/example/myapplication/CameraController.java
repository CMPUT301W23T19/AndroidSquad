package com.example.myapplication;

/**
 * Resource(s):
 * Get user's current location:
 * --From: www.stackoverflow.com
 * --URL: https://stackoverflow.com/q/20438627
 * --Author: https://stackoverflow.com/users/1852924/tharakanirmana
 * --License: CC BY-SA
 */

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.LatLng;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;

public class CameraController {
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private final Context context;
    private final ActivityResultLauncher<ScanOptions> barLauncher;
    private QRCodeControllerDB qrCodeControllerDB;
    private Location location;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;




    public CameraController(Context context, ActivityResultLauncher<ScanOptions> barLauncher) {
        this.context = context;
        this.barLauncher = barLauncher;

    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }
    }

    public void scanCode() {
        // Check if location permission is granted before launching the barcode scanner
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // get user's current location to potentially store QR code location (works when other services/apps have tracked user's location previously)
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location qrLocation : locationResult.getLocations()) {
                        if (qrLocation != null) {
                            location = qrLocation;
                            Log.e("qrLocation", String.valueOf(location));
                        }
                    }
                }
            };
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location qrLocation) {
                    location = qrLocation;
                    launchBarcodeScanner();
                }
            });
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }

        else {
            Log.e("Permission", "denied");
            // Location permission is not granted, request for it
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            launchBarcodeScanner();
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
    public void handleScanResult(String contents, FirebaseFirestore db, Context context, String username, ActivityResultLauncher<Intent> launcher) {
        qrCodeControllerDB = new QRCodeControllerDB(contents, username, db);
        qrCodeControllerDB.setLocation(location);
        qrCodeControllerDB.setLauncher(launcher);
        qrCodeControllerDB.validateAndAdd(context);
    }

 }




