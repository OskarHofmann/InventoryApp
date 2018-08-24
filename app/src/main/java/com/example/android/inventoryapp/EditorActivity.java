package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

import java.util.Currency;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    @BindView(R.id.editor_book_name)
    EditText bookNameEditText;
    @BindView(R.id.editor_price)
    EditText bookPriceEditText;
    @BindView(R.id.editor_currency)
    TextView currencyTextView;
    @BindView(R.id.editor_quantity)
    EditText quantityEditText;
    @BindView(R.id.editor_supplier_name)
    EditText supplierNameEditText;
    @BindView(R.id.editor_supplier_phone)
    EditText supplierPhoneEditText;
    private Uri mBookUri;

    /**
     * OnClickListener that listens for any user clicks on an EditText, implying that they are modifying
     * // the view, setting mBookHasChanged to true
     */
    private boolean mBookHasChanged = false;
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mBookHasChanged = true;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        //Check whether an existing book should be loaded or a completely new book is added
        Intent intent = getIntent();
        mBookUri = intent.getData();
        if (mBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));
            getLoaderManager().initLoader(BOOK_LOADER, null, this);
        }

        //Select the correct currency symbol for the user
        String currencySymbol = Currency.getInstance(Locale.getDefault()).getSymbol();
        currencyTextView.setText(currencySymbol);

        //Set TouchListeners to check whether a change has been made
        bookNameEditText.setOnClickListener(mClickListener);
        bookPriceEditText.setOnClickListener(mClickListener);
        quantityEditText.setOnClickListener(mClickListener);
        supplierNameEditText.setOnClickListener(mClickListener);
        supplierPhoneEditText.setOnClickListener(mClickListener);

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
            cursor.moveToNext();

            //Read out the values from the cursor
            String bookName = cursor.getString(nameColumnIndex);
            float bookPrice = cursor.getFloat(priceColumnIndex);
            int bookQuantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            bookNameEditText.setText(bookName);
            bookPriceEditText.setText(String.format(Locale.getDefault(), "%.2f", bookPrice));
            quantityEditText.setText(bookQuantity);
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);

        } finally {
            //always close the cursor
            cursor.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookNameEditText.getText().clear();
        bookPriceEditText.getText().clear();
        quantityEditText.getText().clear();
        supplierNameEditText.getText().clear();
        supplierPhoneEditText.getText().clear();
    }
}
