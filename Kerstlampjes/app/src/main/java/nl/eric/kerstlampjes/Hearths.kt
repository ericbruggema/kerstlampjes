package nl.eric.kerstlampjes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/** Een haard of vuurplek: kind bepaalt de tekenroutine. */
class HearthStyle(val name: String, val kind: Int, val bgTop: Int, val bgBottom: Int)

object HearthStyles {
    const val CARTOON = 0
    const val BRICK_REAL = 1
    const val STONE_3D = 2
    const val STOVE = 3
    const val CAMPFIRE = 4
    const val BLUE = 5
    const val MODERN = 6
    const val PHOTO = 10        // 10..13 = foto-haarden (zie Credits.photos)

    val all = arrayOf(
        HearthStyle("Cartoon haard", CARTOON, 0xFF22130E.toInt(), 0xFF0D0605.toInt()),
        HearthStyle("Echt vuur", BRICK_REAL, 0xFF22130E.toInt(), 0xFF0D0605.toInt()),
        HearthStyle("Steenhaard 3D", STONE_3D, 0xFF1E1A1A.toInt(), 0xFF0C0A0A.toInt()),
        HearthStyle("Houtkachel", STOVE, 0xFF2A1A10.toInt(), 0xFF120A06.toInt()),
        HearthStyle("Kampvuur", CAMPFIRE, 0xFF040A20.toInt(), 0xFF1B2A4A.toInt()),
        HearthStyle("Blauw vuur", BLUE, 0xFF0A1220.toInt(), 0xFF05080F.toInt()),
        HearthStyle("Moderne haard", MODERN, 0xFF23252B.toInt(), 0xFF0E0F13.toInt()),
        HearthStyle("Foto: Houtvuur", PHOTO, 0xFF0A0605.toInt(), 0xFF0A0605.toInt()),
        HearthStyle("Foto: Kerstkamer", PHOTO + 1, 0xFF0A0605.toInt(), 0xFF0A0605.toInt()),
        HearthStyle("Foto: Stenen haard", PHOTO + 2, 0xFF0A0605.toInt(), 0xFF0A0605.toInt()),
        HearthStyle("Foto: Knetterend vuur", PHOTO + 3, 0xFF0A0605.toInt(), 0xFF0A0605.toInt())
    )
}

/**
 * Deeltjesvuur: honderden zachte, gloeiende deeltjes die additief worden gemengd.
 * Ze stijgen, krimpen en lopen van wit-geel via oranje naar donkerrood.
 */
class FireSim(private val count: Int = 240) {
    private val x = FloatArray(count)
    private val y = FloatArray(count)
    private val vx = FloatArray(count)
    private val vy = FloatArray(count)
    private val age = FloatArray(count)
    private val life = FloatArray(count) { 1f }
    private val size = FloatArray(count)
    private val spark = BooleanArray(count)
    private val rnd = Random(5)
    private var started = false
    private var last = 0L

    private var paletteKey: IntArray = IntArray(0)
    private var sprites: Array<Bitmap> = emptyArray()
    private val paint = Paint(Paint.FILTER_BITMAP_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
    }
    private val dst = RectF()

    private fun ensureSprites(p: IntArray) {
        if (p.contentEquals(paletteKey)) return
        paletteKey = p.copyOf()
        sprites = Array(p.size) { i ->
            val s = 64
            val bmp = Bitmap.createBitmap(s, s, Bitmap.Config.ARGB_8888)
            val cv = Canvas(bmp)
            val col = p[i]
            val a = Paint(Paint.ANTI_ALIAS_FLAG)
            a.shader = RadialGradient(
                s / 2f, s / 2f, s / 2f,
                intArrayOf(col, (col and 0x00FFFFFF) or (0x80 shl 24), col and 0x00FFFFFF),
                floatArrayOf(0f, 0.45f, 1f), Shader.TileMode.CLAMP
            )
            cv.drawCircle(s / 2f, s / 2f, s / 2f, a)
            bmp
        }
    }

    private fun respawn(i: Int, srcX: Float, srcY: Float, halfW: Float, height: Float, sizePx: Float, initial: Boolean) {
        val sp = rnd.nextFloat() < 0.07f
        spark[i] = sp
        life[i] = (0.7f + rnd.nextFloat() * 0.7f) * if (sp) 1.5f else 1f
        x[i] = srcX + (rnd.nextFloat() * 2f - 1f) * halfW * (0.3f + 0.7f * rnd.nextFloat())
        y[i] = srcY - rnd.nextFloat() * height * 0.03f
        vy[i] = -height / life[i] * (0.8f + rnd.nextFloat() * 0.5f) * if (sp) 1.5f else 1f
        vx[i] = (rnd.nextFloat() - 0.5f) * halfW * 0.8f
        size[i] = sizePx * (0.6f + rnd.nextFloat() * 0.8f) * if (sp) 0.18f else 1f
        age[i] = if (initial) rnd.nextFloat() else 0f
    }

