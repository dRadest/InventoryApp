package com.example.android.inventoryapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class OverallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overall_activity);

        // setup fab to start detail activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailActivityIntent = new Intent(OverallActivity.this, DetailActivity.class);
                startActivity(detailActivityIntent);
            }
        });

        // find list view of items
        ListView itemListView = (ListView) findViewById(R.id.list_view);

        // find and set empty view on list view
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);


    }
}
