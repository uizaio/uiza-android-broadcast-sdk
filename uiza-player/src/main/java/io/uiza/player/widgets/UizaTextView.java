package io.uiza.player.widgets;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;

import io.uiza.player.R;

public class UizaTextView extends AppCompatTextView {

    private static final int SIZE_NONE = -1;

    private final String TAG = getClass().getSimpleName();
    private boolean isUseDefault;
    private boolean isLandscape;

    public UizaTextView(Context context) {
        super(context);
    }

    public UizaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public UizaTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.UizaTextView);
        isUseDefault = a.getBoolean(R.styleable.UizaTextView_useDefaultTV, true);
        setShadowLayer(1f, 1f, 1f, Color.BLACK);
        updateSize();
        setSingleLine();
        a.recycle();
    }

    private void updateSize() {
        if (!isUseDefault) {
            return;
        }
        if (isLandscape) {
            int textSize = getTextSizeLand();
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        } else {
            int textSize = getTextSizePortrait();
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        updateSize();
    }

    private int textSizeLand = SIZE_NONE;
    private int textSizePortrait = SIZE_NONE;

    public int getTextSizeLand() {
        return textSizeLand == SIZE_NONE ? 15 : textSizeLand;
    }

    //sp
    public void setTextSizeLand(int textSizeLand) {
        this.textSizeLand = textSizeLand;
    }

    public int getTextSizePortrait() {
        return textSizePortrait == SIZE_NONE ? 10 : textSizePortrait;
    }

    //sp
    public void setTextSizePortrait(int textSizePortrait) {
        this.textSizePortrait = textSizePortrait;
    }
}