    fun draw(
        c: Canvas, nowMs: Long, srcX: Float, srcY: Float, halfW: Float, height: Float,
        sizePx: Float, intensity: Float, speed: Float, palette: IntArray
    ) {
        ensureSprites(palette)
        var dt = if (last == 0L) 0.016f else ((nowMs - last) / 1000f).coerceIn(0.001f, 0.05f)
        last = nowMs
        dt *= speed
        val t = nowMs / 1000f
        val n = sprites.size
        for (i in 0 until count) {
            if (!started || age[i] >= 1f) {
                respawn(i, srcX, srcY, halfW, height, sizePx, !started)
            }
            age[i] += dt / life[i]
            val a = age[i].coerceIn(0f, 1f)
            y[i] += vy[i] * dt
            x[i] += (vx[i] * 0.3f + (srcX - x[i]) * 1.1f * a + sin(t * 6f + i * 1.3f) * halfW * 0.5f * a) * dt

            val idx = if (spark[i]) 0 else min(n - 1, (a * n).toInt())
            val fade = (1f - a).pow(1.2f) * min(1f, a * 8f)
            val alpha = (255f * intensity * if (spark[i]) 0.9f else 0.22f * fade).toInt().coerceIn(0, 255)
            val s = size[i] * (0.5f + 0.5f * (1f - a)) * 0.5f
            dst.set(x[i] - s * 0.8f, y[i] - s * 1.5f, x[i] + s * 0.8f, y[i] + s * 0.5f)
            paint.alpha = alpha
            c.drawBitmap(sprites[idx], null, dst, paint)
        }
        started = true
    }
}

/** Tekent de nieuwe haarden (alle varianten behalve de cartoon-haard). */
class HearthPainter(private val density: Float, private val ctx: Context? = null) {

    private val fire = FireSim(240)
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val rect = RectF()

