package com.chesire.malime.extensions

import androidx.fragment.app.Fragment

inline fun <reified T : Any> Fragment.extra(key: String, default: T? = null) = lazy {
    val value = arguments?.get(key)
    if (value is T) value else default
}
