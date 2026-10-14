package nl.eric.kerstlampjes

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/** Een interactieve kerstachtergrond die je als scène kunt kiezen. */
abstract class Scene(protected val ctx: Context, protected val d: Float) {
    var w = 0f
    var h = 0f
    abstract val name: String
    abstract val hint: String

    open fun resize(w: Float, h: Float) { this.w = w; this.h = h }
    abstract fun draw(c: Canvas, now: Long)
    open fun tap(x: Float, y: Float, now: Long): Boolean = false
    open fun longPress(x: Float, y: Float, now: Long): Boolean = false
    open fun shake(now: Long) {}

    protected val prefs get() = ctx.getSharedPreferences("kerstlampjes", Context.MODE_PRIVATE)
    protected val p = Paint(Paint.ANTI_ALIAS_FLAG)
    protected val path = Path()
    protected val rect = RectF()
    protected val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }

    private var skyKey = 0L
    private var skyShader: Shader? = null
    private val skyPaint = Paint()

    protected fun sky(c: Canvas, top: Int, bottom: Int, to: Float = 1f) {
        val key = top.toLong() * 31 + bottom + (h * to).toLong() * 1_000_003L
        if (skyShader == null || key != skyKey) {
            skyShader = LinearGradient(0f, 0f, 0f, h * to, top, bottom, Shader.TileMode.CLAMP)
            skyKey = key
        }
        skyPaint.shader = skyShader
        c.drawRect(0f, 0f, w, h, skyPaint)
    }

    private val starRnd = Random(21)
    private val starX = FloatArray(70) { starRnd.nextFloat() }
    private val starY = FloatArray(70) { starRnd.nextFloat() }
    private val starS = FloatArray(70) { starRnd.nextFloat() }

    protected fun stars(c: Canvas, t: Float, maxY: Float = 0.5f) {
        p.style = Paint.Style.FILL
        p.color = Color.WHITE
        for (i in starX.indices) {
            p.alpha = (80 + 160 * (0.5f + 0.5f * sin(t * (0.6f + starS[i]) + i))).toInt()
            c.drawCircle(starX[i] * w, starY[i] * h * maxY, (0.6f + starS[i] * 1.3f) * d, p)
        }
        p.alpha = 255
    }

    protected fun glow(c: Canvas, x: Float, y: Float, r: Float, color: Int, alpha: Int) {
        p.shader = RadialGradient(
            x, y, r,
            intArrayOf((color and 0x00FFFFFF) or (alpha.coerceIn(0, 255) shl 24), color and 0x00FFFFFF),
            null, Shader.TileMode.CLAMP
        )
        c.drawCircle(x, y, r, p)
        p.shader = null
    }

    protected fun text(c: Canvas, s: String, x: Float, y: Float, sizeDp: Float, color: Int = Color.WHITE, bold: Boolean = false) {
        textPaint.textSize = sizeDp * d
        textPaint.color = color
        textPaint.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        c.drawText(s, x, y, textPaint)
    }

    protected fun house(c: Canvas, x: Float, y: Float, bw: Float, bh: Float, color: Int, lit: Boolean, roof: Int = 0xFFF2F7FF.toInt()) {
        p.style = Paint.Style.FILL
        p.color = color
        c.drawRect(x, y - bh, x + bw, y, p)
        p.color = roof
        path.reset()
        path.moveTo(x - bw * 0.1f, y - bh)
        path.lineTo(x + bw / 2, y - bh - bw * 0.45f)
        path.lineTo(x + bw * 1.1f, y - bh)
        path.close()
        c.drawPath(path, p)
        // raam en deur
        p.color = if (lit) 0xFFFFD27A.toInt() else 0xFF1A2236.toInt()
        c.drawRect(x + bw * 0.15f, y - bh * 0.75f, x + bw * 0.4f, y - bh * 0.45f, p)
        c.drawRect(x + bw * 0.6f, y - bh * 0.75f, x + bw * 0.85f, y - bh * 0.45f, p)
        p.color = 0xFF3B2314.toInt()
        c.drawRect(x + bw * 0.42f, y - bh * 0.35f, x + bw * 0.58f, y, p)
        if (lit) glow(c, x + bw / 2, y - bh * 0.5f, bw * 1.1f, 0xFFFFC857.toInt(), 70)
    }

    protected fun fir(c: Canvas, x: Float, base: Float, th: Float, color: Int = 0xFF1E6B3A.toInt()) {
        p.style = Paint.Style.FILL
        p.color = color
        for (k in 0..2) {
            val top = base - th + k * th * 0.28f
            val bot = top + th * 0.5f
            val hw = th * (0.18f + 0.1f * k)
            path.reset()
            path.moveTo(x, top); path.lineTo(x + hw, bot); path.lineTo(x - hw, bot); path.close()
            c.drawPath(path, p)
        }
        p.color = 0xFF4A2D18.toInt()
        c.drawRect(x - th * 0.04f, base - th * 0.08f, x + th * 0.04f, base, p)
    }

    /** Neerdwarrelende sneeuw. */
    protected class Flakes(n: Int, seed: Int) {
        private val rnd = Random(seed)
        val x = FloatArray(n) { rnd.nextFloat() }
        val y = FloatArray(n) { rnd.nextFloat() }
        val v = FloatArray(n) { rnd.nextFloat() }
    }

    protected fun snow(c: Canvas, f: Flakes, t: Float, alpha: Int = 200) {
        p.style = Paint.Style.FILL
        p.color = Color.WHITE
        p.alpha = alpha
        for (i in f.x.indices) {
            val yy = ((f.y[i] + t * (0.03f + f.v[i] * 0.05f)) % 1f) * h
            val xx = (f.x[i] + 0.015f * sin(t * 0.7f + i)) * w
            c.drawCircle(xx, yy, (0.8f + f.v[i] * 1.6f) * d, p)
        }
        p.alpha = 255
    }

    protected fun today(): Pair<Int, Int> {
        val cal = Calendar.getInstance()
        return Pair(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }
}

// ───────────────────────────── 1. Sneeuwbol ─────────────────────────────

class SnowGlobeScene(ctx: Context, d: Float) : Scene(ctx, d) {
    override val name = "Sneeuwbol"
    override val hint = "Schud je telefoon of tik op de bol: dan gaat het sneeuwen!"

    private val n = 200
    private val rnd = Random(9)
    private val fx = FloatArray(n) { rnd.nextFloat() * 2f - 1f }
    private val fy = FloatArray(n) { rnd.nextFloat() * 1.4f - 0.9f }
    private val fvx = FloatArray(n)
    private val fvy = FloatArray(n)
    private var last = 0L
    private var shakeAt = -10_000L

    override fun shake(now: Long) {
        shakeAt = now
        for (i in 0 until n) {
            fvy[i] = -(0.8f + rnd.nextFloat() * 1.6f)
            fvx[i] = (rnd.nextFloat() - 0.5f) * 1.6f
        }
    }

    override fun tap(x: Float, y: Float, now: Long): Boolean {
        shake(now)
        return true
    }

    override fun draw(c: Canvas, now: Long) {
        val dt = if (last == 0L) 0.016f else ((now - last) / 1000f).coerceIn(0.001f, 0.05f)
        last = now
        val t = now / 1000f
        c.drawColor(0xFF0A0E1C.toInt())
        stars(c, t, 0.4f)
        val cx = w / 2f
        val cy = h * 0.42f
        val r = w * 0.40f

        // voet
        p.style = Paint.Style.FILL
        p.color = 0xFF7A2E1E.toInt()
        c.drawRoundRect(cx - r * 0.78f, cy + r * 0.72f, cx + r * 0.78f, cy + r * 1.18f, 24f, 24f, p)
        p.color = 0xFFD9A441.toInt()
        c.drawRect(cx - r * 0.78f, cy + r * 0.86f, cx + r * 0.78f, cy + r * 0.9f, p)
        text(c, "Gelukkig kerstfeest", cx, cy + r * 1.08f, 15f, 0xFFFFE8B0.toInt(), true)

        // bol-inhoud
        path.reset()
        path.addCircle(cx, cy, r, Path.Direction.CW)
        c.save()
        c.clipPath(path)
        p.shader = LinearGradient(0f, cy - r, 0f, cy + r, 0xFF2A5599.toInt(), 0xFF0E1E3C.toInt(), Shader.TileMode.CLAMP)
        c.drawRect(cx - r, cy - r, cx + r, cy + r, p)
        p.shader = null
        p.color = 0xFFEAF2FF.toInt()
        c.drawOval(cx - r * 1.3f, cy + r * 0.50f, cx + r * 1.3f, cy + r * 1.5f, p)
        house(c, cx - r * 0.62f, cy + r * 0.66f, r * 0.30f, r * 0.24f, 0xFFB5493A.toInt(), true)
        house(c, cx - r * 0.12f, cy + r * 0.60f, r * 0.36f, r * 0.30f, 0xFF4A6FA5.toInt(), true)
        house(c, cx + r * 0.38f, cy + r * 0.66f, r * 0.30f, r * 0.24f, 0xFFC9913A.toInt(), true)
        fir(c, cx + r * 0.78f, cy + r * 0.66f, r * 0.55f)
        fir(c, cx - r * 0.80f, cy + r * 0.62f, r * 0.45f)

        // sneeuw
        val shakeF = max(0f, 1f - (now - shakeAt) / 2500f)
        p.color = Color.WHITE
        for (i in 0 until n) {
            fvy[i] += (0.55f - 0.0f) * dt * (1f - shakeF * 0.7f)
            fvx[i] += sin(t * 1.3f + i) * 0.15f * dt
            fvy[i] *= (1f - 0.8f * dt)
            fvx[i] *= (1f - 1.2f * dt)
            fx[i] += fvx[i] * dt * 0.5f
            fy[i] += fvy[i] * dt * 0.5f
            val rr = fx[i] * fx[i] + fy[i] * fy[i]
            if (rr > 0.92f) {         // niet door het glas
                fx[i] *= 0.96f; fy[i] *= 0.96f
                fvx[i] *= -0.4f; fvy[i] *= -0.4f
            }
            if (fy[i] > 0.62f - 0.1f * abs(fx[i])) {   // blijft op de heuvel liggen
                fy[i] = 0.62f - 0.1f * abs(fx[i]) + (i % 3) * 0.01f
                fvy[i] = 0f
                if (shakeF <= 0f && rnd.nextFloat() < 0.0005f) fy[i] = -0.9f
            }
            p.alpha = 230
            c.drawCircle(cx + fx[i] * r, cy + fy[i] * r, (1.0f + (i % 3) * 0.6f) * d, p)
        }
        p.alpha = 255
        c.restore()

        // glas
        p.style = Paint.Style.STROKE
        p.strokeWidth = 3f * d
        p.color = 0x55FFFFFF
        c.drawCircle(cx, cy, r, p)
        p.strokeWidth = 5f * d
        p.color = 0x44FFFFFF
        rect.set(cx - r * 0.82f, cy - r * 0.82f, cx + r * 0.82f, cy + r * 0.82f)
        c.drawArc(rect, 200f, 50f, false, p)
        p.style = Paint.Style.FILL
    }
}

