package com.example.androidMapApp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.googlemapapi.R;

import java.util.ArrayList;

public class SeeLocalDbContentActivity extends AppCompatActivity {

    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_local_db_content);

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

                    SeeLocalDbContentActivity.this.setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        };
        registerReceiver(broadcast_reciever, new IntentFilter("finish"));


        SQLiteOpenHelper dbManager = new LocalDbManager(this); //getting a reference to the db

        try {
            //read only!! (getWritableDatabase() to read & write)
            SQLiteDatabase db = dbManager.getReadableDatabase();

            //getting all the records from the table
            Cursor cursor = db.query("locations", new String[] {"username", "description", "latitude", "longitude", "date",
                    "number_votes", "sum_votes"},
                    null, null, null, null, null);


            ArrayList<Location> locationList = new ArrayList<>();


            //read all the data that in the cursor
            while (cursor.moveToNext()) {
                String username = cursor.getString(0);
                String description = cursor.getString(1);
                double latitude = cursor.getDouble(2);
                double longitude = cursor.getDouble(3);
                String date = cursor.getString(4);
                int numberVotes = cursor.getInt(5);
                int sumVotes = cursor.getInt(6);

                locationList.add(new Location(username, description, date, latitude, longitude, numberVotes, sumVotes));
            }

            ListView listView = findViewById(R.id.list_view);
            listView.setAdapter(new LocationAdapter(SeeLocalDbContentActivity.this,locationList ) );

            cursor.close();
            db.close();


        }catch(SQLiteException e){
            Toast.makeText(this, "something went wrong!!!!!!", Toast.LENGTH_SHORT).show();
        }
    }
}
