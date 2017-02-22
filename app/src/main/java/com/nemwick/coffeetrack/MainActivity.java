package com.nemwick.coffeetrack;

import android.content.ContentValues;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.nemwick.coffeetrack.data.CoffeeContract;

public class MainActivity extends AppCompatActivity {

    private Uri lastAddedCoffeeUri; //TODO:  consider adding to saved preferences instead of variable
    private FloatingActionButton fab;
    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addNewCoffee();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabClickListener);
    }

    private void addNewCoffee() {
        Long timeStamp = System.currentTimeMillis()/1000;
        ContentValues values = new ContentValues();
        values.put(CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME, timeStamp);
        lastAddedCoffeeUri = getContentResolver().insert(CoffeeContract.CoffeeEntry.CONTENT_URI, values);
        if(lastAddedCoffeeUri == null){
            Toast.makeText(this, "Coffee was not added to db", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "Success!  Coffee added to db", Toast.LENGTH_SHORT).show();
        }
    }


}
