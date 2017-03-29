package com.example.android.inventoryapp;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
    private TextView mTakePictureTextView;
    private TextView mAddPictureTextView;
    private ImageView mPictureImageView;

    // request code for intent to take a picture
    static final int REQUEST_IMAGE_CAPTURE = 1;

    // request code for intent to select a picture
    static final int REQUEST_IMAGE_GET = 2;

    // maximum image size
    static final float MAX_IMAGE_SIZE = 300;


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
        mTakePictureTextView = (TextView) findViewById(R.id.take_picture_textview);
        mAddPictureTextView = (TextView) findViewById(R.id.add_picture_textview);
        mPictureImageView = (ImageView) findViewById(R.id.preview_imageview);

        // take a picture when this text view is touched
        mTakePictureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // select a picture from storage when this text view is touched
        mAddPictureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchSelectPictureIntent();
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
        String priceString = mPriceEditText.getText().toString().trim();
        // if there's no price entered, bail early
        if (TextUtils.isEmpty(priceString)){
            return;
        }
        double priceDouble = Double.parseDouble(priceString);
        priceDouble = Double.parseDouble(df.format(priceDouble));
        int priceInt = (int) (priceDouble * 100);

        int quantity = 0;
        String quantityString = mQuantityEditText.getText().toString().trim();
        // if there's no quantity entered, it remains 0
        if (!TextUtils.isEmpty(quantityString)){
            quantity = Integer.parseInt(quantityString);
        }

        String name = mNameEditText.getText().toString().trim();
        // if there's no name entered, bail early
        if (TextUtils.isEmpty(name)){
            return;
        }
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

    // dispatch intent to take a photo
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // dispatch intent to select a photo from storage
    private void dispatchSelectPictureIntent(){
        Intent selectPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectPictureIntent.setType("image/*");
        if (selectPictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(selectPictureIntent, REQUEST_IMAGE_GET);
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
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            Uri fullPhotoUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fullPhotoUri);
                Bitmap scaledBitmap = scaleDown(bitmap, MAX_IMAGE_SIZE, true);
                mBitmapImage = scaledBitmap;
                mPictureImageView.setImageBitmap(mBitmapImage);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "I didn't work", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // helper method to scale down bitmap
    private static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                    boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}
