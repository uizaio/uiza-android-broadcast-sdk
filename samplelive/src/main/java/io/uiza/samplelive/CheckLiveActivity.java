package io.uiza.samplelive;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.widget.NestedScrollView;

import io.reactivex.Observable;
import io.uiza.core.models.CreateLiveEntityBody;
import io.uiza.core.models.LiveEntity;
import io.uiza.core.utils.ObservableKt;
import io.uiza.core.utils.StringKt;
import timber.log.Timber;

public class CheckLiveActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_ENTITY = "uiza_extra_entity";
    AppCompatEditText streamNameEdt;
    NestedScrollView contentScroll;
    TextView content;
    AppCompatButton liveBtn;
    ProgressBar progressBar;
    LiveEntity entity;

    Handler handler = new Handler();

    private static final int MAX_RETRY = 10;
    int currentRetry = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_live);
        streamNameEdt = findViewById(R.id.stream_name);
        contentScroll = findViewById(R.id.scrollView);
        content = findViewById(R.id.content);
        liveBtn = findViewById(R.id.live_btn);
        progressBar = findViewById(R.id.progress_bar);
        liveBtn.setOnClickListener(this);
        entity = getIntent().getParcelableExtra(EXTRA_ENTITY);
        if (entity != null) {
            content.setText(StringKt.toPrettyFormat(entity.toString()));
        }
        updateLiveStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLiveStats();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.live_btn) {
            if (streamNameEdt.getVisibility() == View.VISIBLE) {
                createLive(streamNameEdt.getText().toString());
            } else {
                if (entity != null) {
                    if (entity.hasLive()) {
                        if (entity.ingest != null) {
                            Intent liveIntent = new Intent(CheckLiveActivity.this, UizaLiveActivity.class);
                            liveIntent.putExtra(SampleLiveApplication.EXTRA_STREAM_ENDPOINT, entity.ingest.getLiveUrl());
                            startActivity(liveIntent);
                            finish();
                        } else {
                            Toast.makeText(this, "No Live url", Toast.LENGTH_SHORT).show();
                        }

                    } else if (entity.needGetInfo()) {
                        currentRetry = 0;
                        liveBtn.setEnabled(false);
                        getEntity(entity.id);
                    }

                } else {
                    Toast.makeText(this, "No Action", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void updateLiveStats() {
        if (entity == null) {
            streamNameEdt.setVisibility(View.VISIBLE);
            contentScroll.setVisibility(View.GONE);
            liveBtn.setText("Create Live");
        } else {
            streamNameEdt.setVisibility(View.GONE);
            contentScroll.setVisibility(View.VISIBLE);
            if (entity.needGetInfo()) {
                liveBtn.setText("Check Status");
            } else if (entity.hasLive()) {
                liveBtn.setText("Go Live");
            }
        }
    }

    private void createLive(String streamName) {
        progressBar.setVisibility(View.VISIBLE);
        CreateLiveEntityBody body = new CreateLiveEntityBody(streamName, "Uiza Demo Live Stream", SampleLiveApplication.REGION, SampleLiveApplication.APP_ID, SampleLiveApplication.USER_ID);
        Observable<LiveEntity> obs = ((SampleLiveApplication) getApplication()).getLiveService().createEntity(body);
        ObservableKt.execSubscribe(obs, res -> {
            entity = res;
            content.setText(StringKt.toPrettyFormat(res.toString()));
            updateLiveStats();
            progressBar.setVisibility(View.GONE);
        }, throwable -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            updateLiveStats();
        });
    }

    private void getEntity(String entityId) {
        progressBar.setVisibility(View.VISIBLE);
        Observable<LiveEntity> obs = ((SampleLiveApplication) getApplication()).getLiveService().getEntity(entityId);
        ObservableKt.execSubscribe(obs, ent -> {
            entity = ent;
            content.setText(StringKt.toPrettyFormat(ent.toString()));
            if (!entity.hasLive() && currentRetry < MAX_RETRY) {
                Timber.e("currentRetry: %d", currentRetry);
                currentRetry += 1;
                handler.postDelayed(() -> getEntity(entityId), 3000);
            } else {
                updateLiveStats();
                progressBar.setVisibility(View.GONE);
                liveBtn.setEnabled(true);
            }
        }, throwable -> {
            progressBar.setVisibility(View.GONE);
            liveBtn.setEnabled(true);
            Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            updateLiveStats();
        });
    }

}
