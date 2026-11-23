package com.sliit.betterme.ui.hydration.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class BarChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
): View(context, attrs) {
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#89CFF0") }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#22000000"); strokeWidth = 2f }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#66000000"); textSize = 28f }
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val bubbleStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1E90FF"); style = Paint.Style.STROKE; strokeWidth = 4f }
    private val bubbleText = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1E90FF"); textSize = 26f; textAlign = Paint.Align.CENTER }

    private var values: List<Float> = emptyList()
    private var labels: List<String> = emptyList()
    private var maxValue: Float = 0f
    private var showBubbles: Boolean = true

    fun setData(values: List<Float>, labels: List<String>) {
        this.values = values
        this.labels = labels
        this.maxValue = max(1f, values.maxOrNull() ?: 1f)
        invalidate()
    }

    fun setShowBubbles(show: Boolean){ this.showBubbles = show; invalidate() }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val padding = 32f
        val chartTop = padding
        val chartBottom = h - padding * 2
        val chartLeft = padding
        val chartRight = w - padding

        // grid lines (20%,40%,60%,80%)
        for (i in 1..4){
            val y = chartBottom - (i/5f) * (chartBottom - chartTop)
            canvas.drawLine(chartLeft, y, chartRight, y, gridPaint)
        }

        if (values.isEmpty()) return
        val count = values.size
        val slot = (chartRight - chartLeft) / count
        val barWidth = slot * 0.5f

        for (i in 0 until count){
            val v = values[i]
            val xCenter = chartLeft + slot * i + slot/2
            val barHeight = (v / maxValue) * (chartBottom - chartTop)
            val rect = RectF(
                xCenter - barWidth/2, chartBottom - barHeight,
                xCenter + barWidth/2, chartBottom
            )
            canvas.drawRoundRect(rect, 16f, 16f, barPaint)

            val lbl = labels.getOrNull(i) ?: ""
            val tw = textPaint.measureText(lbl)
            canvas.drawText(lbl, xCenter - tw/2, h - padding/2, textPaint)

            if (showBubbles){
                val cy = rect.top - 18f
                val r = 24f
                canvas.drawCircle(xCenter, cy, r, bubblePaint)
                canvas.drawCircle(xCenter, cy, r, bubbleStroke)
                val txt = if (maxValue <= 1.0001f) "0%" else String.format("%d%%", v.toInt())
                val ty = cy - (bubbleText.descent() + bubbleText.ascent())/2
                canvas.drawText(txt, xCenter, ty, bubbleText)
            }
        }
    }
}
