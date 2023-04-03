package com.example.myapplication;

/**
 * Resource: https://www.youtube.com/watch?v=M73Vec1oieM
 * https://stackoverflow.com/questions/19527248/filtering-search-results-by-a-string-in-android
 */

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
 * Custom Adapter class that displays QR codes matching user's search
 * Retrieves QR code information from firebase
 * @author Jessie
 */
public class GeoSearchAdapter extends ArrayAdapter<HashMap<String, String>> implements Filterable {
    private Context context;
    private List<HashMap<String, String>> mQRs;
    private List<HashMap<String, String>> mFilteredQRs;


    /**
     * Constructor function for GeoSearchAdapter
     * @param context - Context context of activity that instantiates this class
     * @param search
     */
    public GeoSearchAdapter(@NonNull Context context, List<HashMap<String, String>> search) {
        super(context, 0, search);
        this.context = context;
        this.mQRs = search;
        this.mFilteredQRs = search;

    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.geosearch_item, parent, false);
        } else {
            view = convertView;
        }

        if (position >= mFilteredQRs.size()) {
            return view;
        }
        HashMap<String, String> user = mFilteredQRs.get(position);
//        HashMap user = getItem(position);
        TextView username = view.findViewById(R.id.geo_name);
        TextView userloc = view.findViewById(R.id.geo_loc);
        String name = user.get("Name");
        if (name!= null) {
            username.setText(name);
        } else {
            username.setText("");
        }

        String title = user.get("Location");
        if (title != null) {
            userloc.setText(title);
        } else {
            userloc.setText("");
        }
        //check if its null


        Log.e("GeoSearchAdapter: ", "getView() called for position " + position);
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

                filteredList.addAll(mQRs);

            } else {
                for (HashMap<String, String> player : mQRs) {
                    if (player.get("Name").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
                mQRs.clear();
                mQRs.addAll((List<HashMap<String, String>>) results.values);
                notifyDataSetChanged();
            } else {

                notifyDataSetInvalidated();
            }
        }
    };
}