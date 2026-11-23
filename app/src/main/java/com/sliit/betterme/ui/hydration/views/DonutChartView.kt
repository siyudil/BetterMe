package com.sliit.betterme.ui.hydration.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class DonutChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
): View(context, attrs) {
    data class Slice(val value: Float, val color: Int, val label: String)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 36f }
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 36f; color = Color.parseColor("#11000000") }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; textSize = 36f; textAlign = Paint.Align.CENTER }

    private var slices: List<Slice> = emptyList()
    private var centerText: String = ""

    fun setData(slices: List<Slice>, centerText: String){
        this.slices = slices
        this.centerText = centerText
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val size = kotlin.math.min(w, h)
        val rect = RectF((w - size)/2 + 20, (h - size)/2 + 20, (w + size)/2 - 20, (h + size)/2 - 20)
        // background ring
        canvas.drawArc(rect, 0f, 360f, false, bgPaint)

        val total = slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
        var start = -90f
        for (s in slices){
            val sweep = (s.value / total) * 360f
            paint.color = s.color
            canvas.drawArc(rect, start, sweep, false, paint)
            start += sweep
        }
        // center text
        val y = h/2 - (textPaint.descent() + textPaint.ascent())/2
        canvas.drawText(centerText, w/2, y, textPaint)
    }
}
