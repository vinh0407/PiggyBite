package com.money.app.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.money.app.util.AppUtils

class LineChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val spendPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFEA5455.toInt() // expense_red
        strokeWidth = 6f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val incomePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF28C76F.toInt() // income_green
        strokeWidth = 6f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textSize = 24f
    }

    private var spendData = FloatArray(7) { 0f }
    private var incomeData = FloatArray(7) { 0f }
    private val labels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    
    private var touchX = -1f
    private var isLongPress = false

    fun setData(spends: FloatArray, incomes: FloatArray) {
        spendData = spends
        incomeData = incomes
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (spendData.isEmpty()) return

        val w = width.toFloat()
        val h = height.toFloat()
        val padding = 40f
        val chartH = h - 80f
        val stepX = (w - 2 * padding) / 6
        
        val maxVal = (spendData.maxOrNull() ?: 1f).coerceAtLeast(incomeData.maxOrNull() ?: 1f).coerceAtLeast(100f)

        val spendPath = Path()
        val incomePath = Path()

        for (i in 0..6) {
            val x = padding + i * stepX
            val ySpend = h - 40f - (spendData[i] / maxVal) * chartH
            val yIncome = h - 40f - (incomeData[i] / maxVal) * chartH
            
            if (i == 0) {
                spendPath.moveTo(x, ySpend)
                incomePath.moveTo(x, yIncome)
            } else {
                spendPath.lineTo(x, ySpend)
                incomePath.lineTo(x, yIncome)
            }
            
            canvas.drawText(labels[i], x - 15f, h - 10f, textPaint)
        }

        canvas.drawPath(spendPath, spendPaint)
        canvas.drawPath(incomePath, incomePaint)
        
        if (isLongPress && touchX >= 0) {
            drawTooltip(canvas, touchX, w, stepX, padding, maxVal, chartH, h)
        }
    }

    private fun drawTooltip(canvas: Canvas, tx: Float, w: Float, stepX: Float, padding: Float, maxVal: Float, chartH: Float, h: Float) {
        val index = ((tx - padding + stepX/2) / stepX).toInt().coerceIn(0, 6)
        val x = padding + index * stepX
        
        val paintTooltip = Paint().apply { color = Color.BLACK; alpha = 180 }
        val rect = RectF(x - 100, 20f, x + 100, 100f)
        canvas.drawRoundRect(rect, 16f, 16f, paintTooltip)
        
        val whiteText = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; textSize = 20f; textAlign = Paint.Align.CENTER }
        canvas.drawText("Chi: ${AppUtils.formatCurrency(spendData[index].toDouble(), context)}", x, 50f, whiteText)
        canvas.drawText("Thu: ${AppUtils.formatCurrency(incomeData[index].toDouble(), context)}", x, 85f, whiteText)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                isLongPress = false
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
