package ru.d9d.itemrent;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import ru.d9d.itemrent.data.ItemContract.ItemEntry;

/**
 * Displays list of items
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ITEM_LOADER = 0;

    ItemCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView itemListView = findViewById(R.id.list_view_catalog);

        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        mCursorAdapter = new ItemCursorAdapter(this, null);
        itemListView.setAdapter(mCursorAdapter);

        itemListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);

                intent.setData(currentItemUri);

                startActivity(intent);
            }
        });

        // Start loader
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    /**
     * Helper method to delete all items in the database.
     */
    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(ItemEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from item database");
    }

    // Inserts item with fake data
    private void insertDummyItem() {
        ContentValues values = new ContentValues();

        values.put(ItemEntry.COLUMN_ITEM_NAME, "Scooter");
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, 1);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_NAME, "Re-Action");
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE, "+7 495 777-777-1");
        values.put(ItemEntry.COLUMN_ITEM_PURCHASE_PRICE, 2500);
        values.put(ItemEntry.COLUMN_ITEM_SELL_PRICE, 4000);

        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
    }

    // Sell item button action
    public void sellItem(int itemId, int quantity) {
        quantity--;
        if (quantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
            Uri updateUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, itemId);
            int rowsUpdated = getContentResolver().update(updateUri, values, null, null);
            if(rowsUpdated>0)
                Toast.makeText(this, getString(R.string.sell_item_success)+quantity, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.sell_item_error), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.sell_item_zero), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_item:
                insertDummyItem();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_SELL_PRICE,
        };

        return new CursorLoader(this, ItemEntry.CONTENT_URI, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}