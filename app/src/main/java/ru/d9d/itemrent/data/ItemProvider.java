package ru.d9d.itemrent.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import ru.d9d.itemrent.data.ItemContract.ItemEntry;

/**
 * {@link ContentProvider} for Items app.
 */
public class ItemProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ItemProvider.class.getSimpleName();

    private ItemDbHelper mDbHelper;

    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS + "/#", ITEM_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new ItemDbHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                // ITEMS could contain multiple rows
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                break;
            case ITEM_ID:
                // In case of ITEM_ID id is extracted from URI
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a item into the database.
     * Returns URI of inserted item
     */
    private Uri insertItem(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(ItemEntry.COLUMN_ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item name is required");
        }

        // Check quantity: not null, 0 or positive value
        Integer quantity = values.getAsInteger(ItemEntry.COLUMN_ITEM_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Invalid item quantity");
        }

        // Check item purchase price: 0 or positive value
        float purchasePrice = values.getAsFloat(ItemEntry.COLUMN_ITEM_PURCHASE_PRICE);
        if (purchasePrice < 0) {
            throw new IllegalArgumentException("Invalid item purchase price");
        }

        // Check item sell price: not 0, positive value
        float sellPrice = values.getAsFloat(ItemEntry.COLUMN_ITEM_SELL_PRICE);
        if (sellPrice <= 0) {
            throw new IllegalArgumentException("Invalid item sell price");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long newRowId = db.insert(ItemEntry.TABLE_NAME, null, values);
        if (newRowId != -1) {
            Log.v(LOG_TAG, "Row inserted id: " + newRowId);
            // Notify ContentResolver there is a change in data
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.v(LOG_TAG, "Failed to insert row for uri: " + uri);
            return null;
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, newRowId);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update items in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more items).
     * Return the number of rows that were successfully updated.
     */
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Return in no values passed
        if (values.size() == 0) {
            return 0;
        }

        // Check that the name is not null
        if (values.containsKey(ItemEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(ItemEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item name is required");
            }
        }

        // Check quantity: not null, 0 or positive value
        if (values.containsKey(ItemEntry.COLUMN_ITEM_QUANTITY)) {
            Integer quantity = values.getAsInteger(ItemEntry.COLUMN_ITEM_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Invalid item quantity");
            }
        }

        // Check item purchase price: 0 or positive value
        if (values.containsKey(ItemEntry.COLUMN_ITEM_PURCHASE_PRICE)) {
            float purchasePrice = values.getAsFloat(ItemEntry.COLUMN_ITEM_PURCHASE_PRICE);
            if (purchasePrice < 0) {
                throw new IllegalArgumentException("Invalid item purchase price");
            }
        }

        // Check item sell price: not 0, positive value
        if (values.containsKey(ItemEntry.COLUMN_ITEM_SELL_PRICE)) {
            float sellPrice = values.getAsFloat(ItemEntry.COLUMN_ITEM_SELL_PRICE);
            if (sellPrice <= 0) {
                throw new IllegalArgumentException("Invalid item sell price");
            }
        }

        // Make SQL request
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            Log.v(LOG_TAG, rowsUpdated + " rows updated");
            // Notify ContentResolver there is a change in data
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.v(LOG_TAG, "Failed to update rows for uri: " + uri);
        }
        return rowsUpdated;
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case ITEMS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
