package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Class that extends SQLiteOpenHelper class
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    // database version and name
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ItemsInventory.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // sql statement for table creation
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME +
                "(" + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InventoryEntry.COLUMN_PRICE + " INTEGER DEFAULT 0, " +
                InventoryEntry.COLUMN_QUANTITY + " INTEGER DEFAULT 0, " +
                InventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_SUPPLIER_WEB + " TEXT, " +
                InventoryEntry.COLUMN_SUPPLIER_EMAIL + " TEXT, " +
                InventoryEntry.COLUMN_PICTURE + " BLOB)";
        // execute sql statement
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // sql statement for table deletion
        String DELETE_ITEMS_TABLE =
                "DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME;
        // start anew
        db.execSQL(DELETE_ITEMS_TABLE);
        onCreate(db);

    }
}
