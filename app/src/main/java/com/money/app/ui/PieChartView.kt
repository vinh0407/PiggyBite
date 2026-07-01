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
 * Custom View vẽ biểu đồ tròn (Donut Chart).
 * Được sử dụng để phân tích tỷ lệ chi tiêu/thu nhập theo các hạng mục.
 * Có hỗ trợ hiệu ứng hoạt họa (Animation) khi hiển thị dữ liệu mới.
 */
class PieChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Model đại diện cho một miếng của biểu đồ
    data class Slice(val value: Float, val color: Int, val label: String)

    private var slices = listOf<Slice>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG) // Cọ vẽ với tính năng khử răng cưa
    private val rect = RectF() // Hình chữ nhật bao quanh để vẽ cung tròn
    private var animationProgress = 0f // Tiến độ hiệu ứng (0.0 -> 1.0)

    /**
     * Thiết lập dữ liệu các phần tử của biểu đồ và bắt đầu vẽ lại kèm hiệu ứng.
     */
    fun setSlices(newSlices: List<Slice>) {
        slices = newSlices
        startAnimation()
    }

    /**
     * Tạo hiệu ứng vẽ biểu đồ chạy từ 0 đến đầy vòng tròn trong 1 giây.
     */
    private fun startAnimation() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator() // Hiệu ứng chậm dần đều
            addUpdateListener {
                animationProgress = it.animatedValue as Float
                invalidate() // Buộc View phải vẽ lại ở mỗi khung hình hiệu ứng
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (slices.isEmpty()) return

        // Tính tổng giá trị để quy đổi ra tỷ lệ phần trăm (độ)
        val total = slices.sumOf { it.value.toDouble() }.toFloat()
        if (total == 0f) return

        val padding = 20f
        rect.set(padding, padding, width - padding, height - padding)

        var startAngle = -90f // Bắt đầu vẽ từ đỉnh trên cùng (12 giờ)
        slices.forEach { slice ->
            val sweepAngle = (slice.value / total) * 360f * animationProgress
            paint.color = slice.color
            paint.style = Paint.Style.FILL
            
            // Vẽ cung tròn cho hạng mục hiện tại
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint)
            
            // Cập nhật góc bắt đầu cho hạng mục kế tiếp
            startAngle += (slice.value / total) * 360f
        }
        
        // Vẽ một hình tròn trắng ở giữa để tạo thành dạng Donut (Bánh vòng)
        paint.color = android.graphics.Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 3.5f, paint)
    }
}
