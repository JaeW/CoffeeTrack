package com.nemwick.coffeetrack;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class CoffeePickerActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private Button findCoffeeButton;
    private ImageButton locationButton;
    private ImageButton phoneButton;
    private ImageButton websiteButton;
    private TextView coffeeShopName;
    private TextView coffeeShopAddy;
    private TextView coffeeShopPhone;
    private TextView coffeeShopWebsite;
    private WebView attribution;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.find_coffee_shop:
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        Intent intent = builder.build(CoffeePickerActivity.this);
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    return;
                case R.id.location_button:
                    //TODO:  launch google Map navigation
                    return;
                case R.id.phone_button:
                    //TODO;  dial phone number
                    return;
                case R.id.website_button:
                    //TODO:  view website
                    return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_picker);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_picker));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findCoffeeButton = (Button) findViewById(R.id.find_coffee_shop);
        locationButton = (ImageButton) findViewById(R.id.location_button);
        phoneButton = (ImageButton) findViewById(R.id.phone_button);
        websiteButton = (ImageButton) findViewById(R.id.website_button);
        coffeeShopName = (TextView) findViewById(R.id.coffee_shop_name);
        coffeeShopAddy = (TextView) findViewById(R.id.coffee_shop_addy);
        coffeeShopPhone = (TextView) findViewById(R.id.coffee_shop_phone);
        coffeeShopWebsite = (TextView) findViewById(R.id.coffee_shop_website);
        attribution = (WebView) findViewById(R.id.attributions);

        //set onClickListener on buttons
        findCoffeeButton.setOnClickListener(onClickListener);
        locationButton.setOnClickListener(onClickListener);
        phoneButton.setOnClickListener(onClickListener);
        websiteButton.setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {

            Place place = PlacePicker.getPlace(CoffeePickerActivity.this, data);
            coffeeShopName.setText(place.getName());
            coffeeShopAddy.setText(place.getAddress());

            if (place.getAttributions() == null) {
                attribution.loadData("no attribution", "text/html; charset=utf-8", "UFT-8");
            } else {
                attribution.loadData(place.getAttributions().toString(), "text/html; charset=utf-8", "UFT-8");
            }
        }
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

}
