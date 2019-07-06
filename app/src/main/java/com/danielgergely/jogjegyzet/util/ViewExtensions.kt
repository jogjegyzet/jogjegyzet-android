package com.danielgergely.jogjegyzet.util

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

inline fun View.show() = this.setVisibility(VISIBLE)

inline fun View.hide() = this.setVisibility(GONE)

var View.vis: Boolean
    get() = visibility == VISIBLE
    set(value) {
        visibility = if (value) VISIBLE else GONE
    }