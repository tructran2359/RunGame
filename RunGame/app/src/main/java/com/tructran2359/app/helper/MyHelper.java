package com.tructran2359.app.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.widget.Toast;

/**
 * Created by tructran on 2/9/15.
 */
public class MyHelper {

    public static class ScreenSize {
        public int width;
        public int height;
    }

    public static ScreenSize mScreenSize = null;

    public static ScreenSize getScreenSize(Activity act) {
        if (mScreenSize == null) {
            DisplayMetrics dm = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(dm);
            mScreenSize = new ScreenSize();
            mScreenSize.width = dm.widthPixels;
            mScreenSize.height = dm.heightPixels;
        }
        return mScreenSize;
    }

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

}
