package com.example.androidMapApp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.googlemapapi.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SynchronizeDatabasesActivity extends AppCompatActivity {
//    TextView txtResponseJson, txtResponseJava;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize_databases);

        i = getIntent();//from MapsActivity

        String SERVER_URL = "https://androidapp2020.000webhostapp.com/get_all_locations.php";

        // Use AsyncTaskto execute potential slow task without freezing GUI
        new LongOperation().execute(SERVER_URL);

    }// /onCreate


    private class LongOperation extends AsyncTask<String, Void, Void> {
        private String jsonResponse;
        private ProgressDialog dialog = new ProgressDialog(SynchronizeDatabasesActivity.this);

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

                // Step4. Convert JSON list into a Java collection of Person objects//
                // prepare to decode JSON response and create Java list
                Gson gson= new Gson();
                Log.e("PostExecute", "content: "+ jsonResponse);

                // set (host) Java type of encoded JSON response
                Type listType = new TypeToken<ArrayList<Location>>() { }.getType();
                Log.e("PostExecute", "arrayType: "+ listType.toString());

                // decode JSON string into appropriate Java container
                ArrayList<Location> locationList = gson.fromJson(jsonResponse, listType);
                Log.e("PostExecute", "OutputData: "+ locationList.toString());

                insertIntoLocalBd(locationList);
            }catch(JsonSyntaxException e){
                Log.e("POST-Execute", e.getMessage());
            }
        }// /onPostExecute
    }// /asynchTask

    private void insertIntoLocalBd(ArrayList<Location> locationList) {
        SQLiteOpenHelper dbManager = new LocalDbManager(this); //getting a reference to the db
        try {
            SQLiteDatabase db = dbManager.getWritableDatabase();

            db.delete("locations", null, null);

            ContentValues c;
            for (Location loc : locationList){
                c = new ContentValues();
                c.put("username", loc.username);
                c.put("description", loc.description);
                c.put("date", loc.date);
                c.put("latitude", loc.latitude);
                c.put("longitude", loc.longitude);
                c.put("number_votes", loc.numberVotes);
                c.put("sum_votes", loc.sumVotes);

                db.insert("locations", null, c);
            }

            db.close();

            setResult(Activity.RESULT_OK, i);
            finish();

        }catch (SQLiteException e){
            Toast.makeText(this, "something went wrong! try later!", Toast.LENGTH_SHORT).show();
        }

    }

}
