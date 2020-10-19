package com.example.androidMapApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.example.googlemapapi.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ExploreOtherLocationsActivity extends AppCompatActivity {
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_other_locations);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        i = getIntent();

        BroadcastReceiver broadcast_reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();

                String username = intent.getStringExtra("username");
                String description = intent.getStringExtra("description");
                double latitude = intent.getDoubleExtra("latitude", 0.0);
                double longitude = intent.getDoubleExtra("longitude", 0.0);
                String date = intent.getStringExtra("date");
                int numberVotes = intent.getIntExtra("numberVotes", 0);
                int sumVotes = intent.getIntExtra("sumVotes", 0);


                if (action.equals("finish")) {

                    i.putExtra("username", username);
                    i.putExtra("description", description);
                    i.putExtra("latitude", latitude);
                    i.putExtra("longitude", longitude);
                    i.putExtra("date", date);
                    i.putExtra("numberVotes", numberVotes);
                    i.putExtra("sumVotes", sumVotes);


                    ExploreOtherLocationsActivity.this.setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        };
        registerReceiver(broadcast_reciever, new IntentFilter("finish"));

        // REQUEST object consists of "URL?ARGUMENTS" (spaces replaced by +)
        // each argument is a KEY=VALUE pair.
        String SERVER_URL = "https://androidapp2020.000webhostapp.com/get_all_locations.php";

        // Use AsyncTask to execute potential slow task without freezing GUI
        new LongOperation().execute(SERVER_URL);

    }// /onCreate




    private class LongOperation extends AsyncTask<String, Void, Void> {
        private String jsonResponse;
        private ProgressDialog dialog = new ProgressDialog(ExploreOtherLocationsActivity.this);

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

                // Convert JSON list into a Java collection of Person objects
                Gson gson = new Gson();
                Log.e("PostExecute", "content: "+ jsonResponse);

                Type listType = new TypeToken<ArrayList<Location>>() { }.getType();
                Log.e("PostExecute", "arrayType: "+ listType.toString());

                ArrayList<Location> locationList = gson.fromJson(jsonResponse, listType);
                Log.e("PostExecute", "OutputData: "+ locationList.toString());

                ListView listView = findViewById(R.id.list_view);
                listView.setAdapter(new LocationAdapter(ExploreOtherLocationsActivity.this, locationList) );

            }catch(JsonSyntaxException e){
                Log.e("POST-Execute", e.getMessage());
            }
        }// /onPostExecute
    }// /asynchTask


}// /activity
