/**
 * An ArrayAdapter for displaying a list of user ranks.
 * Each item in the list consists of a user's username, avatar, and score.
 * The adapter converts the user's avatar from a Base64-encoded string to a Bitmap image.
 */
package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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

/**
 * Custom Adapter class that sets information to be displayed in LeaderboardActivity
 * @author Shirley
 */
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
        //Log.e("RankAdapter: ", "Adding username: " + user.get("Username").toString()+" Avatar is :" + user.get("Avatar"));
        username.setText("username: "+user.get("Username").toString());
        userscore.setText("score: "+ String.valueOf(user.get("Score")));
        if (user.get("Avatar")!=null){
            usericon.setImageBitmap(StringToBitMap((String) user.get("Avatar")));
        }
        return view;
    }

    /**
     * Converts String to bitMap that will be displayed as the player's avatar
     * @param encodedString - String to be converted
     * @return Bitmap to be displayed as an image
     */
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
