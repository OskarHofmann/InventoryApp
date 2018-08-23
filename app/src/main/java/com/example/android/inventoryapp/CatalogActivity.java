package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;
import com.example.android.inventoryapp.data.BookDbHelper;

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

    /* */

    /**
     * Reads out the whole DB
     *
     * @return Cursor with the DB
     *//*
    private Cursor queryData() {

        //Get a DB to read from
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE};

        // Perform a query on the books table
        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        return cursor;

    }*/


    /*private void visualizeCursor(Cursor cursor) {
        //Get the TextView in the layout
        TextView displayView = findViewById(R.id.main_text_view);

        try {
            // Create a header in the Text View:
            displayView.setText("The table contains " + cursor.getCount() + " entries.\n\n");
            displayView.append(BookEntry._ID + " - " +
                    BookEntry.COLUMN_PRODUCT_NAME + " - " +
                    BookEntry.COLUMN_PRICE + " - " +
                    BookEntry.COLUMN_QUANTITY + " - " +
                    BookEntry.COLUMN_SUPPLIER_NAME + " - " +
                    BookEntry.COLUMN_SUPPLIER_PHONE + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                float currentPrice = cursor.getFloat(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhone));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }

    }*/

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
