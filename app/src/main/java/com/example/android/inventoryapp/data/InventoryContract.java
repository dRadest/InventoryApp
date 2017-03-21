package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Contract for Inventory app
 */

public class InventoryContract {

    // private constructor
    private InventoryContract(){}

    public static class InventoryEntry implements BaseColumns {

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
