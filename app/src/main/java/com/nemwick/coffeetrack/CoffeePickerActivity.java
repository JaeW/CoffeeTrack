package com.nemwick.coffeetrack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

public class CoffeePickerActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
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
}
