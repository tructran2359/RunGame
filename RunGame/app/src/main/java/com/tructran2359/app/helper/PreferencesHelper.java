package com.tructran2359.app.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tructran on 2/12/15.
 */
public class PreferencesHelper {

    public static String PREF_NAME = "RunGamePreferences";
    public static final String DEFAULT_STRING = "";
    public static final int DEFAULT_INT = 0;
    public static final boolean DEFAULT_BOOL = false;

    public static final String PREF_HIGHEST_SCORE = "HighestScore";

    protected static SharedPreferences mSharedPreferences;
    protected static PreferencesHelper mInstance = new PreferencesHelper();

    public static PreferencesHelper getInstance(Context ctx) {
        init(ctx);
        return mInstance;
    }

    private static void init(Context ctx) {
        if (mSharedPreferences == null) {
            mSharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    private static boolean putString(String key, String value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
            return true;
        }
        return false;
    }

    private static String getString(String key) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getString(key, DEFAULT_STRING);
        }
        return DEFAULT_STRING;
    }

    private static boolean putInt(String key, int value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(key, value);
            editor.commit();
            return true;
        }
        return false;
    }

    private static int getInt(String key, int defaultValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getInt(key, defaultValue);
        }
        return defaultValue;
    }

    public boolean putHighestScore(int score) {
        return putInt(PREF_HIGHEST_SCORE, score);
    }

    public int getHighestScore() {
        return getInt(PREF_HIGHEST_SCORE, DEFAULT_INT);
    }
}
