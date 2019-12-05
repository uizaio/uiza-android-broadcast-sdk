package io.uiza.samplelive;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.uiza.core.utils.UizaLog;
import io.uiza.live.UizaOpenGLView;
import io.uiza.live.interfaces.FilterRender;
import io.uiza.live.interfaces.Translate;
import io.uiza.live.interfaces.UizaCameraOpenException;
import io.uiza.live.interfaces.UizaLiveListener;

public class UizaLiveActivity extends AppCompatActivity implements UizaLiveListener,
        View.OnClickListener {

    private AppCompatImageButton startButton;
    private AppCompatImageButton bRecord;

    private String liveStreamUrl;
    private String currentDateAndTime = "";
    private File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/uiza-live");
    private UizaOpenGLView openGlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_open_gl);
        openGlView = findViewById(R.id.uiza_open_glview);
        openGlView.setLiveListener(this);
        startButton = findViewById(R.id.b_start_stop);
        startButton.setOnClickListener(this);
        startButton.setEnabled(false);
        bRecord = findViewById(R.id.b_record);
        bRecord.setOnClickListener(this);
        AppCompatImageButton switchCamera = findViewById(R.id.switch_camera);
        switchCamera.setOnClickListener(this);
        liveStreamUrl = getIntent().getStringExtra(SampleLiveApplication.EXTRA_STREAM_ENDPOINT);
        if (TextUtils.isEmpty(liveStreamUrl)) {
            liveStreamUrl = SampleLiveApplication.getLiveEndpoint();
        }
        ActivityCompat.requestPermissions(UizaLiveActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 1001);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1001) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startButton.setEnabled(true);
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                startButton.setEnabled(false);
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(UizaLiveActivity.this, "Permission denied to access your Camera and Record Audio", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gl_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Stop listener for image, text and gif stream objects.
//        openGlView.setFilter(null);
        int itemId = item.getItemId();
        if (itemId == R.id.e_d_fxaa) {
            openGlView.enableAA(!openGlView.isAAEnabled());
            Toast.makeText(this,
                    "FXAA " + (openGlView.isAAEnabled() ? "enabled" : "disabled"),
                    Toast.LENGTH_SHORT).show();
            return true;
            //filters. NOTE: You can change filter values on fly without reset the filter.
            // Example:
            // ColorFilterRender color = new ColorFilterRender()
            // rtmpCamera2.setFilter(color);
            // color.setRGBColor(255, 0, 0); //red tint
        } else if (itemId == R.id.no_filter) {
            openGlView.setFilter(FilterRender.None);
            return true;
        } else if (itemId == R.id.analog_tv) {
            openGlView.setFilter(FilterRender.AnalogTV);
            return true;
        } else if (itemId == R.id.android_view) {
//            AndroidViewFilterRender androidViewFilterRender = new AndroidViewFilterRender();
//            androidViewFilterRender.setView(findViewById(R.id.switch_camera));
            openGlView.setFilter(FilterRender.AndroidView);
            return true;
        } else if (itemId == R.id.basic_deformation) {
            openGlView.setFilter(FilterRender.BasicDeformation);
            return true;
        } else if (itemId == R.id.beauty) {
            openGlView.setFilter(FilterRender.Beauty);
            return true;
        } else if (itemId == R.id.black) {
            openGlView.setFilter(FilterRender.Black);
            return true;
        } else if (itemId == R.id.blur) {
            openGlView.setFilter(FilterRender.Blur);
            return true;
        } else if (itemId == R.id.brightness) {
            openGlView.setFilter(FilterRender.Brightness);
            return true;
        } else if (itemId == R.id.cartoon) {
            openGlView.setFilter(FilterRender.Cartoon);
            return true;
        } else if (itemId == R.id.circle) {
            openGlView.setFilter(FilterRender.Circle);
            return true;
        } else if (itemId == R.id.color) {
            openGlView.setFilter(FilterRender.Color);
            return true;
        } else if (itemId == R.id.contrast) {
            openGlView.setFilter(FilterRender.Contrast);
            return true;
        } else if (itemId == R.id.duotone) {
            openGlView.setFilter(FilterRender.Duotone);
            return true;
        } else if (itemId == R.id.early_bird) {
            openGlView.setFilter(FilterRender.EarlyBird);
            return true;
        } else if (itemId == R.id.edge_detection) {
            openGlView.setFilter(FilterRender.EdgeDetection);
            return true;
        } else if (itemId == R.id.exposure) {
            openGlView.setFilter(FilterRender.Exposure);
            return true;
        } else if (itemId == R.id.fire) {
            openGlView.setFilter(FilterRender.Fire);
            return true;
        } else if (itemId == R.id.gamma) {
            openGlView.setFilter(FilterRender.Gamma);
            return true;
        } else if (itemId == R.id.glitch) {
            openGlView.setFilter(FilterRender.Glitch);
            return true;
        } else if (itemId == R.id.gif) {
            setGifToStream();
            return true;
        } else if (itemId == R.id.grey_scale) {
            openGlView.setFilter(FilterRender.GreyScale);
            return true;
        } else if (itemId == R.id.halftone_lines) {
            openGlView.setFilter(FilterRender.HalftoneLines);
            return true;
        } else if (itemId == R.id.image) {
            setImageToStream();
            return true;
        } else if (itemId == R.id.image_70s) {
            openGlView.setFilter(FilterRender.Image70s);
            return true;
        } else if (itemId == R.id.lamoish) {
            openGlView.setFilter(FilterRender.Lamoish);
            return true;
        } else if (itemId == R.id.money) {
            openGlView.setFilter(FilterRender.Money);
            return true;
        } else if (itemId == R.id.negative) {
            openGlView.setFilter(FilterRender.Negative);
            return true;
        } else if (itemId == R.id.pixelated) {
            openGlView.setFilter(FilterRender.Pixelated);
            return true;
        } else if (itemId == R.id.polygonization) {
            openGlView.setFilter(FilterRender.Polygonization);
            return true;
        } else if (itemId == R.id.rainbow) {
            openGlView.setFilter(FilterRender.Rainbow);
            return true;
        } else if (itemId == R.id.rgb_saturate) {
            FilterRender rgbSaturation = FilterRender.RGBSaturation;
            openGlView.setFilter(rgbSaturation);
            //Reduce green and blue colors 20%. Red will predominate.
            rgbSaturation.setRGBSaturation(1f, 0.8f, 0.8f);
            return true;
        } else if (itemId == R.id.ripple) {
            openGlView.setFilter(FilterRender.Ripple);
            return true;
        } else if (itemId == R.id.rotation) {
            FilterRender filterRender = FilterRender.Rotation;
            openGlView.setFilter(filterRender);
            filterRender.setRotation(90);
            return true;
        } else if (itemId == R.id.saturation) {
            openGlView.setFilter(FilterRender.Saturation);
            return true;
        } else if (itemId == R.id.sepia) {
            openGlView.setFilter(FilterRender.Sepia);
            return true;
        } else if (itemId == R.id.sharpness) {
            openGlView.setFilter(FilterRender.Sharpness);
            return true;
        } else if (itemId == R.id.snow) {
            openGlView.setFilter(FilterRender.Snow);
            return true;
        } else if (itemId == R.id.swirl) {
            openGlView.setFilter(FilterRender.Swirl);
            return true;
        } else if (itemId == R.id.surface_filter) {//You can render this filter with other api that draw in a surface. for example you can use VLC
            FilterRender surfaceFilterRender = FilterRender.Surface;
            openGlView.setFilter(surfaceFilterRender);
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.big_bunny_240p);
            mediaPlayer.setSurface(surfaceFilterRender.getSurface());
            mediaPlayer.start();
            //Video is 360x240 so select a percent to keep aspect ratio (50% x 33.3% screen)
            surfaceFilterRender.setScale(50f, 33.3f);
            openGlView.setFilter(surfaceFilterRender); //Optional
            return true;
        } else if (itemId == R.id.temperature) {
            openGlView.setFilter(FilterRender.Temperature);
            return true;
        } else if (itemId == R.id.text) {
            setTextToStream();
            return true;
        } else if (itemId == R.id.zebra) {
            openGlView.setFilter(FilterRender.Zebra);
            return true;
        }
        return false;
    }

    private void setTextToStream() {
        FilterRender textObject = FilterRender.TextObject;
        openGlView.setFilter(textObject);
        textObject.setText("Hello world", 22, Color.RED);
        textObject.setDefaultScale(openGlView.getStreamWidth(),
                openGlView.getStreamHeight());
        textObject.setPosition(Translate.CENTER);
        openGlView.setFilter(textObject); //Optional
    }

    private void setImageToStream() {
        FilterRender imageObjectFilterRender = FilterRender.ImageObject;
        openGlView.setFilter(imageObjectFilterRender);
        imageObjectFilterRender.setImage(
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        imageObjectFilterRender.setDefaultScale(openGlView.getStreamWidth(),
                openGlView.getStreamHeight());
        imageObjectFilterRender.setPosition(Translate.RIGHT);
        openGlView.setFilter(imageObjectFilterRender); //Optional
//        openGlView.setPreventMoveOutside(false); //Optional
    }

    private void setGifToStream() {
        try {
            FilterRender gifObjectFilterRender = FilterRender.GifObject;
            gifObjectFilterRender.setGif(getResources().openRawResource(R.raw.banana));
            openGlView.setFilter(gifObjectFilterRender);
            gifObjectFilterRender.setDefaultScale(openGlView.getStreamWidth(),
                    openGlView.getStreamHeight());
            gifObjectFilterRender.setPosition(Translate.BOTTOM);
            openGlView.setFilter(gifObjectFilterRender); //Optional
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.b_start_stop) {
            if (!openGlView.isStreaming()) {
                if (openGlView.isRecording()
                        || openGlView.prepareStream()) {
                    startButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_stop_white_48, null));
                    openGlView.startStream(liveStreamUrl);
                } else {
                    Toast.makeText(this, "Error preparing stream, This device cant do it",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                startButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_circle_outline_white_48, null));
                openGlView.stopStream();
            }
        } else if (id == R.id.switch_camera) {
            try {
                openGlView.switchCamera();
            } catch (UizaCameraOpenException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.b_record) {
            if (!openGlView.isRecording()) {
                try {
                    if (!folder.exists()) {
                        try {
                            folder.mkdir();
                        } catch (SecurityException ex) {
                            Toast.makeText(this, ex.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    currentDateAndTime = sdf.format(new Date());
                    if (!openGlView.isStreaming()) {
                        if (openGlView.prepareStream()) {
                            openGlView.startRecord(
                                    folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                            bRecord.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_record_white_24, null));
                            Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error preparing stream, This device cant do it",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        openGlView.startRecord(folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                        bRecord.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_stop_white_24, null));
                        Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    openGlView.stopRecord();
                    bRecord.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_record_white_24, null));
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                openGlView.stopRecord();
                bRecord.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_record_white_24, null));
                Toast.makeText(this,
                        "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
                        Toast.LENGTH_SHORT).show();
                currentDateAndTime = "";
            }
        }
    }

    @Override
    public void onConnectionSuccess() {
        runOnUiThread(() -> {
            Toast.makeText(UizaLiveActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onConnectionFailed(@Nullable final String reason) {
        runOnUiThread(() -> {
            Toast.makeText(UizaLiveActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
                    .show();
        });
    }

    @Override
    public void onNewBitrate(long bitrate) {
        UizaLog.e("UizaOpenGLView", "newBitrate: " + bitrate);
    }

    @Override
    public void onDisconnect() {
        runOnUiThread(() -> {
            startButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_circle_outline_white_48, null));
            Toast.makeText(UizaLiveActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onAuthError() {
        runOnUiThread(() -> Toast.makeText(UizaLiveActivity.this, "Auth error", Toast.LENGTH_SHORT).show());

    }

    @Override
    public void onAuthSuccess() {
        runOnUiThread(() -> {
            Toast.makeText(UizaLiveActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
        });
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
}