// ───────────────────────────── 2. Adventskalender ─────────────────────────────

class AdventCalendarScene(ctx: Context, d: Float) : Scene(ctx, d) {
    override val name = "Adventskalender"
    override val hint = "Tik op het luikje van vandaag. Buiten december mag je ze allemaal alvast openen."

    private val messages = arrayOf(
        "🍪 Tijd voor een kerstkoekje!", "⛄ Bouw vandaag een sneeuwpop", "🎶 Zet je favoriete kerstlied op",
        "🕯️ Steek een kaarsje aan", "🎁 Pakjesavond! Is je schoen gezet?", "🍫 Chocolademelk met slagroom",
        "✉️ Schrijf een kerstkaart", "🌟 Kijk naar de sterren", "🧦 Hang de sokken op",
        "🎄 Versier de boom", "🥧 Bak iets lekkers", "📖 Lees een kerstverhaal",
        "🧣 Doe iets warms aan", "🔥 Maak het gezellig bij de haard", "🎅 Schrijf aan de kerstman",
        "🎨 Knutsel een ster", "🍊 Eet een mandarijn", "👐 Doe iets liefs voor een ander",
        "🦌 Zoek de rendieren", "🏂 Ga naar buiten, hoe koud ook", "🎬 Kijk een kerstfilm",
        "🌙 De langste nacht is voorbij", "🎁 Morgen is het zover!", "🎄 Kerstavond! Geniet ervan"
    )
    private val colors = intArrayOf(
        0xFFC62828.toInt(), 0xFF2E7D32.toInt(), 0xFF1565C0.toInt(), 0xFFF9A825.toInt(), 0xFF6A1B9A.toInt(), 0xFF00838F.toInt()
    )
    private val opened = HashMap<Int, Long>()    // luik -> tijdstip van openen
    private var shownMsg = -1
    private var shownAt = 0L
    private var lockedAt = -10_000L
    private var lockedDoor = -1

    init {
        prefs.getString("advent_open", "")!!.split(',').mapNotNull { it.toIntOrNull() }.forEach { opened[it] = -100_000L }
    }

    private fun cols() = 4
    private fun rows() = 6
    private fun cell(i: Int): RectF {
        val gx = w * 0.05f
        val gy = h * 0.14f
        val cw = (w - 2 * gx) / cols()
        val ch = (h * 0.70f) / rows()
        val col = (i) % cols()
        val row = (i) / cols()
        return RectF(gx + col * cw + cw * 0.06f, gy + row * ch + ch * 0.06f, gx + (col + 1) * cw - cw * 0.06f, gy + (row + 1) * ch - ch * 0.06f)
    }

    private fun allowed(day: Int): Boolean {
        val (m, dd) = today()
        return if (m == 12) day <= dd else true
    }

    override fun tap(x: Float, y: Float, now: Long): Boolean {
        for (i in 0 until 24) {
            if (cell(i).contains(x, y)) {
                val day = i + 1
                if (!allowed(day)) {
                    lockedDoor = i; lockedAt = now
                } else {
                    if (!opened.containsKey(day)) {
                        opened[day] = now
                        prefs.edit().putString("advent_open", opened.keys.joinToString(",")).apply()
                    }
                    shownMsg = i; shownAt = now
                }
                return true
            }
        }
        shownMsg = -1
        return true
    }

    override fun longPress(x: Float, y: Float, now: Long): Boolean {
        opened.clear()
        prefs.edit().putString("advent_open", "").apply()
        shownMsg = -1
        return true
    }

    override fun draw(c: Canvas, now: Long) {
        val t = now / 1000f
        sky(c, 0xFF1B0F2E.toInt(), 0xFF0E2B22.toInt())
        stars(c, t, 0.15f)
        text(c, "Adventskalender", w / 2, h * 0.075f, 24f, 0xFFFFE8B0.toInt(), true)
        val (m, dd) = today()
        text(c, if (m == 12) "Vandaag: ${dd} december" else "Buiten december: alles mag open", w / 2, h * 0.105f, 13f, 0xFFB8C9BE.toInt())
        for (i in 0 until 24) {
            val r = cell(i)
            val day = i + 1
            val ageSec = if (opened.containsKey(day)) (now - opened[day]!!) / 1000f else -1f
            val open = ageSec >= 0f
            // binnenkant
            p.style = Paint.Style.FILL
            p.color = if (open) 0xFFFFE9A8.toInt() else 0xFF2B1F3A.toInt()
            c.drawRoundRect(r, 10f, 10f, p)
            if (open) {
                glow(c, r.centerX(), r.centerY(), r.width() * 0.9f, 0xFFFFC857.toInt(), 120)
                val em = messages[i].takeWhile { it != ' ' }
                text(c, em, r.centerX(), r.centerY() + r.height() * 0.15f, 26f)
            }
            // het luikje zelf (klapt open)
            val k = if (open) min(1f, ageSec / 0.5f) else 0f
            val doorW = r.width() * (1f - 0.85f * k)
            p.color = colors[(i * 7 + 3) % colors.size]
            if (lockedDoor == i) {
                val s = ((now - lockedAt) / 600f)
                if (s in 0f..1f) c.translate(sin(s * 40f) * 5f * d * (1f - s), 0f)
            }
            c.drawRoundRect(r.left, r.top, r.left + doorW, r.bottom, 10f, 10f, p)
            if (doorW > r.width() * 0.4f) {
                p.color = 0x33FFFFFF
                c.drawRoundRect(r.left + 6f, r.top + 6f, r.left + doorW - 6f, r.top + r.height() * 0.4f, 8f, 8f, p)
                text(c, "$day", r.left + doorW / 2, r.centerY() + 9f * d, 22f, Color.WHITE, true)
            }
            if (lockedDoor == i) {
                val s = ((now - lockedAt) / 600f)
                if (s in 0f..1f) c.translate(-sin(s * 40f) * 5f * d * (1f - s), 0f)
            }
            if (!allowed(day)) {
                p.color = 0x66000000
                c.drawRoundRect(r, 10f, 10f, p)
            }
        }
        // boodschap
        if (shownMsg >= 0) {
            val a = min(1f, (now - shownAt) / 300f)
            p.color = ((200 * a).toInt() shl 24) or 0x000000
            c.drawRoundRect(w * 0.08f, h * 0.88f, w * 0.92f, h * 0.97f, 20f, 20f, p)
            text(c, "Dag ${shownMsg + 1}: ${messages[shownMsg]}", w / 2, h * 0.935f, 16f, 0xFFFFE8B0.toInt(), true)
        } else if (lockedAt > 0 && now - lockedAt < 2000) {
            text(c, "Nog niet! Dit luikje is voor dag ${lockedDoor + 1}", w / 2, h * 0.935f, 15f, 0xFFFFB4B4.toInt(), true)
        }
    }
}

// ───────────────────────────── 3. Adventskrans ─────────────────────────────

class AdventWreathScene(ctx: Context, d: Float) : Scene(ctx, d) {
    override val name = "Adventskrans"
    override val hint = "Tik op een kaars om hem aan of uit te steken."

    private val lit = BooleanArray(4)
    private var flicker = 0f

    init {
        val saved = prefs.getString("wreath_lit", null)
        if (saved != null && saved.length == 4) {
            for (i in 0 until 4) lit[i] = saved[i] == '1'
        } else {
            val n = adventSundaysPassed()
            for (i in 0 until 4) lit[i] = i < n
        }
    }

