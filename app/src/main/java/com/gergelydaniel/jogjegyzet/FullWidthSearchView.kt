package com.gergelydaniel.jogjegyzet

import android.content.Context
import android.support.v7.widget.SearchView
import android.util.AttributeSet
import android.view.ViewGroup

class FullWidthSearchView : SearchView {
    constructor(context : Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        layoutParams =ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

        maxWidth = 9999999
    }
}