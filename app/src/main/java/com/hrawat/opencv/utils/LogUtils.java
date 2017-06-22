package com.hrawat.opencv.utils;

import android.support.compat.BuildConfig;
import android.util.Log;

public final class LogUtils {

    public static void v(String tag, String message) {
        if (isLoggable())
            Log.v(tag, message);
    }

    public static void d(String tag, String message) {
        if (isLoggable())
            Log.d(tag, message);
    }

    public static void e(String tag, String message) {
        if (isLoggable())
            Log.e(tag, message);
    }

    public static void i(String tag, String message) {
        if (isLoggable())
            Log.i(tag, message);
    }

    public static void w(String tag, String message) {
        if (isLoggable())
            Log.w(tag, message);
    }

    public static void wtf(String tag, String message) {
        if (isLoggable())
            Log.wtf(tag, message);
    }

    public static void printStackTrace(Throwable e) {
        if (isLoggable())
            e.printStackTrace();
    }

    private static boolean isLoggable() {
        return BuildConfig.DEBUG;
    }
}
