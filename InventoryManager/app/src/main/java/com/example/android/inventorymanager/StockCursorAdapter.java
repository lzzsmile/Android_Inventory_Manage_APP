package com.example.android.inventorymanager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventorymanager.data.StockContract;
import com.example.android.inventorymanager.data.StockContract.StockEntry;

/**
 * Created by Roy Li on 20/12/2017.
 */

public class StockCursorAdapter extends CursorAdapter {

    public StockCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        ImageView sale = (ImageView) view.findViewById(R.id.sale);
        ImageView image = (ImageView) view.findViewById(R.id.image_view);

        String name = cursor.getString(cursor.getColumnIndex(StockEntry.COLUMN_NAME));
        int quantity = cursor.getInt(cursor.getColumnIndex(StockEntry.COLUMN_QUANTITY));
        String price = cursor.getString(cursor.getColumnIndex(StockEntry.COLUMN_PRICE));
        price = "$" + price;

        image.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(StockEntry.COLUMN_IMAGE))));
        nameTextView.setText(name);
        quantityTextView.setText(String.valueOf(quantity));
        priceTextView.setText(price);
    }
}
