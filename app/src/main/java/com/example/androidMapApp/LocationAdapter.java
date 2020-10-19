package com.example.androidMapApp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.example.googlemapapi.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class LocationAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Location> locationList;
    private LayoutInflater inflater;


    public LocationAdapter(Context context, ArrayList<Location> locationList) {
        this.context = context;
        this.locationList = locationList;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return locationList.size();
    }

    @Override
    public Location getItem(int position) {
        return locationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.adapter_item, null);

        Location currentLocation = getItem(position);

        final String username = currentLocation.username;
        final String description = currentLocation.description;
        final double latitude = currentLocation.latitude;
        final double longitude = currentLocation.longitude;
        final String date = currentLocation.date;

        final int numberVotes = currentLocation.numberVotes;
        final int sumVotes = currentLocation.sumVotes;
        final double rating = numberVotes == 0 ? 0.0 : (double)sumVotes / numberVotes;

        TextView locationUsernameView = view.findViewById(R.id.location_username);
        locationUsernameView.append(username);

        TextView locationDescriptionView = view.findViewById(R.id.location_description);
        locationDescriptionView.setText(description);

        DecimalFormat df = new DecimalFormat("#.##");

        TextView locationRatingView = view.findViewById(R.id.location_rating);
        locationRatingView.append( df.format(rating) );

        TextView locationDateView = view.findViewById(R.id.location_date);
        locationDateView.append(date);

        Button btn = view.findViewById(R.id.see_location_on_map_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("finish");

                i.putExtra("username", username);
                i.putExtra("description", description);
                i.putExtra("latitude", latitude);
                i.putExtra("longitude", longitude);
                i.putExtra("date", date);
                i.putExtra("numberVotes", numberVotes);
                i.putExtra("sumVotes", sumVotes);

                context.sendBroadcast(i);
            }
        });

        return view;
    }
}
