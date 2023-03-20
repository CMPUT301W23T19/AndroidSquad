package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

public class RankAdapter extends ArrayAdapter<HashMap<String, Object>> {
    public RankAdapter(@NonNull Context context, List<HashMap<String, Object>> ranks) {
        super(context, 0, ranks);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(R.layout.userranks,parent,false);
        } else {
            view = convertView;
        }

        HashMap user = getItem(position);
        TextView username = view.findViewById(R.id.username);
        ImageView usericon = view.findViewById(R.id.user_icon); // TODO: implement imageview when avatar feature is available
        TextView userscore = view.findViewById(R.id.score);
        Log.e("RankAdapter: ", "Adding username: " + user.get("Username").toString());
        username.setText(user.get("Username").toString());
        userscore.setText(String.valueOf(user.get("Score")));

        return view;
    }
}
