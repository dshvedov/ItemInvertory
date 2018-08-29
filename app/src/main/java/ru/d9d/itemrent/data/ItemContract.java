package ru.d9d.itemrent.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ItemContract {

    // Content authority and base paths
    public static final String CONTENT_AUTHORITY = "ru.d9d.itemrent";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";

    // Empty constructor.
    private ItemContract() {
    }

    public static final class ItemEntry implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public final static String TABLE_NAME = "items";

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
         * Purchase price
         * REAL (float)
         */
        public final static String COLUMN_ITEM_PURCHASE_PRICE = "price_purchase";

        /**
         * Sell price
         * REAL (float)
         */
        public final static String COLUMN_ITEM_SELL_PRICE = "price_sell";

        /**
         * The MIME type of the {@link #CONTENT_URI} for items list
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

    }
}
