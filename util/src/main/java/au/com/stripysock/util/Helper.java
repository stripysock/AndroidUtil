package au.com.stripysock.util;

/**
 * Created by bentrengrove on 03/01/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by bentrengrove on 10/03/15.
 */
public class Helper {
    private static float density = -1.0f;

    public static int getVisibilityForBoolean(boolean show) {
        return show ? View.VISIBLE : View.GONE;
    }

    public static int min(int x, int y) {
        if (x<=y) {
            return x;
        }

        return y;
    }

    public static String stringOrEmptyFromPossibleNull(String possibleNull) {
        if (possibleNull != null) {
            return possibleNull;
        }
        return "";
    }

    public static final String HASH_ALGORITHM_SHA1 = "HmacSHA1";
    public static final String HASH_ALGORITHM_SHA256 = "HmacSHA256";

    public static String hashMac(String text, String secretKey, String algorithm) {
        try {
            Key sk = new SecretKeySpec(secretKey.getBytes(), algorithm);
            Mac mac = Mac.getInstance(sk.getAlgorithm());
            mac.init(sk);
            final byte[] hmac = mac.doFinal(text.getBytes());
            return toHexString(hmac);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static String getDrawableFromSeriesSlug(String seriesSlug) {
        return seriesSlug.replace("-", "_");
    }

    public static void disableTouchTheft(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }

    public static void disableTouchTheftForViewsWithClass(Class clazz, View rootView) {
        for(int i=0; i<((ViewGroup)rootView).getChildCount(); ++i) {
            View nextChild = ((ViewGroup)rootView).getChildAt(i);

            if (clazz.isInstance(nextChild)) {
                disableTouchTheft(nextChild);
            }
            if (nextChild instanceof ViewGroup) {
                disableTouchTheftForViewsWithClass(clazz, nextChild);
            }
        }
    }

    public static boolean inLandscape(Context context) {
        return context.getResources().getConfiguration().orientation  == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean inPortrait(Context context) {
        return !inLandscape(context);
    }

    public static int getWindowHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getWindowHeightDp(Context context) {
        return (int)(getWindowHeight(context) / getDensity(context));
    }

    public static int getWindowWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getWindowWidthDp(Activity activity) {
        return (int)(getWindowWidth(activity) / getDensity(activity));
    }

    public static int getWindowWidthDp(Context context) {
        return (int)(getWindowWidth(context) / getDensity(context));
    }

    public static int getNavigationBarHeightPx(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static float getDensity(Activity activity) {
        if (density == -1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            density = metrics.density;
        }
        return density;
    }

    public static float getDensity(Context context) {
        if (density == -1) {
            DisplayMetrics metrics = new DisplayMetrics();
            ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
            density = metrics.density;
        }
        return density;
    }

    public static boolean isTablet(Context context) {
        int smallest = getSmallestDp(context);
        return smallest >= 552.0f;
    }

    public static int getSmallestDp(Context context) {
        int windowWidthDp = getWindowWidthDp(context);
        int windowHeightDp = getWindowHeightDp(context);

        return min(windowWidthDp, windowHeightDp);
    }

    @Nullable
    public static AlertDialog getErrorDialog(final Context context, final String message, final String stacktrace) {
        if (context == null) {
            return null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", null);

        if (stacktrace != null && BuildConfig.DEBUG) {
            builder.setNegativeButton("Show Detailed Report", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new android.support.v7.app.AlertDialog.Builder(context)
                            .setTitle("Report")
                            .setMessage(stacktrace)
                            .setPositiveButton("OK", null)
                            .show();
                }
            });
        }

        return builder.create();
    }

    public static String getStacktraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
