package com.danielgergely.jogjegyzet.ui

import android.content.Context
import androidx.cardview.widget.CardView
import android.util.AttributeSet
import android.view.View
import com.danielgergely.jogjegyzet.R
import com.danielgergely.jogjegyzet.util.px
import kotlinx.android.synthetic.main.view_cardwithtitle.view.*

class CardWithTitle
@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    init {
        View.inflate(context, R.layout.view_cardwithtitle, this)

        radius = 4.px.toFloat()

        val attrs = attrs
        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CardWithTitle, 0, 0)

            this.card_icon.setImageDrawable(typedArray.getDrawable(R.styleable.CardWithTitle_icon_drawable))
            val iconColor = typedArray.getColor(R.styleable.CardWithTitle_icon_color, 0xffff0000.toInt())
            card_icon.setColorFilter(iconColor)

            this.card_title.text = typedArray.getText(R.styleable.CardWithTitle_title)
        }
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)

        if (child.id == R.id.card_icon || child.id == R.id.card_title) return

        val l = child.layoutParams as LayoutParams
        l.topMargin += 44.px
    }
}