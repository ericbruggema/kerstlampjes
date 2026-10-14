package nl.eric.kerstlampjes

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.os.SystemClock
import android.view.View
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/**
 * Tekent een snoer kerstlampjes langs de rand van de view.
 * De lampjes hangen naar binnen (richting midden van het scherm).
 */
class LightsView(context: Context) : View(context) {

    private var settings = LightSettings()
    private val density = resources.displayMetrics.density

    // Per lampje: positie, binnenwaartse normaal, kleur, fonkel-parameters
    private var count = 0
    private var px = FloatArray(0)
    private var py = FloatArray(0)
    private var angleDeg = FloatArray(0)
    private var colorIdx = IntArray(0)
    private var phase = FloatArray(0)
    private var freq = FloatArray(0)

    private val wirePath = Path()
    private val glassPath = Path()
    private var glassCenterY = 0f
    private var glowRadius = 0f
    private var glowShaders: Array<Shader> = emptyArray()

    private val wirePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0xFF1F4D2B.toInt()
        strokeCap = Paint.Cap.ROUND
    }
    private val socketPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF2B3A30.toInt() }
    private val glassPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val shineRect = RectF()

    private var running = false
    private val startTime = SystemClock.uptimeMillis()
    private val frame = object : Runnable {
        override fun run() {
            if (!running) return
            // "Vast aan" verandert niet: dan hoeft er niets opnieuw getekend te worden.
            if (settings.mode != Modes.STEADY) invalidate()
            postDelayed(this, if (settings.mode == Modes.STEADY) 500L else Perf.frameDelay(context))
        }
    }

    fun apply(s: LightSettings) {
        settings = s
        alpha = s.opacity / 100f
        rebuild()
        invalidate()
    }

    fun setAnimating(on: Boolean) {
        if (on == running) return
        running = on
        removeCallbacks(frame)
        if (on) post(frame)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setAnimating(true)
    }

    override fun onDetachedFromWindow() {
        setAnimating(false)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        rebuild()
    }

    private fun rebuild() {
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        val size = settings.bulbSizeDp * density
        val inset = settings.insetDp * density + size * 0.15f
        val spacing = max(settings.spacingDp * density, size * 1.2f)
        val corner = settings.cornerDp * density
        val sag = settings.sagDp * density

        wirePaint.strokeWidth = max(1.5f, size * 0.14f)

        // Pad langs de rand (met de klok mee → normaal (-ty, tx) wijst naar binnen)
        val rect = RectF(inset, inset, w - inset, h - inset)
        val border = Path().apply { addRoundRect(rect, corner, corner, Path.Direction.CW) }
        val pm = PathMeasure(border, true)
        val len = pm.length
        count = max(4, (len / spacing).toInt())
        val step = len / count

        px = FloatArray(count); py = FloatArray(count); angleDeg = FloatArray(count)
        colorIdx = IntArray(count); phase = FloatArray(count); freq = FloatArray(count)

        val pos = FloatArray(2)
        val tan = FloatArray(2)
        val palette = Palettes.colors[settings.palette.coerceIn(0, Palettes.colors.size - 1)]
        val rnd = Random(42)
        for (i in 0 until count) {
            pm.getPosTan(i * step, pos, tan)
            px[i] = pos[0]; py[i] = pos[1]
            val nx = -tan[1]; val ny = tan[0]
            angleDeg[i] = (atan2(ny, nx) * 180f / PI.toFloat()) - 90f
            colorIdx[i] = i % palette.size
            phase[i] = rnd.nextFloat() * 2f * PI.toFloat()
            freq[i] = 0.35f + rnd.nextFloat() * 0.9f
        }

        // Draad: doorhangende boogjes tussen de lampjes
        wirePath.reset()
        wirePath.moveTo(px[0], py[0])
        for (i in 0 until count) {
            pm.getPosTan(i * step + step / 2f, pos, tan)
            val cx = pos[0] + (-tan[1]) * sag * 2f
            val cy = pos[1] + tan[0] * sag * 2f
            val j = (i + 1) % count
            wirePath.quadTo(cx, cy, px[j], py[j])
        }

        // Glazen lampje in lokale coördinaten (fitting begint op y=0, lampje wijst naar +y)
        val socketH = size * 0.45f
        val bw = size * 0.5f   // halve breedte
        val bh = size * 1.35f  // lengte glas
        val top = socketH * 0.9f
        glassPath.reset()
        glassPath.moveTo(0f, top)
        glassPath.cubicTo(bw * 1.25f, top, bw * 1.15f, top + bh * 0.62f, 0f, top + bh)
        glassPath.cubicTo(-bw * 1.15f, top + bh * 0.62f, -bw * 1.25f, top, 0f, top)
        glassPath.close()
        glassCenterY = top + bh * 0.5f
        glowRadius = size * 2.4f

        glowShaders = Array(palette.size) { k ->
            val c = palette[k]
            RadialGradient(
                0f, 0f, glowRadius,
                intArrayOf(withAlpha(c, 170), withAlpha(c, 60), withAlpha(c, 0)),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
        }
    }

    private fun intensity(i: Int, t: Float): Float {
        val sp = settings.speed / 5f
        return when (settings.mode) {
            Modes.STEADY -> 1f
            Modes.ALTERNATE -> {
                val v = 0.5f + 0.5f * sin(2f * PI.toFloat() * t * sp * 0.5f + (i % 2) * PI.toFloat())
                0.12f + 0.88f * ((v - 0.3f) / 0.4f).coerceIn(0f, 1f)
            }
            Modes.CHASE -> {
                val group = 4f
                var k = (i - t * sp * 3f) % group
                if (k < 0) k += group
                0.12f + 0.88f * exp(-k * k * 1.2f)
            }
            Modes.WAVE -> {
                val v = 0.5f + 0.5f * sin(i * 0.35f - t * sp * 2.5f)
                0.25f + 0.75f * v
            }
            else -> { // TWINKLE
                val v = 0.5f + 0.5f * sin(2f * PI.toFloat() * t * sp * freq[i] + phase[i])
                0.2f + 0.8f * v.pow(2.2f)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (count == 0) return
        val t = (SystemClock.uptimeMillis() - startTime) / 1000f
        val palette = Palettes.colors[settings.palette.coerceIn(0, Palettes.colors.size - 1)]
        val size = settings.bulbSizeDp * density
        val socketW = size * 0.42f
        val socketH = size * 0.45f
        val waveShift = if (settings.mode == Modes.WAVE) (t * settings.speed / 5f * 1.5f).toInt() else 0

        canvas.drawPath(wirePath, wirePaint)

        for (i in 0 until count) {
            val ci = (colorIdx[i] + waveShift) % palette.size
            val base = palette[ci]
            val k = intensity(i, t)

            canvas.save()
            canvas.translate(px[i], py[i])
            canvas.rotate(angleDeg[i])

            // gloed
            glowPaint.shader = glowShaders[ci]
            glowPaint.alpha = (255 * k * k).toInt().coerceIn(0, 255)
            canvas.save()
            canvas.translate(0f, glassCenterY)
            canvas.drawCircle(0f, 0f, glowRadius, glowPaint)
            canvas.restore()

            // fitting
            canvas.drawRoundRect(-socketW / 2, 0f, socketW / 2, socketH, size * 0.08f, size * 0.08f, socketPaint)

            // glas
            glassPaint.color = lerpColor(darken(base), base, k)
            canvas.drawPath(glassPath, glassPaint)

            // glimlicht
            shinePaint.alpha = (60 + 140 * k).toInt()
            shineRect.set(-size * 0.28f, socketH + size * 0.15f, -size * 0.08f, socketH + size * 0.65f)
            canvas.drawOval(shineRect, shinePaint)

            canvas.restore()
        }
    }

    private fun withAlpha(c: Int, a: Int) = Color.argb(a, Color.red(c), Color.green(c), Color.blue(c))

    private fun darken(c: Int): Int =
        Color.rgb(
            (Color.red(c) * 0.28f + 20).toInt(),
            (Color.green(c) * 0.28f + 20).toInt(),
            (Color.blue(c) * 0.28f + 20).toInt()
        )

    private fun lerpColor(a: Int, b: Int, f: Float): Int {
        val x = min(1f, max(0f, f))
        return Color.rgb(
            (Color.red(a) + (Color.red(b) - Color.red(a)) * x).toInt(),
            (Color.green(a) + (Color.green(b) - Color.green(a)) * x).toInt(),
            (Color.blue(a) + (Color.blue(b) - Color.blue(a)) * x).toInt()
        )
    }
}
