# Welcome to UizaSDK

Simple Streaming at scale.
Uiza is the complete toolkit for building a powerful video streaming application with unlimited scalability. We design Uiza so simple that you only need a few lines of codes to start streaming, but sophisticated enough for you to build complex products on top of it.

Read [CHANGELOG](https://github.com/uizaio/uiza-sdk-player/blob/dev/CHANGELOG.md).

# Features:

- [x] Android minSDK 19 (android KITKAT 4.4).
- [x] Use [AndroidX](https://developer.android.com/jetpack/androidx?gclid=Cj0KCQiAt_PuBRDcARIsAMNlBdq2Il2bTw2XtIrq_PWMWQY7SA3WQdaGTqod6HUvGE_eTJ0RiBVMnC4aAhFWEALw_wcB)
- [x] Java 8

> NineOldAndroids has been removed since v5.0. Thanks Jake Wharton.

## uiza-core

Use [Retrofit2](https://square.github.io/retrofit/) and [Okhttp3](https://square.github.io/okhttp/)
> Thanks [Square](https://github.com/square)

## uiza-player

Use [Exoplayer](https://github.com/google/ExoPlayer)

> Thanks Google

## uiza-live
Use [rtmp-rtsp-stream-client-java](https://github.com/pedroSG94/rtmp-rtsp-stream-client-java).
> Thanks Pedro Sánchez

### Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<!--Optional for play store-->
<uses-feature android:name="android.hardware.camera" android:required="false" />
<uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />
```
- Require minSDK 21 (android 5).
- This library use [MediaCodec](https://developer.android.com/reference/android/media/MediaCodec.html) Android class to do hardware encoding.
- Create a RTP packets of video and audio, encapsulate it in flv packets and send to server
- Get audio data from microphone in PCM buffer and from [camera API2](https://developer.android.com/reference/android/hardware/camera2/package-summary.html) rendering a MediaCodec inputsurface. This builder can be executed in background mode if you use a context in the constructor instead of a surfaceview.
