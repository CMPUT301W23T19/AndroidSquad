package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter<HistoryModel> {
    private FirebaseFirestore db;
    private String username;
    private Intent intent;
    public HistoryAdapter(Context context,Intent intent) {
        super(context, 0);
        this.intent = intent;
        username = intent.getStringExtra("username");
        db = FirebaseFirestore.getInstance();
        // connect to the database and store it into History Model
        db.collection("Player").document(username).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> qrCodes = (List<String>) documentSnapshot.get("QRcode");
                for (int i =0; i<qrCodes.size();i++){
                    final String qrCode = qrCodes.get(i);
                    db.collection("QR Code").document(qrCode).get().addOnSuccessListener(documentSnapshot1 -> {
                        Long qrScore = documentSnapshot1.getLong("Score");
                        Long playerCount= documentSnapshot1.getLong("Player Count");
                        //Geopoint location= documentSnapshot1.getString("Location");
                        if (documentSnapshot1.get("Location") != null) {
                            GeoPoint geolocation = documentSnapshot1.getGeoPoint("Location");
                            String location = "Location: " + geolocation.getLatitude() + "," + geolocation.getLongitude();
                            Log.e("Score", qrCode + String.valueOf(qrScore));
                            HistoryModel historyModel = new HistoryModel(qrCode, qrScore, location, playerCount);
                            add(historyModel);
                        }
                    });
                }

            }
        }).addOnFailureListener(e -> {
            Log.e("HistoryAdapter", "Error getting document", e);
        });
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_list_contents, parent, false);
        }

        HistoryModel historyModel = getItem(position);

        TextView qrCodeTextView = convertView.findViewById(R.id.scanned_name);
        TextView qrScoreTextView= convertView.findViewById(R.id.scanned_score);
        //TextView qrLocationTextview = convertView.findViewById(R.id.qr_code_location);
        qrCodeTextView.setText(historyModel.getName());
        qrScoreTextView.setText("Score: " + String.valueOf(historyModel.getScore()));
        //qrLocationTextview.setText(historyModel.getLocation());
        return convertView;
    }

    /**
     * Updates the list containing previously scanned QR codes as well as the listView
     * @param position - Integer representation of the index of the item to be removed
     */
    public void updateDataList(int position) {
        remove(getItem(position));
        notifyDataSetChanged();
    }

}
