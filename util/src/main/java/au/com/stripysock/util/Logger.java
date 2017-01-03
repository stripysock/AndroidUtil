package au.com.stripysock.util;

/**
 * Created by bentrengrove on 03/01/2017.
 */

import android.util.Log;

/**
 * Created by bentrengrove on 2/03/15.
 */
public class Logger {
    private static final String DEFAULT_TAG = "StripySock";

    public static void d(String message) {
        Log.d(getTag(), message);
    }

    public static void i(String message) {
        Log.i(getTag(), message);
    }

    public static void e(String message) {
        Log.e(getTag(), message);
    }

    public static void e(String message, Exception ex) {
        Log.e(getTag(), message + " " + ex.getLocalizedMessage());
    }

    public static void e(String message, Throwable ex) {
        Log.e(getTag(), message + " " + ex.getLocalizedMessage());
    }

    private static String getTag() {
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            StackTraceElement caller = stackTraceElements[4];
            return caller.getClassName();
        } catch (IndexOutOfBoundsException e) {
            return DEFAULT_TAG;
        }
    }
}
