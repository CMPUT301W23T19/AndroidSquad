
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

public class CameraActivity extends CaptureActivity {
    Button exit;

    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.camera);
        exit = findViewById(R.id.exitCam);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
    }
}