package io.uiza.uiza_sdk_player;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.exoplayer2.util.Assertions;

import io.uiza.core.models.UizaEntity;
import io.uiza.core.utils.StringKt;
import io.uiza.core.utils.UizaLog;
import io.uiza.player.UizaPlayerActivity;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatTextView content;
    DrmInfo drmInfo = null;
    UizaEntity entity = null;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_info);
        content = findViewById(R.id.content);
        entity = getIntent().getParcelableExtra("extra_uiza_entity");
        assert (entity != null);
        content.setText(StringKt.toPrettyFormat(entity.toString()));
        findViewById(R.id.playBtn).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playBtn) {
            UizaLog.e("InfoActivity", "uri = " + entity.playback.hls);
            startActivity(buildIntent(false, UizaPlayerActivity.ABR_ALGORITHM_RANDOM, entity.playback.hls));
        }
    }

    public Intent buildIntent(boolean preferExtensionDecoders, String abrAlgorithm, String uri) {
        Intent intent = new Intent();
        intent.putExtra(UizaPlayerActivity.PREFER_EXTENSION_DECODERS_EXTRA, preferExtensionDecoders);
        intent.putExtra(UizaPlayerActivity.ABR_ALGORITHM_EXTRA, abrAlgorithm);
        uri = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8";
        intent.setData(Uri.parse(uri));
        intent.putExtra(UizaPlayerActivity.EXTENSION_EXTRA, "m3u8");
        intent.setAction(UizaPlayerActivity.ACTION_VIEW);

        if (drmInfo != null) {
            drmInfo.updateIntent(intent);
        }
        return intent;
    }

    private static final class DrmInfo {
        public final String drmScheme;
        public final String drmLicenseUrl;
        public final String[] drmKeyRequestProperties;
        public final boolean drmMultiSession;

        public DrmInfo(
                String drmScheme,
                String drmLicenseUrl,
                String[] drmKeyRequestProperties,
                boolean drmMultiSession) {
            this.drmScheme = drmScheme;
            this.drmLicenseUrl = drmLicenseUrl;
            this.drmKeyRequestProperties = drmKeyRequestProperties;
            this.drmMultiSession = drmMultiSession;
        }

        public void updateIntent(Intent intent) {
            Assertions.checkNotNull(intent);
            intent.putExtra(UizaPlayerActivity.DRM_SCHEME_EXTRA, drmScheme);
            intent.putExtra(UizaPlayerActivity.DRM_LICENSE_URL_EXTRA, drmLicenseUrl);
            intent.putExtra(UizaPlayerActivity.DRM_KEY_REQUEST_PROPERTIES_EXTRA, drmKeyRequestProperties);
            intent.putExtra(UizaPlayerActivity.DRM_MULTI_SESSION_EXTRA, drmMultiSession);
        }
    }
}
