package com.nemwick.coffeetrack;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class ActivityCoffeePicker extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private static final int MY_PERMISSION_CALL_PHONE = 102;
    public static final int MY_PERMISSION_INTERNET = 103;
    private static final String STATE_ADDY = "coffeeAddress";
    private static final String STATE_PHONE = "coffeePhone";
    private static final java.lang.String STATE_WEBSITE = "coffeeWebsite";
    private FloatingActionButton findCoffeeButton;
    private ImageButton locationButton;
    private ImageButton phoneButton;
    private ImageButton websiteButton;
    private TextView coffeeShopName;
    private TextView coffeeShopAddy;
    private TextView coffeeShopPhone;
    private TextView coffeeShopWebsite;
    private WebView attribution;
    private ImageView imageView;
    private Place place;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.find_coffee_shop:
                    //launch PlacePicker which allows user to choose from list of places nearby
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        Intent intent = builder.build(ActivityCoffeePicker.this);
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.location_button:
                    //navigation/directions to address obtained from PlacePicker
                    if (coffeeShopAddy.getText() != null) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(coffeeShopAddy.getText().toString()));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                    break;
                case R.id.phone_button:
                    //call establishment obtained from PlacePicker
                    requestPhonePermission();
                    if (coffeeShopPhone.getText() != null) {
                        if (ContextCompat.checkSelfPermission(ActivityCoffeePicker.this,
                                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + coffeeShopPhone.getText()));
                            startActivity(intent);
                        }
                    }
                    break;
                case R.id.website_button:
                    //visit website of establishment obtained from PlacePicker
                    if (coffeeShopWebsite.getText() != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(coffeeShopWebsite.getText().toString())));
                        startActivity(intent);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_picker);
        //set toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_picker));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //load coffee image
        imageView = (ImageView) findViewById(R.id.picker_coffee_image);
        initBackgroundImage();

        //initialize UI views
        findCoffeeButton = (FloatingActionButton) findViewById(R.id.find_coffee_shop);
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

        //check to see if user has granted location permission
        requestLocationPermission();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_ADDY, coffeeShopAddy.getText().toString());
        outState.putString(STATE_PHONE, coffeeShopPhone.getText().toString());
        outState.putString(STATE_WEBSITE, coffeeShopWebsite.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        coffeeShopAddy.setText(savedInstanceState.getString(STATE_ADDY));
        coffeeShopPhone.setText(savedInstanceState.getString(STATE_PHONE));
        coffeeShopWebsite.setText(savedInstanceState.getString(STATE_WEBSITE));
        locationButton.setVisibility(View.VISIBLE);
        phoneButton.setVisibility(View.VISIBLE);
        websiteButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            try {
                //get place object from user's choice in PlacePicker
                place = PlacePicker.getPlace(ActivityCoffeePicker.this, data);
                //show hidden imagebuttons
                locationButton.setVisibility(View.VISIBLE);
                phoneButton.setVisibility(View.VISIBLE);
                websiteButton.setVisibility(View.VISIBLE);
                //populate textviews with data from PlacePicker result
                coffeeShopName.setText(place.getName());
                coffeeShopAddy.setText(place.getAddress());
                coffeeShopPhone.setText(place.getPhoneNumber());
                coffeeShopWebsite.setText(place.getWebsiteUri().toString());
            } catch (Exception e) {
                System.out.println("Error " + e.getMessage());
                e.printStackTrace();
            }

            if (place.getAttributions() == null) {
                attribution.loadData("no attribution", "text/html; charset=utf-8", "UFT-8");
            } else {
                attribution.loadData(place.getAttributions().toString(), "text/html; charset=utf-8", "UFT-8");
            }
        }
    }

    private void initBackgroundImage() {
        //use Glide jar to load coffee image
        Glide
                .with(this).load(R.drawable.coffee_pour)
                .into(imageView);
    }

    private void requestLocationPermission() {
        //if SDK >= 23 (Marshmallow) and user has not granted location permission, request it
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_FINE_LOCATION);
            }
        }
    }

    private void requestPhonePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSION_CALL_PHONE);
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
