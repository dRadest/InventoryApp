package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryDbHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class OverallActivity extends AppCompatActivity {

    // InventoryDbHelper instance to preform CRUD operations
    private InventoryDbHelper mDbHelper;

    // Cursor to retrieve data from the table
    private Cursor mCursor;

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

        // instanciate mDbHelper
        mDbHelper = new InventoryDbHelper(getApplicationContext());

        // get readable database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // query the database and retrieve cursor
        mCursor = queryDatabase(db);

        // setup cursor adapter
        InventoryCursorAdapter inCursorAdapter = new InventoryCursorAdapter(this, mCursor);

        // find list view of items
        ListView itemListView = (ListView) findViewById(R.id.list_view);

        // find and set empty view on list view
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        // set cursor adapter on list view
        itemListView.setAdapter(inCursorAdapter);

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
                insertDummyItem();
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

    // helper method to insert dummy
    public void insertDummyItem(){
        // get writable database from mDbHelper
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // convert bitmap to byte array
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.hammer);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // create content values
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRICE, 55);
        values.put(InventoryEntry.COLUMN_QUANTITY, 20);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Guma proizvod");
        values.put(InventoryEntry.COLUMN_SUPPLIER_WEB, "www.google.com");
        values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, "mail@me.com");
        values.put(InventoryEntry.COLUMN_PICTURE, byteArray);

        // insert it into items table
        long rowId = db.insert(InventoryEntry.TABLE_NAME, null, values);

        if (rowId == -1){
            Toast.makeText(this, "Error inserting item", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Successfully entered item", Toast.LENGTH_SHORT).show();
        }
    }

    // helper query function
    private Cursor queryDatabase (SQLiteDatabase db) {
        // which columns to use (all)
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_WEB,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryEntry.COLUMN_PICTURE
        };

        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
                );
        return cursor;

    }

}