    private fun adventSundaysPassed(): Int {
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val dec24 = Calendar.getInstance().apply { set(year, Calendar.DECEMBER, 24, 0, 0, 0) }
        // 4e advent = zondag op of vlak voor 24 december
        while (dec24.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) dec24.add(Calendar.DAY_OF_YEAR, -1)
        var n = 0
        for (k in 3 downTo 0) {
            val s = (dec24.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -7 * k) }
            if (!now.before(s)) n++
        }
        return n
    }

    private fun candleX(i: Int, cx: Float, r: Float) = cx + cos(Math.toRadians(-135.0 + i * 90.0 / 1.5 + 0.0)).toFloat() * 0f + (i - 1.5f) * r * 0.62f
    private fun candleBase(i: Int, cy: Float, r: Float): Float {
        val k = (i - 1.5f) / 1.5f
        return cy - r * 0.15f + k * k * r * 0.08f
    }

    override fun tap(x: Float, y: Float, now: Long): Boolean {
        val cx = w / 2f
        val cy = h * 0.55f
        val r = w * 0.36f
        for (i in 0 until 4) {
            val bx = candleX(i, cx, r)
            val by = candleBase(i, cy, r)
            if (abs(x - bx) < w * 0.07f && y in (by - h * 0.22f)..(by + h * 0.04f)) {
                lit[i] = !lit[i]
                prefs.edit().putString("wreath_lit", lit.joinToString("") { if (it) "1" else "0" }).apply()
                return true
            }
        }
        return false
    }

    override fun draw(c: Canvas, now: Long) {
        val t = now / 1000f
        sky(c, 0xFF1A0D14.toInt(), 0xFF2B1410.toInt())
        val cx = w / 2f
        val cy = h * 0.55f
        val r = w * 0.36f
        val anyLit = lit.count { it }

        // warme kamerglans van de kaarsen
        if (anyLit > 0) glow(c, cx, cy - r * 0.2f, w * 0.95f, 0xFFFFA040.toInt(), 30 + anyLit * 18)

        // krans (ellips van bladeren)
        val rnd = Random(4)
        p.style = Paint.Style.FILL
        for (i in 0 until 160) {
            val a = (i / 160f * 2f * PI).toFloat()
            val rr = r * (0.92f + rnd.nextFloat() * 0.16f)
            val x = cx + cos(a) * rr
            val y = cy + sin(a) * rr * 0.34f
            p.color = if (i % 3 == 0) 0xFF2E9B4A.toInt() else 0xFF1E6B3A.toInt()
            c.drawOval(x - 14f * d, y - 9f * d, x + 14f * d, y + 9f * d, p)
        }
        p.color = 0xFFD7263D.toInt()
        for (i in 0 until 18) {
            val a = (i / 18f * 2f * PI).toFloat() + 0.2f
            c.drawCircle(cx + cos(a) * r, cy + sin(a) * r * 0.34f, 5f * d, p)
        }
        // strik
        p.color = 0xFFD7263D.toInt()
        c.drawOval(cx - 34f * d, cy + r * 0.34f - 4f * d, cx - 4f * d, cy + r * 0.34f + 20f * d, p)
        c.drawOval(cx + 4f * d, cy + r * 0.34f - 4f * d, cx + 34f * d, cy + r * 0.34f + 20f * d, p)

        // kaarsen
        val wax = intArrayOf(0xFFB71C1C.toInt(), 0xFFB71C1C.toInt(), 0xFFB71C1C.toInt(), 0xFFF5F0E0.toInt())
        for (i in 0 until 4) {
            val bx = candleX(i, cx, r)
            val by = candleBase(i, cy, r)
            val hh = h * 0.17f
            p.color = if (i == 3) 0xFFF5F0E0.toInt() else 0xFFB71C1C.toInt()
            c.drawRoundRect(bx - w * 0.025f, by - hh, bx + w * 0.025f, by + 6f * d, 6f, 6f, p)
            p.color = 0xFF222222.toInt()
            c.drawRect(bx - 1.5f * d, by - hh - 8f * d, bx + 1.5f * d, by - hh, p)
            if (lit[i]) {
                val fl = 0.85f + 0.15f * sin(t * 9f + i * 2f) * sin(t * 4.3f + i)
                val fy = by - hh - 8f * d
                glow(c, bx, fy - 10f * d, 60f * d, 0xFFFFC857.toInt(), (140 * fl).toInt())
                path.reset()
                path.moveTo(bx, fy + 4f * d)
                path.quadTo(bx - 9f * d, fy - 12f * d, bx + sin(t * 6f + i) * 3f * d, fy - 30f * d * fl)
                path.quadTo(bx + 9f * d, fy - 12f * d, bx, fy + 4f * d)
                p.color = 0xFFFFA726.toInt()
                c.drawPath(path, p)
                p.color = 0xFFFFF3B0.toInt()
                c.drawOval(bx - 3.5f * d, fy - 14f * d, bx + 3.5f * d, fy + 2f * d, p)
            }
        }
        val names = arrayOf("1e", "2e", "3e", "4e")
        text(c, "$anyLit van 4 kaarsen branden", cx, h * 0.80f, 15f, 0xFFFFE8B0.toInt(), true)
        text(c, "Advent", cx, h * 0.12f, 28f, 0xFFFFE8B0.toInt(), true)
        if (anyLit == 4) text(c, "Het is bijna kerstmis!", cx, h * 0.16f, 14f, 0xFFB8C9BE.toInt())
        else if (anyLit > 0) text(c, "${names[anyLit - 1]} advent", cx, h * 0.16f, 14f, 0xFFB8C9BE.toInt())
    }
}

// ───────────────────────────── 4. Kerstdorp met trein ─────────────────────────────

class VillageScene(ctx: Context, d: Float) : Scene(ctx, d) {
    override val name = "Kerstdorp"
    override val hint = "Tik op een huisje voor licht, tik op de trein om te toeteren."

    private val lit = BooleanArray(6) { true }
    private var tootAt = -10_000L
    private val flakes = Flakes(70, 3)

    init {
        val s = prefs.getString("village_lit", null)
        if (s != null && s.length == 6) for (i in 0 until 6) lit[i] = s[i] == '1'
    }

    private val houseColors = intArrayOf(0xFFB5493A.toInt(), 0xFF4A6FA5.toInt(), 0xFFC9913A.toInt(), 0xFF5E8C5A.toInt(), 0xFF8E5A9E.toInt(), 0xFFB8604A.toInt())

    private fun houseRect(i: Int): RectF {
        val bw = w * 0.13f
        val gap = (w - 6 * bw) / 7f
        val x = gap + i * (bw + gap)
        val bh = h * (0.09f + (i * 37 % 4) * 0.015f)
        val y = h * 0.64f
        return RectF(x, y - bh - bw * 0.45f, x + bw, y)
    }

    private fun trainX(now: Long): Float {
        val speed = if (now - tootAt < 2500) 0.16f else 0.08f
        val travelled = trainPos
        return travelled
    }

    private var trainPos = 0f
    private var lastT = 0L

    override fun tap(x: Float, y: Float, now: Long): Boolean {
        for (i in 0 until 6) {
            if (houseRect(i).contains(x, y)) {
                lit[i] = !lit[i]
                prefs.edit().putString("village_lit", lit.joinToString("") { if (it) "1" else "0" }).apply()
                return true
            }
        }
        if (y in (h * 0.74f)..(h * 0.86f)) {
            tootAt = now
            return true
        }
        return false
    }

    override fun draw(c: Canvas, now: Long) {
        val t = now / 1000f
        val dt = if (lastT == 0L) 0.016f else ((now - lastT) / 1000f).coerceIn(0.001f, 0.05f)
        lastT = now
        trainPos += dt * (if (now - tootAt < 2500) 0.22f else 0.10f)
        if (trainPos > 1.95f) trainPos -= 1.95f

        sky(c, 0xFF08122B.toInt(), 0xFF1B2F55.toInt(), 0.7f)
        stars(c, t, 0.35f)
        // heuvels
        p.style = Paint.Style.FILL
        p.color = 0xFF15244A.toInt()
        c.drawOval(-w * 0.3f, h * 0.50f, w * 0.7f, h * 0.80f, p)
        c.drawOval(w * 0.3f, h * 0.53f, w * 1.3f, h * 0.82f, p)
        // grond
        p.shader = LinearGradient(0f, h * 0.62f, 0f, h, 0xFFEAF2FF.toInt(), 0xFFB8C9E8.toInt(), Shader.TileMode.CLAMP)
        c.drawRect(0f, h * 0.62f, w, h, p)
        p.shader = null
        // dennen
        fir(c, w * 0.04f, h * 0.65f, h * 0.14f)
        fir(c, w * 0.96f, h * 0.66f, h * 0.16f)
        // huisjes
        for (i in 0 until 6) {
            val r = houseRect(i)
            house(c, r.left, r.bottom, r.width(), r.height() - r.width() * 0.45f, houseColors[i], lit[i])
        }
        // spoor
        val ty = h * 0.80f
        p.color = 0xFF4A4E58.toInt()
        c.drawRect(0f, ty + 14f * d, w, ty + 18f * d, p)
        p.color = 0xFF5A3A22.toInt()
        var sx = 0f
        while (sx < w) { c.drawRect(sx, ty + 12f * d, sx + 8f * d, ty + 22f * d, p); sx += 22f * d }
        // trein (rijdt van rechts naar links)
        val len = w * 0.12f
        val head = w * 1.2f - trainPos * w
        val cars = intArrayOf(0xFFC62828.toInt(), 0xFF2E7D32.toInt(), 0xFFF9A825.toInt(), 0xFF1565C0.toInt())
        for (k in 0 until 4) {
            val x = head + k * (len * 1.15f)
            if (k == 0) {
                p.color = 0xFF222831.toInt()
                c.drawRoundRect(x, ty - 30f * d, x + len, ty + 14f * d, 8f, 8f, p)
                p.color = 0xFFC62828.toInt()
                c.drawRect(x + len * 0.55f, ty - 44f * d, x + len * 0.95f, ty - 20f * d, p)
                p.color = 0xFF111111.toInt()
                c.drawRect(x + len * 0.12f, ty - 46f * d, x + len * 0.30f, ty - 28f * d, p)
                glow(c, x - 6f * d, ty - 8f * d, 40f * d, 0xFFFFF3B0.toInt(), 160)
                // rook
                for (s in 0 until 5) {
                    val ph = ((t * 0.8f + s * 0.2f) % 1f)
                    p.color = 0xFFFFFFFF.toInt()
                    p.alpha = (200 * (1f - ph)).toInt()
                    c.drawCircle(x + len * 0.2f + ph * 40f * d, ty - 50f * d - ph * 80f * d, (5f + ph * 12f) * d, p)
                }
                p.alpha = 255
            } else {
                p.color = cars[k % cars.size]
                c.drawRoundRect(x, ty - 26f * d, x + len, ty + 14f * d, 6f, 6f, p)
                p.color = 0xFFFFD27A.toInt()
                c.drawRect(x + len * 0.15f, ty - 18f * d, x + len * 0.35f, ty - 6f * d, p)
                c.drawRect(x + len * 0.55f, ty - 18f * d, x + len * 0.75f, ty - 6f * d, p)
            }
            p.color = 0xFF111111.toInt()
            c.drawCircle(x + len * 0.2f, ty + 14f * d, 7f * d, p)
            c.drawCircle(x + len * 0.75f, ty + 14f * d, 7f * d, p)
        }
        if (now - tootAt < 1200) text(c, "TOET TOET!", head, ty - 70f * d, 18f, 0xFFFFE8B0.toInt(), true)
        snow(c, flakes, t)
    }
}

// ───────────────────────────── 5. Slee van de kerstman ─────────────────────────────

class SleighScene(ctx: Context, d: Float) : Scene(ctx, d) {
    override val name = "Slee van de kerstman"
    override val hint = "Tik op de slee voor een regen van cadeautjes. Tik op de lucht voor een sterretje."

    private class Gift(var x: Float, var y: Float, var vy: Float, val color: Int, var rot: Float, var born: Long)
    private class Spark(val x: Float, val y: Float, val born: Long)

