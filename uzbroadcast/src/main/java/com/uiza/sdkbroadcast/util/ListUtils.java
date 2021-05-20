package com.uiza.sdkbroadcast.util;

import android.os.Build;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {

    public static <T> List<T> filter(@NonNull List<T> list, Pre<T, Boolean> pre) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return list.stream().filter(pre::get).collect(Collectors.toList());
        } else {
            List<T> col = new ArrayList<>();
            for (int i = 0; i < list.size(); i++)
                if (pre.get(list.get(i)))
                    col.add(list.get(i));
            return col;
        }
    }

    public static <T, R> List<R> map(List<T> list, Pre<T, R> pre) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return list.stream().map(pre::get).collect(Collectors.toList());
        } else {
            List<R> cols = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                cols.add(pre.get(list.get(i)));
            }
            return cols;
        }
    }

    public interface Pre<T, R> {
        R get(T item);
    }

}
