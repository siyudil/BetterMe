package com.sliit.betterme.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class MoodChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // x in hours [0..24], y in levels [1..5]
    private var xs: FloatArray = floatArrayOf()
    private var ys: FloatArray = floatArrayOf()

    private val bgBands = listOf(
        Color.parseColor("#FFF0F0"), // bad
        Color.parseColor("#FFE9DD"),
        Color.parseColor("#FFF7CC"),
        Color.parseColor("#E6F4EA"),
        Color.parseColor("#E8F0FE")  // great
    )

    private val bandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#7C4DFF")
        style = Paint.Style.STROKE
        strokeWidth = resources.displayMetrics.density * 2
    }
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        strokeWidth = resources.displayMetrics.density * 2
    }
    private val pointStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#7C4DFF")
        style = Paint.Style.STROKE
        strokeWidth = resources.displayMetrics.density * 2
    }

    fun setData(hours: List<Float>, levels: List<Float>) {
        xs = hours.toFloatArray()
        ys = levels.toFloatArray()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val padding = resources.displayMetrics.density * 12
        val left = padding
        val right = width - padding
        val top = padding
        val bottom = height - padding
        if (right <= left || bottom <= top) return

        // draw horizontal bands for 5 levels
        val bandHeight = (bottom - top) / 5f
        for (i in 0 until 5) {
            bandPaint.color = bgBands[i]
            val y1 = bottom - (i + 1) * bandHeight
            val y2 = bottom - i * bandHeight
            canvas.drawRect(left, y1, right, y2, bandPaint)
        }

        if (xs.isEmpty() || ys.isEmpty() || xs.size != ys.size) return
        // normalize x: 0..24 to left..right, y: 1..5 to bottom..top
        val path = Path()
        for (i in xs.indices) {
            val x = left + (xs[i] / 24f) * (right - left)
            val clamped = ys[i].coerceIn(1f, 5f)
            val y = bottom - ((clamped - 1f) / 4f) * (bottom - top)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, linePaint)

        for (i in xs.indices) {
            val x = left + (xs[i] / 24f) * (right - left)
            val clamped = ys[i].coerceIn(1f, 5f)
            val y = bottom - ((clamped - 1f) / 4f) * (bottom - top)
            canvas.drawCircle(x, y, resources.displayMetrics.density * 4, pointPaint)
            canvas.drawCircle(x, y, resources.displayMetrics.density * 4, pointStroke)
        }
    }
}
