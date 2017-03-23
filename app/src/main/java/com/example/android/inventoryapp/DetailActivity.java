package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import static android.R.attr.name;

/**
 * Detail activity to view individual item details.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    // views we'll need to set text to
    private TextView mPriceView;
    private TextView mQuantityView;
    private TextView mNameView;
    private TextView mWebView;
    private TextView mEmailView;
    private ImageView mImageView;

    // identifier of the existing loader
    private static final int EXISTING_ITEM_LOADER = 0;

    // uri of the current item
    private Uri mCurrentItemUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        // receive intent that started this activity
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // find views we need
        mPriceView = (TextView) findViewById(R.id.item_price_textview);
        mQuantityView = (TextView) findViewById(R.id.item_quantity_textview);
        mNameView = (TextView) findViewById(R.id.supplier_name_textview);
        mWebView = (TextView) findViewById(R.id.supplier_web_textview);
        mEmailView = (TextView) findViewById(R.id.supplier_email_textview);
        mImageView = (ImageView) findViewById(R.id.item_imageview);

        // prepare loader
        getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
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
                mCurrentItemUri,  // query the content uri for the current item
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of item attributes that we're interested in
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int webColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_WEB);
            int emailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PICTURE);

            // Extract out the value from the Cursor for the given column index
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            String web = cursor.getString(webColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            byte[] imageByteArray = cursor.getBlob(imageColumnIndex);
            Bitmap itemImage = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);


            // Update the views on the screen with the values from the database
            mPriceView.setText(Integer.toString(price));
            mQuantityView.setText(Integer.toString(quantity));
            mNameView.setText(name);
            mWebView.setText(web);
            mEmailView.setText(email);
            mImageView.setImageBitmap(itemImage);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the text fields
        mPriceView.setText("");
        mQuantityView.setText("");
        mNameView.setText("");
        mWebView.setText("");
        mEmailView.setText("");
        mImageView.setImageResource(R.drawable.no_img);

    }
}
