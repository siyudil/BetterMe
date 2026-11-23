package com.sliit.betterme.ui.hydration.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class LineChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
): View(context, attrs) {
    private val areaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#3389CFF0") }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1E90FF"); strokeWidth = 5f; style = Paint.Style.STROKE }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#22000000"); strokeWidth = 2f }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#66000000"); textSize = 28f }
    private val pointFill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val pointStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1E90FF"); style = Paint.Style.STROKE; strokeWidth = 4f }
    private val bubbleText = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1E90FF"); textSize = 26f; textAlign = Paint.Align.CENTER }

    private var values: List<Float> = emptyList()
    private var labels: List<String> = emptyList()
    private var maxValue: Float = 1f
    private var showMarkers: Boolean = true

    fun setData(values: List<Float>, labels: List<String>) {
        this.values = values
        this.labels = labels
        this.maxValue = max(1f, values.maxOrNull() ?: 1f)
        invalidate()
    }
    fun setShowMarkers(show: Boolean){ this.showMarkers = show; invalidate() }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val padding = 32f
        val chartTop = padding
        val chartBottom = h - padding * 2
        val chartLeft = padding
        val chartRight = w - padding

        // grid
        for (i in 1..4){
            val y = chartBottom - (i/5f) * (chartBottom - chartTop)
            canvas.drawLine(chartLeft, y, chartRight, y, gridPaint)
        }

        if (values.isEmpty()) return
        val count = values.size
        val dx = (chartRight - chartLeft) / (count - 1).coerceAtLeast(1)

        val path = Path()
        val area = Path()
        for (i in 0 until count){
            val x = chartLeft + dx * i
            val y = chartBottom - (values[i] / maxValue) * (chartBottom - chartTop)
            if (i == 0){
                path.moveTo(x, y)
                area.moveTo(x, chartBottom)
                area.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                area.lineTo(x, y)
            }
        }
        area.lineTo(chartRight, chartBottom)
        area.close()
        canvas.drawPath(area, areaPaint)
        canvas.drawPath(path, linePaint)

        // labels and markers
        val step = (count - 1).coerceAtLeast(1)
        var maxIndex = values.indexOf(values.maxOrNull() ?: 0f)
        for (i in 0 until count){
            val x = chartLeft + dx * i
            val lbl = labels.getOrNull(i) ?: ""
            val tw = textPaint.measureText(lbl)
            canvas.drawText(lbl, x - tw/2, h - padding/2, textPaint)
            if (showMarkers){
                val y = chartBottom - (values[i] / maxValue) * (chartBottom - chartTop)
                canvas.drawCircle(x, y, 10f, pointFill)
                canvas.drawCircle(x, y, 10f, pointStroke)
                if (i == maxIndex){
                    val cy = y - 26f
                    val r = 24f
                    canvas.drawCircle(x, cy, r, pointFill)
                    canvas.drawCircle(x, cy, r, pointStroke)
                    val txt = values[i].toInt().toString()
                    val ty = cy - (bubbleText.descent() + bubbleText.ascent())/2
                    canvas.drawText(txt, x, ty, bubbleText)
                }
            }
        }
    }
}
