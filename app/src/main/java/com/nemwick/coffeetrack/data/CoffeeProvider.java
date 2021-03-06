package com.nemwick.coffeetrack.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class CoffeeProvider extends ContentProvider {
    public static final int COFFEE = 100;
    public static final int COFFEE_ID = 101;

    private CoffeeDbHelper mCoffeeHelper;
    public static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        String content = CoffeeContract.CONTENT_AUTHORITY;
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, CoffeeContract.PATH_COFFEE, COFFEE);
        matcher.addURI(content, CoffeeContract.PATH_COFFEE + "/#", COFFEE_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mCoffeeHelper = new CoffeeDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase database = mCoffeeHelper.getReadableDatabase();
        Cursor returnCursor;

        switch (sUriMatcher.match(uri)) {
            case (COFFEE):
                returnCursor = database.query(
                        CoffeeContract.CoffeeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case (COFFEE_ID):
                returnCursor = database.query(
                        CoffeeContract.CoffeeEntry.TABLE_NAME,
                        projection,
                        CoffeeContract.CoffeeEntry._ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:  " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        validate(values);
        SQLiteDatabase database = mCoffeeHelper.getWritableDatabase();
        long id;
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case (COFFEE):
                id = database.insert(CoffeeContract.CoffeeEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = CoffeeContract.CoffeeEntry.buildCoffeeUri(id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into:  " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri:  " + uri);
        }
        notifyChanges(uri);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mCoffeeHelper.getWritableDatabase();
        int rowsDeleted = 0;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COFFEE:
                rowsDeleted = database.delete(CoffeeContract.CoffeeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COFFEE_ID:
                selection = CoffeeContract.CoffeeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(CoffeeContract.CoffeeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        notifyChanges(uri);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //Intentionally left blank.  Current app functionality does not allow user to update records
        return 0;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case COFFEE:
                return CoffeeContract.CoffeeEntry.CONTENT_TYPE;
            case COFFEE_ID:
                return CoffeeContract.CoffeeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:  " + uri);
        }
    }

    private void validate(ContentValues values) {
        //ensure time value to be inserted into db is valid
        Long coffeeTime = values.getAsLong(CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME);

        if (coffeeTime == null || coffeeTime <= 0) {
            throw new IllegalArgumentException("coffee time requires valid long integer");
        }
    }

    private void notifyChanges(Uri uri) {
        if (getContext() != null && getContext().getContentResolver() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }
}
