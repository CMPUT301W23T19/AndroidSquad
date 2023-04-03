/**
 * A custom adapter that extends the ArrayAdapter class and implements the Filterable interface.
 * It is used to display search results for a list of players,
 * where each player is represented as a HashMap object containing the player's name and username.
 * The adapter is responsible for inflating the search_item layout and populating its views with data from the players list.
 * It also provides a filter to search for players by username.
 * @param context the context in which the adapter is used
 * @param search a list of HashMap objects containing player data
 * Resource:
 * https://www.youtube.com/watch?v=M73Vec1oieM
  https://stackoverflow.com/questions/19527248/filtering-search-results-by-a-string-in-android
 */
package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
 * Custom Adapter that sets information to be displayed in the current user's search results (SearchActivity)
 * Filters results based on user's search.
 * @authors: Jessie, Shirley, Anika
 */
public class SearchAdapter extends ArrayAdapter<HashMap<String, String>> implements Filterable {
    private Context context;
    private List<HashMap<String, String>> mPlayers;
    private List<HashMap<String, String>> mFilteredPlayers;

    /**
     * Constructor for the SearchAdapter class.
     * @param context the context in which the adapter is used
     * @param search a list of HashMap objects containing player data
     */
    public SearchAdapter(@NonNull Context context, List<HashMap<String, String>> search) {
        super(context, 0, search);
        this.context = context;
        this.mPlayers = search;
        this.mFilteredPlayers = search;

    }
    /**
     * Overrides the getView method of ArrayAdapter to inflate the search_item layout and populate its views with data from the players list.
     * @param position the position of the view in the list
     * @param convertView the view to be converted
     * @param parent the parent view group
     * @return the view that was inflated and populated with data
     */
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

        // getting the name of user to display profile picture
        String name = user.get("Name").toString();
        Bitmap bmp = getPicture(name);
        usericon.setImageBitmap(bmp);
        Log.e("SearchAdapter: ", "getView() called for position " + position);
        return view;
    }


    /***
     * This function is used to draw the first letter of user name and display it as profile picture
     * Created a canvas to draw the first letter of user's name on Bitmap
     * @param realName the user's name
     * @return bitmap
     */
    private Bitmap getPicture(String realName) {
        int size = 250;
        // creating a new bitmap
        Bitmap bitmap = Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888);
        // canvas to draw on Bitmap
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setStyle(Paint.Style.FILL);
        // drawing the first letter of user's name on the Bitmap
        paint.setTextSize(size / 2);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(realName.substring(0, 1).toUpperCase(), size / 2, size / 2 + size / 8, paint);
        return bitmap;
    }


    /**
     * Overrides the getItemId method of ArrayAdapter to return the position of the item in the list.
     * @param position the position of the item in the list
     * @return the position of the item in the list
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Overrides the getFilter method of the Filterable interface to provide a filter to search for players by username.
     * @return the filter to search for players by username
     */
    @Override
    public Filter getFilter() {
        return filter;
    }
    /**
     * A custom filter that searches for players by username.
     */
    Filter filter = new Filter() {
        /**
        * Performs the filtering operation on the players list to find players whose username contains the search query.
        * @param constraint the search query
        * @return a FilterResults object containing the filtered list of players
        */
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

        /**
         * This method updates the filtered data set with the search results.
         * If the results are not null and contain at least one item, the adapter's data set is cleared and replaced with the filtered list.
         * @param constraint the filtering constraint
         * @param results the filtering results
         */
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