package io.uiza.player.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.mediarouter.app.MediaRouteButton;

/**
 * Use {@link MediaRouteButton}.
 */
public class UizaMediaButton extends MediaRouteButton {

    protected Drawable mRemoteIndicatorDrawable;

    public UizaMediaButton(Context context) {
        super(context);
    }

    public UizaMediaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UizaMediaButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setRemoteIndicatorDrawable(Drawable d) {
        mRemoteIndicatorDrawable = d;
        super.setRemoteIndicatorDrawable(d);
    }

    public void applyTint(int color) {
        Drawable wrapDrawable = DrawableCompat.wrap(mRemoteIndicatorDrawable);
        DrawableCompat.setTint(wrapDrawable, color);
    }
}