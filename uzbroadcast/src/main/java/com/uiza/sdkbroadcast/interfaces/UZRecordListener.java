package com.uiza.sdkbroadcast.interfaces;


import com.uiza.sdkbroadcast.enums.RecordStatus;

public interface UZRecordListener {

    void onStatusChange(RecordStatus status);
}
