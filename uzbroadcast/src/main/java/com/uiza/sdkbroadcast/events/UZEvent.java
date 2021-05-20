package com.uiza.sdkbroadcast.events;

public class UZEvent {
    private String message;
    private EventSignal signal;

    public UZEvent(String message) {
        this(EventSignal.UPDATE, message);
    }

    public UZEvent(EventSignal signal, String message) {
        this.signal = signal;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public EventSignal getSignal() {
        return signal;
    }
}

