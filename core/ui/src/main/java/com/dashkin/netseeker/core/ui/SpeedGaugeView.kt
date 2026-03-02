package com.dashkin.netseeker.core.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Custom speedometer gauge that visualises internet speed via a 270° arc.
// The arc spans from 135° (bottom-left) to 405° (bottom-right) with three coloured zones:
//  - Red zone   0 → [THRESHOLD_SLOW_MBPS] Mbps
//  - Yellow zone [THRESHOLD_SLOW_MBPS] → [THRESHOLD_FAST_MBPS] Mbps
//  - Green zone  [THRESHOLD_FAST_MBPS] → [maxSpeedMbps] Mbps
// Call [setSpeed] to animate the needle to a new value.
class SpeedGaugeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    // --- Public API ---
    // Maximum value shown on the gauge. Defaults to 100 Mbps.
    var maxSpeedMbps: Float = MAX_SPEED_DEFAULT
        set(value) {
            field = value.coerceAtLeast(1f)
            invalidate()
        }

    // Current speed driving the needle position (0 .. [maxSpeedMbps]).
    var currentSpeedMbps: Float = 0f
        private set

    // Updates the needle position, optionally animating the transition.
    fun setSpeed(speedMbps: Float, animate: Boolean = true) {
        val target = speedMbps.coerceIn(0f, maxSpeedMbps)
        if (!animate) {
            currentSpeedMbps = target
            invalidate()
            return
        }
        speedAnimator.cancel()
        speedAnimator.setFloatValues(currentSpeedMbps, target)
        speedAnimator.start()
    }

    // --- Drawing geometry ---
    private val arcRect = RectF()
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    // --- Paints ---
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = COLOR_TRACK
        strokeCap = Paint.Cap.ROUND
    }

    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = COLOR_SLOW
        strokeCap = Paint.Cap.BUTT
    }

    private val yellowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = COLOR_MODERATE
        strokeCap = Paint.Cap.BUTT
    }

    private val greenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = COLOR_FAST
        strokeCap = Paint.Cap.BUTT
    }

    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeCap = Paint.Cap.ROUND
    }

    private val hubPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }

    private val speedTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }

    private val unitTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = COLOR_UNIT_TEXT
        textAlign = Paint.Align.CENTER
    }

    private val minLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = COLOR_UNIT_TEXT
        textAlign = Paint.Align.CENTER
    }

    // --- Animation ---

    private val speedAnimator = ValueAnimator().apply {
        duration = ANIMATION_DURATION_MS
        interpolator = DecelerateInterpolator()
        addUpdateListener { animator ->
            currentSpeedMbps = animator.animatedValue as Float
            invalidate()
        }
    }

    // --- Layout ---

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec)
        // Force square, height = width × 0.75 to leave room below the arc ends
        val desiredHeight = (size * HEIGHT_RATIO).toInt()
        setMeasuredDimension(size, resolveSize(desiredHeight, heightMeasureSpec))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val strokeWidth = w * STROKE_WIDTH_RATIO
        val arcPadding = strokeWidth / 2f + w * ARC_PADDING_RATIO

        radius = (min(w, h * 2) / 2f) - arcPadding
        centerX = w / 2f
        centerY = h * CENTER_Y_RATIO

        arcRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        trackPaint.strokeWidth = strokeWidth
        redPaint.strokeWidth = strokeWidth
        yellowPaint.strokeWidth = strokeWidth
        greenPaint.strokeWidth = strokeWidth
        needlePaint.strokeWidth = w * NEEDLE_WIDTH_RATIO

        speedTextPaint.textSize = w * SPEED_TEXT_RATIO
        unitTextPaint.textSize = w * UNIT_TEXT_RATIO
        minLabelPaint.textSize = w * LABEL_TEXT_RATIO
    }

    override fun onDraw(canvas: Canvas) {
        drawZoneArcs(canvas)
        drawNeedle(canvas)
        drawHub(canvas)
        drawText(canvas)
    }

    private fun drawZoneArcs(canvas: Canvas) {
        // Background track (full sweep, dim)
        canvas.drawArc(arcRect, ARC_START_ANGLE, ARC_SWEEP, false, trackPaint)

        // Coloured zone segments
        val slowFraction = (THRESHOLD_SLOW_MBPS / maxSpeedMbps).coerceIn(0f, 1f)
        val fastFraction = (THRESHOLD_FAST_MBPS / maxSpeedMbps).coerceIn(0f, 1f)

        val slowSweep = ARC_SWEEP * slowFraction
        val moderateSweep = ARC_SWEEP * (fastFraction - slowFraction)
        val fastSweep = ARC_SWEEP * (1f - fastFraction)

        canvas.drawArc(arcRect, ARC_START_ANGLE, slowSweep, false, redPaint)
        canvas.drawArc(arcRect, ARC_START_ANGLE + slowSweep, moderateSweep, false, yellowPaint)
        canvas.drawArc(arcRect, ARC_START_ANGLE + slowSweep + moderateSweep, fastSweep, false, greenPaint)
    }

    private fun drawNeedle(canvas: Canvas) {
        val fraction = currentSpeedMbps / maxSpeedMbps
        val angleDeg = ARC_START_ANGLE + fraction * ARC_SWEEP
        val angleRad = angleDeg * (PI / 180.0)

        val tipX = (centerX + radius * cos(angleRad)).toFloat()
        val tipY = (centerY + radius * sin(angleRad)).toFloat()

        // Short stub from center toward hub
        val stubRadius = radius * NEEDLE_BASE_RATIO
        val baseX = (centerX - stubRadius * cos(angleRad)).toFloat()
        val baseY = (centerY - stubRadius * sin(angleRad)).toFloat()

        canvas.drawLine(baseX, baseY, tipX, tipY, needlePaint)
    }

    private fun drawHub(canvas: Canvas) {
        val hubRadius = radius * HUB_RADIUS_RATIO
        canvas.drawCircle(centerX, centerY, hubRadius, hubPaint)
    }

    private fun drawText(canvas: Canvas) {
        val speedInt = currentSpeedMbps.toInt()
        val speedText = if (currentSpeedMbps < 10f && currentSpeedMbps > 0f) {
            "%.1f".format(currentSpeedMbps)
        } else {
            speedInt.toString()
        }

        val textY = centerY + radius * TEXT_OFFSET_RATIO
        canvas.drawText(speedText, centerX, textY, speedTextPaint)
        canvas.drawText("Mbps", centerX, textY + unitTextPaint.textSize * 1.2f, unitTextPaint)

        // Arc endpoint labels
        drawEndLabel(canvas, "0", ARC_START_ANGLE)
        drawEndLabel(canvas, maxSpeedMbps.toInt().toString(), ARC_START_ANGLE + ARC_SWEEP)
    }

    private fun drawEndLabel(canvas: Canvas, label: String, angleDeg: Float) {
        val angleRad = angleDeg * (PI / 180.0)
        val labelRadius = radius + minLabelPaint.textSize * LABEL_OFFSET_RATIO
        val x = (centerX + labelRadius * cos(angleRad)).toFloat()
        val y = (centerY + labelRadius * sin(angleRad)).toFloat()
        canvas.drawText(label, x, y, minLabelPaint)
    }

    companion object {
        // Geometry
        private const val ARC_START_ANGLE = 135f
        private const val ARC_SWEEP = 270f
        private const val HEIGHT_RATIO = 0.75f
        private const val CENTER_Y_RATIO = 0.65f
        private const val STROKE_WIDTH_RATIO = 0.06f
        private const val ARC_PADDING_RATIO = 0.04f
        private const val NEEDLE_WIDTH_RATIO = 0.015f
        private const val NEEDLE_BASE_RATIO = 0.15f
        private const val HUB_RADIUS_RATIO = 0.06f
        private const val TEXT_OFFSET_RATIO = 0.25f
        private const val LABEL_OFFSET_RATIO = 1.8f

        // Text sizes relative to view width
        private const val SPEED_TEXT_RATIO = 0.18f
        private const val UNIT_TEXT_RATIO = 0.065f
        private const val LABEL_TEXT_RATIO = 0.055f

        // Speed thresholds (mirrors SpotQuality thresholds)
        private const val THRESHOLD_SLOW_MBPS = 10f
        private const val THRESHOLD_FAST_MBPS = 50f
        private const val MAX_SPEED_DEFAULT = 100f

        // Colors
        private const val COLOR_TRACK = 0x33FFFFFF.toInt()  // translucent white
        private const val COLOR_SLOW = 0xFFF44336.toInt()   // Material Red 500
        private const val COLOR_MODERATE = 0xFFFFC107.toInt() // Material Amber 500
        private const val COLOR_FAST = 0xFF4CAF50.toInt()   // Material Green 500
        private const val COLOR_UNIT_TEXT = 0xFFB0BEC5.toInt() // Blue Grey 200

        private const val ANIMATION_DURATION_MS = 600L
    }
}
