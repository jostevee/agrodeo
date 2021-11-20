package com.ipb.agrodeo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MarketDetailActivity extends AppCompatActivity {
    AppCompatTextView product_name, product_weight, product_price, product_location, contact_number;
    AppCompatImageView product_image;
    String productName, productLocation, contactNumber, imageUrl;
    int productPrice, productWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail);
        product_name = findViewById(R.id.product_name_detail);
        product_weight = findViewById(R.id.product_weight_detail);
        product_price = findViewById(R.id.product_price_detail);
        product_location = findViewById(R.id.product_location_detail);
        contact_number = findViewById(R.id.contact_number_detail);
        product_image = findViewById(R.id.product_image_detail);

        // Toolbar setup
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.product_detail);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            productName = extras.getString("product_name");
            productPrice = extras.getInt("product_price");
            productWeight = extras.getInt("product_weight");
            productLocation = extras.getString("product_location");
            contactNumber = extras.getString("contact_number");
            imageUrl = extras.getString("image_url");

            Log.d("MarketDetail", productName);
            Log.d("MarketDetail", String.valueOf(productPrice));
            Log.d("MarketDetail", String.valueOf(productWeight));
            Log.d("MarketDetail", productLocation);
            Log.d("MarketDetail", contactNumber);
            Log.d("MarketDetail", imageUrl);

            product_name.setText(productName);
            product_price.setText(getString(R.string.tv_product_price_detail, productPrice));
            product_weight.setText(getString(R.string.tv_product_weight_detail, productWeight));
            product_location.setText(productLocation);
            contact_number.setText(contactNumber);
            Picasso.with(getApplicationContext())
                    .load(imageUrl)
                    .into(product_image);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            // NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}