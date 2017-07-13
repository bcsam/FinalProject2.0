package com.codepath.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity { // TODO: 7/12/17 make the app work if the device is turned sideways 

    RecyclerView rvText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvText = (RecyclerView) findViewById(R.id.rvText);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onProfileView(MenuItem item) {
        //launches the profile view
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }
}

