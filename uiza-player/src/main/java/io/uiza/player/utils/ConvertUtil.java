package io.uiza.player.utils;

import android.content.res.Resources;

public class ConvertUtil {

    public static int dp2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
