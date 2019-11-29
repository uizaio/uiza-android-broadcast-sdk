package io.uiza.player.widgets;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;

import io.uiza.player.R;
import io.uiza.player.utils.ConvertKt;
import io.uiza.player.utils.DeviceKt;
import io.uiza.player.utils.ScreenKt;

public class UizaImageButton extends AppCompatImageButton {

    private final String TAG = getClass().getSimpleName();

    public UizaImageButton(Context context) {
        super(context);
    }

    public UizaImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSizeScreenW(attrs);
    }

    public UizaImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSizeScreenW(attrs);
    }

    private Drawable drawableEnabled;
    private Drawable drawableDisabled;
    private int screenWPortrait;
    private int screenWLandscape;
    private boolean isUseDefault;
    private boolean isSetSrcDrawableEnabled;

    public boolean isSetSrcDrawableEnabled() {
        return isSetSrcDrawableEnabled;
    }

    public void setSrcDrawableEnabled() {
        if (drawableEnabled != null) {
            setClickable(true);
            setFocusable(true);
            setImageDrawable(drawableEnabled);
        }
        clearColorFilter();
        invalidate();
        isSetSrcDrawableEnabled = true;
    }

    public void setSrcDrawableDisabled() {
        setClickable(false);
        setFocusable(false);
        if (drawableDisabled == null) {
            setColorFilter(Color.GRAY);
        } else {
            setImageDrawable(drawableDisabled);
            clearColorFilter();
        }
        invalidate();
        isSetSrcDrawableEnabled = false;
    }

    public void setSrcDrawableDisabledCanTouch() {
        setClickable(true);
        setFocusable(true);
        if (drawableDisabled == null) {
            setColorFilter(Color.GRAY);
        } else {
            setImageDrawable(drawableDisabled);
            clearColorFilter();
        }
        invalidate();
        isSetSrcDrawableEnabled = false;
    }

    private void initSizeScreenW(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.UizaImageButton);
        isUseDefault = a.getBoolean(R.styleable.UizaImageButton_useDefaultIB, true);
        drawableDisabled = a.getDrawable(R.styleable.UizaImageButton_srcDisabled);
        //disable click sound of a particular button in android app
        setSoundEffectsEnabled(false);
        if (!isUseDefault) {
            a.recycle();
            drawableEnabled = getDrawable();
            return;
        }
        boolean isTablet = DeviceKt.isTablet(getContext());
        if (isTablet) {
            ratioLand = DeviceKt.RATIO_LAND_TABLET;
            ratioPort = DeviceKt.RATIO_PORTRAIT_TABLET;
        } else {
            ratioLand = DeviceKt.RATIO_LAND_MOBILE;
            ratioPort = DeviceKt.RATIO_PORTRAIT_MOBILE;
        }
        screenWPortrait = ScreenKt.getScreenWidth();
        screenWLandscape = ScreenKt.screenHeightIncludeNavigationBar(this.getContext());
        //set padding 5dp
        int px = ConvertKt.dp2px(5);
        setPadding(px, px, px, px);
        post(() -> {
            if (ScreenKt.isFullScreen(getContext())) {
                updateSizeLandscape();
            } else {
                updateSizePortrait();
            }
        });
        a.recycle();
        drawableEnabled = getDrawable();
    }

    private int ratioLand = 7;
    private int ratioPort = 5;

    public int getRatioLand() {
        return ratioLand;
    }

    public void setRatioLand(int ratioLand) {
        this.ratioLand = ratioLand;
        if (ScreenKt.isFullScreen(getContext())) {
            updateSizeLandscape();
        } else {
            updateSizePortrait();
        }
    }

    public int getRatioPort() {
        return ratioPort;
    }

    public void setRatioPort(int ratioPort) {
        this.ratioPort = ratioPort;
        if (ScreenKt.isFullScreen(getContext())) {
            updateSizeLandscape();
        } else {
            updateSizePortrait();
        }
    }

    private int size;

    private void updateSizePortrait() {
        if (!isUseDefault) {
            return;
        }
        size = screenWPortrait / ratioPort;
        this.getLayoutParams().width = size;
        this.getLayoutParams().height = size;
        this.requestLayout();
    }

    private void updateSizeLandscape() {
        if (!isUseDefault) {
            return;
        }
        size = screenWLandscape / ratioLand;
        this.getLayoutParams().width = size;
        this.getLayoutParams().height = size;
        this.requestLayout();
    }

    public int getSize() {
        return size;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            updateSizeLandscape();
        } else {
            updateSizePortrait();
        }
    }

    public void setUIVisible(final boolean isVisible) {
        setClickable(isVisible);
        setFocusable(isVisible);
        if (isVisible) {
            setSrcDrawableEnabled();
        } else {
            setImageResource(0);
        }
    }
}
