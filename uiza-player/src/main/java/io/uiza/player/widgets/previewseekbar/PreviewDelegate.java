package io.uiza.player.widgets.previewseekbar;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import io.uiza.core.utils.CommonKt;
import io.uiza.player.R;


public class PreviewDelegate implements PreviewView.OnPreviewChangeListener {

    private FrameLayout previewFrameLayout;
    private View morphView;
    private View previewFrameView;
    private ViewGroup previewParent;
    private PreviewAnimator animator;
    private PreviewView previewView;
    private PreviewLoader previewLoader;

    private int scrubberColor;
    private boolean showing;
    private boolean startTouch;
    private boolean setup;
    private boolean enabled;

    public PreviewDelegate(PreviewView previewView, int scrubberColor) {
        this.previewView = previewView;
        this.previewView.addOnPreviewChangeListener(this);
        this.scrubberColor = scrubberColor;
    }

    public void setPreviewLoader(PreviewLoader previewLoader) {
        this.previewLoader = previewLoader;
    }

    public void onLayout(ViewGroup previewParent, int frameLayoutId) {
        if (!setup) {
            this.previewParent = previewParent;
            FrameLayout frameLayout = findFrameLayout(previewParent, frameLayoutId);
            if (frameLayout != null) {
                attachPreviewFrameLayout(frameLayout);
            }
        }
    }

    public void attachPreviewFrameLayout(FrameLayout frameLayout) {
        if (setup) {
            return;
        }
        this.previewParent = (ViewGroup) frameLayout.getParent();
        this.previewFrameLayout = frameLayout;
        inflateViews(frameLayout);
        morphView.setVisibility(View.INVISIBLE);
        previewFrameLayout.setVisibility(View.INVISIBLE);
        previewFrameView.setVisibility(View.INVISIBLE);
        if (CommonKt.isLlAndAbove()) {
            animator = new PreviewAnimatorLollipopImpl(previewParent, previewView, morphView,
                    previewFrameLayout, previewFrameView);
        } else {
            animator = new PreviewAnimatorImpl(previewParent, previewView, morphView,
                    previewFrameLayout, previewFrameView);
        }

        setup = true;
    }

    public boolean isShowing() {
        return showing;
    }

    public void show() {
        if (!showing && setup) {
            animator.show();
            showing = true;
        }
    }

    public void hide() {
        if (showing) {
            animator.hide();
            showing = false;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPreviewColorTint(@ColorInt int color) {
        Drawable drawable = DrawableCompat.wrap(morphView.getBackground());
        DrawableCompat.setTint(drawable, color);
        morphView.setBackground(drawable);
        previewFrameView.setBackgroundColor(color);
    }

    public void setPreviewColorResourceTint(@ColorRes int color) {
        setPreviewColorTint(ContextCompat.getColor(previewParent.getContext(), color));
    }

    @Override
    public void onStartPreview(PreviewView previewView, int progress) {
        startTouch = true;
    }

    @Override
    public void onStopPreview(PreviewView previewView, int progress) {
        if (showing) {
            animator.hide();
        }
        showing = false;
        startTouch = false;
    }

    @Override
    public void onPreview(PreviewView previewView, int progress, boolean fromUser) {
        if (setup && enabled) {
            animator.move();
            if (!showing && !startTouch && fromUser) {
                show();
            }
            if (previewLoader != null) {
                previewLoader.loadPreview(progress, previewView.getMax());
            }
        }
        startTouch = false;
    }

    public boolean isSetup() {
        return setup;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void inflateViews(FrameLayout frameLayout) {

        // Create morph view
        morphView = new View(frameLayout.getContext());
        morphView.setBackgroundResource(R.drawable.previewseekbar_morph);

        // Setup morph view
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(0, 0);
        layoutParams.width = frameLayout.getResources()
                .getDimensionPixelSize(R.dimen.previewseekbar_indicator_width);
        layoutParams.height = layoutParams.width;
        previewParent.addView(morphView, layoutParams);

        // Create frame view for the circular reveal
        previewFrameView = new View(frameLayout.getContext());
        FrameLayout.LayoutParams frameLayoutParams
                = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.addView(previewFrameView, frameLayoutParams);

        // Apply same color for the morph and frame views
        setPreviewColorTint(scrubberColor);
        frameLayout.requestLayout();
    }

    @Nullable
    private FrameLayout findFrameLayout(ViewGroup parent, int id) {
        if (id == View.NO_ID || parent == null) {
            return null;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.getId() == id && child instanceof FrameLayout) {
                return (FrameLayout) child;
            }
        }
        return null;
    }
}
