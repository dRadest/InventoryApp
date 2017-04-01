package com.example.android.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static android.R.attr.id;

/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of inventory data as its data source. This adapter knows
 * how to create list items for each row of inventory data in the {@link Cursor}.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    // varianle to indicate whether we're displaying grid or list
    private boolean gridDisplayed;

    // variable to hold context
    private Context mContext;

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c, boolean displayed) {
        super(context, c, 0 /* flags */);
        gridDisplayed = displayed;
        this.mContext = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if (gridDisplayed){
            // Inflate a grid item view using the layout specified in grid_item.xml
            return LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        } else {
            // Inflate a list item view using the layout specified in list_item.xml
            return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }
    }

    /**
     * This method binds the inventory data (in the current row pointed to by cursor) to the given
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (gridDisplayed){
            // find grid view items
            TextView nameTextView = (TextView) view.findViewById(R.id.grid_item_textview);
            ImageView itemImageView = (ImageView) view.findViewById(R.id.grid_item_imageView);
            // get column indicies for name and image
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PICTURE);
            // get the name and image at those indicies
            String supName = cursor.getString(nameColumnIndex);
            byte[] imageByteArray = cursor.getBlob(imageColumnIndex);
            if (imageByteArray != null && imageByteArray.length > 2) {
                Bitmap itemImage = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                // set the image on the view
                itemImageView.setImageBitmap(itemImage);
            }
            // set the name on the view
            nameTextView.setText(supName);
        } else {
            // Find individual views that we want to modify in the list item layout
            TextView nameTextView = (TextView) view.findViewById(R.id.list_item_name);
            TextView summaryTextView = (TextView) view.findViewById(R.id.list_item_summary);
            ImageView itemImageView = (ImageView) view.findViewById(R.id.list_item_image);

            // Find the columns of item attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PICTURE);

            // Read the item attributes from the Cursor for the current pet
            String supName = cursor.getString(nameColumnIndex);
            double itemPrice = cursor.getDouble(priceColumnIndex);
            final int itemQuantity = cursor.getInt(quantityColumnIndex);
            // retrieving an image from database
            byte[] imageByteArray = cursor.getBlob(imageColumnIndex);
            if (imageByteArray != null && imageByteArray.length > 2) {
                Bitmap itemImage = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                itemImageView.setImageBitmap(itemImage);
            }

            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.HALF_EVEN);
            String fItemPrice = df.format(itemPrice / 100);

            // String for summary text view
            String summaryString = itemQuantity + " for " + fItemPrice + " $ each";

            // Update the TextViews with the attributes for the current item
            nameTextView.setText(supName);
            summaryTextView.setText(summaryString);

            // get id from cursor
            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            final int currentItemID = cursor.getInt(idColumnIndex);


            // trying to make sell icon work
            ImageView sellIcon = (ImageView) view.findViewById(R.id.list_item_sell_icon);
            sellIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int decQuantity = itemQuantity;
                    if (decQuantity-1 < 0){
                        Toast.makeText(mContext, "Quantity cannot be negative", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("InventoryCursorAdapter", "decremented quantity: " + decQuantity--);
                        ContentValues values = new ContentValues();
                        values.put(InventoryEntry.COLUMN_QUANTITY, decQuantity--);
                        Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, currentItemID);
                        mContext.getContentResolver().update(currentItemUri, values, null, null);
                    }
                }
            });
        }

    }
}
