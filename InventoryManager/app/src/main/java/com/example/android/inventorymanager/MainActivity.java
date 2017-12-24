package com.example.android.inventorymanager;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventorymanager.data.Stock;
import com.example.android.inventorymanager.data.StockContract.StockEntry;
import com.example.android.inventorymanager.data.StockDbHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    private static final int STOCK_LOADER_ID = 0;
    private StockCursorAdapter mCursorAdapter;

    private StockDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        mDbHelper = new StockDbHelper(this);

        ListView stockList = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        mCursorAdapter = new StockCursorAdapter(this, null);
        stockList.setAdapter(mCursorAdapter);
        stockList.setEmptyView(emptyView);

        stockList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentStockUri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, id);
                intent.setData(currentStockUri);
                startActivity(intent);
            }
        });

       getLoaderManager().initLoader(STOCK_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {StockEntry._ID, StockEntry.COLUMN_NAME, StockEntry.COLUMN_QUANTITY, StockEntry.COLUMN_PRICE, StockEntry.COLUMN_IMAGE};
        return new CursorLoader(this, StockEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                Log.v(LOG_TAG, "Start inserting dummy data");
                insertStock();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertStock() {
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_NAME, "MacBook Pro");
        values.put(StockEntry.COLUMN_PRICE, "2000");
        values.put(StockEntry.COLUMN_QUANTITY, "30");
        values.put(StockEntry.COLUMN_SUPPLIER_NAME, "APPLE");
        values.put(StockEntry.COLUMN_SUPPLIER_PHONE, "+1 647-943-4400");
        values.put(StockEntry.COLUMN_SUPPLIER_EMAIL, "example@apple.com");
        values.put(StockEntry.COLUMN_IMAGE, "android.resource://com.example.android.inventorymanager/drawable/mac_book_pro");
        values.put(StockEntry.COLUMN_PART_NUMBER, "20171220LT01");
        getContentResolver().insert(StockEntry.CONTENT_URI, values);
        ContentValues values1 = new ContentValues();
        values1.put(StockEntry.COLUMN_NAME, "Pepsi Cola");
        values1.put(StockEntry.COLUMN_PRICE, "3");
        values1.put(StockEntry.COLUMN_QUANTITY, "66");
        values1.put(StockEntry.COLUMN_SUPPLIER_NAME, "Pepsi");
        values1.put(StockEntry.COLUMN_SUPPLIER_PHONE, "+1 489-333-7777");
        values1.put(StockEntry.COLUMN_SUPPLIER_EMAIL, "example@apple.com");
        values1.put(StockEntry.COLUMN_IMAGE, "android.resource://com.example.android.inventorymanager/drawable/pepsi_coke");
        values1.put(StockEntry.COLUMN_PART_NUMBER, "20171220LT01");
        getContentResolver().insert(StockEntry.CONTENT_URI, values1);
        ContentValues values2 = new ContentValues();
        values2.put(StockEntry.COLUMN_NAME, "Head First Java");
        values2.put(StockEntry.COLUMN_PRICE, "63");
        values2.put(StockEntry.COLUMN_QUANTITY, "6");
        values2.put(StockEntry.COLUMN_SUPPLIER_NAME, "O'REILLY");
        values2.put(StockEntry.COLUMN_SUPPLIER_PHONE, "+1 983-349-8888");
        values2.put(StockEntry.COLUMN_SUPPLIER_EMAIL, "example@apple.com");
        values2.put(StockEntry.COLUMN_IMAGE, "android.resource://com.example.android.inventorymanager/drawable/head_first_java");
        values2.put(StockEntry.COLUMN_PART_NUMBER, "20171220LT01");
        getContentResolver().insert(StockEntry.CONTENT_URI, values2);
    }

    private void deleteAllStocks() {
        int rowsDeleted = getContentResolver().delete(StockEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, rowsDeleted + " rows deleted from pet database");
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllStocks();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
