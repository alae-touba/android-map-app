package com.example.androidMapApp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.googlemapapi.R;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class InsertIntoOnlineDbActivity extends AppCompatActivity {

    TextView txtRequestUrl, txtResponseJson;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_into_online_db);


        i = getIntent();
        Bundle b = i.getExtras();

        String username = b.getString("username");
        String description = b.getString("description").replace(" ", "+");
        double latitude = b.getDouble("latitude");
        double longitude = b.getDouble("longitude");

        txtRequestUrl = findViewById(R.id.txtRequestUrl);
        txtResponseJson = findViewById(R.id.txtResponseJson);

        // REQUEST object consists of "URL?ARGUMENTS" (spaces replaced by +)
        // each argument is a KEY=VALUE pair.
        String SERVER_URL = "https://androidapp2020.000webhostapp.com/insert_locations.php?username=" +
                username +"&description=" + description + "&latitude=" + latitude + "&longitude=" + longitude;

        txtRequestUrl.setText(new Date() + "\n" + SERVER_URL);

        // Use AsyncTask to execute potential slow task without freezing GUI
        new LongOperation().execute(SERVER_URL);

    }

    private class LongOperation extends AsyncTask<String, Void, Void> {
        private String jsonResponse;
        private ProgressDialog dialog = new ProgressDialog(InsertIntoOnlineDbActivity.this);

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

//                 update GUI with JSON Response
                txtResponseJson.setText(jsonResponse);

                setResult(Activity.RESULT_OK, i);
                finish();

            }catch(JsonSyntaxException e){
                Log.e("POST-Execute", e.getMessage());
            }
        }// /onPostExecute
    }// /asynchTask
}
