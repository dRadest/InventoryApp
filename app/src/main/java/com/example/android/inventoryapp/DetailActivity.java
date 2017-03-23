package com.example.android.inventoryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Detail activity to view individual item details.
 */

public class DetailActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        // receive intent that started this activity
        Intent intent = getIntent();
        Uri currentItemUri = intent.getData();

        Log.v("DetailActivity", "Uri from the received intent: " + currentItemUri);
    }
}
