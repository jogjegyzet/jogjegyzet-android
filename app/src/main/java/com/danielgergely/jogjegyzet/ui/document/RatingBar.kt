package com.danielgergely.jogjegyzet.ui.document

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt

private const val COLOR_NONE = 0xFF888888.toInt()
private const val COLOR_LIKE = 0xFF388E3C.toInt()
private const val COLOR_DISLIKE = 0xFFD32F2F.toInt()

class RatingBar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var positiveRatings = 0
    private var negativeRatings = 0

    private val likePaint : Paint = Paint()
    init {
        likePaint.color = COLOR_LIKE
    }

    fun setData(positiveRatings: Int, negativeRatings: Int) {
        this.positiveRatings = positiveRatings
        this.negativeRatings = negativeRatings
        invalidate()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        if(positiveRatings == 0 && negativeRatings == 0) {
            canvas.drawColor(COLOR_NONE)
        } else {
            canvas.drawColor(COLOR_DISLIKE)
            canvas.drawRect(
                    Rect(
                            0,
                            0,
                            (positiveRatings.toFloat() / (positiveRatings + negativeRatings.toFloat()) * width.toFloat()).roundToInt(),
                            height
                    ), likePaint)
        }
    }

}