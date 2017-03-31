package com.nemwick.coffeetrack;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class SessionActivity extends AppCompatActivity {
    private ImageButton startPauseButton;
    private ImageButton stopButton;
    private TextView sessionTimerView;
    private FloatingActionButton fabSessionAddCoffee;

    private View.OnClickListener sessionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.session_fab_add_coffee:
                    //TODO:  implement
                    break;
                case R.id.start_pause_button:
                    buildNotification();
                    //TODO:  implement
                    break;
                case R.id.stop_button:
                    //TODO:  implement
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_session));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startPauseButton = (ImageButton) findViewById(R.id.start_pause_button);
        stopButton = (ImageButton) findViewById(R.id.stop_button);
        sessionTimerView = (TextView) findViewById(R.id.timer_countdown_view);
        fabSessionAddCoffee = (FloatingActionButton) findViewById(R.id.session_fab_add_coffee);

        startPauseButton.setOnClickListener(this.sessionListener);


    }

    private void buildNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_maps_local_cafe);
        builder.setContentTitle("Coffee Track Alarm");
        builder.setContentText("Time to drink coffee!");
        builder.setAutoCancel(true);

        Intent intent = new Intent(this, SessionActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SessionActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(3, builder.build());
    }

}
