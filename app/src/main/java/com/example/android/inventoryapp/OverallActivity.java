package com.example.android.inventoryapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.overall_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.insert_overall_menu_item:
                return true;
            // Respond to a click on the "Clear all" menu option
            case R.id.clear_overall_menu_item:
                return true;
            // Respond to a click on the "Change view" menu option
            case R.id.change_view:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
