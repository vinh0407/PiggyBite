package com.money.app.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator

/**
 * Custom View vẽ biểu đồ tròn Donut Chart dạng Liquid Glass.
 * Được sử dụng để phân tích tỷ lệ chi tiêu/thu nhập theo các hạng mục.
 * Hỗ trợ hiệu ứng hoạt họa mượt mà (Animation) khi hiển thị dữ liệu mới.
 */
class PieChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class Slice(val value: Float, val color: Int, val label: String)

    private var slices = listOf<Slice>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val rect = RectF()
    private var animationProgress = 0f

    fun setSlices(newSlices: List<Slice>) {
        slices = newSlices
        startAnimation()
    }

    private fun startAnimation() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 900
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

        val strokeW = (width.coerceAtMost(height) * 0.18f).coerceAtLeast(20f)
        paint.strokeWidth = strokeW
        val padding = strokeW / 2f + 16f
        rect.set(padding, padding, width - padding, height - padding)

        var startAngle = -90f
        slices.forEach { slice ->
            val sweepAngle = ((slice.value / total) * 360f).toFloat() * animationProgress
            paint.color = slice.color
            
            val actualSweep = (sweepAngle - 3f).coerceAtLeast(1f)
            canvas.drawArc(rect, startAngle + 1.5f, actualSweep, false, paint)
            
            startAngle += (slice.value / total) * 360f
        }
    }
}
