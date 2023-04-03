package com.example.myapplication;//package com.example.myapplication;
//
//
//import android.app.Application;
//import android.content.Context;
//import androidx.test.core.app.ApplicationProvider;
//
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//public class QRCodeControllerTest {
//
//    private String username1 = "emily9";
//    private String username2 = "anna46";
//    private String codeContents1 = "BFG5DG154";
//    private String codeContents2 = "G3G5DG154";
//    private QRCodeController controller;
//
//    /**
//     * Creates a mock QRCodeController instance
//     * @return QRCodeController instance to be used in testing
//     */
//
//    @Before
//    public void setUp() {
//        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
//        controller = new QRCodeController(codeContents1, username1, FirebaseFirestore.getInstance());
//    }
//
//
////    public void mockController() {
////        controller = new QRCodeController(codeContents1, username1, db);
////    }
//
//    @Test
//    public void getNameTest(){
//        setUp();
//        Assert.assertEquals(controller.getName(), "SolarFloGalUltraSpectralTitan");
//    }
//}
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import org.junit.Test;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.Assert.*;
//
//import androidx.annotation.NonNull;
//
//public class QRCodeControllerTest {
//
//    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//
//    @Test
//    public void testFirestoreConnection() {
//        // Create a test document with a random value
//        Map<String, Object> testDocument = new HashMap<>();
//        testDocument.put("testField", "testValue");
//
//        // Add the test document to Firestore
//        firestore.collection("Testing Collection")
//                .add(testDocument)
//                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                        if (task.isSuccessful()) {
//                            // If the document was successfully added, assert that the task is successful
//                            assertTrue(task.isSuccessful());
//                        } else {
//                            // If the document failed to add, fail the test
//                            fail("Firestore connection failed");
//                        }
//                    }
//                });
//    }
//}
