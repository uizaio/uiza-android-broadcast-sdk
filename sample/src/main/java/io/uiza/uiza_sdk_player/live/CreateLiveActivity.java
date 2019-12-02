package io.uiza.uiza_sdk_player.live;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.widget.NestedScrollView;

import io.reactivex.Observable;
import io.uiza.core.models.CreateEntityBody;
import io.uiza.core.models.UizaEntity;
import io.uiza.core.utils.ObservableKt;
import io.uiza.core.utils.StringKt;
import io.uiza.uiza_sdk_player.R;
import io.uiza.uiza_sdk_player.SampleApplication;

public class CreateLiveActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatEditText streamNameEdt;
    NestedScrollView contentScroll;
    TextView content;
    AppCompatButton liveBtn;
    ProgressBar progressBar;
    UizaEntity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_live);
        streamNameEdt = findViewById(R.id.stream_name);
        contentScroll = findViewById(R.id.scrollView);
        content = findViewById(R.id.content);
        liveBtn = findViewById(R.id.live_btn);
        progressBar = findViewById(R.id.progress_bar);
        liveBtn.setOnClickListener(this);
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
                            Intent liveIntent = new Intent(CreateLiveActivity.this, UizaLiveActivity.class);
                            liveIntent.putExtra(SampleApplication.EXTRA_STREAM_ENDPOINT, entity.ingest.getLiveUrl());
                            startActivity(liveIntent);
                        } else {
                            Toast.makeText(this, "No Live url", Toast.LENGTH_SHORT).show();
                        }

                    } else if (entity.needGetInfo()) {
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
        CreateEntityBody body = new CreateEntityBody(streamName, "Uiza Demo Live Stream", SampleApplication.REGION, SampleApplication.APP_ID, SampleApplication.USER_ID);
        Observable<UizaEntity> obs = ((SampleApplication) getApplication()).getLiveService().createEntity(body);
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
        Observable<UizaEntity> obs = ((SampleApplication) getApplication()).getLiveService().getEntity(entityId);
        ObservableKt.execSubscribe(obs, ent -> {
            entity = ent;
            content.setText(StringKt.toPrettyFormat(ent.toString()));
            updateLiveStats();
            progressBar.setVisibility(View.GONE);
        }, throwable -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            updateLiveStats();
        });
    }

}
