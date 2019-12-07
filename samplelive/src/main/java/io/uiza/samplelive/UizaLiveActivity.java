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

import io.uiza.live.UizaLiveView;
import io.uiza.live.enums.FilterRender;
import io.uiza.live.enums.RecordStatus;
import io.uiza.live.enums.Translate;
import io.uiza.live.interfaces.CameraChangeListener;
import io.uiza.live.interfaces.RecordListener;
import io.uiza.live.interfaces.UizaCameraOpenException;
import io.uiza.live.interfaces.UizaLiveListener;
import timber.log.Timber;

public class UizaLiveActivity extends AppCompatActivity implements UizaLiveListener,
        View.OnClickListener, RecordListener, CameraChangeListener {

    private static final String TAG = "UizaLiveActivity";
    private static final String RECORD_FOLDER = "uiza-live";
    private UizaMediaButton startButton;
    private UizaMediaButton bRecord;

    private String liveStreamUrl;
    private String currentDateAndTime = "";
    private File folder;
    private UizaLiveView liveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live_stream);
        liveView = findViewById(R.id.uiza_open_glview);
        liveView.setLiveListener(this);
        startButton = findViewById(R.id.b_start_stop);
        startButton.setOnClickListener(this);
        startButton.setEnabled(false);
        bRecord = findViewById(R.id.b_record);
        bRecord.setOnClickListener(this);
        AppCompatImageButton switchCamera = findViewById(R.id.switch_camera);
        switchCamera.setOnClickListener(this);
        File movieFolder = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (movieFolder != null)
            folder = new File(movieFolder.getAbsolutePath()
                    + RECORD_FOLDER);
        liveStreamUrl = getIntent().getStringExtra(SampleLiveApplication.EXTRA_STREAM_ENDPOINT);
        if (TextUtils.isEmpty(liveStreamUrl)) {
            liveStreamUrl = SampleLiveApplication.getLiveEndpoint();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (liveView.supportFilter()) {
            getMenuInflater().inflate(R.menu.gl_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Stop listener for image, text and gif stream objects.
//        openGlView.setFilter(null);
        int itemId = item.getItemId();
        if (itemId == R.id.e_d_fxaa) {
            liveView.enableAA(!liveView.isAAEnabled());
            Toast.makeText(this,
                    "FXAA " + (liveView.isAAEnabled() ? "enabled" : "disabled"),
                    Toast.LENGTH_SHORT).show();
            return true;
            //filters. NOTE: You can change filter values on fly without reset the filter.
            // Example:
            // ColorFilterRender color = new ColorFilterRender()
            // rtmpCamera2.setFilter(color);
            // color.setRGBColor(255, 0, 0); //red tint
        } else if (itemId == R.id.no_filter) {
            liveView.setFilter(FilterRender.None);
            return true;
        } else if (itemId == R.id.analog_tv) {
            liveView.setFilter(FilterRender.AnalogTV);
            return true;
        } else if (itemId == R.id.android_view) {
//            AndroidViewFilterRender androidViewFilterRender = new AndroidViewFilterRender();
//            androidViewFilterRender.setView(findViewById(R.id.switch_camera));
            liveView.setFilter(FilterRender.AndroidView);
            return true;
        } else if (itemId == R.id.basic_deformation) {
            liveView.setFilter(FilterRender.BasicDeformation);
            return true;
        } else if (itemId == R.id.beauty) {
            liveView.setFilter(FilterRender.Beauty);
            return true;
        } else if (itemId == R.id.black) {
            liveView.setFilter(FilterRender.Black);
            return true;
        } else if (itemId == R.id.blur) {
            liveView.setFilter(FilterRender.Blur);
            return true;
        } else if (itemId == R.id.brightness) {
            liveView.setFilter(FilterRender.Brightness);
            return true;
        } else if (itemId == R.id.cartoon) {
            liveView.setFilter(FilterRender.Cartoon);
            return true;
        } else if (itemId == R.id.circle) {
            liveView.setFilter(FilterRender.Circle);
            return true;
        } else if (itemId == R.id.color) {
            liveView.setFilter(FilterRender.Color);
            return true;
        } else if (itemId == R.id.contrast) {
            liveView.setFilter(FilterRender.Contrast);
            return true;
        } else if (itemId == R.id.duotone) {
            liveView.setFilter(FilterRender.Duotone);
            return true;
        } else if (itemId == R.id.early_bird) {
            liveView.setFilter(FilterRender.EarlyBird);
            return true;
        } else if (itemId == R.id.edge_detection) {
            liveView.setFilter(FilterRender.EdgeDetection);
            return true;
        } else if (itemId == R.id.exposure) {
            liveView.setFilter(FilterRender.Exposure);
            return true;
        } else if (itemId == R.id.fire) {
            liveView.setFilter(FilterRender.Fire);
            return true;
        } else if (itemId == R.id.gamma) {
            liveView.setFilter(FilterRender.Gamma);
            return true;
        } else if (itemId == R.id.glitch) {
            liveView.setFilter(FilterRender.Glitch);
            return true;
        } else if (itemId == R.id.gif) {
            setGifToStream();
            return true;
        } else if (itemId == R.id.grey_scale) {
            liveView.setFilter(FilterRender.GreyScale);
            return true;
        } else if (itemId == R.id.halftone_lines) {
            liveView.setFilter(FilterRender.HalftoneLines);
            return true;
        } else if (itemId == R.id.image) {
            setImageToStream();
            return true;
        } else if (itemId == R.id.image_70s) {
            liveView.setFilter(FilterRender.Image70s);
            return true;
        } else if (itemId == R.id.lamoish) {
            liveView.setFilter(FilterRender.Lamoish);
            return true;
        } else if (itemId == R.id.money) {
            liveView.setFilter(FilterRender.Money);
            return true;
        } else if (itemId == R.id.negative) {
            liveView.setFilter(FilterRender.Negative);
            return true;
        } else if (itemId == R.id.pixelated) {
            liveView.setFilter(FilterRender.Pixelated);
            return true;
        } else if (itemId == R.id.polygonization) {
            liveView.setFilter(FilterRender.Polygonization);
            return true;
        } else if (itemId == R.id.rainbow) {
            liveView.setFilter(FilterRender.Rainbow);
            return true;
        } else if (itemId == R.id.rgb_saturate) {
            FilterRender rgbSaturation = FilterRender.RGBSaturation;
            liveView.setFilter(rgbSaturation);
            //Reduce green and blue colors 20%. Red will predominate.
            rgbSaturation.setRGBSaturation(1f, 0.8f, 0.8f);
            return true;
        } else if (itemId == R.id.ripple) {
            liveView.setFilter(FilterRender.Ripple);
            return true;
        } else if (itemId == R.id.rotation) {
            FilterRender filterRender = FilterRender.Rotation;
            liveView.setFilter(filterRender);
            filterRender.setRotation(90);
            return true;
        } else if (itemId == R.id.saturation) {
            liveView.setFilter(FilterRender.Saturation);
            return true;
        } else if (itemId == R.id.sepia) {
            liveView.setFilter(FilterRender.Sepia);
            return true;
        } else if (itemId == R.id.sharpness) {
            liveView.setFilter(FilterRender.Sharpness);
            return true;
        } else if (itemId == R.id.snow) {
            liveView.setFilter(FilterRender.Snow);
            return true;
        } else if (itemId == R.id.swirl) {
            liveView.setFilter(FilterRender.Swirl);
            return true;
        } else if (itemId == R.id.surface_filter) {//You can render this filter with other api that draw in a surface. for example you can use VLC
            FilterRender surfaceFilterRender = FilterRender.Surface;
            liveView.setFilter(surfaceFilterRender);
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.big_bunny_240p);
            mediaPlayer.setSurface(surfaceFilterRender.getSurface());
            mediaPlayer.start();
            //Video is 360x240 so select a percent to keep aspect ratio (50% x 33.3% screen)
            surfaceFilterRender.setScale(50f, 33.3f);
            liveView.setFilter(surfaceFilterRender); //Optional
            return true;
        } else if (itemId == R.id.temperature) {
            liveView.setFilter(FilterRender.Temperature);
            return true;
        } else if (itemId == R.id.text) {
            setTextToStream();
            return true;
        } else if (itemId == R.id.zebra) {
            liveView.setFilter(FilterRender.Zebra);
            return true;
        }
        return false;
    }

    private void setTextToStream() {
        FilterRender textObject = FilterRender.TextObject;
        liveView.setFilter(textObject);
        textObject.setText("Hello world", 22, Color.RED);
        textObject.setDefaultScale(liveView.getStreamWidth(),
                liveView.getStreamHeight());
        textObject.setPosition(Translate.CENTER);
        liveView.setFilter(textObject); //Optional
    }

    private void setImageToStream() {
        FilterRender imageObjectFilterRender = FilterRender.ImageObject;
        liveView.setFilter(imageObjectFilterRender);
        imageObjectFilterRender.setImage(
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        imageObjectFilterRender.setDefaultScale(liveView.getStreamWidth(),
                liveView.getStreamHeight());
        imageObjectFilterRender.setPosition(Translate.RIGHT);
        liveView.setFilter(imageObjectFilterRender); //Optional
//        liveView.setPreventMoveOutside(false); //Optional
    }

    private void setGifToStream() {
        try {
            FilterRender gifObjectFilterRender = FilterRender.GifObject;
            gifObjectFilterRender.setGif(getResources().openRawResource(R.raw.banana));
            liveView.setFilter(gifObjectFilterRender);
            gifObjectFilterRender.setDefaultScale(liveView.getStreamWidth(),
                    liveView.getStreamHeight());
            gifObjectFilterRender.setPosition(Translate.BOTTOM);
            liveView.setFilter(gifObjectFilterRender); //Optional
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recordAction();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void recordAction() {
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
            if (!liveView.isStreaming()) {
                if (liveView.prepareStream()) {
                    liveView.startRecord(
                            folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                } else {
                    Toast.makeText(this, "Error preparing stream, This device cant do it",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                liveView.startRecord(folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
            }
        } catch (IOException e) {
            liveView.stopRecord();
            bRecord.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_record_white_24, null));
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.b_start_stop) {
            if (!liveView.isStreaming()) {
                if (liveView.isRecording()
                        || liveView.prepareStream()) {
                    liveView.startStream(liveStreamUrl);
                } else {
                    Toast.makeText(this, "Error preparing stream, This device cant do it",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                liveView.stopStream();
            }
        } else if (id == R.id.switch_camera) {
            try {
                liveView.switchCamera();
            } catch (UizaCameraOpenException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.b_record) {
            if (!liveView.isRecording()) {
                ActivityCompat.requestPermissions(UizaLiveActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            } else {
                liveView.stopRecord();
            }
        }
    }

    @Override
    public void onInit(boolean success) {
        startButton.setEnabled(success);
        liveView.setCameraChangeListener(this);
        liveView.setRecordListener(this);
    }

    @Override
    public void onConnectionSuccess() {
        startButton.setChecked(true);
        Toast.makeText(UizaLiveActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRetryConnection(long delay) {
        Toast.makeText(UizaLiveActivity.this, "Retry " + delay / 1000 + " s", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onConnectionFailed(@Nullable final String reason) {
        Toast.makeText(UizaLiveActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onNewBitrate(long bitrate) {
        Timber.e("newBitrate: %ld", bitrate);
    }

    @Override
    public void onDisconnect() {
        startButton.setChecked(false);
        Toast.makeText(UizaLiveActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthError() {
        Toast.makeText(UizaLiveActivity.this, "Auth error", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAuthSuccess() {
        Toast.makeText(UizaLiveActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void surfaceCreated() {
        Timber.e("surfaceCreated");
    }

    @Override
    public void surfaceChanged(int format, int width, int height) {
        Timber.e("surfaceChanged: {" + format + ", " + width + ", " + height + "}");
    }

    @Override
    public void surfaceDestroyed() {
        Timber.e("surfaceDestroyed");
    }

    @Override
    public void onCameraChange(boolean isFrontCamera) {
        Timber.e("onCameraChange: %b", isFrontCamera);
    }

    @Override
    public void onStatusChange(RecordStatus status) {
        bRecord.setChecked(status == RecordStatus.RECORDING);

        if (status == RecordStatus.RECORDING) {
            Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show();
        } else if (status == RecordStatus.STOPPED) {
            currentDateAndTime = "";
            Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(UizaLiveActivity.this, "Record " + status.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
