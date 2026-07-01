package com.money.app.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.money.app.util.AppUtils

/**
 * Custom View vẽ biểu đồ đường (Line Chart).
 * Hiển thị biến động Thu nhập và Chi tiêu trong 7 ngày của tuần hiện tại.
 * Tính năng đặc biệt: Nhấn giữ lên biểu đồ để xem chi tiết số tiền của từng ngày qua Tooltip.
 */
class LineChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Cọ vẽ cho đường chi tiêu (Màu đỏ)
    private val spendPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFEA5455.toInt() // expense_red
        strokeWidth = 6f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND // Đầu đường kẻ bo tròn
    }

    // Cọ vẽ cho đường thu nhập (Màu xanh)
    private val incomePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF28C76F.toInt() // income_green
        strokeWidth = 6f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    // Cọ vẽ cho các nhãn chữ (Thứ 2 -> CN)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textSize = 24f
    }

    private var spendData = FloatArray(7) { 0f } // Dữ liệu chi tiêu 7 ngày
    private var incomeData = FloatArray(7) { 0f } // Dữ liệu thu nhập 7 ngày
    private val labels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    
    private var touchX = -1f // Tọa độ X khi chạm vào màn hình
    private var isLongPress = false // Trạng thái nhấn giữ để hiện Tooltip

    /**
     * Cập nhật dữ liệu mới cho biểu đồ.
     */
    fun setData(spends: FloatArray, incomes: FloatArray) {
        spendData = spends
        incomeData = incomes
        invalidate() // Vẽ lại View
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (spendData.isEmpty()) return

        val w = width.toFloat()
        val h = height.toFloat()
        val padding = 40f
        val chartH = h - 80f // Chiều cao thực tế của vùng vẽ biểu đồ
        val stepX = (w - 2 * padding) / 6 // Khoảng cách giữa các ngày trên trục X
        
        // Tìm giá trị lớn nhất để làm mốc tỷ lệ (Scale)
        val maxVal = (spendData.maxOrNull() ?: 1f)
            .coerceAtLeast(incomeData.maxOrNull() ?: 1f)
            .coerceAtLeast(100f) // Tránh chia cho 0 hoặc số quá nhỏ

        val spendPath = Path()
        val incomePath = Path()

        for (i in 0..6) {
            val x = padding + i * stepX
            // Tính toán tọa độ Y (Y ngược từ trên xuống: 0 ở trên cùng)
            val ySpend = h - 40f - (spendData[i] / maxVal) * chartH
            val yIncome = h - 40f - (incomeData[i] / maxVal) * chartH
            
            if (i == 0) {
                spendPath.moveTo(x, ySpend)
                incomePath.moveTo(x, yIncome)
            } else {
                spendPath.lineTo(x, ySpend)
                incomePath.lineTo(x, yIncome)
            }
            
            // Vẽ tên thứ (T2, T3...)
            canvas.drawText(labels[i], x - 15f, h - 10f, textPaint)
        }

        // Vẽ đường biểu đồ
        canvas.drawPath(spendPath, spendPaint)
        canvas.drawPath(incomePath, incomePaint)
        
        // Vẽ Tooltip nếu người dùng đang nhấn giữ
        if (isLongPress && touchX >= 0) {
            drawTooltip(canvas, touchX, w, stepX, padding, maxVal, chartH, h)
        }
    }

    /**
     * Vẽ khung thông tin (Tooltip) khi người dùng nhấn giữ vào biểu đồ.
     */
    private fun drawTooltip(canvas: Canvas, tx: Float, w: Float, stepX: Float, padding: Float, maxVal: Float, chartH: Float, h: Float) {
        // Xác định ngày nào đang được nhấn dựa trên tọa độ X
        val index = ((tx - padding + stepX/2) / stepX).toInt().coerceIn(0, 6)
        val x = padding + index * stepX
        
        // Vẽ khung nền Tooltip
        val paintTooltip = Paint().apply { color = Color.BLACK; alpha = 180 }
        val rect = RectF(x - 100, 20f, x + 100, 100f)
        canvas.drawRoundRect(rect, 16f, 16f, paintTooltip)
        
        // Vẽ chữ thông tin số tiền bên trong khung
        val whiteText = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.WHITE
            textSize = 20f
            textAlign = Paint.Align.CENTER 
        }
        canvas.drawText("Chi: ${AppUtils.formatCurrency(spendData[index].toDouble(), context)}", x, 50f, whiteText)
        canvas.drawText("Thu: ${AppUtils.formatCurrency(incomeData[index].toDouble(), context)}", x, 85f, whiteText)
    }

    /**
     * Xử lý sự kiện chạm (Touch): Phát hiện nhấn giữ.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                isLongPress = false
                // Nếu giữ quá 500ms thì coi là nhấn giữ (Long Press)
                postDelayed({
                    if (touchX == event.x) {
                        isLongPress = true
                        invalidate()
                    }
                }, 500)
            }
            MotionEvent.ACTION_MOVE -> {
                touchX = event.x
                if (isLongPress) invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isLongPress = false
                invalidate()
            }
        }
        return true
    }
}
