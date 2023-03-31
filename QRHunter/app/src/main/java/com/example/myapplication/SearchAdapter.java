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
public class SearchAdapter extends ArrayAdapter<HashMap<String, String>> implements Filterable {
    private Context context;
    private List<HashMap<String, String>> mPlayers;
    private List<HashMap<String, String>> mFilteredPlayers;


    public SearchAdapter(@NonNull Context context, List<HashMap<String, String>> search) {
        super(context, 0, search);
        this.context = context;
        this.mPlayers = search;
        this.mFilteredPlayers = search;

    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.search_item, parent, false);
        } else {
            view = convertView;
        }

        if (position >= mFilteredPlayers.size()) {
            return view;
        }
        HashMap<String, String> user = mFilteredPlayers.get(position);
//        HashMap user = getItem(position);

        TextView username = view.findViewById(R.id.search_name);
        ImageView usericon = view.findViewById(R.id.rank1);
        TextView userid = view.findViewById(R.id.search_id);
        Log.e("SearchAdapter: ", "Adding username: " + user.get("Username").toString());

        username.setText(user.get("Name").toString());
        userid.setText(String.valueOf(user.get("Username")));
        Log.e("SearchAdapter: ", "getView() called for position " + position);

        return view;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            List<HashMap<String, String>> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                // No filter implemented, return the full list

                filteredList.addAll(mPlayers);

            } else {
                for (HashMap<String, String> player : mPlayers) {
                    if (player.get("Username").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(player);
                    }
                }
            }
//            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                mPlayers.clear();
                mPlayers.addAll((List<HashMap<String, String>>) results.values);
                notifyDataSetChanged();
            } else {

                notifyDataSetInvalidated();
            }
        }
    };
}