package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    private BookCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Find the ListView which will be populated with the data
        ListView bookListView = findViewById(R.id.list_view_books);

        //Create a new CursorAdapter and link it to the list view
        mAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mAdapter);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Inserts (dummy) data entry into the DB
     */
    private void insertDummyData() {

        //Dummy Data
        String bookName = "The Bible";
        float price = 12.99f;
        int quantity = 0;
        String supplier = "The Pope";
        String supplierPhone = "+39 06 6982";

        //Insert dummy data into ContentValues
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, bookName);
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplier);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);

        //write book defined in values into database
        getContentResolver().insert(BookEntry.CONTENT_URI, values);

        //Dummy Data 2
        bookName = "The Bible 2 - Return of Christ";
        price = 29.99f;
        quantity = 42;

        //Insert dummy data into ContentValues
        values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, bookName);
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplier);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);

        //write book defined in values into database
        getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY};

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
