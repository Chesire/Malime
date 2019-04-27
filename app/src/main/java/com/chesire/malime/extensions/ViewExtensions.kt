package com.chesire.malime.extensions

import android.view.View

fun View.hide(invisible: Boolean = false) {
    visibility = if (invisible) View.INVISIBLE else View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.visibleIf(invisible: Boolean = false, callback: () -> Boolean) {
    visibility = if (callback()) {
        View.VISIBLE
    } else {
        if (invisible) View.INVISIBLE else View.GONE
    }
}
