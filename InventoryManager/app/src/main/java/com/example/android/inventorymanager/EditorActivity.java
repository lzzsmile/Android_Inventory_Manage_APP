package com.example.android.inventorymanager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventorymanager.data.Stock;
import com.example.android.inventorymanager.data.StockDbHelper;
import com.example.android.inventorymanager.data.StockContract.StockEntry;

import java.io.File;

/**
 * Created by Roy Li on 20/12/2017.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 0;
    private static final int REQUEST_CODE_TAKE_PHOTO = 0;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;


    private ImageView mProductPhotoImageView;
    private EditText nameEdit;
    private EditText priceEdit;
    private EditText quantityEdit;
    private EditText supplierNameEdit;
    private EditText supplierPhoneEdit;
    private EditText supplierEmailEdit;
    private EditText partNumberEdit;
    private Uri mCurrentUri;
    private Uri imageUri;
    private static final int EXISTING_STOCK_LOADER = 0;

    private Button saleButton;
    private Button orderButton;

    private boolean mStockHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mStockHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        FloatingActionButton fab_image = (FloatingActionButton) findViewById(R.id.fab_stock_image);
        fab_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageSelector();
                mStockHasChanged = true;
            }
        });

        mProductPhotoImageView = (ImageView) findViewById(R.id.product_photo_image_view);

        nameEdit = (EditText) findViewById(R.id.product_name_edit);
        priceEdit = (EditText) findViewById(R.id.price_edit);
        quantityEdit = (EditText) findViewById(R.id.quantity_edit);
        supplierNameEdit = (EditText) findViewById(R.id.supplier_name_edit);
        supplierPhoneEdit = (EditText) findViewById(R.id.supplier_phone_edit);
        supplierEmailEdit = (EditText) findViewById(R.id.supplier_email_edit);
        partNumberEdit = (EditText) findViewById(R.id.part_number_edit);

        saleButton = (Button) findViewById(R.id.sale_button);
        orderButton = (Button) findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(quantityEdit.getText().toString())){
                    int quantity = Integer.parseInt(quantityEdit.getText().toString());
                    quantity++;
                    quantityEdit.setText(String.valueOf(quantity));
                } else {
                    quantityEdit.setText(String.valueOf(1));
                }

            }
        });
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(quantityEdit.getText().toString())){
                    int quantity = Integer.parseInt(quantityEdit.getText().toString());
                    if (quantity > 0) {
                        quantity--;
                        quantityEdit.setText(String.valueOf(quantity));
                    } else {
                        quantityEdit.setText(String.valueOf(0));
                    }
                } else {
                    quantityEdit.setText(String.valueOf(0));
                }
            }
        });

        //mDbHelper = new StockDbHelper(this);
        mCurrentUri = getIntent().getData();
        if (mCurrentUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_pet));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
            orderButton.setEnabled(false);
            saleButton.setEnabled(false);
            imageUri = Uri.parse("android.resource://com.example.android.inventorymanager/drawable/placeholder_image");
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_pet));
            getLoaderManager().initLoader(EXISTING_STOCK_LOADER, null, this);
        }

        nameEdit.setOnTouchListener(mTouchListener);
        partNumberEdit.setOnTouchListener(mTouchListener);
        supplierNameEdit.setOnTouchListener(mTouchListener);
        supplierEmailEdit.setOnTouchListener(mTouchListener);
        supplierPhoneEdit.setOnTouchListener(mTouchListener);
        priceEdit.setOnTouchListener(mTouchListener);
        quantityEdit.setOnTouchListener(mTouchListener);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_NAME,
                StockEntry.COLUMN_PRICE,
                StockEntry.COLUMN_QUANTITY,
                StockEntry.COLUMN_SUPPLIER_NAME,
                StockEntry.COLUMN_SUPPLIER_EMAIL,
                StockEntry.COLUMN_SUPPLIER_PHONE,
                StockEntry.COLUMN_IMAGE,
                StockEntry.COLUMN_PART_NUMBER
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_NAME);
            int supplierEmailColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_EMAIL);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_PHONE);
            int imageColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_IMAGE);
            int partNumberColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PART_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String productName = cursor.getString(nameColumnIndex);
            String productPrice = cursor.getString(priceColumnIndex);
            String productQuantity = cursor.getString(quantityColumnIndex);
            String productSupplierName = cursor.getString(supplierNameColumnIndex);
            String productSupplierEmail = cursor.getString(supplierEmailColumnIndex);
            String productSupplierPhone = cursor.getString(supplierPhoneColumnIndex);
            String productImage = cursor.getString(imageColumnIndex);
            imageUri = Uri.parse(productImage);
            String productPartNumber = cursor.getString(partNumberColumnIndex);

            // Update the views on the screen with the values from the database
            nameEdit.setText(productName);
            priceEdit.setText(productPrice);
            quantityEdit.setText(productQuantity);
            supplierNameEdit.setText(productSupplierName);
            supplierEmailEdit.setText(productSupplierEmail);
            supplierPhoneEdit.setText(productSupplierPhone);
            partNumberEdit.setText(productPartNumber);
            mProductPhotoImageView.setImageURI(Uri.parse(productImage));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        nameEdit.setText("");
        priceEdit.setText("");
        quantityEdit.setText("");
        supplierNameEdit.setText("");
        supplierEmailEdit.setText("");
        supplierPhoneEdit.setText("");
        partNumberEdit.setText("");
        mProductPhotoImageView.setImageURI(Uri.parse("android.resource://com.example.android.inventorymanager/drawable/placeholder_image"));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
            MenuItem menuItem1 = menu.findItem(R.id.action_order);
            menuItem1.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveStock();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_order:
                showOrderConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mStockHasChanged) {
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

    private void showOrderConfirmationDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.order_message);
        builder.setPositiveButton(R.string.phone, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to phone
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhoneEdit.getText().toString().trim()));
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.email, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to email
                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + supplierEmailEdit.getText().toString().trim()));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Recurrent new order");
                String bodyMessage = "Please send us as soon as possible more " +
                        nameEdit.getText().toString().trim() +
                        "!!!";
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
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
    public void onBackPressed() {
        if (!mStockHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteStock();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveStock() {
        String productName = nameEdit.getText().toString().trim();
        String productPrice = priceEdit.getText().toString().trim();
        String productQuantity = quantityEdit.getText().toString().trim();
        String productSupplierName = supplierNameEdit.getText().toString().trim();
        String productSupplierEmail = supplierEmailEdit.getText().toString().trim();
        String productSupplierPhone = supplierPhoneEdit.getText().toString().trim();
        String productImage = imageUri.toString();

        String productPartNumber = partNumberEdit.getText().toString();
        if (mCurrentUri == null && TextUtils.isEmpty(productName) && TextUtils.isEmpty(productPrice) && TextUtils.isEmpty(productQuantity)
                && TextUtils.isEmpty(productSupplierName) && TextUtils.isEmpty(productSupplierEmail) && TextUtils.isEmpty(productSupplierPhone)
                && TextUtils.isEmpty(productPartNumber)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_NAME, productName);
        values.put(StockEntry.COLUMN_PRICE, productPrice);
        values.put(StockEntry.COLUMN_QUANTITY, productQuantity);
        values.put(StockEntry.COLUMN_SUPPLIER_NAME, productSupplierName);
        values.put(StockEntry.COLUMN_SUPPLIER_PHONE, productSupplierPhone);
        values.put(StockEntry.COLUMN_SUPPLIER_EMAIL, productSupplierEmail);
        values.put(StockEntry.COLUMN_IMAGE, productImage);
        values.put(StockEntry.COLUMN_PART_NUMBER, productPartNumber);
        Log.i(LOG_TAG, values.toString());
        if (mCurrentUri == null) {
            Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_stock_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_stock_successful), Toast.LENGTH_SHORT).show();
            }
        } else if (mCurrentUri != null) {
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_stock_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_stock_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteStock() {
        if (mCurrentUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_stock_failed), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_CHOOSE_PHOTO);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                imageUri = resultData.getData();
                mProductPhotoImageView.setImageURI(imageUri);
                mProductPhotoImageView.invalidate();
            }
        }
    }
}
