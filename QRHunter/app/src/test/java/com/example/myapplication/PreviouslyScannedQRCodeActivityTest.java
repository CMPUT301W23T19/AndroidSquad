//package com.example.myapplication;
//
//import android.widget.Button;
//
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import org.junit.Before;
//import org.junit.Test;
//
//public class PreviouslyScannedQRCodeActivityTest {
//    private QRCodeControllerDB qrCodeControllerDB;
//    String codeContents;
//    FirebaseFirestore db;
//
//    @Before
//    public QRCode mockQRCode() {
//        codeContents = "BACJFKD";
//        db = FirebaseFirestore.getInstance();
//        QRCode qrCode = new QRCode(codeContents, null);
//        qrCodeControllerDB = new QRCodeControllerDB(codeContents, "emily9", db);
//        return qrCode;
//    }
//
//    @Test
//    public void deleteQRCodeTest() {
//        QRCode qrCode = mockQRCode();
//
//        // add to database
//    }
//
//}
