
package com.example.myapplication;

/**
 * Resource(s):
 * Making a custom camera using CaptureActivity
 * -- From: www.github.com
 * -- URL: https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/SmallCaptureActivity.java
 * -- License: Apache License 2.0
 */

import android.view.View;
import android.widget.Button;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * Activity class that displays a custom BarCode scanner camera
 * @authors: Randy, Angela
 */
public class CameraActivity extends CaptureActivity {
    Button exit;

    /**
     * Opens the camera with a custom layout
     * @return custom DecoratedBarcodeView containing the scan functionality
     */
    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.camera);
        exit = findViewById(R.id.exitCam);
        exit.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the event when EXIT button is clicked.
             * Returns to Home page (Main Activity)
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
    }
}