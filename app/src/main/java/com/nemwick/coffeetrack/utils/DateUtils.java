package com.nemwick.coffeetrack.utils;


import android.content.Context;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static final int TODAY = 0;
    public static final int YESTERDAY = 1;
    public static final int THIS_WEEK = 2;
    public static final int THIS_MONTH = 3;
    public static final int BEFORE_MONTH = 4;
    public static final int HOUR_IN_SECONDS = 3600;
    public static final int MINUTE_IN_SECONDS = 60;
    public static final int DEFAULT_TIMER_DURACTION = 1800;  //2 hours

    public static long calculatePriorDateInMillis(int days) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DATE, c.get(Calendar.DATE) - days);
        return c.getTimeInMillis();
    }

    public static String calculateTextDuration(int seconds) {
        String timeString = "";
        if (seconds % HOUR_IN_SECONDS != 0) {
            return (seconds / MINUTE_IN_SECONDS + " minutes");
        } else {
            return (seconds / HOUR_IN_SECONDS + " hours");
        }
    }


    private static Calendar clearTimes(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    //method unused at present - retaining for (possible) future feature implementation
    public static int convertSimpleDayFormat(long val) {
        Calendar today = Calendar.getInstance();
        today = clearTimes(today);

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        yesterday = clearTimes(yesterday);

        Calendar last7days = Calendar.getInstance();
        last7days.add(Calendar.DAY_OF_YEAR, -7);
        last7days = clearTimes(last7days);

        Calendar last30days = Calendar.getInstance();
        last30days.add(Calendar.DAY_OF_YEAR, -30);
        last30days = clearTimes(last30days);


        if (val > today.getTimeInMillis()) {
            return TODAY;
        } else if (val > yesterday.getTimeInMillis()) {
            return YESTERDAY;
        } else if (val > last7days.getTimeInMillis()) {
            return THIS_WEEK;
        } else if (val > last30days.getTimeInMillis()) {
            return THIS_MONTH;
        } else {
            return BEFORE_MONTH;
        }
    }

    public static String formatTime(Context context, Date dateObject) {
        DateFormat df = android.text.format.DateFormat.getTimeFormat(context);
        return df.format(dateObject);
    }

    public static String formatDate(Context context, Date dateObject) {
        DateFormat df = android.text.format.DateFormat.getDateFormat(context);
        return df.format(dateObject);
    }


}
