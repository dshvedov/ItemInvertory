package ru.d9d.itemrent;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import ru.d9d.itemrent.data.ItemContract.ItemEntry;
import ru.d9d.itemrent.data.ItemDbHelper;

public class CatalogActivity extends AppCompatActivity {

    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    // Initialize DbHelper for Db operations
    private ItemDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mDbHelper = new ItemDbHelper(this);

        displayDatabaseInfo();
    }

    /**
     * Displays db contents in textview
     */
    private void displayDatabaseInfo() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_SUPPLIER_NAME,
                ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE,
                ItemEntry.COLUMN_ITEM_PURCHASE_PRICE,
                ItemEntry.COLUMN_ITEM_SELL_PRICE,
                ItemEntry.COLUMN_ITEM_AVAILABILITY
        };

        Cursor cursor = db.query(
                ItemEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        TextView displayView = (TextView) findViewById(R.id.text_view_debug);
        try {
            displayView.setText("Table contains " + cursor.getCount() + " items.\n\n");
            displayView.append(ItemEntry._ID + "|" +
                    ItemEntry.COLUMN_ITEM_NAME + "|" +
                    ItemEntry.COLUMN_ITEM_QUANTITY + "|" +
                    ItemEntry.COLUMN_ITEM_SUPPLIER_NAME + "|" +
                    ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE + "|" +
                    ItemEntry.COLUMN_ITEM_PURCHASE_PRICE + "|" +
                    ItemEntry.COLUMN_ITEM_SELL_PRICE + "|" +
                    ItemEntry.COLUMN_ITEM_AVAILABILITY + "\n");

            int idColumnIndex = cursor.getColumnIndex(ItemEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE);
            int purchasePriceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PURCHASE_PRICE);
            int sellPriceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SELL_PRICE);
            int availabilityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_AVAILABILITY);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                displayView.append(("\n" + currentID + "|" +
                        currentName + "|" +
                        cursor.getString(quantityColumnIndex) + "|" +
                        cursor.getString(supplierNameColumnIndex) + "|" +
                        cursor.getString(supplierPhoneColumnIndex) + "|" +
                        cursor.getString(purchasePriceColumnIndex) + "|" +
                        cursor.getString(sellPriceColumnIndex) + "|" +
                        cursor.getString(availabilityColumnIndex)
                ));
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Insert 1 dummy item record
     */
    private void insertDummyItem() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, "Scooter");
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, 1);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_NAME, "Re-Action");
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE, "+7 495 777-777-1");
        values.put(ItemEntry.COLUMN_ITEM_PURCHASE_PRICE, 2500);
        values.put(ItemEntry.COLUMN_ITEM_SELL_PRICE, 4000);
        values.put(ItemEntry.COLUMN_ITEM_AVAILABILITY, ItemEntry.AVAILABILITY_IN_STOCK);
        long newRowId = db.insert(ItemEntry.TABLE_NAME, null, values);

        Log.v("CatalogActivity", "Row inserted id: " + newRowId);
    }

    /**
     * Menus
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyItem();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
