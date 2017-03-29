package com.nemwick.coffeetrack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class SessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_session));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
