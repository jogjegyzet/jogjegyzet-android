package com.gergelydaniel.jogjegyzet.ui

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import com.gergelydaniel.jogjegyzet.ui.TitleView.State.*
import com.gergelydaniel.jogjegyzet.util.hide
import com.gergelydaniel.jogjegyzet.util.px
import com.gergelydaniel.jogjegyzet.util.show
import com.gergelydaniel.jogjegyzet.util.vis
import kotlinx.android.synthetic.main.view_title.view.*
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY


class TitleView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private enum class State {
        SEARCH_ENABLED,
        SEARCH_DISABLED,
        SEARCH_TYPING
    }
    private var state: State = SEARCH_ENABLED

    var title: String
        get() = title_text.text.toString()
        set(value) {
            title_text.text = value
        }

    var backVisible: Boolean = true
        set(value) {
            field = value
            setViewVisibility()
        }

    var searchEnabled: Boolean
        get() = state != SEARCH_DISABLED
        set(value) {
            if (value && state == SEARCH_DISABLED) {
                state = SEARCH_ENABLED
                setViewVisibility()
            } else if (!value && state != SEARCH_DISABLED) {
                state = SEARCH_DISABLED
                setViewVisibility()
            }
        }

    lateinit var onBackPressed: () -> Unit

    init {
        View.inflate(context, com.gergelydaniel.jogjegyzet.R.layout.view_title, this)
        setViewVisibility()

        orientation = HORIZONTAL
        setBackgroundColor(primaryColor())

        setPadding(10.px, 0, 0, 8.px)

        button_back.setOnClickListener {
            when (state) {
                SEARCH_TYPING -> {
                    state = SEARCH_ENABLED
                    setViewVisibility()
                }
                else -> onBackPressed?.invoke()
            }
        }

        button_search.setOnClickListener {
            if (state == SEARCH_ENABLED) {
                state = SEARCH_TYPING
                setViewVisibility()
                search_field.requestFocus()
            }
        }

        search_field.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            search_field.post {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (hasFocus) {
                    imm.showSoftInput(search_field, SHOW_IMPLICIT)
                } else {
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }

        search_field.requestFocus()
    }

    private fun setViewVisibility() {
        when (state) {
            SEARCH_ENABLED -> {
                button_search.show()
                button_back.vis = backVisible
                title_text.show()
                search_field.hide()
                button_clear.hide()
            }
            SEARCH_DISABLED -> {
                button_search.hide()
                button_back.vis = backVisible
                title_text.show()
                search_field.hide()
                button_clear.hide()
            }
            SEARCH_TYPING -> {
                button_search.hide()
                button_back.show()
                title_text.hide()
                search_field.show()
                button_clear.show()
            }
        }
    }

    private fun primaryColor(): Int {
        val typedValue = TypedValue()
        val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(com.gergelydaniel.jogjegyzet.R.attr.colorPrimary))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }
}