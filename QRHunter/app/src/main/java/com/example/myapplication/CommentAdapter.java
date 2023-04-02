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

/**
 * Updates information on CommentListViewActivity
 */
public class CommentAdapter extends ArrayAdapter<HashMap<String, String>> {
    private Context context;
    private List<HashMap<String, String>> mComments;

    public CommentAdapter(@NonNull Context context, List<HashMap<String, String>> comments) {
        super(context, 0, comments);
        this.context = context;
        this.mComments= comments;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView==null){
            view= LayoutInflater.from(getContext()).inflate(R.layout.comment_item,parent,false);
        } else {
            view = convertView;
        }

        if (position >= mComments.size()) {
            return view;
        }

        HashMap<String, String> comment = mComments.get(position);

        TextView username = view.findViewById(R.id.comment_id);
        Log.e("CommentAdapter: ", "Adding comment: " + comment.get("comment").toString());

        username.setText(String.format("@%s said: %s", comment.get("username").toString(), comment.get("comment").toString()));
        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
