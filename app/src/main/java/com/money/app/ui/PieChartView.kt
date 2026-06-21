package com.money.app.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator

class PieChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class Slice(val value: Float, val color: Int, val label: String)

    private var slices = listOf<Slice>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    private var animationProgress = 0f

    fun setSlices(newSlices: List<Slice>) {
        slices = newSlices
        startAnimation()
    }

    private fun startAnimation() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                animationProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (slices.isEmpty()) return

        val total = slices.sumOf { it.value.toDouble() }.toFloat()
        if (total == 0f) return

        val padding = 20f
        rect.set(padding, padding, width - padding, height - padding)

        var startAngle = -90f
        slices.forEach { slice ->
            val sweepAngle = (slice.value / total) * 360f * animationProgress
            paint.color = slice.color
            paint.style = Paint.Style.FILL
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint)
            startAngle += (slice.value / total) * 360f
        }
        
        // Donut hole
        paint.color = android.graphics.Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 3.5f, paint)
    }
}
