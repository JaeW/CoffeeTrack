package com.nemwick.coffeetrack;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nemwick.coffeetrack.data.CoffeeContract;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RecyclerViewCursorAdapter extends RecyclerView.Adapter<RecyclerViewCursorAdapter.CoffeeHolder> {
    private Cursor coffeeCursor;

    public RecyclerViewCursorAdapter(Cursor coffeeCursor) {
        this.coffeeCursor = coffeeCursor;
    }

    @Override
    public CoffeeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View coffeeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coffee, parent, false);
        return new CoffeeHolder(coffeeView);
    }

    @Override
    public void onBindViewHolder(CoffeeHolder holder, int position) {
        coffeeCursor.moveToPosition(position);
        Long coffeeTime = coffeeCursor.getLong(coffeeCursor.getColumnIndex(CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME));
        Date dateObjectCoffee = new Date(coffeeTime);
        holder.tvCoffeeTime.setText(formatDateTime(dateObjectCoffee));
    }

    @Override
    public int getItemCount() {
        return (coffeeCursor != null) ? coffeeCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (coffeeCursor != null) {
            if (coffeeCursor.moveToPosition(position)) {
                return coffeeCursor.getLong(coffeeCursor.getColumnIndex(CoffeeContract.CoffeeEntry._ID));
            }
        }
        return 0;
    }

    public void setCursor(Cursor cursor) {
        coffeeCursor = cursor;
        notifyDataSetChanged();
    }

    private String formatDateTime(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a - MMM dd, yyyy");
        return dateFormat.format(dateObject);
    }

    public static class CoffeeHolder extends RecyclerView.ViewHolder {
        public TextView tvCoffeeTime;

        public CoffeeHolder(View itemView) {
            super(itemView);
            tvCoffeeTime = (TextView) itemView.findViewById(R.id.last_coffee_time);
        }
    }
}
