package com.example.android.inventorymanager.data;
import com.example.android.inventorymanager.MainActivity;
import com.example.android.inventorymanager.data.StockContract.StockEntry;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Roy Li on 20/12/2017.
 */

public class StockProvider extends ContentProvider {

    public static final String LOG_TAG = StockProvider.class.getSimpleName();

    private StockDbHelper mDbHelper;

    private static final int STOCKS = 100;
    private static final int STOCKS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_PETS, STOCKS);
        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_PETS + "/#", STOCKS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new StockDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCKS:
                cursor = database.query(StockEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case STOCKS_ID:
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(StockEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCKS:
                return insertStock(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertStock(Uri uri, ContentValues contentValues) {
        String name = contentValues.getAsString(StockEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Stock requires a name!");
        }
        String price = contentValues.getAsString(StockEntry.COLUMN_PRICE);
        if (price != null && Integer.parseInt(price) < 0) {
            throw new IllegalArgumentException("Stock requires valid price!");
        }
        Integer quantity = Integer.parseInt(contentValues.getAsString(StockEntry.COLUMN_QUANTITY));
        if (quantity == null || quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Stock requires valid quantity!");
        }
        String supplierName = contentValues.getAsString(StockEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Stock requires supplier name!");
        }
        String supplierPhone = contentValues.getAsString(StockEntry.COLUMN_SUPPLIER_PHONE);
        if (supplierPhone == null) {
            throw new IllegalArgumentException("Stock requires supplier phone number!");
        }
        String supplierEmail = contentValues.getAsString(StockEntry.COLUMN_SUPPLIER_EMAIL);
        if (supplierEmail == null) {
            throw new IllegalArgumentException("Stock requires supplier email!");
        }
        String image = contentValues.getAsString(StockEntry.COLUMN_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException("Stock requires an image!");
        }
        String partNumber = contentValues.getAsString(StockEntry.COLUMN_PART_NUMBER);
        if (partNumber == null) {
            throw new IllegalArgumentException("Stock requires a part number!");
        }

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        long newRowID = database.insert(StockEntry.TABLE_NAME, null, contentValues);
        if (newRowID == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Uri newUri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, newRowID);
        Log.v(LOG_TAG, newUri.toString());
        return newUri;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCKS:
                return updateStock(uri, contentValues, selection, selectionArgs);
            case STOCKS_ID:
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateStock(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateStock(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(StockEntry.COLUMN_NAME)) {
            String name = contentValues.getAsString(StockEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Stock requires a name!");
            }
        }
        if (contentValues.containsKey(StockEntry.COLUMN_PRICE)) {
            String price = contentValues.getAsString(StockEntry.COLUMN_PRICE);
            if (price == null || (price != null && Integer.parseInt(price) < 0)) {
                throw new IllegalArgumentException("Stock requires valid price!");
            }
        }
        if (contentValues.containsKey(StockEntry.COLUMN_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(StockEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Stock requires valid quantity!");
            }
        }
        if (contentValues.containsKey(StockEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = contentValues.getAsString(StockEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Stock requires supplier name!");
            }
        }
        if (contentValues.containsKey(StockEntry.COLUMN_SUPPLIER_PHONE)) {
            String supplierPhone = contentValues.getAsString(StockEntry.COLUMN_SUPPLIER_PHONE);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("Stock requires supplier phone number!");
            }
        }
        if (contentValues.containsKey(StockEntry.COLUMN_SUPPLIER_EMAIL)) {
            String supplierEmail = contentValues.getAsString(StockEntry.COLUMN_SUPPLIER_EMAIL);
            if (supplierEmail == null) {
                throw new IllegalArgumentException("Stock requires supplier email!");
            }
        }
        if (contentValues.containsKey(StockEntry.COLUMN_IMAGE)) {
            String image = contentValues.getAsString(StockEntry.COLUMN_IMAGE);
            if (image == null) {
                throw new IllegalArgumentException("Stock requires an image!");
            }
        }
        if (contentValues.containsKey(StockEntry.COLUMN_PART_NUMBER)) {
            String partNumber = contentValues.getAsString(StockEntry.COLUMN_PART_NUMBER);
            if (partNumber == null) {
                throw new IllegalArgumentException("Stock requires a part number!");
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        int affectedRows = database.update(StockEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (affectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case STOCKS:
                rowsDeleted = database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCKS_ID:
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCKS:
                return StockEntry.CONTENT_LIST_TYPE;
            case STOCKS_ID:
                return StockEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
