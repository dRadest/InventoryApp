package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import static android.view.View.GONE;

public class OverallActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "OverallActivity";
    // context of this activity
    private Context mContext = OverallActivity.this;
    // name of the preference that stores boolean mGridVisible
    private static final String VIEW_PREF_NAME = "ViewPrefs";
    // preference key
    private static final String VIEW_PREF_KEY = "gridVisible";

    // identifier for the loader
    private static final int ITEM_LOADER = 0;

    // cursor adapters
    private InventoryCursorAdapter listInventoryAdapter;
    private InventoryCursorAdapter gridInventoryAdapter;

    // variable for the grid view
    private GridView mGridView;

    // variable for the list view
    private ListView mListView;

    // variable indicating if grid view is visible
    public boolean mGridVisible;

    // view stubs for list and grid views
    private ViewStub mListViewStub, mGridViewStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overall_activity);

        // setup fab to start detail activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemIntent = new Intent(OverallActivity.this, AddItemActivity.class);
                startActivity(addItemIntent);
            }
        });

        // find view stubs
        mListViewStub = (ViewStub) findViewById(R.id.list_viewstub);
        mGridViewStub = (ViewStub) findViewById(R.id.grid_viewstub);
        // inflate view stubs
        mListViewStub.inflate();
        mGridViewStub.inflate();

        // find list view of items
        mListView = (ListView) findViewById(R.id.list_view);
        mGridView = (GridView) findViewById(R.id.grid_view);

        // find and set empty view on list view
        View emptyView = findViewById(R.id.empty_view);
        mListView.setEmptyView(emptyView);
        mGridView.setEmptyView(emptyView);

        // initialize the adapters
        listInventoryAdapter = new InventoryCursorAdapter(this, null, false);
        gridInventoryAdapter = new InventoryCursorAdapter(this, null, true);

        // prepare loader
        getLoaderManager().initLoader(ITEM_LOADER, null, this);

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(VIEW_PREF_NAME, 0);
        mGridVisible = settings.getBoolean(VIEW_PREF_KEY, false);
    }

    // helper method to change the views
    private void changeViews(){
        if (mGridVisible){
            mGridViewStub.setVisibility(View.VISIBLE);
            mListViewStub.setVisibility(View.GONE);
        } else{
            mGridViewStub.setVisibility(View.GONE);
            mListViewStub.setVisibility(View.VISIBLE);
        }
        setAdapter();
    }

    // helper method to set the adapter
    private void setAdapter(){
        if (mGridVisible){
            mGridView.setAdapter(gridInventoryAdapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // send uri of the clicked item with the intent
                    Intent intent = new Intent(OverallActivity.this, DetailActivity.class);
                    Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                    intent.setData(currentItemUri);
                    startActivity(intent);
                }
            });
        } else {
            mListView.setAdapter(listInventoryAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // send uri of the clicked item with the intent
                    Intent intent = new Intent(OverallActivity.this, DetailActivity.class);
                    Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                    intent.setData(currentItemUri);
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        changeViews();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(VIEW_PREF_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(VIEW_PREF_KEY, mGridVisible);

        // Commit the edits!
        editor.apply();

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
                displayClearAlertDialog();
                return true;
            // Respond to a click on the "Change view" menu option
            case R.id.change_view:
                mGridVisible = !mGridVisible;
                changeViews();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // helper method to insert dummy item
    public void insertDummyItem(){
        // convert bitmap to byte array
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.hammer);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // create content values
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRICE, 55);
        values.put(InventoryEntry.COLUMN_QUANTITY, 20);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, getString(R.string.dummy_supplier_name));
        values.put(InventoryEntry.COLUMN_SUPPLIER_WEB, getString(R.string.dummy_supplier_web));
        values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, getString(R.string.dummy_supplier_email));
        values.put(InventoryEntry.COLUMN_PICTURE, byteArray);

        // insert it into items table
        getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // which columns to use in a query (all)
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_WEB,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryEntry.COLUMN_PICTURE
        };

        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // swap the new cursor in
        gridInventoryAdapter.swapCursor(data);
        listInventoryAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // no longer using old cursor
        gridInventoryAdapter.swapCursor(null);
        listInventoryAdapter.swapCursor(null);

    }
    
    // helper method to display alert dialog when clear all is chosen
    private void displayClearAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.dialog_clear_title);
        builder.setMessage(R.string.dialog_clear_msg);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
                if (rowsDeleted > 0){
                    Toast.makeText(getApplicationContext(), "Items deleted: " + rowsDeleted, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else{
                    dialog.dismiss();
                }

            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
                dialog.dismiss();
            }
        });
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
