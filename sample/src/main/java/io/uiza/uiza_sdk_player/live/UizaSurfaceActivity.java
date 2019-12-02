package io.uiza.uiza_sdk_player.live;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.uiza.uiza_sdk_player.R;
import io.uiza.uiza_sdk_player.SampleApplication;

public class UizaSurfaceActivity extends AppCompatActivity
        implements ConnectCheckerRtmp, View.OnClickListener, SurfaceHolder.Callback {


    private RtmpCamera2 rtmpCamera2;
    private Button liveButton;
    private Button bRecord;
    private EditText etUrl;

    private String currentDateAndTime = "";
    private File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/uiza-live");

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_surface_view);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        liveButton = findViewById(R.id.b_start_stop);
        liveButton.setOnClickListener(this);
        bRecord = findViewById(R.id.b_record);
        bRecord.setOnClickListener(this);
        Button switchCamera = findViewById(R.id.switch_camera);
        switchCamera.setOnClickListener(this);
        etUrl = findViewById(R.id.et_rtp_url);
        etUrl.setHint(R.string.hint_rtmp);
        rtmpCamera2 = new RtmpCamera2(surfaceView, this);
        rtmpCamera2.setReTries(10);
        surfaceView.getHolder().addCallback(this);
        String endpoint = getIntent().getStringExtra(SampleApplication.EXTRA_STREAM_ENDPOINT);
        if (endpoint != null) {
            etUrl.setText(endpoint);
        }
        ActivityCompat.requestPermissions(UizaSurfaceActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 1001);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1001) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                liveButton.setEnabled(true);
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                liveButton.setEnabled(false);
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(UizaSurfaceActivity.this, "Permission denied to access your Camera and Record Audio", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UizaSurfaceActivity.this, "Connection success", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void onConnectionFailedRtmp(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rtmpCamera2.shouldRetry(reason)) {
                    Toast.makeText(UizaSurfaceActivity.this, "Retry", Toast.LENGTH_SHORT)
                            .show();
                    rtmpCamera2.reTry(5000);  //Wait 5s and retry connect stream
                } else {
                    Toast.makeText(UizaSurfaceActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT).show();
                    rtmpCamera2.stopStream();
                    liveButton.setText(R.string.start_button);
                }
            }
        });
    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {

    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UizaSurfaceActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UizaSurfaceActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UizaSurfaceActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.b_start_stop) {
            if (!rtmpCamera2.isStreaming()) {
                if (rtmpCamera2.isRecording()
                        || rtmpCamera2.prepareAudio() && rtmpCamera2.prepareVideo()) {
                    liveButton.setText(R.string.stop_button);
                    rtmpCamera2.startStream(etUrl.getText().toString());
                } else {
                    Toast.makeText(this, "Error preparing stream, This device cant do it",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                liveButton.setText(R.string.start_button);
                rtmpCamera2.stopStream();
            }
        } else if (id == R.id.switch_camera) {
            try {
                rtmpCamera2.switchCamera();
            } catch (CameraOpenException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.b_record) {
            if (!rtmpCamera2.isRecording()) {
                try {
                    if (!folder.exists()) {
                        folder.mkdir();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    currentDateAndTime = sdf.format(new Date());
                    if (!rtmpCamera2.isStreaming()) {
                        if (rtmpCamera2.prepareAudio() && rtmpCamera2.prepareVideo()) {
                            rtmpCamera2.startRecord(
                                    folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                            bRecord.setText(R.string.stop_record);
                            Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error preparing stream, This device cant do it",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        rtmpCamera2.startRecord(folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                        bRecord.setText(R.string.stop_record);
                        Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    rtmpCamera2.stopRecord();
                    bRecord.setText(R.string.start_record);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                rtmpCamera2.stopRecord();
                bRecord.setText(R.string.start_record);
                Toast.makeText(this,
                        "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
                        Toast.LENGTH_SHORT).show();
                currentDateAndTime = "";
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        rtmpCamera2.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (rtmpCamera2.isRecording()) {
            rtmpCamera2.stopRecord();
            bRecord.setText(R.string.start_record);
            Toast.makeText(this,
                    "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
                    Toast.LENGTH_SHORT).show();
            currentDateAndTime = "";
        }
        if (rtmpCamera2.isStreaming()) {
            rtmpCamera2.stopStream();
            liveButton.setText(getResources().getString(R.string.start_button));
        }
        rtmpCamera2.stopPreview();
    }
}
