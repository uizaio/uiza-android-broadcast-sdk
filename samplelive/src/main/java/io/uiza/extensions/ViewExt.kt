package io.uiza.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * Extension method to set OnClickListener on a view.
 */
fun View.onClick(block: () -> Unit) {
    setOnClickListener { block() }
}

infix fun ViewGroup.inflate(layoutResId: Int): View =
    LayoutInflater.from(context).inflate(layoutResId, this, false)