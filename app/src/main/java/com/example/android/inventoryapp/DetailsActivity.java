package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    @BindView(R.id.details_book_name) TextView bookNameView;
    @BindView(R.id.details_price) TextView bookPriceView;
    @BindView(R.id.details_quantity) TextView quantityView;
    @BindView(R.id.details_supplier_name) TextView supplierNameView;
    @BindView(R.id.details_supplier_phone) TextView supplierPhoneView;
    private Uri mBookUri;
    private int mCurrentQuantity = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mBookUri = intent.getData();

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }


    /**
     * increase/decrease the quantity of the current book when the plus/minus buttons are pressed
     */
    public void quantityChange(View v) {
        if (mCurrentQuantity > -1) {
            switch (v.getId()) {
                case R.id.plus_button:
                    mCurrentQuantity++;
                    break;
                case R.id.minus_button:
                    if (mCurrentQuantity > 0) {
                        mCurrentQuantity--;
                        break;
                    } else
                        return;
            }
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_QUANTITY, mCurrentQuantity);

            //Update the current book with the reduced quantity
            v.getContext().getContentResolver().update(
                    mBookUri,
                    values,
                    null,
                    null
            );
            quantityView.setText(String.valueOf(mCurrentQuantity));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Edit" menu option
            case R.id.action_edit:
                Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);
                intent.setData(mBookUri);
                startActivity(intent);
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete_details:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * show a dialog to conform the deletion of the book
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_one_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        int rowsDeleted = getContentResolver().delete(mBookUri, null, null);
        if (rowsDeleted == 1) {
            Toast.makeText(this, getString(R.string.delete_book_successful),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.delete_book_not_successful),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                mBookUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        try {
            // Figure out the index of each column
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            //Move cursor form start position -1 to position 0
            cursor.moveToFirst();

            //Read out the values from the cursor
            String bookName = cursor.getString(nameColumnIndex);
            float bookPrice = cursor.getFloat(priceColumnIndex);
            int bookQuantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            mCurrentQuantity = bookQuantity;

            //Get the format for the user's local currency
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

            bookNameView.setText(bookName);
            bookPriceView.setText(currencyFormatter.format(bookPrice));
            quantityView.setText(String.valueOf(bookQuantity));
            supplierNameView.setText(supplierName);
            supplierPhoneView.setText(supplierPhone);

            //make a click on a valid phone number open a phone app
            Linkify.addLinks(supplierPhoneView, Linkify.PHONE_NUMBERS);
        } catch (Exception e) {
            cursor.close();
        }

        //do NOT close the cursor when there is no exception as when we edit a book
        //and come back to the DetailsActivity, we need to read
        //the updated cursor
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
