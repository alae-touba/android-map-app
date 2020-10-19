package com.example.androidMapApp;

import android.app.Activity;
import android.app.Dialog;
import android.widget.TextView;

import com.example.googlemapapi.R;

public class CostumPopupMapType extends Dialog {
    private TextView hybridView;
    private TextView satelliteView;
    private TextView terrainView;
    private TextView normalView;

    public CostumPopupMapType(Activity activity){
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.map_type_popup_template);

        hybridView = findViewById(R.id.hybrid);
        satelliteView = findViewById(R.id.satellite);
        terrainView = findViewById(R.id.terrain);
        normalView = findViewById(R.id.normal);
    }


    public TextView getHybridView() {
        return hybridView;
    }

    public TextView getNormalView() {
        return normalView;
    }

    public TextView getSatelliteView() {
        return satelliteView;
    }

    public TextView getTerrainView() {
        return terrainView;
    }

    public void build(){
        show();
    }
}
