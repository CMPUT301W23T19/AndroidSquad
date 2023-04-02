package com.example.myapplication;

import android.app.appsearch.SearchResult;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Resource: https://www.youtube.com/watch?v=M73Vec1oieM
 * https://stackoverflow.com/questions/19527248/filtering-search-results-by-a-string-in-android
 */
public class CommentAdapter extends ArrayAdapter<HashMap<String, String>> {
    private Context context;
    private List<HashMap<String, String>> mComment;



    public CommentAdapter(@NonNull Context context, List<HashMap<String, String>> comment) {
        super(context, 0, comment);
        this.context = context;
        this.mComment= comment;

    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(R.layout.comment_item,parent,false);
        } else {
            view = convertView;
        }

        if (position >= mComment.size()) {
            return view;
        }
        HashMap<String, String> user = mComment.get(position);


        TextView username = view.findViewById(R.id.comment_id);
        TextView usercomment = view.findViewById(R.id.comment_content);
        Log.e("CommentAdapter: ", "Adding comment: " + user.get("Comment").toString());

        username.setText(user.get("Username").toString());
        usercomment.setText(String.valueOf(user.get("Comment")));

        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
