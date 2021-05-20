package com.uiza.sdkbroadcast.util;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;

public class ValidValues {
    private ValidValues() {
    }

    public static void check(int value, int min, int max) {
        if (value > max || value < min)
            throw new IllegalArgumentException(String.format("You must set value in [%d, %d]", min, max));
//        else pass
    }

    public static <T extends Service> boolean isMyServiceRunning(Context context, Class<T> tClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null)
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (tClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        return false;
    }
}