    private val gifts = ArrayList<Gift>()
    private val sparks = ArrayList<Spark>()
    private var sleighX = 0f
    private var sleighY = 0f
    private val colors = intArrayOf(0xFFD7263D.toInt(), 0xFF2E86DE.toInt(), 0xFF1E9E57.toInt(), 0xFFF4B400.toInt(), 0xFFB36BFF.toInt())

    override fun tap(x: Float, y: Float, now: Long): Boolean {
        if (abs(x - sleighX) < w * 0.35f && abs(y - sleighY) < h * 0.1f) {
            for (i in 0 until 9) {
                gifts.add(Gift(sleighX + (i - 4) * w * 0.02f, sleighY + h * 0.04f, 0.1f + (i % 3) * 0.05f, colors[i % colors.size], i * 17f, now))
            }
        } else {
            sparks.add(Spark(x, y, now))
        }
        return true
    }

    override fun draw(c: Canvas, now: Long) {
        val t = now / 1000f
        sky(c, 0xFF050B24.toInt(), 0xFF1B2A5C.toInt())
        stars(c, t, 0.7f)
        // maan
        glow(c, w * 0.8f, h * 0.12f, w * 0.25f, 0xFFCFE0FF.toInt(), 80)
        p.style = Paint.Style.FILL
        p.color = 0xFFEFF3FF.toInt()
        c.drawCircle(w * 0.8f, h * 0.12f, w * 0.05f, p)

        // heuvels met huisjes
        p.color = 0xFFDDE8FA.toInt()
        c.drawOval(-w * 0.2f, h * 0.80f, w * 0.8f, h * 1.2f, p)
        p.color = 0xFFC9D8F0.toInt()
        c.drawOval(w * 0.2f, h * 0.84f, w * 1.3f, h * 1.25f, p)
        house(c, w * 0.12f, h * 0.90f, w * 0.14f, h * 0.05f, 0xFFB5493A.toInt(), true)
        house(c, w * 0.62f, h * 0.92f, w * 0.16f, h * 0.055f, 0xFF4A6FA5.toInt(), true)
        fir(c, w * 0.45f, h * 0.90f, h * 0.10f)

        // slee + rendieren vliegen een lus
        val period = 14f
        val ph = (t % period) / period
        sleighX = -w * 0.62f + ph * w * 1.95f
        sleighY = h * 0.30f + sin(ph * 2f * PI.toFloat() * 2f) * h * 0.06f + sin(t * 2f) * h * 0.008f
        val tilt = cos(ph * 2f * PI.toFloat() * 2f) * 6f

        c.save()
        c.translate(sleighX, sleighY)
        c.rotate(tilt)
        val u = w * 0.012f
        // touwen
        p.style = Paint.Style.STROKE
        p.strokeWidth = 1.5f * d
        p.color = 0xFFB9A27A.toInt()
        c.drawLine(10 * u, 0f, 36 * u, 0f, p)
        p.style = Paint.Style.FILL
        // rendieren (4 stuks)
        for (k in 0 until 4) {
            val rx = (14 + k * 6) * u
            val bob = sin(t * 7f + k) * u * 0.8f
            p.color = 0xFF6B4423.toInt()
            c.drawOval(rx, -3 * u + bob, rx + 5 * u, 1 * u + bob, p)
            c.drawCircle(rx + 6 * u, -4 * u + bob, 1.4f * u, p)
            // poten
            p.style = Paint.Style.STROKE
            p.strokeWidth = 1.3f * d
            val lg = sin(t * 9f + k) * u
            c.drawLine(rx + 1 * u, 1 * u + bob, rx + 0 * u + lg, 4 * u, p)
            c.drawLine(rx + 4 * u, 1 * u + bob, rx + 5 * u - lg, 4 * u, p)
            // gewei
            c.drawLine(rx + 6 * u, -5 * u + bob, rx + 5 * u, -8 * u + bob, p)
            c.drawLine(rx + 6.5f * u, -5 * u + bob, rx + 8 * u, -8 * u + bob, p)
            p.style = Paint.Style.FILL
            if (k == 3) {
                p.color = 0xFFFF2B2B.toInt()
                c.drawCircle(rx + 7.6f * u, -4 * u + bob, 0.9f * u, p)
                glow(c, rx + 7.6f * u, -4 * u + bob, 4 * u, 0xFFFF4040.toInt(), 150)
            }
        }
        // slee
        p.color = 0xFFC62828.toInt()
        path.reset()
        path.moveTo(-8 * u, -6 * u)
        path.lineTo(10 * u, -4 * u)
        path.lineTo(8 * u, 3 * u)
        path.lineTo(-6 * u, 3 * u)
        path.quadTo(-10 * u, 0f, -8 * u, -6 * u)
        path.close()
        c.drawPath(path, p)
        p.style = Paint.Style.STROKE
        p.strokeWidth = 2f * d
        p.color = 0xFFD9A441.toInt()
        c.drawLine(-9 * u, 5 * u, 10 * u, 5 * u, p)
        c.drawLine(-6 * u, 3 * u, -6 * u, 5 * u, p)
        c.drawLine(6 * u, 3 * u, 6 * u, 5 * u, p)
        p.style = Paint.Style.FILL
        // kerstman
        p.color = 0xFFC62828.toInt()
        c.drawCircle(-1 * u, -8 * u, 3 * u, p)
        p.color = 0xFFFFD6B0.toInt()
        c.drawCircle(-1 * u, -10.5f * u, 1.5f * u, p)
        p.color = Color.WHITE
        c.drawCircle(-1 * u, -9.2f * u, 1.3f * u, p)
        p.color = 0xFFC62828.toInt()
        c.drawCircle(-1 * u, -12.2f * u, 1.3f * u, p)
        // zak met cadeautjes
        p.color = 0xFF8A5A2B.toInt()
        c.drawCircle(-6 * u, -7 * u, 3.2f * u, p)
        glow(c, 0f, 0f, 24 * u, 0xFFFFE8B0.toInt(), 30)
        c.restore()

        // vallende cadeautjes
        val it = gifts.iterator()
        while (it.hasNext()) {
            val g = it.next()
            val age = (now - g.born) / 1000f
            g.vy += 0.002f
            g.y += g.vy * h * 0.016f
            g.rot += 3f
            if (g.y > h * 0.93f || age > 6f) { it.remove(); continue }
            c.save()
            c.translate(g.x, g.y)
            c.rotate(g.rot)
            p.color = g.color
            c.drawRect(-9f * d, -9f * d, 9f * d, 9f * d, p)
            p.color = 0xFFFFE8B0.toInt()
            c.drawRect(-9f * d, -1.5f * d, 9f * d, 1.5f * d, p)
            c.drawRect(-1.5f * d, -9f * d, 1.5f * d, 9f * d, p)
            c.restore()
        }
        // sterretjes
        val sp = sparks.iterator()
        while (sp.hasNext()) {
            val s = sp.next()
            val a = (now - s.born) / 1200f
            if (a > 1f) { sp.remove(); continue }
            p.color = 0xFFFFF3B0.toInt()
            p.alpha = (255 * (1f - a)).toInt()
            for (k in 0 until 8) {
                val ang = k * PI.toFloat() / 4f
                val r1 = a * 36f * d
                c.drawCircle(s.x + cos(ang) * r1, s.y + sin(ang) * r1, (3f - a * 2f) * d, p)
            }
            p.alpha = 255
        }
    }
}

// ───────────────────────────── 6. Sneeuwpop bouwen ─────────────────────────────

class SnowmanScene(ctx: Context, d: Float) : Scene(ctx, d) {
    override val name = "Sneeuwpop bouwen"
    override val hint = "Tik om de sneeuwpop stap voor stap te bouwen. Houd ingedrukt om opnieuw te beginnen."

    private var stage = prefs.getInt("snowman_stage", 0)
    private var variant = prefs.getInt("snowman_variant", 0)
    private var stageAt = -10_000L
    private val flakes = Flakes(80, 6)
    private val hatColors = intArrayOf(0xFF222222.toInt(), 0xFFC62828.toInt(), 0xFF1565C0.toInt(), 0xFF2E7D32.toInt())
    private val scarfColors = intArrayOf(0xFFC62828.toInt(), 0xFF2E7D32.toInt(), 0xFFF9A825.toInt(), 0xFF6A1B9A.toInt())
    private val labels = arrayOf(
        "Tik om te beginnen", "Een grote sneeuwbal", "Nu de middelste", "En een hoofd!",
        "Ogen en knopen van kolen", "Een wortelneus", "Een hoed", "Een warme sjaal. Klaar!"
    )

    private fun save() {
        prefs.edit().putInt("snowman_stage", stage).putInt("snowman_variant", variant).apply()
    }

    override fun tap(x: Float, y: Float, now: Long): Boolean {
        if (stage < 7) { stage++; stageAt = now } else { variant = (variant + 1) % 4; stageAt = now }
        save()
        return true
    }

    override fun longPress(x: Float, y: Float, now: Long): Boolean {
        stage = 0; variant = 0; stageAt = now; save()
        return true
    }

    private fun pop(now: Long, forStage: Int): Float {
        if (forStage != stage || stage == 0) return 1f
        val a = ((now - stageAt) / 450f).coerceIn(0f, 1f)
        return 1f - (1f - a).pow(3) * cos(a * 6f)
    }

