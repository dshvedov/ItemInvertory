package ru.d9d.itemrent;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.d9d.itemrent.data.ItemContract.ItemEntry;

/**
 * Allows user to create a new item or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the item data loader
     */
    private static final int EXISTING_ITEM_LOADER = 0;

    private boolean mItemHasChanged = false;

    // Content URI for item (existing or new)
    private Uri mCurrentItemUri;

    // Edit fields
    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPricePurchaseEditText;
    private EditText mPriceSellEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new item or editing an existing one.
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();


        if (mCurrentItemUri == null) {
            // This is a new item
            setTitle(getString(R.string.editor_activity_title_new_item));
            invalidateOptionsMenu();

            // Buttons to increase and decrease quantity of item
            Button plusQuantity = findViewById(R.id.button_plus_quantity);
            plusQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(mQuantityEditText.getText())) mQuantityEditText.setText("0");
                    int quantity = Integer.parseInt(mQuantityEditText.getText().toString());
                    mQuantityEditText.setText(String.valueOf(quantity+1));
                }
            });
            Button minusQuantity = findViewById(R.id.button_minus_quantity);
            minusQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(mQuantityEditText.getText())) mQuantityEditText.setText("0");
                    int quantity = Integer.parseInt(mQuantityEditText.getText().toString());
                    if (quantity>0)
                        mQuantityEditText.setText(String.valueOf(quantity-1));
                    else
                        Toast.makeText(getApplicationContext(), getString(R.string.decrease_quantity_zero), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Otherwise this is an existing item
            setTitle(getString(R.string.editor_activity_title_edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_item_name);
        mQuantityEditText = findViewById(R.id.edit_item_quantity);
        mPricePurchaseEditText = findViewById(R.id.edit_item_price_purchase);
        mPriceSellEditText = findViewById(R.id.edit_item_price_sell);
        mSupplierNameEditText = findViewById(R.id.edit_item_supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.edit_item_supplier_phone);

        // Attach touch listener to monitor if there was changes or user activity in fields
        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPricePurchaseEditText.setOnTouchListener(mTouchListener);
        mPriceSellEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

    }

    /**
     * Get user input from editor and save item to database
     */
    private void saveItem() {

        boolean validData=true;
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String pricePurchaseString = mPricePurchaseEditText.getText().toString().trim();
        String priceSellString = mPriceSellEditText.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhone = mSupplierPhoneEditText.getText().toString().trim();

        // Do not save anything if new item and all values are empty
        if (mCurrentItemUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(quantityString)
                && TextUtils.isEmpty(pricePurchaseString) && TextUtils.isEmpty(priceSellString)
                && TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierPhone))
            return;

        // Check name
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getText(R.string.editor_item_name_null),
                    Toast.LENGTH_SHORT).show();
            validData=false;
        }

        // Check quantity
        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getText(R.string.editor_item_name_null),
                    Toast.LENGTH_SHORT).show();
            validData=false;
        }

        // Check prices
        float pricePurchase = 0;
        if (!TextUtils.isEmpty(pricePurchaseString)) {
            pricePurchase = Float.parseFloat(pricePurchaseString);
        }
        float priceSell = 0;
        if (!TextUtils.isEmpty(priceSellString)) {
            priceSell = Float.parseFloat(priceSellString);
        } else {
            Toast.makeText(this, getString(R.string.editor_price_sell_zero),
                    Toast.LENGTH_SHORT).show();
            validData=false;
        }

        if(validData) {
            // Create a ContentValues object where column names are the keys,
            // and item attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(ItemEntry.COLUMN_ITEM_NAME, nameString);
            values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantityString);
            values.put(ItemEntry.COLUMN_ITEM_PURCHASE_PRICE, pricePurchase);
            values.put(ItemEntry.COLUMN_ITEM_SELL_PRICE, priceSell);
            values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_NAME, supplierName);
            values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE, supplierPhone);

            // Determine if this is a new or existing item by checking if mCurrentItemUri is null or not
            if (mCurrentItemUri == null) {
                Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.editor_update_item_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_update_item_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the item in the database.
     */
    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    // Increase quantity of item by 1
    public void incQuantity(int itemId, int quantity) {
        quantity++;
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
        int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
        if (rowsAffected == 0)
            Toast.makeText(this, getString(R.string.editor_update_item_failed),
                    Toast.LENGTH_SHORT).show();
    }

    // Decrease quantity of item by 1
    public void decQuantity(int itemId, int quantity) {
        quantity--;
        if (quantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            if (rowsAffected == 0)
                Toast.makeText(this, getString(R.string.editor_update_item_failed),
                        Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.decrease_quantity_zero), Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save item to database
                saveItem();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
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
                ItemEntry.COLUMN_ITEM_PURCHASE_PRICE,
                ItemEntry.COLUMN_ITEM_SELL_PRICE,
                ItemEntry.COLUMN_ITEM_SUPPLIER_NAME,
                ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE};

        return new CursorLoader(this, mCurrentItemUri, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {

            int idColumnIndex = cursor.getColumnIndex(ItemEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
            int pricePurchaseColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PURCHASE_PRICE);
            int priceSellColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SELL_PRICE);
            int supplierNameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            final Integer itemId = cursor.getInt(idColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            final Integer quantity = cursor.getInt(quantityColumnIndex);
            float pricePurchase = cursor.getFloat(pricePurchaseColumnIndex);
            float priceSell = cursor.getFloat(priceSellColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            final String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mQuantityEditText.setText(String.valueOf(quantity));
            mPricePurchaseEditText.setText(String.valueOf(pricePurchase));
            mPriceSellEditText.setText(String.valueOf(priceSell));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);

            // Buttons to increase and decrease quantity of item
            Button plusQuantity = findViewById(R.id.button_plus_quantity);
            plusQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    incQuantity(itemId, quantity);
                }
            });
            Button minusQuantity = findViewById(R.id.button_minus_quantity);
            minusQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decQuantity(itemId, quantity);
                }
            });


            // Attach call supplier button action, if supplier phone present
            if (!TextUtils.isEmpty(supplierPhone)) {
                Button callButton = findViewById(R.id.button_call_supplier);
                // Show button
                callButton.setVisibility(View.VISIBLE);
                callButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + supplierPhone));
                        try {
                            startActivity(intent);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            // Delete button
            Button deleteButton = findViewById(R.id.button_delete);
            // Show button
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteConfirmationDialog();
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPricePurchaseEditText.setText("");
        mPriceSellEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }
}