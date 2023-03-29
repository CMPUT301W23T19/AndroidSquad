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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
                        Log.e("Score", qrCode + String.valueOf(qrScore));
                        HistoryModel historyModel = new HistoryModel(qrCode,qrScore);
                        add(historyModel);
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
        qrCodeTextView.setText(historyModel.getName());
        qrScoreTextView.setText("Score: " + String.valueOf(historyModel.getScore()));
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
