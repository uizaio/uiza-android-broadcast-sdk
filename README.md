
## Welcome to UIZA Android BroadCast SDK

Simple Streaming at scale.

Uiza is the complete toolkit for building a powerful video streaming application with unlimited scalability. We design Uiza so simple that you only need a few lines of codes to start streaming, but sophisticated enough for you to build complex products on top of it.

Read [CHANGELOG here](https://github.com/uizaio/uiza-android-broadcast-sdk/blob/master/CHANGELOG.md).

## Importing the Library
**Step 1. Add the `JitPack` repository to your `build.gradle` file**

```xml
    allprojects {
          repositories {
             maven { url 'https://jitpack.io' }
          }
    }
```

**Step 2. Add the dependency**

```xml
    dependencies {
        implementation 'com.github.uizaio:uiza-android-broadcast-sdk:1.x.x'
    }
```

Get latest release number [HERE](https://github.com/uizaio/uiza-android-broadcast-sdk/releases).

**Turn on Java 8 support**

If not enabled already, you need to turn on Java 8 support in all `build.gradle` files depending on ExoPlayer, by adding the following to the `android` section:

```gradle
compileOptions {
  targetCompatibility JavaVersion.VERSION_1_8
}
```

## Init SDK

1. If you want show log, install any `Tree` instances you want in the `onCreate` of your application class

```java
	if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
```

## How to broadcast with UZBroadCast?:
It's very easy, plz follow these steps below to implement:

XML:

```xml
    <com.uiza.sdk.view.UZBroadCastView
      android:id="@+id/uz_broadcast"
      android:layout_width="match_parent"
      android:layout_height="match_parent" />
```

In class [`LivePortraitActivity`](https://github.com/uizaio/uiza-android-broadcast-sdk/blob/master/samplebroadcast/src/main/java/com/uiza/samplebroadcast/UZBroadCastActivity.java):
```java
    public class UZBroadCastActivity extends AppCompatActivity implements UZBroadCastListener {
        // ...
    }
```
In `onCreate()`:

```java
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
    uzBroadCast = (UZBroadCastView) findViewById(R.id.uz_broadcast);
    uzBroadCast.setUZBroadcastListener(this);
```

In `onResume()`:

```java
    @Override
    protected void onResume() {
        uzBroadCast.onResume();
        super.onResume();
    }
```
Start a Stream: see [`VideoAttributes`](https://github.com/uizaio/uiza-android-broadcast-sdk/blob/master/uzbroadcast/src/main/java/com/uiza/sdk/profile/VideoAttributes.java) and [`AudioAttributes`](https://github.com/uizaio/uiza-android-broadcast-sdk/blob/master/uzbroadcast/src/main/java/com/uiza/sdk/profile/AudioAttributes.java)

```java
    uzBroadCast.setVideoAttributes(VideoAttributes videoAttrs);
    uzBroadCast.setAudioAttributes(AudioAttributes audioAttrs);
    if (uzBroadCast.prepareBroadCast()) {
        uzBroadCast.startBroadCast("broadCastUrl");
    }
```

or

```java
    if (uzBroadCast.prepareBroadCast(AudioAttributes audioAttrs, VideoAttributes videoAttrs, boolean isLandscape)) {
        uzBroadCast.startBroadCast("broadCastUrl");
    }
```

Stop streaming (It auto saves mp4 file in your gallery if you start a broadcast with option save local file)

```java
    uzBroadCast.stopBroadCast();
```

Switch camera:

```java
    uzBroadCast.switchCamera();
```
Allows streaming again after back from background:

```java
    uzBroadCast.setBackgroundAllowedDuration(YOUR_ALLOW_TIME); // default time is 2 minutes
```

This sample help you know how to use all Uiza SDK for livestream, please refer to  [THIS](https://github.com/uizaio/uiza-android-broadcast-sdk/tree/master/samplebroadcast)

## For contributors

 Uiza Checkstyle configuration is based on the Google coding conventions from Google Java Style
 that can be found at [here](https://google.github.io/styleguide/javaguide.html).

 Your code must be followed the rules that defined in our [`uiza_style.xml` rules](https://github.com/uizaio/uiza-android-broadcast-sdk/tree/master/configs/codestyle/uiza_style.xml)

 You can setting the rules after import project to Android Studio follow below steps:

 1. **File** > **Settings** > **Editor** > **Code Style**
 2. Right on the `Scheme`, select the setting icon > **Import Scheme** > **Intellij IDEA code style XML**
 3. Select the `uiza_style.xml` file path
 4. Click **Apply** > **OK**, then ready to go

 For apply check style, install [CheckStyle-IDEA plugin](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea), then

 1. **File** > **Settings** > **Other Settings** > **Checkstyle**
 2. In Configuration file, select the **`+`** icon
 3. Check `Use local checkstyle file` & select path to `uiza_check.xml` file
 4. Select **OK** & you're ready to go

 To run checkstyle for project

 1. Right click on project
 2. Select **Analyze** > **Inspect Code**


## Docs
[Docs](https://uizaio.github.io/uiza-android-broadcast-sdk/)

## Supported devices

Support all devices which have ***Android 5.0 (API level 21) above.***
For a given use case, we aim to support UizaSDK on all Android devices that satisfy the minimum version requirement.

**Note:** Some Android emulators do not properly implement components of Android’s media stack, and as a result do not support UizaSDK. This is an issue with the emulator, not with UizaSDK. Android’s official emulator (“Virtual Devices” in Android Studio) supports UizaSDK provided the system image has an API level of at least 23. System images with earlier API levels do not support UizaSDK. The level of support provided by third party emulators varies. Issues running UizaSDK on third party emulators should be reported to the developer of the emulator rather than to the UizaSDK team. Where possible, we recommend testing media applications on physical devices rather than emulators.


## Support

If you've found an error in this sample, please file an [issue ](https://github.com/uizaio/uiza-android-broadcast-sdk/issues)

Patches are encouraged, and may be submitted by forking this project and submitting a pull request through GitHub. Please feel free to contact me anytime: developer@uiza.io for more details.

Address: _33 Ubi Avenue 3 #08- 13, Vertex Tower B, Singapore 408868_
Email: _developer@uiza.io_
Website: _[uiza.io](https://uiza.io/)_

## License

UizaSDK is released under the BSD license. See  [LICENSE](https://github.com/uizaio/uiza-android-broadcast-sdk/blob/master/LICENSE)  for details.


