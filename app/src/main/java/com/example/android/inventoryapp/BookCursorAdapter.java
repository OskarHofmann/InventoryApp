package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    }

    static class ViewHolder {
        @BindView(R.id.book_name)
        TextView nameView;
        @BindView(R.id.book_price)
        TextView priceView;
        @BindView(R.id.book_quantity)
        TextView quantityView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
