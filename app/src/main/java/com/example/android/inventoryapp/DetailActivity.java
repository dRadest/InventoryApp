package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static android.R.attr.name;
import static android.R.id.message;

/**
 * Detail activity to view individual item details.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    // context of this activity
    private Context mContext = DetailActivity.this;

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

    // buttons to edit item
    private Button mTrackButton;
    private Button mOrderButton;
    private Button mDeleteButton;

    // edit text view of the alert dialog
    private EditText mEditText;

    // variable to check if we're selling or ordering the item
    private boolean mSellingItem = false;


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

        // find buttons
        mTrackButton = (Button) findViewById(R.id.button_track);
        mOrderButton = (Button) findViewById(R.id.button_order);
        mDeleteButton = (Button) findViewById(R.id.button_delete);


        // set OnItemClickListener to the buttons
        mTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSellingItem = true;
                displayUpdateAlertDialog(mSellingItem);
            }
        });

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSellingItem = false;
                displayUpdateAlertDialog(mSellingItem);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDeleteAlertDialog();
            }
        });

        // set click listeners on web and email views
        mWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String web = mWebView.getText().toString().trim();
                if (web.equals(getString(R.string.web_not_provided))){
                    Toast.makeText(getApplicationContext(), "No URL to open", Toast.LENGTH_SHORT).show();
                } else {
                    // preform a web search for given web address
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    intent.putExtra(SearchManager.QUERY, web);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });
        mEmailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailView.getText().toString().trim();
                if (email.equals(getString(R.string.email_not_provided))){
                    Toast.makeText(getApplicationContext(), "No email address to send to", Toast.LENGTH_SHORT).show();
                } else {
                    // Send the email message to provided email address
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + email)); // only email apps should handle this

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });
        mNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayEditNameAlertDialog();
            }
        });
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
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            String web = cursor.getString(webColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            byte[] imageByteArray = cursor.getBlob(imageColumnIndex);
            if (imageByteArray.length > 1){
                Bitmap itemImage = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                mImageView.setImageBitmap(itemImage);
            }

            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.HALF_EVEN);
            String fItemPrice = df.format(price/100);


            // Update the views on the screen with the values from the database
            mPriceView.setText(fItemPrice + " $");
            mQuantityView.setText(Integer.toString(quantity));
            mNameView.setText(name);
            // if there's no web address provided, set the predefined text
            if (TextUtils.isEmpty(web)){
                mWebView.setText(getString(R.string.web_not_provided));
            } else{
                mWebView.setText(web);
            }
            // if there's no email address provided, set the predefined text
            if (TextUtils.isEmpty(email)){
                mEmailView.setText(getString(R.string.email_not_provided));
            } else{
                mEmailView.setText(email);
            }

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

    // helper method to update item quantity
    private void updateQuantity(int enteredQuantity){
        // variable used for calculating new quantity to update
        int newQuantity;
        // first we query the db for current quantity
        String[] projection = {InventoryEntry._ID,
                                InventoryEntry.COLUMN_QUANTITY};
        Cursor cursor = getContentResolver().query(mCurrentItemUri, projection, null, null, null);

        // bail early if the returned cursosr is null
        if (cursor == null){
            return;
        }
        // move cursor to the first item (which should be the only one)
        cursor.moveToFirst();
        int currentQuantity = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY));
        cursor.close();
        ContentValues values = new ContentValues();
        // if we are ordering, quantity increases
        if (!mSellingItem){
            newQuantity = currentQuantity + enteredQuantity;
            values.put(InventoryEntry.COLUMN_QUANTITY, newQuantity);
        } else{ // we're selling, quantity decreases
            newQuantity = currentQuantity - enteredQuantity;
            if (newQuantity >= 0){
                values.put(InventoryEntry.COLUMN_QUANTITY, newQuantity);
            } else{
                Toast.makeText(mContext, "Cannot sell more than you have", Toast.LENGTH_SHORT).show();
            }
        }
        getContentResolver().update(mCurrentItemUri, values, null, null);
    }

    // helper function to display alert dialog when updating the database
    private void displayUpdateAlertDialog(boolean sell){
        final View dialogLayout = getLayoutInflater().inflate(R.layout.custom_alertdialog, null);
        mEditText = (EditText) dialogLayout.findViewById(R.id.cad_edittext);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // set title and hint if we're selling or ordering
        if (sell){
            builder.setTitle(R.string.dialog_sell_title);
            mEditText.setHint(R.string.dialog_sell_item_hint);
        } else {
            builder.setTitle(R.string.dialog_order_title);
            mEditText.setHint(R.string.dialog_order_item_hint);
        }
        builder.setView(dialogLayout);
        builder.setPositiveButton(R.string.dialog_submit_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredAmount = mEditText.getText().toString().trim();
                if (TextUtils.isEmpty(enteredAmount)){
                    dialog.dismiss();
                } else {
                    int enteredQuantity = Integer.parseInt(enteredAmount);
                    updateQuantity(enteredQuantity);
                    dialog.dismiss();
                }

            }
        });
        builder.setNegativeButton(R.string.dialog_discard_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
    // helper function to display alert dialog when delete button is clicked
    private void displayDeleteAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.dialog_delete_title);
                builder.setMessage(R.string.dialog_delete_msg);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
                        if (rowsDeleted > 0){
                            Toast.makeText(mContext, "Item deleted from database", Toast.LENGTH_SHORT).show();
                            finish();
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
    // helper function to display alert dialog when updating the database
    private void displayEditNameAlertDialog(){
        final View dialogLayout = getLayoutInflater().inflate(R.layout.custom_alertdialog, null);
        mEditText = (EditText) dialogLayout.findViewById(R.id.cad_edittext);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // set title and hint
        builder.setTitle(R.string.dialog_edit_title);
        mEditText.setHint(R.string.dialog_edit_hint);

        // change input type of edit text
        mEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        builder.setView(dialogLayout);
        builder.setPositiveButton(R.string.dialog_submit_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredName = mEditText.getText().toString().trim();
                if (TextUtils.isEmpty(enteredName)){
                    dialog.dismiss();
                } else {
                    updateName(enteredName);
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_discard_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
    // helper method to update supplier's name
    private void updateName(String name){
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, name);
        int updatedRows = getContentResolver().update(mCurrentItemUri, values, null, null);
        if (updatedRows < 0){
            Toast.makeText(mContext, "Error changing name", Toast.LENGTH_SHORT).show();
        }
    }
}
