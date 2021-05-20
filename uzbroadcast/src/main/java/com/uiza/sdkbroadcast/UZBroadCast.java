package com.uiza.sdkbroadcast;

import androidx.annotation.DrawableRes;

import org.greenrobot.eventbus.EventBus;

public class UZBroadCast {
    @DrawableRes
    static int iconNotify;


    private UZBroadCast() {
    }

    public static void init() {
        init(R.drawable.ic_start_live);
    }

    /**
     * @param iconNotify
     */
    public static void init(@DrawableRes int iconNotify) {
        UZBroadCast.iconNotify = iconNotify;
        EventBus.builder().installDefaultEventBus();
    }

    @DrawableRes
    public static int getIconNotify() {
        return iconNotify;
    }
}
