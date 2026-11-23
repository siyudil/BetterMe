package com.sliit.betterme.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data: FloatArray = floatArrayOf()
    private var lineColor: Int = Color.parseColor("#7C4DFF") // purple
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = lineColor
        style = Paint.Style.STROKE
        strokeWidth = resources.displayMetrics.density * 2
    }
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = lineColor
        style = Paint.Style.FILL
    }
    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#22000000")
        strokeWidth = resources.displayMetrics.density
    }

    fun setData(values: List<Float>, color: Int? = null) {
        data = values.toFloatArray()
        color?.let {
            lineColor = it
            linePaint.color = it
            pointPaint.color = it
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return

        val padding = resources.displayMetrics.density * 12
        val left = padding
        val right = width - padding
        val top = padding
        val bottom = height - padding
        canvas.drawLine(left, bottom, right, bottom, axisPaint)

        if (data.isEmpty()) return
        val maxVal = max(1f, data.maxOrNull() ?: 1f)
        val step = (right - left) / (data.size - 1).coerceAtLeast(1)

        var prevX = left
        var prevY = bottom - (data[0] / maxVal) * (bottom - top)
        for (i in 1 until data.size) {
            val x = left + step * i
            val y = bottom - (data[i] / maxVal) * (bottom - top)
            canvas.drawLine(prevX, prevY, x, y, linePaint)
            canvas.drawCircle(x, y, resources.displayMetrics.density * 3, pointPaint)
            prevX = x
            prevY = y
        }
        // first point dot
        canvas.drawCircle(left, bottom - (data[0] / maxVal) * (bottom - top), resources.displayMetrics.density * 3, pointPaint)
    }
}
