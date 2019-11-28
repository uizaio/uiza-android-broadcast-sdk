package io.uiza.player.widgets.previewseekbar;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;

public interface PreviewView {
    int getProgress();

    int getMax();

    int getThumbOffset();

    int getDefaultColor();

    boolean isShowingPreview();

    void showPreview();

    void hidePreview();

    void setPreviewLoader(PreviewLoader previewLoader);

    void setPreviewColorTint(@ColorInt int color);

    void setPreviewColorResourceTint(@ColorRes int color);

    void attachPreviewFrameLayout(FrameLayout frameLayout);

    void addOnPreviewChangeListener(OnPreviewChangeListener listener);

    void removeOnPreviewChangeListener(OnPreviewChangeListener listener);

    interface OnPreviewChangeListener {
        void onStartPreview(PreviewView previewView, int progress);

        void onStopPreview(PreviewView previewView, int progress);

        void onPreview(PreviewView previewView, int progress, boolean fromUser);
    }
}
