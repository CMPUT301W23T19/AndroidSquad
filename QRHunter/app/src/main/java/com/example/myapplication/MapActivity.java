package com.example.myapplication;

/**
 * Getting distance between two markers
 * --From: www.stackoverflow.com
 * --URL: https://stackoverflow.com/q/14394366
 * --Author: https://stackoverflow.com/users/1304830/fr4nz
 * --License: CC BY-SA
 *
 * Customizing marker colour
 * --From: www.stackoverflow.com
 * --URL: https://stackoverflow.com/q/19076124
 * --Author: https://stackoverflow.com/users/1777471/jrowan
 * --License: CC BY-SA
 *
 * Displaying map and getting current location
 * https://www.youtube.com/watch?v=kRAyXxgwOhQ
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * Activity class that displays the map containing current user's location and location of QR Codes
 * Location is retrieved from the firebase
 * @authors: Anika, Angela, Jessie
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng currentLocation;
    private Button backButton;
    private FirebaseFirestore db;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * Handles the event when BACK button is clicked.
             * Returns to Home page (Main Activity)
             * @param v The view that was clicked.
             */
            public void onClick(View v) {
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        username = getIntent().getStringExtra("username");
        double latitude = getIntent().getDoubleExtra("latitude", 53.5312);
        double longitude = getIntent().getDoubleExtra("longitude", -113.4907);
        currentLocation = new LatLng(latitude, longitude);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gMap);
        supportMapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Displays the map containing markers for the current user's location as well as QR codes with
     * a known location.
     * Uses Google Map API.
     * Returns to Home page (Main Activity)
     * @param googleMap - GoogleMap map to be displayed
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // get the QR code name value passed from the intent
            String qrCodeName = getIntent().getStringExtra("qrpassname");
            Log.e("MapActivity","Check the qrname passed correctly: " + qrCodeName);
            if (qrCodeName != null) {
                displaySpecificQRCodes(qrCodeName);
            } else {
                currentLocation();
                displayQRCodes();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Displays the location of the current user on the map
     */
    private void currentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                db.collection("Player").document(username)
                                .update("Location", geoPoint);
                Toast.makeText(this, "The service is available!", Toast.LENGTH_SHORT).show();
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(currentLocation).title(username)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                googleMap.addMarker(markerOptions).hideInfoWindow();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            } else {
                Toast.makeText(this, "The service is not available!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * using the value passed of the specific qrcode location, it takes user to that marker on the map
     * @param qrCodeValue
     */
    public void displaySpecificQRCodes(String qrCodeValue) {
        Log.e("MapActivity","This is the value passed for display: " + qrCodeValue);
        DocumentReference qrDocRef = db.collection("QR Code").document(qrCodeValue);
        qrDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot qrCode = task.getResult();
                    if (qrCode.exists()) {
                        if (qrCode.get("Location") != null) {
                            Log.e("qrLocation", String.valueOf(qrCode.get("Name")));
                            GeoPoint geopoint = (GeoPoint) qrCode.get("Location");
                            LatLng qrLocation = new LatLng(geopoint.getLatitude(), geopoint.getLongitude());
                            MarkerOptions marker = new MarkerOptions()
                                    .position(qrLocation)
                                    .title(String.format("Name: %s", qrCodeValue))
                                    .snippet(String.format("Score: %d", qrCode.get("Score")))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                            googleMap.addMarker(marker);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(qrLocation, 15));
                        }
                    } else {
                        Log.e("qrCodeNotFound", "QR code not found in database.");
                    }
                } else {
                    Log.e("qrCodeFetchError", "Error fetching QR code from database.", task.getException());
                }
            }
        });
    }

    /**
     * Displays the QR Codes on the map
     * A small window containing the selected QR code's score and distance from the user is displayed
     */
    public void displayQRCodes() {
        CollectionReference qrColRef = db.collection("QR Code");
        qrColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> qrCodes = task.getResult().getDocuments();
                for (DocumentSnapshot qrCode : qrCodes) {
                    if (qrCode.get("Location") != null) {
                        Log.e("qrLocation", String.valueOf(qrCode.get("Name")));
                        GeoPoint geopoint = (GeoPoint) qrCode.get("Location");
                        LatLng qrLocation = new LatLng(geopoint.getLatitude(), geopoint.getLongitude());
                        float distance = getDistance(qrLocation);
                        MarkerOptions markers = new MarkerOptions()
                                .position(qrLocation)
                                .title(String.format("Score: %d", qrCode.get("Score")))
                                .snippet(String.format("Distance: %.2f m", distance))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        googleMap.addMarker(markers);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(qrLocation, 15));
                    }
                }
            }
        });
    }

    /**
     * Requests for user's permission to track location
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            currentLocation();
        } else {
            Toast.makeText(this, "Location permission is not granted!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Calculates the distance between the current user and a QR Code
     * @param qrCodeLocation - LatLng representing the QR Code location
     * @return float representation of the distance
     */
    public float getDistance(LatLng qrCodeLocation) {
        float[] distances = new float[1];
        Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,
                qrCodeLocation.latitude, qrCodeLocation.longitude, distances);
        return distances[0];
    }
}