    override fun draw(c: Canvas, now: Long) {
        val t = now / 1000f
        sky(c, 0xFF0A1530.toInt(), 0xFF27406E.toInt(), 0.75f)
        stars(c, t, 0.4f)
        p.style = Paint.Style.FILL
        p.shader = LinearGradient(0f, h * 0.68f, 0f, h, 0xFFF4F8FF.toInt(), 0xFFC4D4F0.toInt(), Shader.TileMode.CLAMP)
        c.drawOval(-w * 0.3f, h * 0.68f, w * 1.3f, h * 1.3f, p)
        p.shader = null
        fir(c, w * 0.10f, h * 0.76f, h * 0.2f)
        fir(c, w * 0.90f, h * 0.78f, h * 0.24f)

        val cx = w / 2f
        val base = h * 0.84f
        val r1 = w * 0.17f
        val r2 = w * 0.125f
        val r3 = w * 0.09f

        // schaduw
        p.color = 0x22000000
        c.drawOval(cx - r1 * 1.2f, base - r1 * 0.12f, cx + r1 * 1.2f, base + r1 * 0.2f, p)

        fun ball(cy: Float, r: Float, k: Float) {
            if (k <= 0.01f) return
            p.color = 0xFFF8FBFF.toInt()
            c.drawCircle(cx, cy, r * k, p)
            p.color = 0x22000033
            c.drawCircle(cx + r * 0.15f * k, cy + r * 0.15f * k, r * 0.85f * k, p)
            p.color = 0xFFFFFFFF.toInt()
            c.drawCircle(cx - r * 0.2f * k, cy - r * 0.25f * k, r * 0.55f * k, p)
        }
        val y1 = base - r1
        val y2 = y1 - r1 * 0.85f - r2 * 0.3f
        val y3 = y2 - r2 * 0.85f - r3 * 0.35f

        if (stage == 0) {
            p.color = 0xFFEAF2FF.toInt()
            c.drawOval(cx - r1 * 0.6f, base - r1 * 0.3f, cx + r1 * 0.6f, base + r1 * 0.1f, p)
        }
        if (stage >= 1) ball(y1, r1, pop(now, 1))
        if (stage >= 2) ball(y2, r2, pop(now, 2))
        if (stage >= 3) ball(y3, r3, pop(now, 3))
        if (stage >= 4) {
            val k = pop(now, 4)
            p.color = 0xFF111111.toInt()
            c.drawCircle(cx - r3 * 0.35f, y3 - r3 * 0.15f, r3 * 0.12f * k, p)
            c.drawCircle(cx + r3 * 0.35f, y3 - r3 * 0.15f, r3 * 0.12f * k, p)
            for (i in 0 until 3) c.drawCircle(cx, y2 - r2 * 0.35f + i * r2 * 0.35f, r2 * 0.08f * k, p)
            for (i in 0 until 5) {
                val a = (-0.5f + i * 0.25f) * 1.2f
                c.drawCircle(cx + sin(a) * r3 * 0.5f, y3 + cos(a) * r3 * 0.42f, r3 * 0.05f * k, p)
            }
        }
        if (stage >= 5) {
            val k = pop(now, 5)
            p.color = 0xFFFF8F1F.toInt()
            path.reset()
            path.moveTo(cx - r3 * 0.12f, y3 + r3 * 0.02f)
            path.lineTo(cx + r3 * 0.12f, y3 + r3 * 0.02f)
            path.lineTo(cx + r3 * 0.85f * k, y3 + r3 * 0.12f)
            path.close()
            c.drawPath(path, p)
        }
        if (stage >= 7) {
            val k = pop(now, 7)
            p.color = scarfColors[variant]
            c.drawRoundRect(cx - r3 * 0.95f, y3 + r3 * 0.72f, cx + r3 * 0.95f, y3 + r3 * 1.12f * k, 14f, 14f, p)
            c.drawRoundRect(cx + r3 * 0.35f, y3 + r3 * 0.9f, cx + r3 * 0.75f, y3 + r3 * 2.1f * k, 12f, 12f, p)
        }
        if (stage >= 6) {
            val k = pop(now, 6)
            p.color = hatColors[variant]
            c.drawRoundRect(cx - r3 * 1.05f, y3 - r3 * 0.88f, cx + r3 * 1.05f, y3 - r3 * 0.62f, 10f, 10f, p)
            c.drawRoundRect(cx - r3 * 0.62f, y3 - r3 * (0.88f + 1.05f * k), cx + r3 * 0.62f, y3 - r3 * 0.8f, 10f, 10f, p)
            p.color = scarfColors[variant]
            c.drawRect(cx - r3 * 0.62f, y3 - r3 * 0.84f, cx + r3 * 0.62f, y3 - r3 * 0.70f, p)
        }
        // armen
        if (stage >= 4) {
            p.style = Paint.Style.STROKE
            p.strokeWidth = 3f * d
            p.strokeCap = Paint.Cap.ROUND
            p.color = 0xFF4A2D18.toInt()
            val wave = if (stage >= 7) sin(t * 3f) * r2 * 0.15f else 0f
            c.drawLine(cx - r2 * 0.85f, y2 - r2 * 0.1f, cx - r2 * 1.9f, y2 - r2 * 0.7f + wave, p)
            c.drawLine(cx + r2 * 0.85f, y2 - r2 * 0.1f, cx + r2 * 1.9f, y2 - r2 * 0.7f - wave, p)
            p.style = Paint.Style.FILL
        }

        snow(c, flakes, t, 210)
        text(c, "Sneeuwpop", cx, h * 0.09f, 26f, 0xFFFFE8B0.toInt(), true)
        text(c, labels[stage], cx, h * 0.135f, 15f, 0xFFB8C9BE.toInt())
        if (stage >= 7) text(c, "Tik voor andere kleuren", cx, h * 0.17f, 13f, 0xFF8FA597.toInt())
    }
}

// ───────────────────────────── 7. IJsbaan ─────────────────────────────

class IceRinkScene(ctx: Context, d: Float) : Scene(ctx, d) {
    override val name = "IJsbaan"
    override val hint = "Tik op het ijs voor een schaatser erbij. Tik op een schaatser en hij valt. Houd ingedrukt om ze weg te halen."

    private class Skater(var ang: Float, val speed: Float, val rx: Float, val color: Int, var dir: Int) {
        val tx = FloatArray(18)
        val ty = FloatArray(18)
        var n = 0
        var turnAt = -10_000L
        var fallAt = -1L
        var fx = 0f
        var fy = 0f
        var sx = 0f
        var sy = 0f
        var su = 1f
    }

    private val skaters = ArrayList<Skater>()
    private val colors = intArrayOf(0xFFD7263D.toInt(), 0xFF2E86DE.toInt(), 0xFFF4B400.toInt(), 0xFF1E9E57.toInt(), 0xFFFF4FD8.toInt(), 0xFFFF9500.toInt())
    private val flakes = Flakes(60, 13)
    private var last = 0L

    init {
        for (i in 0 until 4) skaters.add(Skater(i * 1.6f, 0.5f + i * 0.08f, 0.5f + i * 0.14f, colors[i], if (i % 2 == 0) 1 else -1))
    }

    private val rnd = Random(2)

    override fun tap(x: Float, y: Float, now: Long): Boolean {
        for (s in skaters) {
            if (s.fallAt < 0 && abs(x - s.sx) < s.su * 7f && y > s.sy - s.su * 15f && y < s.sy + s.su * 3f) {
                s.fallAt = now; s.fx = s.sx; s.fy = s.sy
                return true
            }
        }
        if (skaters.size >= 14) return true
        val cx = w / 2f
        val cy = h * 0.58f
        val a = kotlin.math.atan2((y - cy) / (h * 0.2f), (x - cx) / (w * 0.42f))
        val rr = min(1f, kotlin.math.sqrt(((x - cx) / (w * 0.42f)).pow(2) + ((y - cy) / (h * 0.2f)).pow(2)))
        skaters.add(Skater(a, 0.45f + rnd.nextFloat() * 0.4f, max(0.3f, rr), colors[rnd.nextInt(colors.size)], if (rnd.nextBoolean()) 1 else -1))
        return true
    }

    override fun longPress(x: Float, y: Float, now: Long): Boolean {
        while (skaters.size > 1) skaters.removeAt(skaters.size - 1)
        skaters.firstOrNull()?.fallAt = -1L
        return true
    }

    override fun draw(c: Canvas, now: Long) {
        val t = now / 1000f
        val dt = if (last == 0L) 0.016f else ((now - last) / 1000f).coerceIn(0.001f, 0.05f)
        last = now
        sky(c, 0xFF08122B.toInt(), 0xFF1F3C6E.toInt())
        stars(c, t, 0.3f)
        val cx = w / 2f
        val cy = h * 0.58f
        val rx = w * 0.42f
        val ry = h * 0.2f
        p.style = Paint.Style.FILL
        // ijs
        p.shader = LinearGradient(0f, cy - ry, 0f, cy + ry, 0xFFEAF8FF.toInt(), 0xFF8FC8EE.toInt(), Shader.TileMode.CLAMP)
        c.drawOval(cx - rx, cy - ry, cx + rx, cy + ry, p)
        p.shader = null
        p.style = Paint.Style.STROKE
        p.strokeWidth = 6f * d
        p.color = 0xFF5A3A22.toInt()
        c.drawOval(cx - rx - 3f * d, cy - ry - 3f * d, cx + rx + 3f * d, cy + ry + 3f * d, p)
        // krassen
        p.strokeWidth = 1f * d
        p.color = 0x55FFFFFF
        for (i in 0 until 6) {
            val a = i * 1.05f
            rect.set(cx - rx * (0.5f + 0.08f * i), cy - ry * (0.4f + 0.09f * i), cx + rx * (0.5f + 0.08f * i), cy + ry * (0.4f + 0.09f * i))
            c.drawArc(rect, a * 57f, 40f, false, p)
        }
        p.style = Paint.Style.FILL

        // slinger langs de baan
        for (i in 0 until 14) {
            val a = (i / 14f * 2f * PI).toFloat()
            val x = cx + cos(a) * (rx + 18f * d)
            val y = cy + sin(a) * (ry + 18f * d)
            val col = TreeColors.all[i % TreeColors.all.size]
            glow(c, x, y, 18f * d, col, (120 + 90 * sin(t * 2f + i)).toInt().coerceIn(0, 255))
            p.color = col
            c.drawCircle(x, y, 4f * d, p)
        }

        // schaatsers: eerst bewegen, dan botsingen vermijden door te draaien
        skaters.removeAll { it.fallAt >= 0 && now - it.fallAt > 2200 }
        val live = skaters.filter { it.fallAt < 0 }
        for (s in live) s.ang += s.speed * s.dir * dt
        for (i in live.indices) for (j in i + 1 until live.size) {
            val a = live[i]; val b = live[j]
            if (now - a.turnAt < 1500 || now - b.turnAt < 1500) continue
            val ax = cos(a.ang) * rx * a.rx * 0.9f; val ay = sin(a.ang) * ry * a.rx * 0.9f
            val bx = cos(b.ang) * rx * b.rx * 0.9f; val by = sin(b.ang) * ry * b.rx * 0.9f
            if (abs(ax - bx) < w * 0.07f && abs(ay - by) < h * 0.04f) {
                a.dir = -a.dir; b.dir = -b.dir
                a.turnAt = now; b.turnAt = now
            }
        }
        skaters.sortedBy { sin(it.ang) }.forEach { s ->
            var x = cx + cos(s.ang) * rx * s.rx * 0.9f
            var y = cy + sin(s.ang) * ry * s.rx * 0.9f
            val falling = s.fallAt >= 0
            val age = if (falling) (now - s.fallAt) / 1000f else 0f
            if (falling) {
                // glijdt op zijn buik van het ijs af
                val vx = (s.fx - cx) / rx
                val vy = (s.fy - cy) / ry
                val len = max(0.2f, kotlin.math.sqrt(vx * vx + vy * vy))
                val e = min(1f, age / 2.2f)
                x = s.fx + vx / len * rx * 0.7f * e * e
                y = s.fy + vy / len * ry * 0.7f * e * e
            } else {
                // spoor
                System.arraycopy(s.tx, 0, s.tx, 1, s.tx.size - 1)
                System.arraycopy(s.ty, 0, s.ty, 1, s.ty.size - 1)
                s.tx[0] = x; s.ty[0] = y
                s.n = min(s.tx.size, s.n + 1)
            }
            p.style = Paint.Style.STROKE
            p.strokeWidth = 1.6f * d
            for (k in 0 until s.n - 1) {
                p.color = Color.WHITE
                p.alpha = (140 * (1f - k / s.n.toFloat())).toInt()
                c.drawLine(s.tx[k], s.ty[k], s.tx[k + 1], s.ty[k + 1], p)
            }
            p.alpha = 255
            p.style = Paint.Style.FILL
            val sc = 0.8f + 0.5f * (sin(s.ang) * 0.5f + 0.5f)
            val u = w * 0.012f * sc
            s.sx = x; s.sy = y; s.su = u
            val lean = if (falling) -s.dir * 90f * min(1f, age / 0.35f) else s.dir * 4f
            c.save()
            c.translate(x, y)
            c.rotate(lean)
            p.color = 0x33000000
            c.drawOval(-5 * u, -0.5f * u, 5 * u, 1.5f * u, p)
            p.color = s.color
            c.drawRoundRect(-2.4f * u, -9 * u, 2.4f * u, -2 * u, 6f, 6f, p)       // jas
            p.color = 0xFF222831.toInt()
            c.drawRect(-2 * u, -2.5f * u, -0.3f * u, 0f, p)                        // benen
            c.drawRect(0.3f * u, -2.5f * u, 2 * u, 0f, p)
            p.color = 0xFFFFD6B0.toInt()
            c.drawCircle(0f, -11 * u, 1.8f * u, p)                                 // hoofd
            p.color = s.color
            c.drawRoundRect(-2 * u, -12.8f * u, 2 * u, -11.8f * u, 4f, 4f, p)      // muts
            c.drawRect(-2.6f * u, -9.2f * u, 2.6f * u, -8.2f * u, p)               // sjaal
            c.restore()
        }
        snow(c, flakes, t, 200)
        text(c, "IJsbaan", cx, h * 0.09f, 26f, 0xFFE8F4FF.toInt(), true)
    }
}

