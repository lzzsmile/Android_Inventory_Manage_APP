package com.example.android.inventorymanager.data;
import com.example.android.inventorymanager.data.StockContract.StockEntry;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roy Li on 20/12/2017.
 */

public class StockDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = StockDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "inventory.db";

    private static final int DATABASE_VERSION = 1;

    public StockDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_STOCK_TABLE = "CREATE TABLE " + StockEntry.TABLE_NAME + " (" +
                StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StockEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                StockEntry.COLUMN_PRICE + " TEXT NOT NULL, " +
                StockEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                StockEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                StockEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL, " +
                StockEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, " +
                StockEntry.COLUMN_IMAGE + " TEXT NOT NULL, " +
                StockEntry.COLUMN_PART_NUMBER + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(CREATE_STOCK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
