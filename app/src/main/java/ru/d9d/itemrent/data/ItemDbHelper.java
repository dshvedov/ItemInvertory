package ru.d9d.itemrent.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.d9d.itemrent.data.ItemContract.ItemEntry;

public class ItemDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ItemDbHelper.class.getSimpleName();

    //Name of the database file
    private static final String DATABASE_NAME = "rent.db";

    // Database version
    private static final int DATABASE_VERSION = 1;

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Database initialization.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 1,"
                + ItemEntry.COLUMN_ITEM_SUPPLIER_NAME + " TEXT, "
                + ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE + " TEXT, "
                + ItemEntry.COLUMN_ITEM_PURCHASE_PRICE + " REAL, "
                + ItemEntry.COLUMN_ITEM_SELL_PRICE + " REAL)";
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    /**
     * Upgrade database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(LOG_TAG, "Updating table from " + oldVersion + " to " + newVersion);
    }
}
