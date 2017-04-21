package com.nemwick.coffeetrack.utils;


import java.util.Calendar;

public class DateUtils {

    public static final int TODAY = 0;
    public static final int YESTERDAY = 1;
    public static final int THIS_WEEK = 2;
    public static final int THIS_MONTH = 3;
    public static final int BEFORE_MONTH = 4;

    public static long calculatePriorDateInMillis(int days) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DATE, c.get(Calendar.DATE) - days);
        return c.getTimeInMillis();
    }

    private static Calendar clearTimes(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c;
    }

    public static int convertSimpleDayFormat(long val) {
        Calendar today=Calendar.getInstance();
        today=clearTimes(today);

        Calendar yesterday=Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR,-1);
        yesterday=clearTimes(yesterday);

        Calendar last7days=Calendar.getInstance();
        last7days.add(Calendar.DAY_OF_YEAR,-7);
        last7days=clearTimes(last7days);

        Calendar last30days=Calendar.getInstance();
        last30days.add(Calendar.DAY_OF_YEAR,-30);
        last30days=clearTimes(last30days);


        if(val >today.getTimeInMillis())
        {
            return TODAY;
        }
        else if(val>yesterday.getTimeInMillis())
        {
            return YESTERDAY;
        }
        else if(val>last7days.getTimeInMillis())
        {
            return THIS_WEEK;
        }
        else if(val>last30days.getTimeInMillis())
        {
            return THIS_MONTH;
        }
        else
        {
            return BEFORE_MONTH;
        }
    }
}