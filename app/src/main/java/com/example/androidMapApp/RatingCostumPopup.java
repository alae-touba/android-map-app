package com.example.androidMapApp;

import android.app.Activity;
import android.app.Dialog;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.googlemapapi.R;

public class RatingCostumPopup extends Dialog {

    private String title, description;
    private double ratingValue;
    private TextView titleView, descriptionView, ratingValueView;
    private RatingBar ratingBar;

    public RatingCostumPopup(Activity activity){
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.rating_popup_template);

        title = "default title";
        description = "default description";
        ratingValue = 0.0;
        titleView = findViewById(R.id.loc_title);
        descriptionView = findViewById(R.id.loc_description);
        ratingValueView = findViewById(R.id.loc_rating_value);
        ratingBar = findViewById(R.id.loc_rating_bar);
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public RatingBar getRatingBar() {
        return ratingBar;
    }

    public void setRatingValue(double ratingValue) {
        this.ratingValue = ratingValue;

    }

    public void build(){
        titleView.setText(title);
        descriptionView.setText(description);
        ratingValueView.append(Double.toString(ratingValue));
        show();
    }
}
