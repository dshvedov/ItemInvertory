package ru.d9d.itemrent.data;

import android.provider.BaseColumns;

public class ItemContract {

    // Empty constructor.
    private ItemContract() {
    }

    public static final class ItemEntry implements BaseColumns {

        public final static String TABLE_NAME = "items";

        /**
         * Unique ID number for the item (only for use in the database table).
         * INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Item name
         * TEXT
         */
        public final static String COLUMN_ITEM_NAME = "name";

        /**
         * Item quantity
         * INTEGER
         */
        public final static String COLUMN_ITEM_QUANTITY = "quantity";

        /**
         * Item supplier name
         * TEXT
         */
        public final static String COLUMN_ITEM_SUPPLIER_NAME = "supplier_name";

        /**
         * Item supplier phone
         * TEXT
         */
        public final static String COLUMN_ITEM_SUPPLIER_PHONE = "supplier_phone";

        /**
         * Availability
         * Possible values are {@link #AVAILABILITY_OUT_OF_STOCK}, {@link #AVAILABILITY_IN_STOCK}, {@link #AVAILABILITY_BUSY}.
         * INTEGER
         */
        public final static String COLUMN_ITEM_AVAILABILITY = "availability";

        /**
         * Purchase price
         * INTEGER
         */
        public final static String COLUMN_ITEM_PURCHASE_PRICE = "price_purchase";

        /**
         * Sell price
         * INTEGER
         */
        public final static String COLUMN_ITEM_SELL_PRICE = "price_sell";

        /**
         * Possible values for availability.
         */
        public static final int AVAILABILITY_OUT_OF_STOCK = 0;
        public static final int AVAILABILITY_IN_STOCK = 1;
        public static final int AVAILABILITY_BUSY = 2;
    }
}
