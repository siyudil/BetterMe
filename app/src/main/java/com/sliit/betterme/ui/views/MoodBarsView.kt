package com.sliit.betterme.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class MoodBarsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var hours: FloatArray = floatArrayOf() // not used in layout, but kept for reference
    private var levels: FloatArray = floatArrayOf() // 1..5
    private var labels: Array<String> = emptyArray() // time strings like 10:00
    private var colors: IntArray? = null

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#EEEEEE")
        style = Paint.Style.FILL
    }
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#7C4DFF")
        style = Paint.Style.FILL
    }
    private val emojiPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 36f * resources.displayMetrics.scaledDensity / resources.displayMetrics.density
        textAlign = Paint.Align.CENTER
    }
    private val labelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6B7280")
        textSize = 12f * resources.displayMetrics.scaledDensity
        textAlign = Paint.Align.CENTER
    }

    fun setData(hours: List<Float>, levels: List<Float>, labels: List<String>, colors: IntArray? = null) {
        this.hours = hours.toFloatArray()
        this.levels = levels.map { it.coerceIn(1f, 5f) }.toFloatArray()
        this.labels = labels.toTypedArray()
        this.colors = colors
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return
        val n = levels.size
        if (n == 0) return

        val density = resources.displayMetrics.density
        val padding = 16f * density
        val barWidth = 24f * density
        val gap = ((width - 2 * padding) - n * barWidth) / max(1, n - 1)
        val topPadding = 12f * density
        val bottomForLabels = 24f * density
        val usableTop = topPadding
        val usableBottom = height - bottomForLabels - padding

        for (i in 0 until n) {
            val x = padding + i * (barWidth + gap)
            val bgRect = RectF(x, usableTop, x + barWidth, usableBottom)
            // draw background tube
            canvas.drawRoundRect(bgRect, barWidth / 2, barWidth / 2, bgPaint)

            // level 1..5 maps to 20%..100% of height
            val frac = (levels[i] - 1f) / 4f // 0..1
            val fillTop = usableBottom - frac * (usableBottom - usableTop)
            colors?.let { if (i < it.size) fillPaint.color = it[i] }
            val fillRect = RectF(x, fillTop, x + barWidth, usableBottom)
            canvas.drawRoundRect(fillRect, barWidth / 2, barWidth / 2, fillPaint)

            // emoji indicator as small circle or text at top of filled bar
            val emojiY = fillTop - 6f * density
            canvas.drawText(levelEmoji(levels[i]), x + barWidth / 2, emojiY, emojiPaint)

            // time label
            val label = if (i < labels.size) labels[i] else ""
            canvas.drawText(label, x + barWidth / 2, height - 6f * density, labelPaint)
        }
    }

    private fun levelEmoji(l: Float): String = when {
        l >= 4.5f -> "😎"
        l >= 3.5f -> "😀"
        l >= 2.5f -> "🙂"
        l >= 1.5f -> "😕"
        else -> "😢"
    }
}
