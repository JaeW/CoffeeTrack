package com.nemwick.coffeetrack;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class CoffeePickerActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private Button findCoffeeBtn;
    private TextView coffeeShopName;
    private TextView coffeeShopAddy;
    private WebView attribution;

    private View.OnClickListener findCoffeeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                Intent intent = builder.build(CoffeePickerActivity.this);
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_picker);

        findCoffeeBtn = (Button) findViewById(R.id.find_coffee_shop);
        coffeeShopName = (TextView) findViewById(R.id.coffee_shop_name);
        coffeeShopAddy = (TextView) findViewById(R.id.coffee_shop_addy);
        attribution = (WebView) findViewById(R.id.attributions);

        //set onClickListener on findCoffee button
        findCoffeeBtn.setOnClickListener(findCoffeeClickListener);
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
