package com.money.app.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.money.app.util.AppUtils

/**
 * Custom View vẽ biểu đồ đường (Line Chart) mượt mà dạng Liquid Glass.
 * Hỗ trợ đường cong Bézier, vùng phủ gradient dạ quang, guideline nét đứt và tooltip tương tác.
 */
class LineChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val spendPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFEA5455.toInt()
        strokeWidth = 8f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val incomePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF28C76F.toInt()
        strokeWidth = 8f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val guideLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x80475569.toInt()
        strokeWidth = 3f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        style = Paint.Style.STROKE
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF475569.toInt()
        textSize = 26f
        textAlign = Paint.Align.CENTER
    }

    private var spendData = FloatArray(7) { 0f }
    private var incomeData = FloatArray(7) { 0f }
    private val labels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    
    private var touchX = -1f
    private var isTouching = false
    private var animProgress = 0f

    fun setData(spends: FloatArray, incomes: FloatArray) {
        spendData = spends
        incomeData = incomes
        startAnim()
    }

    private fun startAnim() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 800
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                animProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (spendData.isEmpty()) return

        val w = width.toFloat()
        val h = height.toFloat()
        val padding = 50f
        val chartH = h - 90f
        val stepX = (w - 2 * padding) / 6
        
        val maxVal = (spendData.maxOrNull() ?: 1f)
            .coerceAtLeast(incomeData.maxOrNull() ?: 1f)
            .coerceAtLeast(100f)

        val spendPoints = ArrayList<PointF>()
        val incomePoints = ArrayList<PointF>()

        for (i in 0..6) {
            val x = padding + i * stepX
            val ySpend = h - 50f - (spendData[i] / maxVal) * chartH * animProgress
            val yIncome = h - 50f - (incomeData[i] / maxVal) * chartH * animProgress
            
            spendPoints.add(PointF(x, ySpend))
            incomePoints.add(PointF(x, yIncome))

            canvas.drawText(labels[i], x, h - 10f, textPaint)
        }

        // Vẽ đường Bézier mượt cho Chi tiêu
        drawSmoothCurve(canvas, spendPoints, spendPaint, fillPaint, 0x33EA5455, h - 50f)
        // Vẽ đường Bézier mượt cho Thu nhập
        drawSmoothCurve(canvas, incomePoints, incomePaint, fillPaint, 0x3328C76F, h - 50f)

        // Vẽ các điểm mốc tròn (Dots)
        for (i in 0..6) {
            dotPaint.color = 0xFFEA5455.toInt()
            canvas.drawCircle(spendPoints[i].x, spendPoints[i].y, 7f, dotPaint)
            dotPaint.color = 0xFF28C76F.toInt()
            canvas.drawCircle(incomePoints[i].x, incomePoints[i].y, 7f, dotPaint)
        }

        // Nếu chạm hoặc rê tay, vẽ đường thẳng nét đứt chỉ hướng (Guideline) và Tooltip
        if (isTouching && touchX >= 0) {
            val index = ((touchX - padding + stepX / 2) / stepX).toInt().coerceIn(0, 6)
            val guideX = padding + index * stepX
            
            // Guideline nét đứt
            canvas.drawLine(guideX, 20f, guideX, h - 50f, guideLinePaint)
            
            drawTooltip(canvas, index, guideX, h)
        }
    }

    private fun drawSmoothCurve(canvas: Canvas, points: List<PointF>, linePaint: Paint, fillP: Paint, fillColor: Int, baselineY: Float) {
        if (points.size < 2) return
        val path = Path()
        val fillPath = Path()

        path.moveTo(points[0].x, points[0].y)
        fillPath.moveTo(points[0].x, baselineY)
        fillPath.lineTo(points[0].x, points[0].y)

        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            val controlX1 = p1.x + (p2.x - p1.x) / 2f
            val controlY1 = p1.y
            val controlX2 = p1.x + (p2.x - p1.x) / 2f
            val controlY2 = p2.y

            path.cubicTo(controlX1, controlY1, controlX2, controlY2, p2.x, p2.y)
            fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, p2.x, p2.y)
        }

        fillPath.lineTo(points.last().x, baselineY)
        fillPath.close()

        fillP.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), fillColor, Color.TRANSPARENT, Shader.TileMode.CLAMP)
        canvas.drawPath(fillPath, fillP)
        canvas.drawPath(path, linePaint)
    }

    private fun drawTooltip(canvas: Canvas, index: Int, x: Float, h: Float) {
        val paintTooltip = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.parseColor("#F01E2235")
        }
        val rect = RectF(x - 110, 10f, x + 110, 110f)
        canvas.drawRoundRect(rect, 20f, 20f, paintTooltip)
        
        val whiteText = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = Color.WHITE
            textSize = 22f
            textAlign = Paint.Align.CENTER 
        }
        canvas.drawText("Chi: ${AppUtils.formatCurrency(spendData[index].toDouble(), context)}", x, 50f, whiteText)
        canvas.drawText("Thu: ${AppUtils.formatCurrency(incomeData[index].toDouble(), context)}", x, 90f, whiteText)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                touchX = event.x
                isTouching = true
                invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTouching = false
                invalidate()
            }
        }
        return true
    }
}
