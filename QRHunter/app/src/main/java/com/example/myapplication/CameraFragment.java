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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.LatLng;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;

public class CameraFragment extends Fragment {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    ImageButton back;
    private final Context context;
    private Button backButton;
    private final ActivityResultLauncher<ScanOptions> barLauncher;
    private QRCodeControllerDB qrCodeControllerDB;
    private Location location;

    public CameraFragment(Context context, ActivityResultLauncher<ScanOptions> barLauncher) {
        this.context = context;
        this.barLauncher = barLauncher;

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.customcamera, container, false);

        scanCode();

        backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return rootView;
    }

    public void scanCode() {
        // Check if location permission is granted before launching the barcode scanner
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // get user's current location to potentially store QR code location (works when other services/apps have tracked user's location previously)
            List<String> providers = locationManager.getProviders(true);
            location = null;
            for (String provider : providers) {
                Location currentLocation = locationManager.getLastKnownLocation(provider);
                if (currentLocation == null) {
                    continue;
                }
                if (location == null || currentLocation.getAccuracy() < location.getAccuracy()) {
                    location = currentLocation;
                }
                Log.e("Location", "latitude: " + location.getLatitude() + " longitude: " + location.getLongitude());
            }
            launchBarcodeScanner();
        } else {
            Log.e("Permission", "denied");
            // Location permission is not granted, request for it
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    // Called after requesting for location permission
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
     *
     * @param contents QR code contents in string format
     * @param db       FirestoreFirebase where data is being stored, added and modified
     * @param context  Context context of previous Activity
     */
    public void handleScanResult(String contents, FirebaseFirestore db, Context context, String username) {
        qrCodeControllerDB = new QRCodeControllerDB(contents, username, db);
        qrCodeControllerDB.setLocation(location);
        qrCodeControllerDB.validateAndAdd(context);
    }
}
