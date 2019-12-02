package io.uiza.uiza_sdk_player.live;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.res.ResourcesCompat;

import com.pedro.encoder.input.gl.render.filters.AnalogTVFilterRender;
import com.pedro.encoder.input.gl.render.filters.AndroidViewFilterRender;
import com.pedro.encoder.input.gl.render.filters.BasicDeformationFilterRender;
import com.pedro.encoder.input.gl.render.filters.BeautyFilterRender;
import com.pedro.encoder.input.gl.render.filters.BlackFilterRender;
import com.pedro.encoder.input.gl.render.filters.BlurFilterRender;
import com.pedro.encoder.input.gl.render.filters.BrightnessFilterRender;
import com.pedro.encoder.input.gl.render.filters.CartoonFilterRender;
import com.pedro.encoder.input.gl.render.filters.CircleFilterRender;
import com.pedro.encoder.input.gl.render.filters.ColorFilterRender;
import com.pedro.encoder.input.gl.render.filters.ContrastFilterRender;
import com.pedro.encoder.input.gl.render.filters.DuotoneFilterRender;
import com.pedro.encoder.input.gl.render.filters.EarlyBirdFilterRender;
import com.pedro.encoder.input.gl.render.filters.EdgeDetectionFilterRender;
import com.pedro.encoder.input.gl.render.filters.ExposureFilterRender;
import com.pedro.encoder.input.gl.render.filters.FireFilterRender;
import com.pedro.encoder.input.gl.render.filters.GammaFilterRender;
import com.pedro.encoder.input.gl.render.filters.GlitchFilterRender;
import com.pedro.encoder.input.gl.render.filters.GreyScaleFilterRender;
import com.pedro.encoder.input.gl.render.filters.HalftoneLinesFilterRender;
import com.pedro.encoder.input.gl.render.filters.Image70sFilterRender;
import com.pedro.encoder.input.gl.render.filters.LamoishFilterRender;
import com.pedro.encoder.input.gl.render.filters.MoneyFilterRender;
import com.pedro.encoder.input.gl.render.filters.NegativeFilterRender;
import com.pedro.encoder.input.gl.render.filters.NoFilterRender;
import com.pedro.encoder.input.gl.render.filters.PixelatedFilterRender;
import com.pedro.encoder.input.gl.render.filters.PolygonizationFilterRender;
import com.pedro.encoder.input.gl.render.filters.RGBSaturationFilterRender;
import com.pedro.encoder.input.gl.render.filters.RainbowFilterRender;
import com.pedro.encoder.input.gl.render.filters.RippleFilterRender;
import com.pedro.encoder.input.gl.render.filters.RotationFilterRender;
import com.pedro.encoder.input.gl.render.filters.SaturationFilterRender;
import com.pedro.encoder.input.gl.render.filters.SepiaFilterRender;
import com.pedro.encoder.input.gl.render.filters.SharpnessFilterRender;
import com.pedro.encoder.input.gl.render.filters.SnowFilterRender;
import com.pedro.encoder.input.gl.render.filters.SwirlFilterRender;
import com.pedro.encoder.input.gl.render.filters.TemperatureFilterRender;
import com.pedro.encoder.input.gl.render.filters.ZebraFilterRender;
import com.pedro.encoder.input.gl.render.filters.object.GifObjectFilterRender;
import com.pedro.encoder.input.gl.render.filters.object.ImageObjectFilterRender;
import com.pedro.encoder.input.gl.render.filters.object.SurfaceFilterRender;
import com.pedro.encoder.input.gl.render.filters.object.TextObjectFilterRender;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.encoder.utils.gl.TranslateTo;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.uiza.core.utils.UizaLog;
import io.uiza.live.UizaOpenGLView;
import io.uiza.live.interfaces.UizaLiveListener;
import io.uiza.uiza_sdk_player.R;
import io.uiza.uiza_sdk_player.SampleApplication;

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
        bRecord = findViewById(R.id.b_record);
        bRecord.setOnClickListener(this);
        AppCompatImageButton switchCamera = findViewById(R.id.switch_camera);
        switchCamera.setOnClickListener(this);
        liveStreamUrl = getIntent().getStringExtra(SampleApplication.EXTRA_STREAM_ENDPOINT);
        if (TextUtils.isEmpty(liveStreamUrl)) {
            Toast.makeText(this,
                    "Live stream url is empty", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gl_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Stop listener for image, text and gif stream objects.
        openGlView.setBaseObjectFilterRender(null);
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
            openGlView.setFilter(new NoFilterRender());
            return true;
        } else if (itemId == R.id.analog_tv) {
            openGlView.setFilter(new AnalogTVFilterRender());
            return true;
        } else if (itemId == R.id.android_view) {
            AndroidViewFilterRender androidViewFilterRender = new AndroidViewFilterRender();
            androidViewFilterRender.setView(findViewById(R.id.switch_camera));
            openGlView.setFilter(androidViewFilterRender);
            return true;
        } else if (itemId == R.id.basic_deformation) {
            openGlView.setFilter(new BasicDeformationFilterRender());
            return true;
        } else if (itemId == R.id.beauty) {
            openGlView.setFilter(new BeautyFilterRender());
            return true;
        } else if (itemId == R.id.black) {
            openGlView.setFilter(new BlackFilterRender());
            return true;
        } else if (itemId == R.id.blur) {
            openGlView.setFilter(new BlurFilterRender());
            return true;
        } else if (itemId == R.id.brightness) {
            openGlView.setFilter(new BrightnessFilterRender());
            return true;
        } else if (itemId == R.id.cartoon) {
            openGlView.setFilter(new CartoonFilterRender());
            return true;
        } else if (itemId == R.id.circle) {
            openGlView.setFilter(new CircleFilterRender());
            return true;
        } else if (itemId == R.id.color) {
            openGlView.setFilter(new ColorFilterRender());
            return true;
        } else if (itemId == R.id.contrast) {
            openGlView.setFilter(new ContrastFilterRender());
            return true;
        } else if (itemId == R.id.duotone) {
            openGlView.setFilter(new DuotoneFilterRender());
            return true;
        } else if (itemId == R.id.early_bird) {
            openGlView.setFilter(new EarlyBirdFilterRender());
            return true;
        } else if (itemId == R.id.edge_detection) {
            openGlView.setFilter(new EdgeDetectionFilterRender());
            return true;
        } else if (itemId == R.id.exposure) {
            openGlView.setFilter(new ExposureFilterRender());
            return true;
        } else if (itemId == R.id.fire) {
            openGlView.setFilter(new FireFilterRender());
            return true;
        } else if (itemId == R.id.gamma) {
            openGlView.setFilter(new GammaFilterRender());
            return true;
        } else if (itemId == R.id.glitch) {
            openGlView.setFilter(new GlitchFilterRender());
            return true;
        } else if (itemId == R.id.gif) {
            setGifToStream();
            return true;
        } else if (itemId == R.id.grey_scale) {
            openGlView.setFilter(new GreyScaleFilterRender());
            return true;
        } else if (itemId == R.id.halftone_lines) {
            openGlView.setFilter(new HalftoneLinesFilterRender());
            return true;
        } else if (itemId == R.id.image) {
            setImageToStream();
            return true;
        } else if (itemId == R.id.image_70s) {
            openGlView.setFilter(new Image70sFilterRender());
            return true;
        } else if (itemId == R.id.lamoish) {
            openGlView.setFilter(new LamoishFilterRender());
            return true;
        } else if (itemId == R.id.money) {
            openGlView.setFilter(new MoneyFilterRender());
            return true;
        } else if (itemId == R.id.negative) {
            openGlView.setFilter(new NegativeFilterRender());
            return true;
        } else if (itemId == R.id.pixelated) {
            openGlView.setFilter(new PixelatedFilterRender());
            return true;
        } else if (itemId == R.id.polygonization) {
            openGlView.setFilter(new PolygonizationFilterRender());
            return true;
        } else if (itemId == R.id.rainbow) {
            openGlView.setFilter(new RainbowFilterRender());
            return true;
        } else if (itemId == R.id.rgb_saturate) {
            RGBSaturationFilterRender rgbSaturationFilterRender = new RGBSaturationFilterRender();
            openGlView.setFilter(rgbSaturationFilterRender);
            //Reduce green and blue colors 20%. Red will predominate.
            rgbSaturationFilterRender.setRGBSaturation(1f, 0.8f, 0.8f);
            return true;
        } else if (itemId == R.id.ripple) {
            openGlView.setFilter(new RippleFilterRender());
            return true;
        } else if (itemId == R.id.rotation) {
            RotationFilterRender rotationFilterRender = new RotationFilterRender();
            openGlView.setFilter(rotationFilterRender);
            rotationFilterRender.setRotation(90);
            return true;
        } else if (itemId == R.id.saturation) {
            openGlView.setFilter(new SaturationFilterRender());
            return true;
        } else if (itemId == R.id.sepia) {
            openGlView.setFilter(new SepiaFilterRender());
            return true;
        } else if (itemId == R.id.sharpness) {
            openGlView.setFilter(new SharpnessFilterRender());
            return true;
        } else if (itemId == R.id.snow) {
            openGlView.setFilter(new SnowFilterRender());
            return true;
        } else if (itemId == R.id.swirl) {
            openGlView.setFilter(new SwirlFilterRender());
            return true;
        } else if (itemId == R.id.surface_filter) {//You can render this filter with other api that draw in a surface. for example you can use VLC
            SurfaceFilterRender surfaceFilterRender = new SurfaceFilterRender();
            openGlView.setFilter(surfaceFilterRender);
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.big_bunny_240p);
            mediaPlayer.setSurface(surfaceFilterRender.getSurface());
            mediaPlayer.start();
            //Video is 360x240 so select a percent to keep aspect ratio (50% x 33.3% screen)
            surfaceFilterRender.setScale(50f, 33.3f);
            openGlView.setBaseObjectFilterRender(surfaceFilterRender); //Optional
            return true;
        } else if (itemId == R.id.temperature) {
            openGlView.setFilter(new TemperatureFilterRender());
            return true;
        } else if (itemId == R.id.text) {
            setTextToStream();
            return true;
        } else if (itemId == R.id.zebra) {
            openGlView.setFilter(new ZebraFilterRender());
            return true;
        }
        return false;
    }

    private void setTextToStream() {
        TextObjectFilterRender textObjectFilterRender = new TextObjectFilterRender();
        openGlView.setFilter(textObjectFilterRender);
        textObjectFilterRender.setText("Hello world", 22, Color.RED);
        textObjectFilterRender.setDefaultScale(openGlView.getStreamWidth(),
                openGlView.getStreamHeight());
        textObjectFilterRender.setPosition(TranslateTo.CENTER);
        openGlView.setBaseObjectFilterRender(textObjectFilterRender); //Optional
    }

    private void setImageToStream() {
        ImageObjectFilterRender imageObjectFilterRender = new ImageObjectFilterRender();
        openGlView.setFilter(imageObjectFilterRender);
        imageObjectFilterRender.setImage(
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        imageObjectFilterRender.setDefaultScale(openGlView.getStreamWidth(),
                openGlView.getStreamHeight());
        imageObjectFilterRender.setPosition(TranslateTo.RIGHT);
        openGlView.setBaseObjectFilterRender(imageObjectFilterRender); //Optional
        openGlView.setPreventMoveOutside(false); //Optional
    }

    private void setGifToStream() {
        try {
            GifObjectFilterRender gifObjectFilterRender = new GifObjectFilterRender();
            gifObjectFilterRender.setGif(getResources().openRawResource(R.raw.banana));
            openGlView.setFilter(gifObjectFilterRender);
            gifObjectFilterRender.setDefaultScale(openGlView.getStreamWidth(),
                    openGlView.getStreamHeight());
            gifObjectFilterRender.setPosition(TranslateTo.BOTTOM);
            openGlView.setBaseObjectFilterRender(gifObjectFilterRender); //Optional
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
            } catch (CameraOpenException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.b_record) {
            if (!openGlView.isRecording()) {
                try {
                    if (!folder.exists()) {
                        folder.mkdir();
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
                        bRecord.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.exo_icon_stop, null));
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
        runOnUiThread(() -> Toast.makeText(UizaLiveActivity.this, "Connection success", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onConnectionFailed(@Nullable final String reason) {
        runOnUiThread(() -> Toast.makeText(UizaLiveActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
                .show());
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
        runOnUiThread(() -> Toast.makeText(UizaLiveActivity.this, "Auth success", Toast.LENGTH_SHORT).show());
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
