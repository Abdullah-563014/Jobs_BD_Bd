package com.mominur77.Jobs_BD;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.ImageView;

import com.squareup.picasso.Transformation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Utils {


    public static void saveStringToStorage(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("JobBdStorage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String retrieveStringFromStorage(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("JobBdStorage", Context.MODE_PRIVATE);
        String result = sharedPreferences.getString(key, null);
        return result;
    }

    public static void saveBooleanToStorage(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("JobBdStorage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean retrieveBooleanFromStorage(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("JobBdStorage", Context.MODE_PRIVATE);
        boolean result = sharedPreferences.getBoolean(key, true);
        return result;
    }

    public static boolean haveInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= 23) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) {
                return false;
            }
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (connectivityManager.getActiveNetworkInfo() != null
                    && connectivityManager.getActiveNetworkInfo().isAvailable()
                    && connectivityManager.getActiveNetworkInfo().isConnected()) {
                return true;
            } else {
                return false;
            }

        }
    }

    public static synchronized String increaseTimeUsingValues(int increasingDays) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + increasingDays);
        return sdf.format(calendar.getTime());
    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static String getTimeDifBetweenToTime(String startTime, String endTime) {
        try {

            Date date1;
            Date date2;

            SimpleDateFormat dates = new SimpleDateFormat("dd-MM-yyyy");

            //Setting dates
            date1 = dates.parse(startTime);
            date2 = dates.parse(endTime);

            //Comparing dates
            long difference = Math.abs(date1.getTime() - date2.getTime());
            long differenceDates = difference / (60 * 60 * 1000);

            return Long.toString(differenceDates);

        } catch (Exception exception) {
            return null;
        }
    }



    static class PicassoTransform implements Transformation {
        int targetWidth;
        public PicassoTransform(int targetWidth) {
            this.targetWidth=targetWidth;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            int targetHeight = (int) (targetWidth * aspectRatio);
            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
            if (result != source) {
                // Same bitmap is returned if sizes are the same
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "transformation" + " desiredWidth";
        }
    };
}
