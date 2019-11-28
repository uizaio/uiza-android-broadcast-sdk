package io.uiza.player.utils;


import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ViewUtil {
    public static void visibleViews(View... views) {
        for (View v : views) {
            if (v != null && v.getVisibility() != View.VISIBLE) {
                v.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void goneViews(View... views) {
        for (View v : views) {
            if (v != null && v.getVisibility() != View.GONE) {
                v.setVisibility(View.GONE);
            }
        }
    }

    public static void invisibleViews(View... views) {
        for (View v : views) {
            if (v != null && v.getVisibility() != View.INVISIBLE) {
                v.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static void setVisibilityViews(int visibility, View... views) {
        for (View v : views) {
            if (v != null && v.getVisibility() != visibility) {
                v.setVisibility(visibility);
            }
        }
    }

    public static void setFocusableViews(boolean focusable, View... views) {
        for (View v : views) {
            if (v != null && !v.isFocusable()) {
                v.setFocusable(focusable);
            }
        }
    }

    public static void performClick(View view) {
        if (view != null) view.performClick();
    }


    public static ArrayList<View> getAllChildren(View v) {
        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<>();
            viewArrayList.add(v);
            return viewArrayList;
        }
        ArrayList<View> result = new ArrayList<>();
        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            ArrayList<View> viewArrayList = new ArrayList<>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));
            result.addAll(viewArrayList);
        }
        return result;
    }
}