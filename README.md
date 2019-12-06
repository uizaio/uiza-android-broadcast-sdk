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

### Use

In `AndroidManifest.xml` added:

```xml
  <activity
            android:name="io.uiza.player.UizaPlayerActivity"
            android:configChanges="keyboard|keyboardHidden|orientation|screenSize|screenLayout|smallestScreenSize|uiMode"
            android:launchMode="singleTop"
            android:parentActivityName=".InfoActivity"
            android:theme="@style/PlayerTheme">
            <intent-filter>
                <action android:name="io.uiza.player.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="http" />
                <data android:scheme="https" />
                <data android:scheme="content" />
                <data android:scheme="asset" />
                <data android:scheme="file" />
            </intent-filter>
            <intent-filter>
                <action android:name="io.uiza.player.action.VIEW_LIST" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </activity>
```

Implement in java:

```java
Intent intent = new Intent();
intent.setData(Uri.parse(uri));
intent.putExtra(UizaPlayerActivity.EXTENSION_EXTRA, "m3u8"); // or "mpd"
intent.setAction(UizaPlayerActivity.ACTION_VIEW);
startActivity(intent);
```


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

- Support [camera1](https://developer.android.com/reference/android/hardware/Camera.html) and [camera2](https://developer.android.com/reference/android/hardware/camera2/package-summary.html) API
- Support [SurfaceView](https://developer.android.com/reference/android/view/SurfaceView), [TextureView](https://developer.android.com/reference/android/view/TextureView), OpenGLView and LightOpenGLView
- This library use [MediaCodec](https://developer.android.com/reference/android/media/MediaCodec.html) Android class to do hardware encoding.
- Create a RTP packets of video and audio, encapsulate it in flv packets and send to server
- Get audio data from microphone in PCM buffer and from [camera API2](https://developer.android.com/reference/android/hardware/camera2/package-summary.html) rendering a MediaCodec inputsurface.

### Implement

```xml
<io.uiza.live.UizaLiveView
    android:id="@+id/uiza_live_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:viewType="surfaceView" // textureView, lightOpenGLView and default openGLView
    app:adaptiveBitrate="false"
    app:audioStereo="true"
    app:fps="24"
    app:useCamera2="true" // API < 21 always false
    app:videoSize="p720" // p1080, p360
    app:audioBitrate="64" // Kbps
    app:audioSampleRate="32000" // Hz
    app:keyframe="2"/>       
```

```java
uizaLiveView = findViewById(uiza_live_view);
```

```java
  uizaLiveView.setLiveListener(new UizaLiveListener() {
        @Override
        public void onConnectionSuccess() {
            
        }

        @Override
        public void onConnectionFailed(@Nullable String reason) {

        }

        @Override
        public void onNewBitrate(long bitrate) {

        }

        @Override
        public void onDisconnect() {

        }

        @Override
        public void onAuthError() {

        }

        @Override
        public void onAuthSuccess() {

        }

        @Override
        public void surfaceCreated() {

        }

        @Override
        public void surfaceChanged(int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed() {

        }
    });
```

__Start stream__

```java
if (uizaLiveView.prepareStream()) {
	openGlView.startStream(liveStreamUrl);
}
```

__Stop stream__

```java
uizaLiveView.stopStream();
```

__Switch camera__


```java
try {
    uizaLiveView.switchCamera();
} catch (UizaCameraOpenException e) { }
```


__Record__

```java
uizaLiveView.startRecord(<file_name>);
```

__Stop record__

```java
uizaLiveView.stopRecord();
```

__Set Filter__

```java
uizaLiveView.setFilter(FilterRender.Beauty);
```