package com.example.androidMapApp;

//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.googlemapapi.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;


//usually MapsActivity extends FragmentActivity but AppCompactActivity itself extends FragmentActivity sop no problem here
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, SearchView.OnQueryTextListener {

    SearchView searchView;

    private GoogleMap mMap;
    private UiSettings mUiSettings;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;


    private static final LatLng RABAT_COORD = new LatLng(33.985142, -6.870655);
    private static final LatLng CASABLANCA_COORD = new LatLng(33.570122, -7.589913);
    private Marker mRabat;
    private Marker mCasablanca;

    private Marker myMarker;

    public final static int SAVE_PLACE_CODE = 1;
    public final static int EXPLORE_CODE = 2;
    public final static int SYNCHRONIZE_CODE = 3;
    public final static int EXPLORE_LOCAL_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = findViewById(R.id.sv_location);
        searchView.setFocusable(false);
        searchView.setOnQueryTextListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //adds items on the main_menu.xml to the app bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         //Inflate the menu; this adds items to the app bar.
         getMenuInflater().inflate(R.menu.main_menu, menu);
         return super.onCreateOptionsMenu(menu);
    }

    //handling clicks on the items on the app bar
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.explore_online:
                Intent i = new Intent(MapsActivity.this, ExploreOtherLocationsActivity.class );
                startActivityForResult(i, EXPLORE_CODE);
                return true;

            case R.id.synchronise:
                Intent intentSynchro = new Intent(MapsActivity.this, SynchronizeDatabasesActivity.class);
                startActivityForResult(intentSynchro, SYNCHRONIZE_CODE);
                return true;

            case R.id.explore_local:
                Intent intentExploreLocal = new Intent(MapsActivity.this, SeeLocalDbContentActivity.class);
                startActivityForResult(intentExploreLocal, EXPLORE_LOCAL_CODE);
                return true;

            case R.id.change_map_type:
                final CostumPopupMapType popupMapType = new CostumPopupMapType(MapsActivity.this);
                popupMapType.getHybridView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        popupMapType.dismiss();
                    }
                });
                popupMapType.getNormalView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        popupMapType.dismiss();
                    }
                });
                popupMapType.getSatelliteView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        popupMapType.dismiss();
                    }
                });
                popupMapType.getTerrainView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        popupMapType.dismiss();
                    }
                });
                popupMapType.build();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in rabat and casablanca and move the camera to rabat
        mRabat = mMap.addMarker(
                    new MarkerOptions()
                    .position(RABAT_COORD)
                    .title("Marker in rabat")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet("welcome to the capital of morocco hehe!"));

        mCasablanca = mMap.addMarker(
                        new MarkerOptions()
                        .position(CASABLANCA_COORD)
                        .title("Marker in casablanca")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        mRabat.setTag(999);
        mCasablanca.setTag(0);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(RABAT_COORD, 8));

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);

        //set a listener for window info events (marker window info)
        mMap.setOnInfoWindowClickListener(this);

        //playing around with the user interface (zoom btns..etc)
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);

        //listening for click events on POI(points of interest)
        mMap.setOnPoiClickListener(this);

        //listening for clicks on the map
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);

        //requesting permission to access current location of the user and setting events when clicking on button to show curr location
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();


    }//end onMapReady


    //responding to click events on the markers
    @Override
    public boolean onMarkerClick( Marker marker) {
        Object tag = marker.getTag();

        if(tag instanceof Integer){
            marker.showInfoWindow();
        }else {
            com.example.androidMapApp.Location location = (com.example.androidMapApp.Location) marker.getTag();
            String ratingg = "" + (double) location.sumVotes / location.numberVotes;

            String username = location.username;
            String description = location.description;
            final double latitude = location.latitude;
            final double longitude = location.longitude;
            String date = location.date;
            int numberVotes = location.numberVotes;
            int sumVotes = location.sumVotes;

            double rating = numberVotes == 0 ? 0.0 : (double) sumVotes / numberVotes;
            DecimalFormat df = new DecimalFormat("#.##");


            RatingCostumPopup ratingPopup = new RatingCostumPopup(MapsActivity.this);
            ratingPopup.setTitle(username);
            ratingPopup.setDescription(description);
            ratingPopup.setRatingValue(Double.parseDouble( df.format(rating) ));


            ratingPopup.getRatingBar().setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    Toast.makeText(getApplicationContext(), "rating : " + rating, Toast.LENGTH_SHORT).show();


                    String SERVER_URL = "https://androidapp2020.000webhostapp.com/update_rating.php?latitude=" + latitude +
                            "&longitude=" + longitude + "&rating=" + (int) rating;

                    new LongOperation().execute(SERVER_URL);

                }
            });

            ratingPopup.build();
        }
        return false;
    }



    private class LongOperation extends AsyncTask<String, Void, Void> {
        private String jsonResponse;
        private ProgressDialog dialog = new ProgressDialog(MapsActivity.this);

        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();
        }

        protected Void doInBackground(String... urls) {

            try {
                // STEP1. Create a HttpURLConnectionobject releasing REQUEST to given site
                URL url = new URL(urls[0]);  //argument supplied in the call to AsyncTask
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("User-Agent", "");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                // STEP2. wait for incoming RESPONSE stream, place data in a buffer
                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                // STEP3. Arriving JSON fragments are concatenate into a StringBuilder
                String myLine = "";
                StringBuilder strBuilder = new StringBuilder();
                while ((myLine = responseBuffer.readLine()) != null) {
                    strBuilder.append(myLine);
                }

                //show response (JSON encoded data)
                jsonResponse = strBuilder.toString();
                Log.e("RESPONSE", jsonResponse);

            } catch (Exception e) {
                Log.e("RESPONSE Error", e.getMessage());
            }
            return null; // needed to gracefully terminate Void method

        }//end doInBackground


        protected void onPostExecute(Void unused){

            try{
                dialog.dismiss();
            }catch(JsonSyntaxException e){
                Log.e("POST-Execute", e.getMessage());
            }
        }// /onPostExecute
    }// /asynchTask

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode == SAVE_PLACE_CODE && resultCode == Activity.RESULT_OK){
                Toast.makeText(getApplicationContext(), "place inserted into the db!", Toast.LENGTH_LONG).show();
            }

            if((requestCode == EXPLORE_CODE && resultCode == Activity.RESULT_OK) ||
                    (requestCode == EXPLORE_LOCAL_CODE && resultCode == Activity.RESULT_OK) ){

                int numberVotess = data.getIntExtra("numberVotes",0);
                int sumVotess = data.getIntExtra("sumVotes", 0);
                String tmp = numberVotess + " " + sumVotess + " " + (double)sumVotess/numberVotess;


                String username = data.getStringExtra("username");
                String description = data.getStringExtra("description");
                double latitude = data.getDoubleExtra("latitude", 0.0);
                double longitude = data.getDoubleExtra("longitude", 0.0);
                String date = data.getStringExtra("date");
                int numberVotes = data.getIntExtra("numberVotes",0);
                int sumVotes = data.getIntExtra("sumVotes", 0);

                com.example.androidMapApp.Location l = new com.example.androidMapApp.Location(username, description, date, latitude, longitude,
                        numberVotes, sumVotes);

                LatLng coord = new LatLng(latitude, longitude);

                if(myMarker != null) {
                    com.example.androidMapApp.Location loc = (com.example.androidMapApp.Location)myMarker.getTag();
                    double lat = loc.latitude;
                    double lon = loc.longitude;

                    if(latitude == lat && longitude == lon) {
                        myMarker.remove();
                        myMarker = null;
                    }
                }

                myMarker = mMap.addMarker(new MarkerOptions().position(coord).
                        title("choosed by: " + username).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).
                        snippet(description));
                myMarker.setTag(l);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 8));
            }


            if (requestCode == SYNCHRONIZE_CODE && resultCode == Activity.RESULT_OK){
                Toast.makeText(getApplicationContext(), "the synchronisation was successful", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "problem/ request code: " + requestCode +
                    "result code:" + resultCode, Toast.LENGTH_LONG).show();
        }
    }


    //responding to click events on info window (marker info window)
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getApplicationContext(), "click on info window for marker: " + marker.getTitle(), Toast.LENGTH_LONG).show();
    }

    //handling clicks on POI (points of interest)
    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(getApplicationContext(), "POI Clicked: " + poi.name + "\nPlace ID:" + poi.placeId + "\nLatitude:" +
                poi.latLng.latitude + " Longitude:" + poi.latLng.longitude, Toast.LENGTH_LONG).show();
    }


    //handling clicks on the map
    @Override
    public void onMapLongClick(LatLng latLng) {

        final double latitude = latLng.latitude;
        final double longitude = latLng.longitude;

        //getting the name and desc of the location to insert into the database
        final CostumPopup popup = new CostumPopup(MapsActivity.this);
        popup.setTitle("whats here ? ");
        popup.setUsernameHint("your name : ");
        popup.setDescritpionHint("location description : ");
        popup.setNegativeButtonText("cancel");
        popup.setPositiveButtonText("save");



        popup.getSaveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = popup.getUsernameEditText().getText().toString();
                String description = popup.getDescriptionEditText().getText().toString();

                Intent i = new Intent(MapsActivity.this, InsertIntoOnlineDbActivity.class);
                i.putExtra("latitude", latitude);
                i.putExtra("longitude", longitude);
                i.putExtra("username", username);
                i.putExtra("description", description);
//                startActivity(i);
                startActivityForResult(i, SAVE_PLACE_CODE);
                popup.dismiss();

            }
        });

        popup.getCancelButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        popup.build();
    }
    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(getApplicationContext(), "short click\nLocation: " + latLng.toString(), Toast.LENGTH_LONG).show();
    }



    //handling showing user current location
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    //handling search queries on the search view
    @Override
    public boolean onQueryTextSubmit(String query) {
        List<Address> addressList = null;

        if(!query.equals("")){

            Geocoder geocoder = new Geocoder(getApplicationContext());

            try {
                addressList = geocoder.getFromLocationName(query, 1);

            }catch (IOException e){
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            Marker m;
            m = mMap.addMarker(new MarkerOptions().title(query).position(latLng));
            m.setTag(187);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4));

        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