// ───────────────────────────── 8. Kerststal met wensen ─────────────────────────────

class NativityScene(ctx: Context, d: Float) : Scene(ctx, d) {
    override val name = "Kerststal"
    override val hint = "Tik op de sterrenhemel om een wens te doen."

    private class Wish(val x: Float, val y: Float, val born: Long)

    private val wishes = ArrayList<Wish>()
    private var count = prefs.getInt("wish_count", 0)
    private val flakes = Flakes(0, 1)

    override fun tap(x: Float, y: Float, now: Long): Boolean {
        if (y < h * 0.55f) {
            wishes.add(Wish(x, y, now))
            count++
            prefs.edit().putInt("wish_count", count).apply()
        }
        return true
    }

    override fun draw(c: Canvas, now: Long) {
        val t = now / 1000f
        sky(c, 0xFF050722.toInt(), 0xFF2A2E6B.toInt(), 0.75f)
        stars(c, t, 0.55f)

        // grote ster van Bethlehem
        val sx = w * 0.60f
        val sy = h * 0.14f
        val pulse = 0.85f + 0.15f * sin(t * 2f)
        glow(c, sx, sy, w * 0.28f * pulse, 0xFFFFE8A0.toInt(), 150)
        p.style = Paint.Style.FILL
        p.color = 0xFFFFF3B0.toInt()
        path.reset()
        for (n in 0 until 8) {
            val r = if (n % 2 == 0) w * 0.1f * pulse else w * 0.025f
            val a = (-PI / 2 + n * PI / 4).toFloat()
            val x = sx + r * cos(a)
            val y = sy + r * sin(a)
            if (n == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        c.drawPath(path, p)
        // lichtstraal naar de stal
        p.shader = LinearGradient(sx, sy, w * 0.4f, h * 0.62f, 0x55FFF3B0, 0x00FFF3B0, Shader.TileMode.CLAMP)
        path.reset()
        path.moveTo(sx - 6f * d, sy + 10f * d); path.lineTo(sx + 6f * d, sy + 10f * d)
        path.lineTo(w * 0.48f, h * 0.62f); path.lineTo(w * 0.32f, h * 0.62f); path.close()
        c.drawPath(path, p)
        p.shader = null

        // heuvels
        p.color = 0xFF121835.toInt()
        c.drawOval(-w * 0.4f, h * 0.62f, w * 0.9f, h * 1.1f, p)
        p.color = 0xFF0D1229.toInt()
        c.drawOval(w * 0.2f, h * 0.66f, w * 1.5f, h * 1.15f, p)

        // stal
        val bx = w * 0.40f
        val by = h * 0.84f
        val bw = w * 0.62f
        val bh = h * 0.235f
        glow(c, bx, by - bh * 0.4f, bw * 0.75f, 0xFFFFB14A.toInt(), 140)
        // achterwand met warm licht
        p.style = Paint.Style.FILL
        p.color = 0xFF3A2616.toInt()
        c.drawRect(bx - bw / 2, by - bh, bx + bw / 2, by, p)
        p.color = 0xFFFFC66E.toInt()
        p.alpha = 190
        c.drawRect(bx - bw / 2 + 10f * d, by - bh * 0.96f, bx + bw / 2 - 10f * d, by, p)
        p.alpha = 255
        // balken
        p.color = 0xFF5A3A22.toInt()
        c.drawRect(bx - bw / 2, by - bh * 1.02f, bx - bw / 2 + 10f * d, by, p)
        c.drawRect(bx + bw / 2 - 10f * d, by - bh * 1.02f, bx + bw / 2, by, p)

        // dieren achterin: os en ezel
        fun animal(x: Float, s: Float, body: Int, ears: Boolean, face: Int) {
            p.style = Paint.Style.FILL
            p.color = body
            c.drawOval(x - s, by - s * 1.15f, x + s, by - s * 0.35f, p)
            val hx = x + face * s * 1.05f
            c.drawOval(hx - s * 0.38f, by - s * 1.5f, hx + s * 0.38f, by - s * 0.7f, p)
            c.drawRect(x - s * 0.7f, by - s * 0.5f, x - s * 0.5f, by, p)
            c.drawRect(x + s * 0.5f, by - s * 0.5f, x + s * 0.7f, by, p)
            p.style = Paint.Style.STROKE
            p.strokeWidth = 2f * d
            p.color = if (ears) body else 0xFFEFE6D2.toInt()
            if (ears) {
                c.drawLine(hx - s * 0.1f, by - s * 1.45f, hx - s * 0.2f, by - s * 2.0f, p)
                c.drawLine(hx + s * 0.15f, by - s * 1.45f, hx + s * 0.3f, by - s * 1.98f, p)
            } else {
                c.drawLine(hx - s * 0.3f, by - s * 1.4f, hx - s * 0.5f, by - s * 1.75f, p)
                c.drawLine(hx + s * 0.3f, by - s * 1.4f, hx + s * 0.5f, by - s * 1.75f, p)
            }
            p.style = Paint.Style.FILL
            p.color = 0xFF111111.toInt()
            c.drawCircle(hx + face * s * 0.15f, by - s * 1.2f, s * 0.05f, p)
        }
        animal(bx - bw * 0.39f, bw * 0.07f, 0xFF6B4A32.toInt(), false, 1)
        animal(bx + bw * 0.39f, bw * 0.06f, 0xFF8E8E96.toInt(), true, -1)

        // mensen
        fun figure(x: Float, hgt: Float, robe: Int, hood: Int, lean: Float, spread: Float = 0.38f, halo: Boolean = true) {
            p.style = Paint.Style.FILL
            // schaduw
            p.color = 0x33000000
            c.drawOval(x - hgt * spread, by - hgt * 0.04f, x + hgt * spread, by + hgt * 0.04f, p)
            p.color = robe
            path.reset()
            path.moveTo(x - hgt * 0.15f, by - hgt * 0.78f)
            path.lineTo(x + hgt * 0.15f, by - hgt * 0.78f)
            path.quadTo(x + hgt * spread * 0.8f, by - hgt * 0.4f, x + hgt * spread, by)
            path.lineTo(x - hgt * spread, by)
            path.quadTo(x - hgt * spread * 0.8f, by - hgt * 0.4f, x - hgt * 0.15f, by - hgt * 0.78f)
            path.close()
            c.drawPath(path, p)
            c.drawCircle(x, by - hgt * 0.76f, hgt * 0.17f, p)
            // plooien
            p.color = 0x22000000
            c.drawRect(x - hgt * 0.01f, by - hgt * 0.6f, x + hgt * 0.01f, by, p)
            if (halo) glow(c, x, by - hgt * 0.92f, hgt * 0.34f, 0xFFFFE8A0.toInt(), 130)
            p.color = hood
            c.drawCircle(x, by - hgt * 0.91f, hgt * 0.16f, p)
            val fx = x + lean * hgt * 0.035f
            p.color = 0xFFFFD6B0.toInt()
            c.drawCircle(fx, by - hgt * 0.89f, hgt * 0.108f, p)
            // gezicht
            p.color = 0xFF3A2218.toInt()
            c.drawCircle(fx - hgt * 0.035f, by - hgt * 0.9f, hgt * 0.013f, p)
            c.drawCircle(fx + hgt * 0.035f, by - hgt * 0.9f, hgt * 0.013f, p)
            p.color = 0x55FF8A8A
            c.drawCircle(fx - hgt * 0.06f, by - hgt * 0.86f, hgt * 0.018f, p)
            c.drawCircle(fx + hgt * 0.06f, by - hgt * 0.86f, hgt * 0.018f, p)
            p.style = Paint.Style.STROKE
            p.strokeWidth = 1.3f * d
            p.color = 0xFF8A4A3A.toInt()
            c.drawArc(fx - hgt * 0.03f, by - hgt * 0.875f, fx + hgt * 0.03f, by - hgt * 0.84f, 20f, 140f, false, p)
            p.style = Paint.Style.FILL
            // armen en handen
            p.color = robe
            c.drawOval(x + lean * hgt * 0.05f - hgt * 0.16f, by - hgt * 0.66f, x + lean * hgt * 0.05f + hgt * 0.16f, by - hgt * 0.42f, p)
            p.color = 0xFFFFD6B0.toInt()
            c.drawCircle(x + lean * hgt * 0.17f, by - hgt * 0.52f, hgt * 0.05f, p)
        }
        // Maria (blauw, sluier, gebogen naar het kind), Jozef (staf, baard, lantaarn)
        figure(bx - bw * 0.15f, bh * 0.72f, 0xFF2A4C9C.toInt(), 0xFFEFEFF8.toInt(), 1f, 0.42f)
        val jx = bx + bw * 0.17f
        val jh = bh * 0.82f
        figure(jx, jh, 0xFF8A5A2B.toInt(), 0xFF5A3A22.toInt(), -1f, 0.34f)
        p.color = 0xFFE4DCCB.toInt()
        c.drawOval(jx - jh * 0.09f, by - jh * 0.87f, jx + jh * 0.09f, by - jh * 0.73f, p)
        p.color = 0xFFFFD6B0.toInt()
        c.drawCircle(jx - jh * 0.035f, by - jh * 0.91f, jh * 0.012f, p)
        c.drawCircle(jx + jh * 0.035f, by - jh * 0.91f, jh * 0.012f, p)
        p.style = Paint.Style.STROKE
        p.strokeWidth = 3f * d
        p.color = 0xFF6B4423.toInt()
        c.drawLine(jx + jh * 0.46f, by, jx + jh * 0.46f, by - jh * 1.05f, p)
        c.drawArc(jx + jh * 0.36f, by - jh * 1.12f, jx + jh * 0.56f, by - jh * 0.96f, 180f, 200f, false, p)
        c.drawLine(jx - jh * 0.34f, by - jh * 0.5f, jx - jh * 0.34f, by - jh * 0.38f, p)
        p.style = Paint.Style.FILL
        glow(c, jx - jh * 0.34f, by - jh * 0.3f, jh * 0.3f, 0xFFFFD27A.toInt(), 210)
        p.color = 0xFF3A2218.toInt()
        c.drawRect(jx - jh * 0.38f, by - jh * 0.38f, jx - jh * 0.3f, by - jh * 0.22f, p)
        p.color = 0xFFFFE8A0.toInt()
        c.drawRect(jx - jh * 0.365f, by - jh * 0.36f, jx - jh * 0.315f, by - jh * 0.24f, p)

        // de drie koningen met geschenken, rechts van de stal
        val kingH = bh * 0.7f
        val kings = intArrayOf(0xFF7B2D8E.toInt(), 0xFFB71C1C.toInt(), 0xFF1B7A4A.toInt())
        for (k in 0 until 3) {
            val kx = w * (0.795f + k * 0.075f)
            val bob = sin(t * 1.2f + k) * 1.5f * d
            figure(kx, kingH * (1f - k * 0.04f), kings[k], 0xFFF2C94C.toInt(), -1f, 0.22f, false)
            val kh = kingH * (1f - k * 0.04f)
            // kroon
            p.color = 0xFFF2C94C.toInt()
            path.reset()
            path.moveTo(kx - kh * 0.13f, by - kh * 1.0f)
            path.lineTo(kx - kh * 0.13f, by - kh * 1.12f)
            path.lineTo(kx - kh * 0.06f, by - kh * 1.07f)
            path.lineTo(kx, by - kh * 1.14f)
            path.lineTo(kx + kh * 0.06f, by - kh * 1.07f)
            path.lineTo(kx + kh * 0.13f, by - kh * 1.12f)
            path.lineTo(kx + kh * 0.13f, by - kh * 1.0f)
            path.close()
            c.drawPath(path, p)
            // geschenk in de handen
            val gy = by - kh * 0.5f + bob
            val gx = kx - kh * 0.2f
            when (k) {
                0 -> { p.color = 0xFFD9A441.toInt(); c.drawRect(gx - kh * 0.1f, gy - kh * 0.12f, gx + kh * 0.1f, gy + kh * 0.06f, p) }
                1 -> { p.color = 0xFFF4B400.toInt(); c.drawOval(gx - kh * 0.08f, gy - kh * 0.14f, gx + kh * 0.08f, gy + kh * 0.06f, p) }
                else -> { p.color = 0xFF8E24AA.toInt(); c.drawRect(gx - kh * 0.09f, gy - kh * 0.1f, gx + kh * 0.09f, gy + kh * 0.07f, p)
                    p.color = 0xFFFFE8B0.toInt(); c.drawRect(gx - kh * 0.01f, gy - kh * 0.1f, gx + kh * 0.01f, gy + kh * 0.07f, p) }
            }
            glow(c, gx, gy - kh * 0.03f, kh * 0.2f, 0xFFFFE8A0.toInt(), 90)
        }

        // kribbe met stro en kindje
        p.color = 0xFF8A5A2B.toInt()
        path.reset()
        path.moveTo(bx - bw * 0.1f, by - bh * 0.24f)
        path.lineTo(bx + bw * 0.1f, by - bh * 0.24f)
        path.lineTo(bx + bw * 0.075f, by - bh * 0.1f)
        path.lineTo(bx - bw * 0.075f, by - bh * 0.1f)
        path.close()
        c.drawPath(path, p)
        p.style = Paint.Style.STROKE
        p.strokeWidth = 3f * d
        c.drawLine(bx - bw * 0.075f, by - bh * 0.1f, bx - bw * 0.1f, by, p)
        c.drawLine(bx + bw * 0.075f, by - bh * 0.1f, bx + bw * 0.1f, by, p)
        p.style = Paint.Style.FILL
        p.color = 0xFFE8C45A.toInt()
        c.drawOval(bx - bw * 0.105f, by - bh * 0.3f, bx + bw * 0.105f, by - bh * 0.2f, p)
        glow(c, bx, by - bh * 0.32f, bw * 0.2f, 0xFFFFFFFF.toInt(), 170)
        p.color = 0xFFFFF3E0.toInt()
        c.drawOval(bx - bw * 0.06f, by - bh * 0.32f, bx + bw * 0.06f, by - bh * 0.22f, p)
        p.color = 0xFFFFD6B0.toInt()
        c.drawCircle(bx - bw * 0.055f, by - bh * 0.3f, bw * 0.03f, p)

        // dak van stro
        p.color = 0xFF9A7B3A.toInt()
        path.reset()
        path.moveTo(bx - bw * 0.6f, by - bh * 0.98f)
        path.lineTo(bx, by - bh * 1.75f)
        path.lineTo(bx + bw * 0.6f, by - bh * 0.98f)
        path.close()
        c.drawPath(path, p)
        p.style = Paint.Style.STROKE
        p.strokeWidth = 1.5f * d
        p.color = 0xFF6B5320.toInt()
        for (k in 1..5) {
            val yy = by - bh * (0.98f + k * 0.13f)
            val half = bw * 0.6f * (1f - k * 0.17f)
            c.drawLine(bx - half, yy + bh * 0.02f, bx + half, yy + bh * 0.02f, p)
        }
        p.style = Paint.Style.FILL
        p.color = 0xFFEAF2FF.toInt()
        c.drawRect(bx - bw * 0.55f, by - bh * 1.04f, bx + bw * 0.55f, by - bh * 0.98f, p)

        // schaapjes buiten de stal
        fun sheep(x: Float, s: Float) {
            p.color = 0xFF222222.toInt()
            c.drawRect(x - s * 0.5f, by - s * 0.5f, x - s * 0.35f, by, p)
            c.drawRect(x + s * 0.35f, by - s * 0.5f, x + s * 0.5f, by, p)
            p.color = 0xFFF2F2F2.toInt()
            c.drawCircle(x - s * 0.4f, by - s * 0.8f, s * 0.45f, p)
            c.drawCircle(x, by - s * 0.9f, s * 0.5f, p)
            c.drawCircle(x + s * 0.4f, by - s * 0.8f, s * 0.45f, p)
            p.color = 0xFF2B2B2B.toInt()
            c.drawCircle(x + s * 0.75f, by - s * 0.85f, s * 0.28f, p)
        }

        // engel boven de stal
        val ax = bx - bw * 0.1f
        val ay = by - bh * 2.2f + sin(t * 1.5f) * 4f * d
        glow(c, ax, ay, bw * 0.12f, 0xFFFFF3B0.toInt(), 130)
        p.color = 0xFFFFFFFF.toInt()
        c.drawOval(ax - bw * 0.07f, ay - bw * 0.04f, ax - bw * 0.005f, ay + bw * 0.04f, p)
        c.drawOval(ax + bw * 0.005f, ay - bw * 0.04f, ax + bw * 0.07f, ay + bw * 0.04f, p)
        path.reset()
        path.moveTo(ax, ay - bw * 0.02f)
        path.lineTo(ax + bw * 0.035f, ay + bw * 0.09f)
        path.lineTo(ax - bw * 0.035f, ay + bw * 0.09f)
        path.close()
        c.drawPath(path, p)
        p.color = 0xFFFFD6B0.toInt()
        c.drawCircle(ax, ay - bw * 0.04f, bw * 0.022f, p)

        // wensen
        val it = wishes.iterator()
        while (it.hasNext()) {
            val wsh = it.next()
            val a = (now - wsh.born) / 2500f
            if (a > 1f) { it.remove(); continue }
            val rr = (6f + a * 34f) * d
            glow(c, wsh.x, wsh.y - a * 40f * d, rr * 2.2f, 0xFFFFE8A0.toInt(), (200 * (1f - a)).toInt())
            p.color = 0xFFFFFFFF.toInt()
            p.alpha = (255 * (1f - a)).toInt()
            c.drawCircle(wsh.x, wsh.y - a * 40f * d, 4f * d * (1f - a * 0.5f), p)
            p.alpha = 255
        }
        text(c, "Kerststal", w * 0.28f, h * 0.075f, 26f, 0xFFFFE8B0.toInt(), true)
        text(c, "Wensen gedaan: $count", w / 2, h * 0.975f, 13f, 0xFFB8C9BE.toInt())
    }
}

// ───────────────────────────── 9. Cadeautjes met berichtjes ─────────────────────────────

class GiftsScene(ctx: Context, d: Float) : Scene(ctx, d) {
    override val name = "Cadeautjes"
    override val hint = "Tik op een cadeautje om het te openen. Houd ingedrukt om je eigen bericht en datum in te stellen."

    /** Wordt door de app gezet om een bericht te laten bewerken (in de wallpaper is dit null). */
    var editor: ((index: Int, message: String, unlock: Int, done: (String, Int) -> Unit) -> Unit)? = null

    private val unlockOptions = intArrayOf(0, 1205, 1224, 1225, 1231, 101)   // 0 = altijd, anders MMDD
    private val messages = arrayOf(
        "Dit cadeautje mag altijd open. Fijne feestdagen! 🎁",
        "Pakjesavond! Veel plezier met je schoencadeautje 🥾",
        "Kerstavond: een warme knuffel van mij voor jou ❤️",
        "Vrolijk kerstfeest! Je krijgt een etentje van mij 🍽️",
        "Een glas bubbels op het nieuwe jaar! 🥂",
        "Gelukkig nieuwjaar! Op een jaar vol mooie dingen ✨"
    )
    private val unlock = intArrayOf(0, 1205, 1224, 1225, 1231, 101)
    private val opened = BooleanArray(6)
    private var showing = -1
    private var showAt = 0L
    private var lockedIdx = -1
    private var lockedAt = -10_000L
    private val colors = intArrayOf(0xFFC62828.toInt(), 0xFF1565C0.toInt(), 0xFF2E7D32.toInt(), 0xFFF9A825.toInt(), 0xFF8E24AA.toInt(), 0xFF00838F.toInt())

    init {
        for (i in 0 until 6) {
            messages[i] = prefs.getString("gift_msg_$i", messages[i])!!
            unlock[i] = prefs.getInt("gift_unlock_$i", unlock[i])
            opened[i] = prefs.getBoolean("gift_open_$i", false)
        }
    }

    private fun giftRect(i: Int): RectF {
        val col = i % 3
        val row = i / 3
        val gw = w * 0.22f
        val gap = (w - 3 * gw) / 4f
        val x = gap + col * (gw + gap)
        val y = h * 0.60f + row * h * 0.17f
        return RectF(x, y, x + gw, y + gw * 0.8f)
    }

    private fun unlocked(i: Int): Boolean {
        val u = unlock[i]
        if (u == 0) return true
        val (m, dd) = today()
        val now = m * 100 + dd
        // "1 januari" (101) geldt ook in het nieuwe jaar vóór de zomer
        return if (u < 600) now in u..599 else now >= u
    }

    private fun label(u: Int): String = if (u == 0) "altijd" else "${u % 100} ${arrayOf("", "jan", "feb", "mrt", "apr", "mei", "jun", "jul", "aug", "sep", "okt", "nov", "dec")[u / 100]}"

    override fun tap(x: Float, y: Float, now: Long): Boolean {
        if (showing >= 0) { showing = -1; return true }
        for (i in 0 until 6) {
            if (giftRect(i).contains(x, y)) {
                if (unlocked(i)) {
                    opened[i] = true
                    prefs.edit().putBoolean("gift_open_$i", true).apply()
                    showing = i; showAt = now
                } else {
                    lockedIdx = i; lockedAt = now
                }
                return true
            }
        }
        return false
    }

    override fun longPress(x: Float, y: Float, now: Long): Boolean {
        for (i in 0 until 6) {
            if (giftRect(i).contains(x, y)) {
                val e = editor ?: return true
                e(i, messages[i], unlock[i]) { msg, u ->
                    messages[i] = msg
                    unlock[i] = u
                    prefs.edit().putString("gift_msg_$i", msg).putInt("gift_unlock_$i", u).putBoolean("gift_open_$i", false).apply()
                    opened[i] = false
                }
                return true
            }
        }
        return false
    }

    fun unlockChoices(): IntArray = unlockOptions
    fun unlockLabel(u: Int) = label(u)

    override fun draw(c: Canvas, now: Long) {
        val t = now / 1000f
        sky(c, 0xFF14090F.toInt(), 0xFF2B1A14.toInt())
        // boom (klein) met lampjes
        val tx = w / 2f
        val base = h * 0.54f
        fir(c, tx, base, h * 0.40f, 0xFF1E6B3A.toInt())
        for (i in 0 until 14) {
            val a = i * 2.4f
            val x = tx + sin(a) * h * 0.09f * (0.3f + (i % 5) * 0.2f)
            val y = base - h * 0.06f - (i / 14f) * h * 0.28f
            val col = TreeColors.all[i % TreeColors.all.size]
            glow(c, x, y, 16f * d, col, (120 + 100 * sin(t * 2.2f + i)).toInt().coerceIn(0, 255))
            p.style = Paint.Style.FILL
            p.color = col
            c.drawCircle(x, y, 3.5f * d, p)
        }
        p.color = 0xFFFFD54F.toInt()
        c.drawCircle(tx, base - h * 0.40f, 8f * d, p)
        glow(c, tx, base - h * 0.40f, 40f * d, 0xFFFFE8A0.toInt(), 120)

        // vloer
        p.color = 0xFF3A2218.toInt()
        c.drawRect(0f, h * 0.56f, w, h, p)

        // cadeautjes
        for (i in 0 until 6) {
            val r = giftRect(i)
            val ok = unlocked(i)
            val col = colors[i]
            var dy = 0f
            if (lockedIdx == i && now - lockedAt < 600) dy = sin((now - lockedAt) / 40f) * 4f * d
            p.style = Paint.Style.FILL
            p.color = col
            c.drawRoundRect(r.left, r.top + dy, r.right, r.bottom + dy, 10f, 10f, p)
            p.color = 0x33FFFFFF
            c.drawRect(r.left, r.top + dy, r.right, r.top + r.height() * 0.22f + dy, p)
            p.color = 0xFFFFE8B0.toInt()
            c.drawRect(r.centerX() - 5f * d, r.top + dy, r.centerX() + 5f * d, r.bottom + dy, p)
            c.drawRect(r.left, r.centerY() - 5f * d + dy, r.right, r.centerY() + 5f * d + dy, p)
            c.drawCircle(r.centerX() - 8f * d, r.top + dy - 4f * d, 7f * d, p)
            c.drawCircle(r.centerX() + 8f * d, r.top + dy - 4f * d, 7f * d, p)
            // status
            if (!ok) {
                p.color = 0x66000000
                c.drawRoundRect(r.left, r.top + dy, r.right, r.bottom + dy, 10f, 10f, p)
                text(c, "🔒", r.centerX(), r.centerY() + 8f * d, 22f)
            } else if (opened[i]) {
                text(c, "✓", r.right - 12f * d, r.top + 18f * d, 18f, Color.WHITE, true)
            }
            text(c, label(unlock[i]), r.centerX(), r.bottom + 16f * d, 11f, 0xFFB8C9BE.toInt())
        }

        text(c, "Cadeautjes", w / 2, h * 0.07f, 26f, 0xFFFFE8B0.toInt(), true)
        if (lockedIdx >= 0 && now - lockedAt < 2200) {
            text(c, "Nog niet! Te openen op ${label(unlock[lockedIdx])}", w / 2, h * 0.98f, 14f, 0xFFFFB4B4.toInt(), true)
        }

        // bericht
        if (showing >= 0) {
            val a = min(1f, (now - showAt) / 300f)
            p.color = ((200 * a).toInt() shl 24)
            c.drawRect(0f, 0f, w, h, p)
            p.color = 0xFFFFF8E8.toInt()
            p.alpha = (255 * a).toInt()
            c.drawRoundRect(w * 0.08f, h * 0.3f, w * 0.92f, h * 0.55f, 28f, 28f, p)
            p.alpha = 255
            text(c, "🎁", w / 2, h * 0.37f, 32f)
            // lange berichten over twee regels verdelen
            val msg = messages[showing]
            val words = msg.split(' ')
            val lines = ArrayList<String>()
            var cur = ""
            for (wd in words) {
                if ((cur + " " + wd).length > 24 && cur.isNotEmpty()) { lines.add(cur); cur = wd } else cur = if (cur.isEmpty()) wd else "$cur $wd"
            }
            if (cur.isNotEmpty()) lines.add(cur)
            for ((k, ln) in lines.take(4).withIndex()) {
                text(c, ln, w / 2, h * 0.43f + k * 22f * d, 17f, 0xFF3A2218.toInt(), true)
            }
            text(c, "Tik om te sluiten", w / 2, h * 0.53f, 11f, 0xFF8A7A6A.toInt())
        }
    }
}

/** Beheert alle scènes en geeft de TreeView/wallpaper één plek om mee te praten. */
class SceneHost(ctx: Context) {
    private val d = ctx.resources.displayMetrics.density
    val scenes: List<Scene> = listOf(
        SnowGlobeScene(ctx, d), AdventCalendarScene(ctx, d), AdventWreathScene(ctx, d),
        VillageScene(ctx, d), SleighScene(ctx, d), SnowmanScene(ctx, d),
        IceRinkScene(ctx, d), NativityScene(ctx, d), GiftsScene(ctx, d)
    )
    val gifts get() = scenes.last() as GiftsScene

    fun resize(w: Float, h: Float) = scenes.forEach { it.resize(w, h) }
    fun scene(i: Int) = scenes[i.coerceIn(0, scenes.size - 1)]

    companion object {
        val names = arrayOf(
            "Sneeuwbol", "Adventskalender", "Adventskrans", "Kerstdorp met trein", "Slee van de kerstman",
            "Sneeuwpop bouwen", "IJsbaan", "Kerststal", "Cadeautjes"
        )
        const val COUNT = 9
    }
}
