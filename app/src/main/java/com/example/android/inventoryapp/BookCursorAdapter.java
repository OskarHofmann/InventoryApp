package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookCursorAdapter extends CursorAdapter {


    BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        //set up a view holder to avoid unnecessary findViewById calls
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Get the ViewHolder from the given view
        ViewHolder holder = (ViewHolder) view.getTag();

        // read the book name, price and quantity from the given cursor
        String bookName = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_NAME));
        float price = cursor.getFloat(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_QUANTITY));

        //Get the format for the user's local currency
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

        //Set the corresponding TextViews
        holder.nameView.setText(bookName);
        holder.priceView.setText(currencyFormatter.format(price));
        holder.quantityView.setText(String.format(Locale.getDefault(), "%d", quantity));

        //Set quantity to red in case it is zero or otherwise to its default color
        if (quantity == 0) {
            holder.quantityView.setTextColor(context.getResources().getColor(R.color.empty_stock));
        } else {
            holder.quantityView.setTextColor(context.getResources().getColor(R.color.list_item_book_details_text_color));
        }


        // Pass the Uri of the current book as a tag to the "Sale" button
        // and define what happens when the button is clicked
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry._ID));
        Uri bookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

        holder.saleButton.setTag(bookUri);
        holder.saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the quantity of the current book
                Uri currentBookUri = (Uri) v.getTag();
                Cursor c = v.getContext().getContentResolver().query(
                        currentBookUri,
                        new String[]{BookEntry.COLUMN_QUANTITY},
                        null,
                        null,
                        null);
                c.moveToFirst();
                int currentQuantity = c.getInt(c.getColumnIndexOrThrow(BookEntry.COLUMN_QUANTITY));

                if (currentQuantity > 0) {
                    //Create ContentValues with the reduced quantity
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_QUANTITY, currentQuantity - 1);

                    //Update the current book with the reduced quantity
                    v.getContext().getContentResolver().update(
                            currentBookUri,
                            values,
                            null,
                            null);
                }
            }
        });
    }

    static class ViewHolder {
        @BindView(R.id.book_name) TextView nameView;
        @BindView(R.id.book_price) TextView priceView;
        @BindView(R.id.book_quantity) TextView quantityView;
        @BindView(R.id.sale_button) Button saleButton;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
