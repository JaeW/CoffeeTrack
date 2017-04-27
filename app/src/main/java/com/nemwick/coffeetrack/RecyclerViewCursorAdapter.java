package com.nemwick.coffeetrack;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nemwick.coffeetrack.data.CoffeeContract;
import com.nemwick.coffeetrack.utils.DateUtils;

import java.text.DateFormat;
import java.util.Date;


public class RecyclerViewCursorAdapter extends RecyclerView.Adapter<RecyclerViewCursorAdapter.CoffeeHolder> {
    private Cursor coffeeCursor;
    private Context context;
    private int colorToday;
    private int colorOther;

    public RecyclerViewCursorAdapter() {
        //intentionally left blank
    }

    @Override
    public CoffeeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
            colorToday = ContextCompat.getColor(context, R.color.colorIcons);
            colorOther = ContextCompat.getColor(context, R.color.colorPrimaryLight);
        }
        View coffeeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coffee, parent, false);
        return new CoffeeHolder(coffeeView);
    }

    @Override
    public void onBindViewHolder(CoffeeHolder holder, int position) {
        coffeeCursor.moveToPosition(position);
        Long coffeeTime = coffeeCursor.getLong(coffeeCursor.getColumnIndex(CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME));
        Date dateObjectCoffee = new Date(coffeeTime);

        if (DateUtils.convertSimpleDayFormat(coffeeTime) == DateUtils.TODAY) {
            holder.itemHolder.setBackgroundColor(colorToday);
        } else{
            holder.itemHolder.setBackgroundColor(colorOther);
        }
        holder.tvCoffeeTime.setText(formatTime(dateObjectCoffee));
        holder.tvCoffeeDate.setText(formatDate(dateObjectCoffee));
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

    void setCursor(Cursor cursor) {
        coffeeCursor = cursor;
        notifyDataSetChanged();
    }

    private String formatDate(Date dateObject) {
        DateFormat df = android.text.format.DateFormat.getDateFormat(context);
        return df.format(dateObject);
    }

    private String formatTime(Date dateObject) {
        DateFormat df = android.text.format.DateFormat.getTimeFormat(context);
        return df.format(dateObject);
    }

    static class CoffeeHolder extends RecyclerView.ViewHolder {
        TextView tvCoffeeTime;
        TextView tvCoffeeDate;
        LinearLayout itemHolder;

        CoffeeHolder(View itemView) {
            super(itemView);
            itemHolder = (LinearLayout) itemView.findViewById(R.id.item_holder);
            tvCoffeeTime = (TextView) itemView.findViewById(R.id.last_coffee_time);
            tvCoffeeDate = (TextView) itemView.findViewById(R.id.last_coffee_date);
        }
    }
}
