package com.dev.joks.lockscreen;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Evgeniy on 31.01.2017.
 */

public class SharedPrefsUtil {

    private static final String PREFS_FILE = "prefs_file";

    public static void putStringData(Context context, String sharePrefKey, String data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(sharePrefKey, data).apply();
    }

    public static String getStringData(Context context, String sharePrefKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(sharePrefKey, "");
    }

    public static void putBooleanData(Context context, String sharePrefKey, boolean data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(sharePrefKey, data).apply();
    }

    public static boolean getBooleanData(Context context, String sharePrefKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(sharePrefKey, false);
    }

    public static void putIntData(Context context, String sharePrefKey, int data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(sharePrefKey, data).apply();
    }

    public static int getIntData(Context context, String sharePrefKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(sharePrefKey, -1);
    }

    public static void clearValue(Context context, String sharePrefKey) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
            sharedPreferences.edit().remove(sharePrefKey).apply();
        }
    }

    public static void clean(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    public static void putStringSet(Context context, String sharePrefKey, HashSet<String> data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().putStringSet(sharePrefKey, data).apply();
    }

    public static Set<String> getStringSet(Context context, String sharePrefKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(sharePrefKey, new HashSet<String>());
    }
}
