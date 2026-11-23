package com.sliit.betterme.ui.hydration.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.sliit.betterme.R
import kotlin.math.min

class HalfCircleGaugeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var goal: Int = 2500
    private var value: Int = 0

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 28f
        color = Color.parseColor("#E9EEF4")
        strokeCap = Paint.Cap.ROUND
    }
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 28f
        color = ContextCompat.getColor(context, R.color.colorPrimary)
        strokeCap = Paint.Cap.ROUND
    }
    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.parseColor("#D0D7DF")
    }
    private val dropPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#F4F7FA")
    }
    private val dropFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.colorPrimary)
    }

    private val arcRect = RectF()

    fun setGoal(g: Int) { goal = g.coerceAtLeast(1); invalidate() }
    fun setValue(v: Int) { value = v.coerceAtLeast(0); invalidate() }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = (w * 0.6f).toInt()
        setMeasuredDimension(resolveSize(w, widthMeasureSpec), resolveSize(h, heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val padding = 32f
        val size = min(width.toFloat(), height.toFloat() * 2f)
        val left = padding
        val top = padding
        val right = width - padding
        val bottom = height * 2 - padding
        arcRect.set(left, top, right, bottom)

        // background half arc 180..180
        canvas.drawArc(arcRect, 180f, 180f, false, bgPaint)

        // ticks
        for (i in 0..10) {
            val angle = 180f + i * 18f
            val p1 = pointOnArc(arcRect, angle, 1f)
            val p2 = pointOnArc(arcRect, angle, 0.94f)
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, tickPaint)
        }

        // progress
        val sweep = (value.toFloat() / goal.toFloat()).coerceIn(0f, 1f) * 180f
        if (sweep > 0f) canvas.drawArc(arcRect, 180f, sweep, false, progressPaint)

        // center drop shape and fill
        val cx = width / 2f
        val cy = height * 0.65f
        val r = height * 0.28f
        val path = Path()
        path.moveTo(cx, cy - r)
        path.cubicTo(cx + r * 0.6f, cy - r * 0.4f, cx + r * 0.6f, cy + r * 0.2f, cx, cy + r)
        path.cubicTo(cx - r * 0.6f, cy + r * 0.2f, cx - r * 0.6f, cy - r * 0.4f, cx, cy - r)
        canvas.drawPath(path, dropPaint)

        // fill level inside drop
        val pct = (value.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
        if (pct > 0f) {
            val bounds = RectF()
            path.computeBounds(bounds, true)
            val levelY = bounds.bottom - bounds.height() * pct
            val fill = Path()
            fill.addRect(bounds.left - 20f, levelY, bounds.right + 20f, bounds.bottom + 20f, Path.Direction.CW)
            val filled = Path()
            filled.op(path, fill, Path.Op.INTERSECT)
            canvas.drawPath(filled, dropFillPaint)
        }
    }

    private fun pointOnArc(oval: RectF, angle: Float, radiusScale: Float): PointF {
        val rX = oval.width() / 2f * radiusScale
        val rY = oval.height() / 2f * radiusScale
        val cx = oval.centerX()
        val cy = oval.centerY()
        val rad = Math.toRadians(angle.toDouble())
        return PointF((cx + rX * Math.cos(rad)).toFloat(), (cy + rY * Math.sin(rad)).toFloat())
    }
}
