package com.uiza.sdk.enums;

public enum FrameRate {
    FPS_20(20),
    FPS_24(24),
    FPS_30(30),
    FPS_60(60);

    private final int value;

    FrameRate(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
