package com.nemwick.coffeetrack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class CoffeeStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_stats);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_statistics));
    }
}
