package com.nemwick.coffeetrack.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class CoffeeContract {
    public static final String CONTENT_AUTHORITY = "com.nemwick.coffeetrack";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_COFFEE = "coffee";

    private CoffeeContract() {
        // private to prevent instantiation - singleton
    }

    public static abstract class CoffeeEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COFFEE).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_COFFEE;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_COFFEE;

        public static final String TABLE_NAME = "coffee";
        //primary key auto-incrementing
        public static final String _ID = BaseColumns._ID;
        /* date/time coffee was consumed saved in Unix time */
        public static final String COLUMN_COFFEE_TIME = "time";
        // latitude of location where coffee was consumed
        public static final String COLUMN_LATITUDE = "latitute";
        //longitude of location where coffee was consumed
        public static final String COLUMN_LONGITUDE = "longitude";

        public static Uri buildCoffeeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
