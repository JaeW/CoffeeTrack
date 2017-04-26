package com.nemwick.coffeetrack;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nemwick.coffeetrack.data.CoffeeContract;
import com.nemwick.coffeetrack.utils.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityMain extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ID = 111;
    public static final String COFFEE_PREFERENCES = "preferenceFile"; //key to retrieve saved preferences file
    public static final String LAST_SAVED = "lastCoffeeSaved"; //key to save/retrieve most recent uri to saved preferences
    public static final String LAST_SAVED_TIME = "lastSavedTime"; //key to save/retrieve time last coffee consumed
    public static final String SESSION_TIMER_STATE = "sessionTimerState";
    private Uri previousAddedCoffeeUri; //uri value for coffee record 1 prior to most recent
    private long previousAddedCoffeeTime; // time value for coffee record 1 prior to most recent
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private Menu menu;
    private RecyclerViewCursorAdapter adapter;
    private Snackbar snackbar;
    private int timerDurationInSeconds;


    private View.OnClickListener mainClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab:
                    addNewCoffee(); //AddNewCoffee saves lastCoffeeTime or lastUri to saved preferences
                    snackbar.show();  //gives option to undo the addition of the record just added to ContentProvider db
                    updateWidget();  //updates widget textView with time of most recent coffee
                    break;
                case android.support.design.R.id.snackbar_action:
                    //delete most recent coffee record from db / content provider
                    getContentResolver().delete(getLastAddedCoffeeUri(), null, null);
                    //update Shared Preferences to reflect prior coffee is now the most recent coffee
                    saveLastAddedCoffee(previousAddedCoffeeUri, previousAddedCoffeeTime);
                    updateWidget();
                    break;
            }
        }
    };

    //Overridden Callback Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load image in ImageView
        ImageView imageView = (ImageView) findViewById(R.id.coffee_main_backdrop);
        Glide.with(this).load(R.drawable.coffee_main).into(imageView);

        //toolbar setup
        Toolbar toolbarMain = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbarMain);

        //snackbar setup
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        snackbar = Snackbar.make(coordinatorLayout, R.string.snackbar_coffee_recorded, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_undo, this.mainClickListener);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        //fab setup
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(mainClickListener);

        //recyclerview setup
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_coffee_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new RecyclerViewCursorAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        //recyclervview
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        //cursorloader initialization
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(COFFEE_PREFERENCES, Context.MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences(COFFEE_PREFERENCES, Context.MODE_PRIVATE);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    //Overridden Menu Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (getSessionTimerState()) {
            hideOption(R.id.menu_item_start_session);
        } else {
            hideOption(R.id.menu_item_stop_session);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_item_coffee_stats:
                //view coffee consumption statistics for last month in new activity
                intent = new Intent(this, ActivityCoffeeStats.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_start_session:
                //start timer session - launches alarm & notification
                scheduleNotification();
                saveSessionTimerState(true);
                Toast.makeText(this, "Coffee timer set for " + DateUtils.calculateTextDuration(timerDurationInSeconds), Toast.LENGTH_SHORT).show();
                setSessionButtonVisibility();
                return true;
            case R.id.menu_item_stop_session:
                //stop timer session - cancel alarm & notification
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent);
                }
                saveSessionTimerState(false);
                setSessionButtonVisibility();
                Toast.makeText(this, "Coffee timer cancelled", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_item_find_coffee:
                //launch PlacePicker activity to find nearby coffee shop
                intent = new Intent(this, ActivityCoffeePicker.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_settings:
                intent = new Intent(this, ActivityEditPreferences.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Overridden Loader Methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME + " DESC";
        return new CursorLoader(this, CoffeeContract.CoffeeEntry.CONTENT_URI, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setCursor(null);
    }

    //Overridden Preferences Changed Method
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {
            case SESSION_TIMER_STATE:
                setSessionButtonVisibility();
        }
    }


    private void updateWidget() {
        Context context = getApplicationContext();
        //retrieve instance of AppWidgetManager responsible for updating widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        // retrieve identifiers for each instance of widget
        ComponentName thisWidget = new ComponentName(context, CoffeeWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        long coffeeTime = getLastAddedCoffeeTime();
        //update each active widget with time of most recent coffee
        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_coffee);
            DateFormat sdf = new SimpleDateFormat("HH:mm EEE");
            Date d = new Date(coffeeTime);
            rv.setTextViewText(R.id.date_time_last_coffee, sdf.format(d));
            //partial update refreshes value of TextView with most recent coffee time but leaves the
            //ImageButton - along with its pending intent - unchanged
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, rv);
        }
    }

    private void addNewCoffee() {
        /* retrieve uri and time values for most recent coffee from shared preferences
        and save locally in the event the undo feature is clicked on the snackbar and
        values need to be saved back to shared preferences*/
        previousAddedCoffeeTime = getLastAddedCoffeeTime();
        previousAddedCoffeeUri = getLastAddedCoffeeUri();

        /*add a new coffee record to db / content provider
        although this is technically blocking the main UI, due to minimal time required to save non-sql-joined save
         to db / content provider, decision was made to not use a service to perform this add  at this time */
        long addedCoffeeTime = java.lang.System.currentTimeMillis(); //get current Unix time
        ContentValues values = new ContentValues();
        values.put(CoffeeContract.CoffeeEntry.COLUMN_LATITUDE, 1);
        values.put(CoffeeContract.CoffeeEntry.COLUMN_LONGITUDE, 1);
        values.put(CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME, addedCoffeeTime);
        Uri lastAddedCoffeeUri = getContentResolver().insert(CoffeeContract.CoffeeEntry.CONTENT_URI, values);

        //save new uri and time values to Shared Preferences
        saveLastAddedCoffee(lastAddedCoffeeUri, addedCoffeeTime);
    }

    private Notification buildAlarmNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_maps_local_cafe);
        builder.setContentTitle(getString(R.string.notification_title));
        builder.setContentText(getString(R.string.notification_text));
        builder.setAutoCancel(true);
        builder.setColor(ContextCompat.getColor(this, R.color.colorAccent));

        Intent intent = new Intent(this, ActivityCoffeePicker.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ActivityCoffeePicker.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        Notification notification = builder.build();
        //get notification ringtone from shared preferences
        String sound = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getString(R.string.ringtone_preference_key), getString(R.string.ringtone_default));
        notification.sound = Uri.parse(sound);
        return notification;
    }

    private void scheduleNotification() {
        //get timer duraction from preferences - units in seconds
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        timerDurationInSeconds = Integer.parseInt(preferences.getString(getString(R.string.timer_preference_key), Integer.toString(DateUtils.DEFAULT_TIMER_DURACTION)));

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, buildAlarmNotification());
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, 1);
        pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + timerDurationInSeconds * 1000;
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private void saveSessionTimerState(boolean active) {
        SharedPreferences preferences = getSharedPreferences(COFFEE_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SESSION_TIMER_STATE, active);
        editor.apply();
    }

    private boolean getSessionTimerState() {
        SharedPreferences preferences = getSharedPreferences(COFFEE_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(SESSION_TIMER_STATE, false);
    }

    //store the uri and time values for the coffee last added to the db & update Shared Preferences
    private void saveLastAddedCoffee(Uri uri, Long lastTime) {
        SharedPreferences preferences = getSharedPreferences(COFFEE_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LAST_SAVED, uri.toString());
        editor.putLong(LAST_SAVED_TIME, lastTime);
        //apply() functions asynchronously as opposed to commit() which does not
        editor.apply();
    }

    //returns uri value from Shared Preferences of most recent coffee
    private Uri getLastAddedCoffeeUri() {
        SharedPreferences preferences = getSharedPreferences(COFFEE_PREFERENCES, Context.MODE_PRIVATE);
        String lastSavedUri = preferences.getString(LAST_SAVED, CoffeeContract.CoffeeEntry.CONTENT_URI + "0");
        return Uri.parse(lastSavedUri);
    }

    //returns time value from Shared Preferences of most recent coffee
    private long getLastAddedCoffeeTime() {
        SharedPreferences preferences = getSharedPreferences(COFFEE_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getLong(LAST_SAVED_TIME, 0);
    }

    //Methods to show/hide session-related buttons dependent on AlarmManager state
    private void setSessionButtonVisibility() {
        if (getSessionTimerState()) {
            showOption(R.id.menu_item_stop_session);
            hideOption(R.id.menu_item_start_session);
        } else {
            showOption(R.id.menu_item_start_session);
            hideOption(R.id.menu_item_stop_session);
        }
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

}//end ActivityMain
