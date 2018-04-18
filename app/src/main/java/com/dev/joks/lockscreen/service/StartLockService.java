package com.dev.joks.lockscreen.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.dev.joks.lockscreen.R;
import com.dev.joks.lockscreen.SharedPrefsUtil;
import com.dev.joks.lockscreen.activity.LockNoPasscode;
import com.dev.joks.lockscreen.activity.PasswordActivity;
import com.dev.joks.lockscreen.event.RestartTimerEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.dev.joks.lockscreen.activity.MainActivity.HOURS;
import static com.dev.joks.lockscreen.activity.MainActivity.MINUTES;
import static com.dev.joks.lockscreen.activity.MainActivity.SECONDS;

public class StartLockService extends Service {

    private static final String TAG = StartLockService.class.getSimpleName();
    //    private Timer timer;
    private ScheduledExecutorService service;
    private ScheduledFuture scheduledFuture;
    private static final int TO_MILLISECONDS = 1000;
    private int hours;
    private int minutes;
    private int seconds;

    private static final int NOTIFICATION_ID = 99;

    public StartLockService() {
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Start Main Service");
//        timer = new Timer();
        hours = SharedPrefsUtil.getIntData(this, HOURS);
        minutes = SharedPrefsUtil.getIntData(this, MINUTES);
        seconds = SharedPrefsUtil.getIntData(this, SECONDS);

        runAsForeground();

//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                startLock();
//                Log.d(TAG, "Timer started");
//            }
//        }, (hours * 3600 + minutes * 60 + seconds) * TO_MILLISECONDS);

        Runnable runnable = () -> {
            startLock();
            Log.d(TAG, "Timer started");
        };

        service = Executors
                .newSingleThreadScheduledExecutor();
        scheduledFuture = service.schedule(runnable,
                (hours * 3600 + minutes * 60 + seconds) * TO_MILLISECONDS,
                TimeUnit.MILLISECONDS);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RestartTimerEvent event) {

//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Log.d(TAG, "Message get! Timer started");
//                startLock();
//            }
//        }, (hours * 3600 + minutes * 60 + seconds) * TO_MILLISECONDS);
        if (scheduledFuture.isDone()) {
            Log.d(TAG, "Task is done");
            if (!service.isShutdown() && !service.isTerminated()) {
                Runnable runnable = () -> {
                    startLock();
                    Log.d(TAG, "Timer started");
                };

                scheduledFuture = service.schedule(runnable,
                        (hours * 3600 + minutes * 60 + seconds) * TO_MILLISECONDS,
                        TimeUnit.MILLISECONDS);
            } else {
                Log.d(TAG, "Service stopped");
            }
        } else {
            Log.d(TAG, "Task in process");
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "Task removed");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        timer.cancel();
        scheduledFuture.cancel(true);
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "Stop Main Service");

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
    }

    private void runAsForeground() {
        Intent notificationIntent = new Intent(this, PasswordActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(getString(R.string.app_name) + " service is running")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void startLock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                final Intent intent = new Intent(StartLockService.this, LockNoPasscode.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Log.d(TAG, "You need overlay permission!");
            }
        } else {
            Intent intent = new Intent(StartLockService.this, LockNoPasscode.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
