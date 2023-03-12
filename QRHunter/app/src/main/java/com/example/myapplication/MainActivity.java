package com.example.myapplication;

/** Reference(s):
 * Using setOnItemSelectedListener to make bottom navigation bar functional:
 * -- From: www.stackoverflow.com
 * -- URL: https://stackoverflow.com/q/67641594
 * -- Author: https://stackoverflow.com/users/10429009/ali-moghadam
 *
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarMenu;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_page);
        BottomNavigationView bottomNavigationView  = (BottomNavigationView) findViewById(R.id.nav_bar);
        db = FirebaseFirestore.getInstance();

        // Bottom Navigation bar functionality
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.camera) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        });
    }
}