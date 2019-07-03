package com.gergelydaniel.jogjegyzet.ui

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.util.px
import com.gergelydaniel.jogjegyzet.util.vis
import kotlinx.android.synthetic.main.view_title.view.*


class TitleView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var title: String
        get() = title_text.text.toString()
        set(value) {
            title_text.text = value
        }

    var backVisible: Boolean
        get() = button_back.vis
        set(value) {
            button_back.vis = value
        }

    var searchEnabled: Boolean
        get() = button_search.vis
        set(value) {
            button_search.vis = value
        }

    lateinit var onBackPressed: () -> Unit

    init {
        View.inflate(context, R.layout.view_title, this)

        orientation = HORIZONTAL
        setBackgroundColor(primaryColor())

        setPadding(10.px, 0, 0, 8.px)

        button_back.setOnClickListener {
            if (onBackPressed != null) {
                onBackPressed()
            }
        }
    }

    private fun primaryColor(): Int {
        val typedValue = TypedValue()
        val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorPrimary))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }
}