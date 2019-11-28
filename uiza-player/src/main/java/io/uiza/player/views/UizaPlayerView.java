package io.uiza.player.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

import io.uiza.core.utils.SentryUtil;
import io.uiza.player.utils.ViewUtil;

public class UizaPlayerView extends PlayerView implements PlayerControlView.VisibilityListener {

    public interface PlayerControlVisibilityListener {
        void onVisibilityChange(int visibility);
    }

    public interface OnTouchEvent {
        void onSingleTapConfirmed(float x, float y);

        void onLongPress(float x, float y);

        void onDoubleTap(float x, float y);

        void onSwipeRight();

        void onSwipeLeft();

        void onSwipeBottom();

        void onSwipeTop();
    }

    private boolean controllerVisible;
    private GestureDetector mDetector;
    private OnTouchEvent onTouchEvent;

    private boolean useWithVDHView = true;
    private boolean settingPlayer = true;

    PlayerControlVisibilityListener playerVisibilityListener;

    public UizaPlayerView(Context context) {
        this(context, null);
    }

    public UizaPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UizaPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setControllerVisibilityListener(this);
        mDetector = new GestureDetector(context, new UizaGestureListener());
    }

    public boolean isControllerVisible() {
        return controllerVisible;
    }

    public boolean isUseWithVDHView() {
        return useWithVDHView;
    }

    public boolean isSettingPlayer() {
        return settingPlayer;
    }

    public void setUseWithVDHView(boolean useWithVDHView) {
        this.useWithVDHView = useWithVDHView;
    }

    public void setSettingPlayer(boolean settingPlayer) {
        this.settingPlayer = settingPlayer;
    }

    public void setPlayerVisibilityListener(PlayerControlVisibilityListener playerVisibilityListener) {
        this.playerVisibilityListener = playerVisibilityListener;
    }

    /**
     * @param visibilityListener
     * @deprecated use {@link #setPlayerVisibilityListener}
     */
    @Deprecated
    @Override
    public void setControllerVisibilityListener(PlayerControlView.VisibilityListener visibilityListener) {
        // Nothing
    }

    public void toggleShowHideController() {
        if (controllerVisible) {
            hideController();
        } else {
            showController();
        }
    }

    @Override
    public void showController() {
        if (settingPlayer) {
            super.showController();
        }
    }

    @Override
    public void hideController() {
        if (settingPlayer) {
            super.hideController();
        }
    }

    public void setOnTouchEvent(OnTouchEvent onTouchEvent) {
        this.onTouchEvent = onTouchEvent;
    }

//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (useWithVDHView) {
//            return false;
//        } else {
//            mDetector.onTouchEvent(event);
//            return true;
//        }
//
//    }

    @Override
    public void onVisibilityChange(int visibility) {
        controllerVisible = visibility == View.VISIBLE;
        if (playerVisibilityListener != null) {
            playerVisibilityListener.onVisibilityChange(visibility);
        }
    }

    public PlayerControlView getPlayerControlView() {
        for (int i = 0; i < this.getChildCount(); i++) {
            if (this.getChildAt(i) instanceof PlayerControlView) {
                return (PlayerControlView) getChildAt(i);
            }
        }
        return null;
    }

    public View[] getAllChild() {
        PlayerControlView playerControlView = getPlayerControlView();
        if (playerControlView == null) {
            return null;
        }
        List<View> viewList = ViewUtil.getAllChildren(playerControlView);
        return viewList.toArray(new View[viewList.size()]);
    }

    private class UizaGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent event) {
            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!controllerVisible) {
                showController();
            } else if (getControllerHideOnTouch()) {
                hideController();
            }
            if (onTouchEvent != null) {
                onTouchEvent.onSingleTapConfirmed(e.getX(), e.getY());
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (onTouchEvent != null) {
                onTouchEvent.onLongPress(e.getX(), e.getY());
            }
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (onTouchEvent != null) {
                onTouchEvent.onDoubleTap(e.getX(), e.getY());
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            //LLog.d(TAG, "onSwipeRight");
                            if (onTouchEvent != null) {
                                onTouchEvent.onSwipeRight();
                            }
                        } else {
                            //LLog.d(TAG, "onSwipeLeft");
                            if (onTouchEvent != null) {
                                onTouchEvent.onSwipeLeft();
                            }
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            //LLog.d(TAG, "onSwipeBottom");
                            if (onTouchEvent != null) {
                                onTouchEvent.onSwipeBottom();
                            }
                        } else {
                            //LLog.d(TAG, "onSwipeTop");
                            if (onTouchEvent != null) {
                                onTouchEvent.onSwipeTop();
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                SentryUtil.captureException(exception);
            }
            return true;
        }
    }

}
