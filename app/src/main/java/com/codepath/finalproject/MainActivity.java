package com.codepath.finalproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Button btCheck = (Button) findViewById(btCheck);

    }

   /*
    btCheck.setOnClickListener(new View.OnClickListener(){
        public void onClick (View v){
            Intent intent = new Intent(context, PostCheckActivity.class);
            context.startActivity(intent);
        }
    });
    */
}