    private val unitGlow = RadialGradient(
        0f, 0f, 1f, intArrayOf(0xFFFFFFFF.toInt(), 0x00FFFFFF), null, Shader.TileMode.CLAMP
    )
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = unitGlow }
    private val filters = HashMap<Int, ColorFilter>()

    private val fireYoung = intArrayOf(
        0xFFFFFFE6.toInt(), 0xFFFFE066.toInt(), 0xFFFFA126.toInt(),
        0xFFE8541A.toInt(), 0xFFB02A12.toInt(), 0xFF4A1A10.toInt()
    )
    private val fireBlue = intArrayOf(
        0xFFE6FFFF.toInt(), 0xFF8EF0FF.toInt(), 0xFF29B6F6.toInt(),
        0xFF1565C0.toInt(), 0xFF4A2D9C.toInt(), 0xFF1A1050.toInt()
    )

    private var starsFor = 0
    private var stars = FloatArray(0)

    private fun glow(c: Canvas, cx: Float, cy: Float, r: Float, color: Int, alpha: Int) {
        glowPaint.colorFilter = filters.getOrPut(color) { PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN) }
        glowPaint.alpha = alpha.coerceIn(0, 255)
        c.save()
        c.translate(cx, cy)
        c.scale(r, r)
        c.drawCircle(0f, 0f, 1f, glowPaint)
        c.restore()
    }

    fun fireRect(kind: Int, w: Float, h: Float): RectF {
        val cx = w / 2f
        if (kind >= HearthStyles.PHOTO) {
            return RectF(lastFireX - 0.22f * w, lastFireY - 0.18f * h, lastFireX + 0.22f * w, lastFireY + 0.12f * h)
        }
        return when (kind) {
            HearthStyles.STONE_3D -> RectF(cx - 0.27f * w, 0.42f * h, cx + 0.27f * w, 0.80f * h)
            HearthStyles.STOVE -> RectF(cx - 0.14f * w, 0.56f * h, cx + 0.14f * w, 0.75f * h)
            HearthStyles.CAMPFIRE -> RectF(cx - 0.20f * w, 0.50f * h, cx + 0.20f * w, 0.80f * h)
            HearthStyles.MODERN -> RectF(cx - 0.40f * w, 0.52f * h, cx + 0.40f * w, 0.70f * h)
            else -> RectF(cx - 0.26f * w, 0.43f * h, cx + 0.26f * w, 0.80f * h)
        }
    }

    fun draw(c: Canvas, w: Float, h: Float, t: Float, now: Long, kind: Int, flare: Float, speed: Float) {
        val flick = 0.85f + 0.15f * sin(t * speed * 6.3f) * sin(t * speed * 2.1f + 1f)
        if (kind >= HearthStyles.PHOTO) {
            photoHearth(c, w, h, now, flare, flick, speed, kind - HearthStyles.PHOTO)
            return
        }
        when (kind) {
            HearthStyles.BRICK_REAL -> brick(c, w, h, now, flare, flick, speed, false)
            HearthStyles.BLUE -> brick(c, w, h, now, flare, flick, speed, true)
            HearthStyles.STONE_3D -> stone(c, w, h, now, flare, flick, speed)
            HearthStyles.STOVE -> stove(c, w, h, now, flare, flick, speed)
            HearthStyles.CAMPFIRE -> camp(c, w, h, t, now, flare, flick, speed)
            HearthStyles.MODERN -> modern(c, w, h, now, flare, flick, speed)
        }
    }


    // ---------- foto-haarden (Wikimedia Commons, zie Credits) ----------

    private val bitmaps = HashMap<Int, Bitmap?>()
    private val imgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val dstRect = RectF()
    private val creditPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xCCFFFFFF.toInt()
        setShadowLayer(3f, 0f, 1f, 0xFF000000.toInt())
    }
    var lastFireX = 0f
        private set
    var lastFireY = 0f
        private set

    /** Positie van het vuur in elke foto, als fractie van de foto. */
    private val photoFire = arrayOf(
        floatArrayOf(0.36f, 0.58f), floatArrayOf(0.60f, 0.57f), floatArrayOf(0.46f, 0.46f),
        floatArrayOf(0.59f, 0.42f)
    )

    private fun photo(i: Int): Bitmap? {
        if (!bitmaps.containsKey(i)) {
            bitmaps.clear()
            bitmaps[i] = try {
                ctx?.assets?.open("hearths/" + Credits.photos[i].asset)?.use { BitmapFactory.decodeStream(it) }
            } catch (e: Exception) {
                null
            }
        }
        return bitmaps[i]
    }

    private fun photoHearth(c: Canvas, w: Float, h: Float, now: Long, fl: Float, flick: Float, speed: Float, idx: Int) {
        val b = photo(idx)
        if (b == null) {
            c.drawColor(0xFF1A0F0A.toInt())
            return
        }
        val bw = b.width.toFloat()
        val bh = b.height.toFloat()
        val fireAt = photoFire[idx]
        val portrait = bh / bw > 1.2f
        val scale = max(w / bw, h / bh)
        var fw: Float
        var fh: Float
        var fx: Float
        var fy: Float
        if (portrait) {
            fw = bw * scale; fh = bh * scale
            val l = (w - fw) / 2f
            val t = (h - fh) / 2f
            dstRect.set(l, t, l + fw, t + fh)
            c.drawBitmap(b, null, dstRect, imgPaint)
            fx = l + fireAt[0] * fw
            fy = t + fireAt[1] * fh
        } else {
            // donkere, vergrote achtergrond met de scherpe foto erover
            val dw = bw * scale
            val dh = bh * scale
            dstRect.set((w - dw) / 2f, (h - dh) / 2f, (w + dw) / 2f, (h + dh) / 2f)
            c.drawBitmap(b, null, dstRect, imgPaint)
            p.style = Paint.Style.FILL
            p.color = 0xC8000000.toInt()
            c.drawRect(0f, 0f, w, h, p)
            fw = w * 0.94f
            fh = bh * fw / bw
            val l = (w - fw) / 2f
            val t = h * 0.55f - fh / 2f
            path.reset()
            rect.set(l, t, l + fw, t + fh)
            path.addRoundRect(rect, 28f, 28f, Path.Direction.CW)
            c.save()
            c.clipPath(path)
            dstRect.set(l, t, l + fw, t + fh)
            c.drawBitmap(b, null, dstRect, imgPaint)
            c.restore()
            fx = l + fireAt[0] * fw
            fy = t + fireAt[1] * fh
        }
        lastFireX = fx
        lastFireY = fy
        // warme gloed die meebeweegt met het vuur
        glow(c, fx, fy, w * 0.55f, 0xFFFF8A2A.toInt(), (55 * flick + 110 * fl).toInt())
        c.drawText(Credits.short(idx), 12f * density, h - 10f * density, creditPaint.apply { textSize = 10.5f * density })
    }

    // ---------- baksteen met echt vuur (warm of blauw) ----------

    private fun brick(c: Canvas, w: Float, h: Float, now: Long, fl: Float, flick: Float, speed: Float, blue: Boolean) {
        val cx = w / 2f
        val mantelTop = h * 0.30f
        val mantelBottom = h * 0.345f
        val glowC = if (blue) 0xFF3D8BFF.toInt() else 0xFFFF7A1F.toInt()
        glow(c, cx, h * 0.72f, w * 0.95f, glowC, (150 * flick).toInt())

        val bl = cx - 0.42f * w
        val br = cx + 0.42f * w
        val bb = h * 0.80f
        p.style = Paint.Style.FILL
        p.color = if (blue) 0xFF2B2F3A.toInt() else 0xFF8C3F2E.toInt()
        c.drawRect(bl, mantelBottom, br, bb, p)
        p.style = Paint.Style.STROKE
        p.strokeWidth = 1.5f * density
        p.color = if (blue) 0xFF161922.toInt() else 0xFF5E2A1F.toInt()
        val rowH = h * 0.03f
        var row = 0
        var y = mantelBottom
        while (y < bb) {
            c.drawLine(bl, y, br, y, p)
            var x = bl + (row % 2) * w * 0.04f
            while (x < br) {
                if (x > bl) c.drawLine(x, y, x, min(y + rowH, bb), p)
                x += w * 0.08f
            }
            y += rowH
            row++
        }
        p.style = Paint.Style.FILL

        // mantel
        p.color = if (blue) 0xFF3A404E.toInt() else 0xFF6B4423.toInt()
        c.drawRect(cx - 0.47f * w, mantelTop, cx + 0.47f * w, mantelBottom, p)
        p.color = if (blue) 0xFF5B6478.toInt() else 0xFF8A5E34.toInt()
        c.drawRect(cx - 0.47f * w, mantelTop, cx + 0.47f * w, mantelTop + h * 0.007f, p)

        if (!blue) {
            // kerstsokken
            val socks = arrayOf(
                Pair(-0.38f, 0xFFD7263D.toInt()),
                Pair(-0.31f, 0xFF1E9E57.toInt()),
                Pair(0.30f, 0xFF2E86DE.toInt())
            )
            for (s in socks) {
                val sx = cx + s.first * w
                val tw = w * 0.06f
                val len = h * 0.11f
                p.color = s.second
                c.drawRect(sx - tw / 2, mantelBottom, sx + tw / 2, mantelBottom + len, p)
                c.drawRoundRect(sx - tw / 2, mantelBottom + len - h * 0.035f, sx + tw * 1.15f,
                    mantelBottom + len + h * 0.012f, tw * 0.4f, tw * 0.4f, p)
                p.color = 0xFFFFFFFF.toInt()
                c.drawRect(sx - tw * 0.62f, mantelBottom, sx + tw * 0.62f, mantelBottom + h * 0.02f, p)
            }
        }

        // opening
        val l = cx - 0.26f * w
        val r = cx + 0.26f * w
        val tp = h * 0.43f
        val rad = 0.12f * w
        path.reset()
        path.addRoundRect(l, tp, r, bb, floatArrayOf(rad, rad, rad, rad, 0f, 0f, 0f, 0f), Path.Direction.CW)
        p.color = 0xFF090403.toInt()
        c.drawPath(path, p)

        c.save()
        c.clipPath(path)
        glow(c, cx, bb, w * 0.34f, if (blue) 0xFF4DA3FF.toInt() else 0xFFFF9A3A.toInt(), (255 * (0.75f + 0.25f * flick + 0.3f * fl)).toInt())
        val baseY = bb - h * 0.045f
        // houtblokken
        p.color = 0xFF3B2314.toInt()
        c.save(); c.rotate(-9f, cx, baseY)
        c.drawRoundRect(cx - 0.21f * w, baseY, cx + 0.19f * w, baseY + h * 0.045f, h * 0.022f, h * 0.022f, p)
        c.restore()
        c.save(); c.rotate(9f, cx, baseY)
        c.drawRoundRect(cx - 0.19f * w, baseY + h * 0.008f, cx + 0.21f * w, baseY + h * 0.05f, h * 0.022f, h * 0.022f, p)
        c.restore()
        fire.draw(c, now, cx, baseY + h * 0.012f, 0.11f * w, 0.30f * h * (1f + 0.6f * fl), 0.12f * w,
            1f + 0.3f * fl, speed, if (blue) fireBlue else fireYoung)
        c.restore()

        // haardplaat + vloer
        p.color = 0xFF4B4B55.toInt()
        c.drawRect(cx - 0.45f * w, bb, cx + 0.45f * w, bb + h * 0.035f, p)
        p.color = if (blue) 0xFF10141C.toInt() else 0xFF2A1A12.toInt()
        c.drawRect(0f, bb + h * 0.035f, w, h, p)
        glow(c, cx, bb + h * 0.06f, w * 0.7f, glowC, (110 * flick).toInt())
    }

    // ---------- steenhaard in 3D ----------

    private fun stone(c: Canvas, w: Float, h: Float, now: Long, fl: Float, flick: Float, speed: Float) {
        val cx = w / 2f
        val mantelTop = h * 0.30f
        val mantelBottom = h * 0.345f
        glow(c, cx, h * 0.72f, w * 0.95f, 0xFFFF7A1F.toInt(), (150 * flick).toInt())

        val bl = cx - 0.43f * w
        val br = cx + 0.43f * w
        val bb = h * 0.80f
        c.save()
        c.clipRect(bl, mantelBottom, br, bb)
        val rowH = h * 0.045f
        val widths = floatArrayOf(0.10f, 0.14f, 0.12f, 0.09f, 0.13f)
        var row = 0
        var y = mantelBottom
        while (y < bb) {
            var x = bl - (row % 2) * w * 0.05f
            var k = 0
            while (x < br) {
                val wd = widths[(row * 3 + k) % widths.size] * w
                val shade = 0x68 + ((row * 31 + k * 17) % 5) * 0x0A
                p.color = 0xFF000000.toInt() or (shade shl 16) or ((shade + 2) shl 8) or (shade + 8)
                p.style = Paint.Style.FILL
                c.drawRoundRect(x + 2f, y + 2f, x + wd - 2f, y + rowH - 2f, 8f, 8f, p)
                x += wd
                k++
            }
            y += rowH
            row++
        }
        // ronding: zijkanten donkerder
        p.shader = LinearGradient(
            bl, 0f, br, 0f,
            intArrayOf(0xB0000000.toInt(), 0x00000000, 0x00000000, 0xB0000000.toInt()),
            floatArrayOf(0f, 0.25f, 0.75f, 1f), Shader.TileMode.CLAMP
        )
        c.drawRect(bl, mantelBottom, br, bb, p)
        p.shader = null
        c.restore()

        // boogopening met diepte
        val l = cx - 0.27f * w
        val r = cx + 0.27f * w
        val tp = h * 0.42f
        val rad = 0.15f * w
        path.reset()
        path.addRoundRect(l, tp, r, bb, floatArrayOf(rad, rad, rad, rad, 0f, 0f, 0f, 0f), Path.Direction.CW)
        p.color = 0xFF070303.toInt()
        c.drawPath(path, p)
        c.save()
        c.clipPath(path)
        // achterwand
        val inset = 0.07f * w
        p.shader = LinearGradient(0f, tp, 0f, bb, 0xFF1A0F0A.toInt(), 0xFF3A1F12.toInt(), Shader.TileMode.CLAMP)
        c.drawRect(l + inset, tp + inset * 1.2f, r - inset, bb - h * 0.03f, p)
        p.shader = null
        // zijwanden (perspectief)
        path.reset()
        path.moveTo(l, tp); path.lineTo(l + inset, tp + inset * 1.2f); path.lineTo(l + inset, bb - h * 0.03f); path.lineTo(l, bb); path.close()
        p.color = 0xFF0F0806.toInt(); c.drawPath(path, p)
        path.reset()
        path.moveTo(r, tp); path.lineTo(r - inset, tp + inset * 1.2f); path.lineTo(r - inset, bb - h * 0.03f); path.lineTo(r, bb); path.close()
        p.color = 0xFF150B08.toInt(); c.drawPath(path, p)
        glow(c, cx, bb - h * 0.03f, w * 0.36f, 0xFFFF9A3A.toInt(), (255 * (0.75f + 0.25f * flick + 0.3f * fl)).toInt())
        val baseY = bb - h * 0.065f
        p.color = 0xFF3B2314.toInt()
        c.save(); c.rotate(-8f, cx, baseY)
        c.drawRoundRect(cx - 0.17f * w, baseY, cx + 0.15f * w, baseY + h * 0.04f, h * 0.02f, h * 0.02f, p)
        c.restore()
        c.save(); c.rotate(8f, cx, baseY)
        c.drawRoundRect(cx - 0.15f * w, baseY + h * 0.008f, cx + 0.17f * w, baseY + h * 0.045f, h * 0.02f, h * 0.02f, p)
        c.restore()
        fire.draw(c, now, cx, baseY + h * 0.012f, 0.10f * w, 0.30f * h * (1f + 0.6f * fl), 0.12f * w,
            1f + 0.3f * fl, speed, fireYoung)
        c.restore()

        // lichte boogrand
        p.style = Paint.Style.STROKE
        p.strokeWidth = 0.022f * w
        p.color = 0xFFA5A9B3.toInt()
        path.reset()
        path.addRoundRect(l, tp, r, bb, floatArrayOf(rad, rad, rad, rad, 0f, 0f, 0f, 0f), Path.Direction.CW)
        c.drawPath(path, p)
        p.style = Paint.Style.FILL

        // stenen mantel met schuine rand en schaduw
        p.color = 0xFFC0C4CC.toInt()
        c.drawRect(cx - 0.48f * w, mantelTop - h * 0.012f, cx + 0.48f * w, mantelTop + h * 0.006f, p)
        p.color = 0xFF8E9299.toInt()
        c.drawRect(cx - 0.47f * w, mantelTop + h * 0.006f, cx + 0.47f * w, mantelBottom + h * 0.006f, p)
        p.shader = LinearGradient(0f, mantelBottom, 0f, mantelBottom + h * 0.03f, 0x66000000, 0x00000000, Shader.TileMode.CLAMP)
        c.drawRect(bl, mantelBottom + h * 0.006f, br, mantelBottom + h * 0.03f, p)
        p.shader = null

        // krans boven de haard
        val kx = cx
        val ky = h * 0.19f
        val kr = 0.09f * w
        p.style = Paint.Style.STROKE
        p.strokeWidth = 0.035f * w
        p.color = 0xFF1E6B3A.toInt()
        c.drawCircle(kx, ky, kr, p)
        p.strokeWidth = 0.02f * w
        p.color = 0xFF2E9B4A.toInt()
        c.drawCircle(kx, ky, kr * 1.02f, p)
        p.style = Paint.Style.FILL
        p.color = 0xFFD7263D.toInt()
        for (i in 0 until 7) {
            val a = (i / 7f * 2f * PI).toFloat()
            c.drawCircle(kx + cos(a) * kr, ky + sin(a) * kr, 0.012f * w, p)
        }
        c.drawOval(kx - 0.05f * w, ky + kr - 0.02f * w, kx - 0.005f * w, ky + kr + 0.02f * w, p)
        c.drawOval(kx + 0.005f * w, ky + kr - 0.02f * w, kx + 0.05f * w, ky + kr + 0.02f * w, p)

        // haardplaat (twee treden) + vloer
        p.color = 0xFF5A5C66.toInt()
        c.drawRect(cx - 0.47f * w, bb, cx + 0.47f * w, bb + h * 0.02f, p)
        p.color = 0xFF454750.toInt()
        c.drawRect(cx - 0.50f * w, bb + h * 0.02f, cx + 0.50f * w, bb + h * 0.045f, p)
        p.color = 0xFF2A1A12.toInt()
        c.drawRect(0f, bb + h * 0.045f, w, h, p)
        glow(c, cx, bb + h * 0.07f, w * 0.7f, 0xFFFF7A1F.toInt(), (110 * flick).toInt())
    }

    // ---------- houtkachel met glazen deur ----------

    private fun stove(c: Canvas, w: Float, h: Float, now: Long, fl: Float, flick: Float, speed: Float) {
        val cx = w / 2f
        val mantelTop = h * 0.30f
        // houten planken
        p.style = Paint.Style.STROKE
        p.strokeWidth = 2f * density
        p.color = 0xFF1C0F08.toInt()
        var x = 0f
        while (x < w) { c.drawLine(x, 0f, x, h * 0.82f, p); x += w * 0.09f }
        p.style = Paint.Style.FILL
        glow(c, cx, h * 0.66f, w * 0.8f, 0xFFFF7A1F.toInt(), (120 * flick).toInt())

        // kachelpijp
        p.color = 0xFF1B1B1F.toInt()
        c.drawRect(cx - 0.04f * w, 0f, cx + 0.04f * w, h * 0.50f, p)
        p.color = 0xFF34353C.toInt()
        c.drawRect(cx - 0.025f * w, 0f, cx - 0.01f * w, h * 0.50f, p)

        // balk als mantel
        p.color = 0xFF4A2F1A.toInt()
        c.drawRect(0f, mantelTop, w, h * 0.345f, p)
        p.color = 0xFF6B4423.toInt()
        c.drawRect(0f, mantelTop, w, mantelTop + h * 0.007f, p)

        // kachel
        p.color = 0xFF2B2C33.toInt()
        c.drawRoundRect(cx - 0.25f * w, h * 0.485f, cx + 0.25f * w, h * 0.50f, 8f, 8f, p)
        p.color = 0xFF1E1F24.toInt()
        c.drawRoundRect(cx - 0.22f * w, h * 0.50f, cx + 0.22f * w, h * 0.80f, 14f, 14f, p)
        p.color = 0xFF14151A.toInt()
        c.drawRect(cx - 0.20f * w, h * 0.80f, cx - 0.16f * w, h * 0.83f, p)
        c.drawRect(cx + 0.16f * w, h * 0.80f, cx + 0.20f * w, h * 0.83f, p)

        // glazen deur met vuur
        val l = cx - 0.14f * w
        val r = cx + 0.14f * w
        val tp = h * 0.56f
        val bt = h * 0.75f
        p.color = 0xFF080303.toInt()
        c.drawRoundRect(l, tp, r, bt, 16f, 16f, p)
        c.save()
        rect.set(l, tp, r, bt)
        path.reset(); path.addRoundRect(rect, 16f, 16f, Path.Direction.CW)
        c.clipPath(path)
        glow(c, cx, bt, w * 0.22f, 0xFFFF9A3A.toInt(), (255 * (0.8f + 0.2f * flick + 0.3f * fl)).toInt())
        p.color = 0xFF3B2314.toInt()
        c.drawRoundRect(cx - 0.11f * w, bt - h * 0.035f, cx + 0.11f * w, bt - h * 0.01f, 10f, 10f, p)
        fire.draw(c, now, cx, bt - h * 0.025f, 0.075f * w, 0.17f * h * (1f + 0.6f * fl), 0.09f * w,
            1f + 0.3f * fl, speed, fireYoung)
        // glasreflectie
        p.color = 0x22FFFFFF
        path.reset()
        path.moveTo(l + 0.02f * w, tp); path.lineTo(l + 0.08f * w, tp); path.lineTo(l + 0.02f * w, bt); path.lineTo(l - 0.04f * w, bt); path.close()
        c.drawPath(path, p)
        c.restore()
        p.style = Paint.Style.STROKE
        p.strokeWidth = 7f * density
        p.color = 0xFF3A3B42.toInt()
        c.drawRoundRect(l, tp, r, bt, 16f, 16f, p)
        p.style = Paint.Style.FILL
        p.color = 0xFFB9BCC6.toInt()
        c.drawRoundRect(r + 0.02f * w, (tp + bt) / 2 - 0.03f * h, r + 0.035f * w, (tp + bt) / 2 + 0.03f * h, 6f, 6f, p)

        // vloer
        p.color = 0xFF2A1A12.toInt()
        c.drawRect(0f, h * 0.83f, w, h, p)
        glow(c, cx, h * 0.86f, w * 0.65f, 0xFFFF7A1F.toInt(), (130 * flick).toInt())
    }

    // ---------- kampvuur ----------

    private fun camp(c: Canvas, w: Float, h: Float, t: Float, now: Long, fl: Float, flick: Float, speed: Float) {
        val cx = w / 2f
        if (starsFor != 1) {
            val rnd = Random(3)
            stars = FloatArray(60) { rnd.nextFloat() }
            starsFor = 1
        }
        // sterren
        p.color = 0xFFFFFFFF.toInt()
        var j = 0
        while (j + 2 < stars.size) {
            p.alpha = (80 + 140 * (0.5f + 0.5f * sin(t * 0.9f + j))).toInt()
            c.drawCircle(stars[j] * w, stars[j + 1] * h * 0.5f, (0.6f + stars[j + 2]) * density, p)
            j += 3
        }
        p.alpha = 255
        // maan
        glow(c, w * 0.82f, h * 0.12f, w * 0.22f, 0xFFCFE0FF.toInt(), 90)
        p.color = 0xFFEFF3FF.toInt()
        c.drawCircle(w * 0.82f, h * 0.12f, w * 0.045f, p)

        // bomenrij
        p.color = 0xFF07101A.toInt()
        for (i in 0..9) {
            val bx = i * 0.115f * w
            val ph = (0.10f + ((i * 37) % 5) * 0.025f) * h
            path.reset()
            path.moveTo(bx - 0.07f * w, h * 0.68f)
            path.lineTo(bx, h * 0.68f - ph)
            path.lineTo(bx + 0.07f * w, h * 0.68f)
            path.close()
            c.drawPath(path, p)
        }
        // grond
        p.shader = LinearGradient(0f, h * 0.68f, 0f, h, 0xFF14201A.toInt(), 0xFF060A08.toInt(), Shader.TileMode.CLAMP)
        c.drawRect(0f, h * 0.68f, w, h, p)
        p.shader = null
        glow(c, cx, h * 0.80f, w * 0.75f, 0xFFFF7A1F.toInt(), (170 * flick).toInt())

        // palen voor de slinger
        p.color = 0xFF2B1B10.toInt()
        c.drawRect(0.04f * w, h * 0.29f, 0.065f * w, h * 0.86f, p)
        c.drawRect(0.935f * w, h * 0.29f, 0.96f * w, h * 0.86f, p)

        // stenen rondom
        for (i in 0 until 11) {
            val a = (i / 11f * 2f * PI).toFloat()
            val sx = cx + cos(a) * 0.20f * w
            val sy = h * 0.805f + sin(a) * 0.045f * h
            if (sin(a) < 0) continue            // achterste stenen zitten achter het vuur
            p.color = 0xFF4A4E56.toInt()
            c.drawOval(sx - 0.035f * w, sy - 0.02f * h, sx + 0.035f * w, sy + 0.02f * h, p)
        }
        // houtstapel (tipi)
        p.style = Paint.Style.STROKE
        p.strokeCap = Paint.Cap.ROUND
        p.strokeWidth = 0.035f * w
        p.color = 0xFF4A2D18.toInt()
        for (k in -2..2) {
            c.drawLine(cx + k * 0.075f * w, h * 0.80f, cx + k * 0.012f * w, h * 0.68f, p)
        }
        p.strokeWidth = 0.04f * w
        p.color = 0xFF3B2314.toInt()
        c.drawLine(cx - 0.14f * w, h * 0.805f, cx + 0.10f * w, h * 0.795f, p)
        c.drawLine(cx - 0.10f * w, h * 0.795f, cx + 0.14f * w, h * 0.805f, p)
        p.style = Paint.Style.FILL

        glow(c, cx, h * 0.77f, w * 0.3f, 0xFFFF9A3A.toInt(), (255 * (0.8f + 0.2f * flick + 0.3f * fl)).toInt())
        fire.draw(c, now, cx, h * 0.79f, 0.09f * w, 0.28f * h * (1f + 0.7f * fl), 0.13f * w,
            1f + 0.3f * fl, speed, fireYoung)

        // voorste stenen
        for (i in 0 until 11) {
            val a = (i / 11f * 2f * PI).toFloat()
            if (sin(a) < 0) continue
            val sx = cx + cos(a) * 0.20f * w
            val sy = h * 0.805f + sin(a) * 0.045f * h
            p.color = 0xFF5A5E66.toInt()
            c.drawOval(sx - 0.035f * w, sy - 0.02f * h, sx + 0.035f * w, sy + 0.02f * h, p)
        }
    }

    // ---------- moderne gashaard ----------

    private fun modern(c: Canvas, w: Float, h: Float, now: Long, fl: Float, flick: Float, speed: Float) {
        val cx = w / 2f
        val mantelTop = h * 0.30f
        // betonnen wand met panelen
        p.style = Paint.Style.STROKE
        p.strokeWidth = 1.5f * density
        p.color = 0xFF16171C.toInt()
        var x = 0f
        while (x < w) { c.drawLine(x, 0f, x, h * 0.84f, p); x += w * 0.25f }
        p.style = Paint.Style.FILL
        glow(c, cx, h * 0.64f, w * 0.9f, 0xFFFF8A3A.toInt(), (120 * flick).toInt())

        // zwevende plank
        p.color = 0xFF2A1A10.toInt()
        c.drawRect(cx - 0.45f * w, mantelTop + h * 0.02f, cx + 0.45f * w, mantelTop + h * 0.032f, p)
        p.color = 0xFFB08A5A.toInt()
        c.drawRect(cx - 0.45f * w, mantelTop, cx + 0.45f * w, mantelTop + h * 0.02f, p)

        // nis
        val l = cx - 0.42f * w
        val r = cx + 0.42f * w
        val tp = h * 0.52f
        val bt = h * 0.70f
        p.color = 0xFF050506.toInt()
        c.drawRect(l, tp, r, bt, p)
        c.save()
        c.clipRect(l, tp, r, bt)
        glow(c, cx, bt, w * 0.5f, 0xFFFF9A3A.toInt(), (255 * (0.8f + 0.2f * flick + 0.3f * fl)).toInt())
        // glaskiezels
        for (i in 0 until 22) {
            val gx = l + (i + 0.5f) / 22f * (r - l)
            val tone = 0xB0 + (i * 13) % 0x40
            p.color = 0xFF000000.toInt() or (tone shl 16) or ((tone - 30) shl 8) or (tone - 70)
            c.drawOval(gx - 0.02f * w, bt - h * 0.022f, gx + 0.02f * w, bt - h * 0.002f, p)
        }
        fire.draw(c, now, cx, bt - h * 0.015f, 0.38f * w, 0.15f * h * (1f + 0.7f * fl), 0.095f * w,
            1f + 0.3f * fl, speed, fireYoung)
        // glasreflectie
        p.color = 0x14FFFFFF
        path.reset()
        path.moveTo(l + 0.1f * w, tp); path.lineTo(l + 0.3f * w, tp); path.lineTo(l + 0.18f * w, bt); path.lineTo(l, bt); path.close()
        c.drawPath(path, p)
        c.restore()
        p.style = Paint.Style.STROKE
        p.strokeWidth = 6f * density
        p.color = 0xFF2A2C33.toInt()
        c.drawRect(l, tp, r, bt, p)
        p.style = Paint.Style.FILL

        // vloer met weerspiegeling
        p.color = 0xFF191A1F.toInt()
        c.drawRect(0f, h * 0.70f + 0.0f, w, h, p)
        p.shader = LinearGradient(0f, bt, 0f, bt + h * 0.12f, 0x55FF8A3A, 0x00FF8A3A, Shader.TileMode.CLAMP)
        c.drawRect(l, bt, r, bt + h * 0.12f, p)
        p.shader = null
    }
}
