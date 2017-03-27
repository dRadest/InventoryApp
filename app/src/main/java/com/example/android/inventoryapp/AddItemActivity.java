package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static android.app.Activity.RESULT_OK;

/**
 * Class for activity to add new item to the database
 */

public class AddItemActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddItemActivity.class.getName();

    // View we'll be using to enter item into database
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mNameEditText;
    private EditText mWebEditText;
    private EditText mEmailEditText;
    private TextView mPictureTextView;
    private ImageView mPictureImageView;

    // request code for intent to take a picture
    static final int REQUEST_IMAGE_CAPTURE = 1;


    // bitmap variable for captured image
    private Bitmap mBitmapImage;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_activity);

        // find views
        mPriceEditText = (EditText) findViewById(R.id.price_edit_text);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
        mWebEditText = (EditText) findViewById(R.id.web_edit_text);
        mEmailEditText = (EditText) findViewById(R.id.email_edit_text);
        mPictureTextView = (TextView) findViewById(R.id.add_picture_textview);
        mPictureImageView = (ImageView) findViewById(R.id.preview_imageview);

        mPictureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                saveItem();
                finish();
                return true;
            case R.id.action_delete:
                clearAllFields();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // helper method to save item
    private void saveItem(){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        // get values from views
        double priceDouble = Double.parseDouble(mPriceEditText.getText().toString().trim());
        priceDouble = Double.parseDouble(df.format(priceDouble));
        int priceInt = (int) (priceDouble * 100);

        int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
        String name = mNameEditText.getText().toString().trim();
        String web = mWebEditText.getText().toString().trim();
        String email = mEmailEditText.getText().toString().trim();

        // convert bitmap to byte array
        byte[] byteArray = {};
        if (mBitmapImage != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mBitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        }


        // create content values
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, priceInt);
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, quantity);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, name);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_WEB, web);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL, email);
        values.put(InventoryContract.InventoryEntry.COLUMN_PICTURE, byteArray);

        // insert it into items table
        getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
    }

    // helper method to clear all fields
    private void clearAllFields(){
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mNameEditText.setText("");
        mWebEditText.setText("");
        mEmailEditText.setText("");
        mPictureImageView.setImageResource(R.drawable.no_img);
        mBitmapImage = null;

    }

    // testing capturing an image
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            mBitmapImage = (Bitmap) extras.get("data");
            // set image on test image view
            mPictureImageView.setImageBitmap(mBitmapImage);
        }
    }
}
