package com.sliit.betterme.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data: FloatArray = floatArrayOf()
    private var barColor: Int = Color.parseColor("#B39DDB") // default lavender
    private var barColors: IntArray? = null
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = barColor
        style = Paint.Style.FILL
    }
    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#22000000")
        strokeWidth = resources.displayMetrics.density
    }

    fun setData(values: List<Float>, color: Int? = null, colors: IntArray? = null) {
        data = values.toFloatArray()
        barColors = colors
        color?.let {
            barColor = it
            barPaint.color = it
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return

        // Draw baseline
        val padding = resources.displayMetrics.density * 12
        val left = padding
        val right = width - padding
        val top = padding
        val bottom = height - padding
        canvas.drawLine(left, bottom, right, bottom, axisPaint)

        if (data.isEmpty()) return
        val maxVal = max(1f, data.maxOrNull() ?: 1f)
        val count = data.size
        val totalGap = padding * (count + 1)
        val barWidth = ((right - left - totalGap) / count.toFloat()).coerceAtLeast(1f)
        var x = left + padding
        data.forEachIndexed { idx, v ->
            // choose color per bar if provided
            barColors?.let { arr ->
                if (idx in arr.indices) barPaint.color = arr[idx] else barPaint.color = barColor
            }
            val h = (v / maxVal) * (bottom - top)
            canvas.drawRoundRect(x, bottom - h, x + barWidth, bottom, 12f, 12f, barPaint)
            x += barWidth + padding
        }
    }
}
