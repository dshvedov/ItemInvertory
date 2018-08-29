package ru.d9d.itemrent;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ru.d9d.itemrent.data.ItemContract.ItemEntry;

/**
 * {@link ItemCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of item data as its data source. This adapter knows
 * how to create list items for each row of item data in the {@link Cursor}.
 */
public class ItemCursorAdapter extends CursorAdapter {

    public static final String LOG_TAG = ItemCursorAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link ItemCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.name);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView priceTextView = view.findViewById(R.id.price);

        // Find the columns of item attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(BaseColumns._ID);
        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
        int sellPriceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SELL_PRICE);

        // Read the item attributes from the Cursor for the current item
        final Integer itemId = cursor.getInt(idColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        final Integer quantity = cursor.getInt(quantityColumnIndex);
        String price = cursor.getString(sellPriceColumnIndex);

        // Update current item views

        // If quantity = 0 then show "out of stock" instead
        if (quantity > 0) quantityTextView.setText(String.valueOf(quantity));
        else quantityTextView.setText(context.getString(R.string.out_of_stock));
        nameTextView.setText(name);
        priceTextView.setText(price);

        // Button to sell product
        Button sellButton = view.findViewById(R.id.button_sell);
        sellButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CatalogActivity catalogActivity = (CatalogActivity) context;
                catalogActivity.sellItem(itemId, quantity);
            }
        });

    }
}
