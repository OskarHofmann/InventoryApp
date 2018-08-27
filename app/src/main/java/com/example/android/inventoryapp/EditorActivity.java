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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
     * OnTouchListener that listens for any user clicks on an EditText, implying that they are modifying
     * // the view, setting mBookHasChanged to true
     */
    private boolean mBookHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
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
        bookNameEditText.setOnTouchListener(mTouchListener);
        bookPriceEditText.setOnTouchListener(mTouchListener);
        quantityEditText.setOnTouchListener(mTouchListener);
        supplierNameEditText.setOnTouchListener(mTouchListener);
        supplierPhoneEditText.setOnTouchListener(mTouchListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_editor);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                boolean saved = saveBook();
                if (saved)
                    finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete_editor:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity} when entering a new book or go back to
                // the previous activity {@link DetailsActivity} when in the edit mode
                if (!mBookHasChanged) {
                    if (mBookUri == null) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    } else {
                        super.onBackPressed();
                    }

                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent or previous activity.
                                if (mBookUri == null) {
                                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                } else {
                                    EditorActivity.super.onBackPressed();
                                }
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * show a dialog to confirm the deletion of the book
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
     * Warn the user that there are unsaved changes and ask, if he really wants to discard them
     */
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

    /**
     * save any changes for the new book or update the existing one
     *
     * @return true if the book was successfully saved or false otherwise
     */
    private boolean saveBook() {
        //read out all EditText fields
        String bookNameText = bookNameEditText.getText().toString();
        String bookPriceText = bookPriceEditText.getText().toString();
        String bookQuantityText = quantityEditText.getText().toString();
        String supplierNameText = supplierNameEditText.getText().toString();
        String supplierPhoneText = supplierPhoneEditText.getText().toString();

        //if all fields are empty just finish the activity without saving
        if (TextUtils.isEmpty(bookNameText) && TextUtils.isEmpty(bookPriceText) && TextUtils.isEmpty(bookQuantityText) &&
                TextUtils.isEmpty(supplierNameText) && TextUtils.isEmpty(supplierPhoneText)) {
            finish();
            return false;
        }

        //Check if all required fields are filled out and fill ContentValues accordingly
        //Book and supplier name and supplier phone must be given and cannot be an empty String
        if (TextUtils.isEmpty(bookNameText)) {
            Toast.makeText(this, getString(R.string.no_valid_book_name),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(supplierNameText)) {
            Toast.makeText(this, getString(R.string.no_valid_supplier_name),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(supplierPhoneText)) {
            Toast.makeText(this, getString(R.string.no_valid_supplier_phone),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, bookNameText.trim());
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameText.trim());
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneText.trim());

        // price and quantity are required but have default values
        // if we are updating a book, we just don't change the corresponding value if the field is empty
        if (!TextUtils.isEmpty(bookPriceText)) {
            try {
                float bookPriceFloat = Float.parseFloat(bookPriceText.trim());
                values.put(BookEntry.COLUMN_PRICE, bookPriceFloat);
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.no_valid_price),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (!TextUtils.isEmpty(bookQuantityText)) {
            try {
                int bookQuantityInt = Integer.parseInt(bookQuantityText.trim());
                values.put(BookEntry.COLUMN_QUANTITY, bookQuantityInt);
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.no_valid_quantity),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // if a new book should be added
        if (mBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.insert_book_failed),
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.insert_book_successful),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {
            //otherwise we update the existing book
            int updatedRows = getContentResolver().update(
                    mBookUri,
                    values,
                    null,
                    null
            );
            if (updatedRows == 0) {
                Toast.makeText(this, getString(R.string.update_book_failed),
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(this, getString(R.string.update_book_successful),
                        Toast.LENGTH_SHORT).show();
                return true;
            }

        }
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

        if (cursor == null || !cursor.moveToFirst())
            return;

        // Figure out the index of each column
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);


        //Read out the values from the cursor
        String bookName = cursor.getString(nameColumnIndex);
        float bookPrice = cursor.getFloat(priceColumnIndex);
        int bookQuantity = cursor.getInt(quantityColumnIndex);
        String supplierName = cursor.getString(supplierNameColumnIndex);
        String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

        bookNameEditText.setText(bookName);
        bookPriceEditText.setText(String.format(Locale.getDefault(), "%.2f", bookPrice));
        quantityEditText.setText(String.valueOf(bookQuantity));
        supplierNameEditText.setText(supplierName);
        supplierPhoneEditText.setText(supplierPhone);


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
