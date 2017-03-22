package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for Inventory app
 */

public class InventoryContract {

    // content authority for content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    // base uri for all uris apps will use to contact content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // path to items table
    public static final String PATH_ITEMS = "items";

    // private constructor
    private InventoryContract(){}

    public static class InventoryEntry implements BaseColumns{

        // MIME type of list of items
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        // MIME type of a single row
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        // content uri to access item data in content provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        // strings for table and column names
        public static final String TABLE_NAME = "items";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "sup_name";
        public static final String COLUMN_SUPPLIER_WEB = "sup_web";
        public static final String COLUMN_SUPPLIER_EMAIL = "sup_email";
        public static final String COLUMN_PICTURE = "picture";
    }
}
