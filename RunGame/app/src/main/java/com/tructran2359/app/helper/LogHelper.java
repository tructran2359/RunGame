package com.tructran2359.app.helper;

import android.util.Log;

/**
 * Created by tructran on 2/10/15.
 */
public class LogHelper {

    public static final boolean CAN_LOG = true;

    public static void v(String tag, String msg) {
        if (CAN_LOG) {
            Log.v(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (CAN_LOG) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (CAN_LOG) {
            Log.w(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (CAN_LOG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (CAN_LOG) {
            Log.e(tag, msg);
        }
    }

}
