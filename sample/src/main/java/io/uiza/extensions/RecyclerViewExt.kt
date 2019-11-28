package io.uiza.extensions

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.LinearLayout

fun RecyclerView.setVertical(spanCount: Int = 1, reverse: Boolean = false) {
    attachLayout(LinearLayout.VERTICAL, spanCount, reverse)
}

fun RecyclerView.setHorizontal(spanCount: Int = 1, reverse: Boolean = false) {
    attachLayout(LinearLayout.HORIZONTAL, spanCount, reverse)
}

private fun RecyclerView.attachLayout(orientation: Int = RecyclerView.VERTICAL, spanCount: Int = 1, reverse: Boolean = false) {
    layoutManager = if (spanCount == 1)
        LinearLayoutManager(context, orientation, reverse)
    else
        GridLayoutManager(context, spanCount, orientation, reverse)
    setHasFixedSize(true)
}

fun RecyclerView.scrollToTop(elementNum: Int) {
    (layoutManager as? LinearLayoutManager)?.let {
        val offset = if (it.orientation == LinearLayout.VERTICAL)
            height
        else
            width
        it.scrollToPositionWithOffset(elementNum, offset)
    }
    (layoutManager as? GridLayoutManager)?.let {
        val offset = if (it.orientation == LinearLayout.VERTICAL)
            height
        else
            width
        it.scrollToPositionWithOffset(elementNum, offset)
    }
}