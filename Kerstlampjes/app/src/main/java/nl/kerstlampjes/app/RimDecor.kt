package nl.kerstlampjes.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/** Versiering rond de rand van het scherm; elk onderdeel is los aan of uit te zetten. */
object Decor {
    const val TRAIN = 1
    const val SNOW = 2
    const val STARS = 4
    const val FIREWORKS = 8
    const val PILE = 16
    const val ICICLES = 32
    const val GARLAND = 64
    const val SLEIGH = 128
    const val WALKERS = 256
    const val MARQUEE = 512

    val flags = intArrayOf(TRAIN, SNOW, STARS, FIREWORKS, PILE, ICICLES, GARLAND, SLEIGH, WALKERS, MARQUEE)
    val names = arrayOf(
        "Treintje", "Sneeuwvlokken en sterretjes", "Vallende sterren", "Vuurwerk", "Sneeuw die blijft liggen",
        "Ijspegels", "Slinger", "Slee met rendieren", "Pinguïns en sneeuwpop", "Lichtkrant"
    )
    val trainViews = arrayOf("Van boven", "Van opzij")
    const val ALL = 1023
}

/** Gesloten afgeronde rechthoek; [at] zet (x, y) en de looprichting (tx, ty) op afstand s, met de klok mee. */
class RimPath(val x0: Float, val y0: Float, val x1: Float, val y1: Float, radius: Float) {
    val r = radius.coerceIn(0f, max(0f, min(x1 - x0, y1 - y0) / 2f))
    private val sw = max(0f, (x1 - x0) - 2 * r)
    private val sh = max(0f, (y1 - y0) - 2 * r)
    private val arc = r * (PI.toFloat() / 2f)
    private val seg = floatArrayOf(sw, arc, sh, arc, sw, arc, sh, arc)
    val length = 2 * sw + 2 * sh + 4 * arc
    var x = 0f; var y = 0f; var tx = 1f; var ty = 0f

    fun at(sIn: Float) {
        var s = sIn % length
        if (s < 0) s += length
        var k = 0
        while (k < 7 && s >= seg[k]) { s -= seg[k]; k++ }
        when (k) {
            0 -> { x = x0 + r + s; y = y0; tx = 1f; ty = 0f; return }
            2 -> { x = x1; y = y0 + r + s; tx = 0f; ty = 1f; return }
            4 -> { x = x1 - r - s; y = y1; tx = -1f; ty = 0f; return }
            6 -> { x = x0; y = y1 - r - s; tx = 0f; ty = -1f; return }
        }
        val cx = if (k == 1 || k == 3) x1 - r else x0 + r
        val cy = if (k == 1 || k == 7) y0 + r else y1 - r
        val th0 = -(PI.toFloat() / 2f) + ((k - 1) / 2) * (PI.toFloat() / 2f)
        val th = th0 + (if (r > 0f) s / r else 0f)
        x = cx + r * cos(th); y = cy + r * sin(th)
        tx = -sin(th); ty = cos(th)
    }
}

/** Schudden van de telefoon herkennen (zelfde drempel als bij de boom). */
class ShakeDetector(private val ctx: Context, private val onShake: () -> Unit) : SensorEventListener {
    private var last = 0L
    private var started = false

    fun start() {
        if (started) return
        val sm = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            started = true
        }
    }

    fun stop() {
        if (!started) return
        (ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager).unregisterListener(this)
        started = false
    }

    override fun onSensorChanged(e: SensorEvent) {
        val g = sqrt(e.values[0] * e.values[0] + e.values[1] * e.values[1] + e.values[2] * e.values[2])
        val now = SystemClock.uptimeMillis()
        if (abs(g - 9.81f) > 9f && now - last > 1200) { last = now; onShake() }
    }

    override fun onAccuracyChanged(s: Sensor?, a: Int) {}
}

class RimDecor(private val d: Float) {
    private class Spark(var x: Float, var y: Float, var vx: Float, var vy: Float, var age: Float,
                        val life: Float, val color: Int, val size: Float, val grav: Float)
    private class Rocket(var x: Float, var y: Float, val vy: Float, val ty: Float)
    private class Star(var x: Float, var y: Float, val vx: Float, val vy: Float, var age: Float, val life: Float)
    private class Puff(var x: Float, var y: Float, val vx: Float, val vy: Float, var age: Float, val life: Float, val r: Float)
    private class Penguin(var x: Float, var dir: Float, var pause: Float, var hopY: Float, var hopV: Float, val phase: Float)

    private var s = LightSettings()
    private var w = 0f
    private var h = 0f
    private val rnd = Random(System.nanoTime())
    private var time = 0f
    private var lastMs = 0L

    private var size = 14f
    private var insetPx = 0f
    private var cornerPx = 0f
    private var reach = 0f

    val active get() = s.decor != 0
    private fun on(f: Int) = (s.decor and f) != 0

    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val rect = RectF()

    // ---- sneeuwvlokken en sterretjes
    private val nFlakes = 60
    private val fx = FloatArray(nFlakes); private val fy = FloatArray(nFlakes)
    private val fv = FloatArray(nFlakes); private val fs = FloatArray(nFlakes); private val fp = FloatArray(nFlakes)
    private val nSpark = 14
    private val sx = FloatArray(nSpark); private val sy = FloatArray(nSpark)
    private val sp = FloatArray(nSpark); private val ss = FloatArray(nSpark)

    // ---- sneeuw die blijft liggen
    private var pile = FloatArray(0)
    private var pileAcc = 0f
    private var clearT = 0f

    // ---- ijspegels
    private var ix = FloatArray(0); private var iw = FloatArray(0); private var imax = FloatArray(0)
    private var ilen = FloatArray(0); private var iy = FloatArray(0); private var ivy = FloatArray(0)
    private var ifall = BooleanArray(0); private var inext = FloatArray(0)

    // ---- slinger
    private val garlandPath = Path()
    private var needles = FloatArray(0)
    private var balls = FloatArray(0)

    // ---- trein
    private var trainS = 0f
    private var trackTop: RimPath? = null
    private var sleepers = FloatArray(0)
    private val puffs = ArrayList<Puff>()
    private var puffT = 0f
    private val pose = FloatArray(3)

    // ---- vuurwerk, vallende sterren, slee, pinguïns
    private val sparks = ArrayList<Spark>()
    private val rockets = ArrayList<Rocket>()
    private var fireT = 2f
    private val stars = ArrayList<Star>()
    private var starT = 3f
    private var sleighActive = false
    private var sleighX = 0f
    private var sleighDir = 1f
    private var sleighT = 8f
    private var trailT = 0f
    private val penguins = ArrayList<Penguin>()
    private var snowmanX = 0f
    private var snowmanWiggle = 0f

    private fun palette() = Palettes.colors[s.palette.coerceIn(0, Palettes.colors.size - 1)]
    private fun r(a: Float, b: Float) = a + rnd.nextFloat() * (b - a)

    // ------------------------------------------------------------------ opbouw

    fun layout(settings: LightSettings, width: Float, height: Float) {
        s = settings
        if (width <= 0f || height <= 0f) return
        val resized = width != w || height != h
        w = width; h = height
        size = s.bulbSizeDp * d
        insetPx = s.insetDp * d + size * 0.15f
        cornerPx = s.cornerDp * d
        reach = if (s.showLights) size * 1.9f else 0f

        if (resized || pile.isEmpty()) {
            pile = FloatArray(max(2, (w / (4f * d)).toInt() + 1))
            buildIcicles()
            for (i in 0 until nFlakes) resetFlake(i, true)
            for (i in 0 until nSpark) { newSparkle(i) }
            penguins.clear()
            penguins.add(Penguin(w * 0.25f, 1f, 0f, 0f, 0f, 0f))
            penguins.add(Penguin(w * 0.55f, -1f, 1f, 0f, 0f, 2f))
            snowmanX = w * 0.82f
        }
        if (on(Decor.GARLAND)) buildGarland()
        if (on(Decor.TRAIN)) buildTrack()
    }

    private fun bandW() = (w * 0.16f).coerceIn(60f * d, 110f * d)

    private fun resetFlake(i: Int, anywhere: Boolean) {
        val left = rnd.nextBoolean()
        val b = bandW()
        fx[i] = if (left) r(0f, b) else r(w - b, w)
        fy[i] = if (anywhere) r(-20f * d, h) else -10f * d
        fv[i] = r(28f, 70f) * d
        fs[i] = r(1.2f, 3.2f) * d
        fp[i] = r(0f, 6.28f)
    }

    private fun newSparkle(i: Int) {
        val b = bandW()
        if (rnd.nextInt(3) == 0) { sx[i] = r(0f, w); sy[i] = r(0f, b) }
        else { sx[i] = if (rnd.nextBoolean()) r(0f, b) else r(w - b, w); sy[i] = r(0f, h) }
        sp[i] = r(0f, 6.28f)
        ss[i] = r(4f, 9f) * d
    }

    private fun buildIcicles() {
        val xs = ArrayList<Float>()
        var x = 18f * d
        while (x < w - 18f * d) { xs.add(x); x += r(18f, 38f) * d }
        val n = xs.size
        ix = FloatArray(n) { xs[it] }
        iw = FloatArray(n) { r(6f, 11f) * d }
        imax = FloatArray(n) { r(12f, 42f) * d }
        ilen = FloatArray(n) { imax[it] }
        iy = FloatArray(n); ivy = FloatArray(n)
        ifall = BooleanArray(n)
        inext = FloatArray(n) { r(30f, 120f) }
    }

    private fun buildGarland() {
        garlandPath.reset()
        val rp = RimPath(insetPx, insetPx, w - insetPx, h - insetPx, cornerPx)
        val swag = 64f * d
        val n = max(4, (rp.length / swag).roundToInt())
        val step = rp.length / n
        val nl = ArrayList<Float>()
        val bl = ArrayList<Float>()
        val pal = palette()
        val sag = 9f * d
        for (i in 0 until n) {
            rp.at(i * step); val ax = rp.x; val ay = rp.y
            rp.at((i + 1) * step); val bx = rp.x; val by = rp.y
            rp.at(i * step + step / 2f)
            val cx = rp.x + (-rp.ty) * sag * 2f
            val cy = rp.y + rp.tx * sag * 2f
            if (i == 0) garlandPath.moveTo(ax, ay)
            garlandPath.quadTo(cx, cy, bx, by)
            val m = max(6, (step / (2.6f * d)).toInt())
            for (k in 0 until m) {
                val t = k / m.toFloat()
                val u = 1f - t
                val px = u * u * ax + 2 * u * t * cx + t * t * bx
                val py = u * u * ay + 2 * u * t * cy + t * t * by
                val dx = 2 * u * (cx - ax) + 2 * t * (bx - cx)
                val dy = 2 * u * (cy - ay) + 2 * t * (by - cy)
                val a = atan2(dy, dx)
                for (sgn in intArrayOf(-1, 1)) {
                    val aa = a + sgn * r(0.7f, 1.3f)
                    val len = r(4f, 8f) * d
                    nl.add(px); nl.add(py); nl.add(px + cos(aa) * len); nl.add(py + sin(aa) * len)
                }
            }
            // bal halverwege de slinger
            val mx = 0.25f * ax + 0.5f * cx + 0.25f * bx
            val my = 0.25f * ay + 0.5f * cy + 0.25f * by
            bl.add(mx); bl.add(my + 5f * d); bl.add(5.2f * d); bl.add(pal[i % pal.size].toFloat())
        }
        needles = nl.toFloatArray()
        balls = bl.toFloatArray()
    }

    private fun trackOffset() = insetPx + reach + 9f * d

    private fun buildTrack() {
        val off = trackOffset()
        val rp = RimPath(off, off, w - off, h - off, max(10f * d, cornerPx - (off - insetPx)))
        trackTop = rp
        if (s.trainSide) { sleepers = FloatArray(0); return }
        val n = (rp.length / (8f * d)).toInt()
        val arr = FloatArray(n * 4)
        for (i in 0 until n) {
            rp.at(i * rp.length / n)
            val nx = -rp.ty; val ny = rp.tx
            arr[i * 4] = rp.x - nx * 5f * d; arr[i * 4 + 1] = rp.y - ny * 5f * d
            arr[i * 4 + 2] = rp.x + nx * 5f * d; arr[i * 4 + 3] = rp.y + ny * 5f * d
        }
        sleepers = arr
    }

    // ------------------------------------------------------------------ bediening

    /** Verwerkt een tik; geeft true als er iets op reageerde. */
    fun onTouch(x: Float, y: Float): Boolean {
        if (w <= 0f) return false
        // vallende sterren -> vuurwerk
        for (st in stars) {
            if (abs(st.x - x) < 54f * d && abs(st.y - y) < 54f * d) {
                burst(st.x, st.y); stars.remove(st); return true
            }
        }
        // ijspegels laten vallen
        if (on(Decor.ICICLES)) {
            for (i in ix.indices) {
                if (!ifall[i] && ilen[i] > 4f * d && abs(ix[i] - x) < 20f * d && y < ilen[i] + 36f * d) {
                    ifall[i] = true; ivy[i] = 0f; Sound.ding(); return true
                }
            }
        }
        // trein: fluiten
        if (on(Decor.TRAIN) && hitTrain(x, y)) {
            Sound.toot()
            for (k in 0 until 6) { chimney(pose); spawnPuff(pose[0], pose[1], 1.4f) }
            return true
        }
        // pinguïns springen, sneeuwman wiebelt
        if (on(Decor.WALKERS)) {
            for (pg in penguins) {
                if (abs(pg.x - x) < 22f * d && abs(groundY(pg.x) - y) < 34f * d) {
                    pg.hopV = 380f * d; pg.hopY = 0.01f; return true
                }
            }
            if (abs(snowmanX - x) < 26f * d && abs(groundY(snowmanX) - 22f * d - y) < 40f * d) {
                snowmanWiggle = 1.2f
                for (k in 0 until 14) addSpark(snowmanX, groundY(snowmanX) - 40f * d, 0xFFFFFFFF.toInt(), 1.0f, 2f)
                return true
            }
        }
        // overal elders: vuurwerk
        if (on(Decor.FIREWORKS)) { burst(x, y); return true }
        return false
    }

    /** Schudden: sneeuw die blijft liggen vliegt weg. */
    fun shake() {
        if (!on(Decor.PILE) || w <= 0f) return
        clearT = 0.9f
        var i = 0
        while (i < pile.size) {
            if (pile[i] > 2f * d) {
                val px = i * 4f * d
                val sp = Spark(px, h - pile[i], r(-50f, 50f) * d, -r(90f, 260f) * d, 0f, r(0.7f, 1.3f), 0xFFF2F7FF.toInt(), r(1.5f, 3f) * d, 260f * d)
                sparks.add(sp)
            }
            i += 2
        }
    }

    private fun hitTrain(x: Float, y: Float): Boolean {
        val n = 1 + s.trainWagons
        for (k in 0 until n) {
            carPose(k, pose)
            if (s.trainSide) {
                if (abs(pose[0] - x) < carLen() / 2f && abs(pose[1] - y) < 16f * d) return true
            } else if (abs(pose[0] - x) < 20f * d && abs(pose[1] - y) < 20f * d) return true
        }
        return false
    }

    // ------------------------------------------------------------------ simulatie

    private fun carLen() = if (s.trainSide) 44f * d else 26f * d
    private fun carGap() = if (s.trainSide) 5f * d else 4f * d
    private fun trainSpeed() = (24f + 16f * s.speed) * d
    private fun sideCycle(): Float = w + (1 + s.trainWagons) * (carLen() + carGap()) + carLen() * 2f
    private fun trackY() = h - max(10f * d, insetPx + 2f * d)
    private val sideH get() = 22f * d

    /** Midden (x, y) en hoek (radialen) van wagon k; k = 0 is de locomotief. */
    private fun carPose(k: Int, out: FloatArray) {
        val sk = trainS - k * (carLen() + carGap())
        if (s.trainSide) {
            val cycle = sideCycle()
            var x = sk % cycle
            if (x < 0) x += cycle
            out[0] = x - carLen(); out[1] = trackY() - 13f * d; out[2] = 0f
        } else {
            val rp = trackTop ?: return
            rp.at(sk + carLen() / 2f); val fx = rp.x; val fy = rp.y
            rp.at(sk - carLen() / 2f); val rx = rp.x; val ry = rp.y
            out[0] = (fx + rx) / 2f; out[1] = (fy + ry) / 2f; out[2] = atan2(fy - ry, fx - rx)
        }
    }

    private fun chimney(out: FloatArray) {
        carPose(0, out)
        val a = out[2]
        val lx = carLen() * (if (s.trainSide) 0.30f else 0.22f)
        val ly = if (s.trainSide) -sideH * 0.5f else 0f
        out[0] = out[0] + cos(a) * lx - sin(a) * ly
        out[1] = out[1] + sin(a) * lx + cos(a) * ly
    }

    private fun spawnPuff(x: Float, y: Float, scale: Float) {
        if (puffs.size > 48) return
        val side = s.trainSide
        puffs.add(Puff(x, y, if (side) -r(4f, 14f) * d else r(-9f, 9f) * d, if (side) -r(18f, 30f) * d else r(-9f, 9f) * d,
            0f, r(0.9f, 1.5f), (if (side) 3f else 2.4f) * d * scale))
    }

    private fun addSpark(x: Float, y: Float, color: Int, life: Float, spread: Float) {
        if (sparks.size > 520) return
        sparks.add(Spark(x, y, r(-30f, 30f) * d * spread, -r(10f, 60f) * d * spread, 0f, life * r(0.6f, 1f), color, r(1.2f, 2.6f) * d, 120f * d))
    }

    private fun burst(x: Float, y: Float) {
        Sound.boom()
        val hue = r(0f, 360f)
        val n = 46
        val ring = rnd.nextBoolean()
        val hsv = FloatArray(3)
        for (i in 0 until n) {
            if (sparks.size > 520) break
            val a = (2 * PI * i / n).toFloat() + r(-0.05f, 0.05f)
            val sp = (if (ring) 130f else r(50f, 170f)) * d
            hsv[0] = (hue + r(-25f, 25f) + 360f) % 360f; hsv[1] = r(0.45f, 0.9f); hsv[2] = 1f
            sparks.add(Spark(x, y, cos(a) * sp, sin(a) * sp, 0f, r(1.0f, 1.8f), Color.HSVToColor(hsv), r(1.6f, 3.0f) * d, 85f * d))
        }
    }

    fun update() {
        val now = SystemClock.uptimeMillis()
        val dt = if (lastMs == 0L) 0.033f else ((now - lastMs) / 1000f).coerceIn(0.001f, 0.1f)
        lastMs = now
        time += dt
        if (w <= 0f) return

        if (on(Decor.SNOW)) {
            for (i in 0 until nFlakes) {
                fy[i] += fv[i] * dt
                fx[i] += sin(time * 1.3f + fp[i]) * 12f * d * dt
                if (fy[i] > h) resetFlake(i, false)
            }
        }

        if (on(Decor.PILE)) {
            pileAcc += 14f * d * dt
            val maxH = 26f * d
            while (pileAcc > 0.25f * d) {
                pileAcc -= 0.25f * d
                val c = rnd.nextInt(pile.size)
                if (pile[c] < maxH) pile[c] += 0.25f * d
            }
            for (i in pile.indices) {
                val l = if (i > 0) i - 1 else i
                val rr = if (i < pile.size - 1) i + 1 else i
                val lim = 2.2f * d
                if (pile[i] - pile[l] > lim) { val m = 0.3f * (pile[i] - pile[l] - lim); pile[i] -= m; pile[l] += m }
                if (pile[i] - pile[rr] > lim) { val m = 0.3f * (pile[i] - pile[rr] - lim); pile[i] -= m; pile[rr] += m }
            }
            if (clearT > 0f) {
                clearT -= dt
                val f = max(0f, 1f - 3f * dt)
                for (i in pile.indices) pile[i] *= f
            }
        } else if (pile.isNotEmpty() && pile.any { it > 0f }) {
            java.util.Arrays.fill(pile, 0f)
        }

        if (on(Decor.ICICLES)) {
            for (i in ix.indices) {
                if (ifall[i]) {
                    ivy[i] += 1100f * d * dt
                    iy[i] += ivy[i] * dt
                    if (iy[i] + ilen[i] > h) { ifall[i] = false; ilen[i] = 0f; iy[i] = 0f; inext[i] = r(40f, 140f) }
                } else if (ilen[i] < imax[i]) {
                    ilen[i] = min(imax[i], ilen[i] + imax[i] * dt / 9f)
                } else {
                    inext[i] -= dt
                    if (inext[i] <= 0f) { ifall[i] = true; ivy[i] = 0f }
                }
            }
        }

        if (on(Decor.TRAIN)) {
            trainS += trainSpeed() * dt
            val len = if (s.trainSide) sideCycle() else (trackTop?.length ?: 1f)
            if (trainS > len * 4) trainS -= len * 4
            puffT -= dt
            if (puffT <= 0f) { puffT = 0.14f; chimney(pose); spawnPuff(pose[0], pose[1], 1f) }
        }
        val it = puffs.iterator()
        while (it.hasNext()) {
            val pf = it.next()
            pf.age += dt; pf.x += pf.vx * dt; pf.y += pf.vy * dt
            if (pf.age > pf.life) it.remove()
        }

        if (on(Decor.FIREWORKS)) {
            fireT -= dt
            if (fireT <= 0f) {
                fireT = r(2.5f, 6f)
                rockets.add(Rocket(r(w * 0.12f, w * 0.88f), h, -r(420f, 620f) * d, r(h * 0.15f, h * 0.5f)))
            }
        }
        val rit = rockets.iterator()
        while (rit.hasNext()) {
            val rk = rit.next()
            rk.y += rk.vy * dt
            if (rk.y <= rk.ty) { burst(rk.x, rk.y); rit.remove() }
        }
        val sit = sparks.iterator()
        while (sit.hasNext()) {
            val sp = sit.next()
            sp.age += dt
            sp.vy += sp.grav * dt
            sp.x += sp.vx * dt; sp.y += sp.vy * dt
            if (sp.age > sp.life || sp.y > h + 20f * d) sit.remove()
        }

        if (on(Decor.STARS)) {
            starT -= dt
            if (starT <= 0f) {
                starT = r(5f, 11f)
                val dir = if (rnd.nextBoolean()) 1f else -1f
                val ang = r(0.45f, 0.8f)
                val v = r(380f, 520f) * d
                stars.add(Star(if (dir > 0) r(0f, w * 0.5f) else r(w * 0.5f, w), r(0f, h * 0.25f), cos(ang) * v * dir, sin(ang) * v, 0f, r(1.3f, 1.8f)))
            }
        }
        val tit = stars.iterator()
        while (tit.hasNext()) {
            val st = tit.next()
            st.age += dt; st.x += st.vx * dt; st.y += st.vy * dt
            if (st.age > st.life) tit.remove()
        }

        if (on(Decor.SLEIGH)) {
            if (!sleighActive) {
                sleighT -= dt
                if (sleighT <= 0f) {
                    sleighActive = true
                    sleighDir = if (rnd.nextBoolean()) 1f else -1f
                    sleighX = if (sleighDir > 0) -170f * d else w + 170f * d
                }
            } else {
                sleighX += sleighDir * 110f * d * dt
                trailT -= dt
                if (trailT <= 0f) { trailT = 0.04f; addSpark(sleighX - sleighDir * 20f * d, sleighY() + 4f * d, 0xFFFFD54F.toInt(), 1.2f, 0.6f) }
                if ((sleighDir > 0 && sleighX > w + 190f * d) || (sleighDir < 0 && sleighX < -190f * d)) {
                    sleighActive = false; sleighT = r(25f, 45f)
                }
            }
        }

        if (on(Decor.WALKERS)) {
            for (pg in penguins) {
                if (pg.pause > 0f) pg.pause -= dt
                else {
                    pg.x += pg.dir * 22f * d * dt
                    if (pg.x < 30f * d) pg.dir = 1f
                    if (pg.x > w - 30f * d) pg.dir = -1f
                    if (rnd.nextFloat() < 0.35f * dt) pg.pause = r(1f, 3f)
                }
                if (pg.hopY > 0f) {
                    pg.hopY += pg.hopV * dt; pg.hopV -= 1700f * d * dt
                    if (pg.hopY <= 0f) { pg.hopY = 0f; pg.hopV = 0f }
                }
            }
            if (snowmanWiggle > 0f) snowmanWiggle -= dt
        }
    }

    private fun sleighY() = insetPx + reach + 62f * d + 16f * d * sin(sleighX / (70f * d))

    private fun pileAt(x: Float): Float {
        if (pile.isEmpty() || !on(Decor.PILE)) return 0f
        val f = (x / (4f * d)).coerceIn(0f, pile.size - 1.001f)
        val i = f.toInt()
        return pile[i] + (pile[i + 1] - pile[i]) * (f - i)
    }

    private fun groundY(x: Float) = h - 3f * d - pileAt(x)

    // ------------------------------------------------------------------ tekenen (achter de lampjes)

    fun drawBack(c: Canvas) {
        if (w <= 0f) return
        if (on(Decor.GARLAND)) drawGarland(c)
        if (on(Decor.ICICLES)) drawIcicles(c)
        if (on(Decor.PILE)) drawPile(c)
        if (on(Decor.TRAIN)) drawTrack(c)
    }

    private fun drawGarland(c: Canvas) {
        p.style = Paint.Style.STROKE; p.strokeCap = Paint.Cap.ROUND
        p.strokeWidth = 7f * d; p.color = 0xFF1B5E2E.toInt()
        c.drawPath(garlandPath, p)
        p.strokeWidth = 1.7f * d; p.color = 0xFF3FA65E.toInt()
        c.drawLines(needles, p)
        p.strokeWidth = 2.2f * d; p.color = 0xFF2E8B4A.toInt()
        c.drawPath(garlandPath, p)
        p.style = Paint.Style.FILL
        if (s.garlandBalls) {
            var i = 0
            while (i < balls.size) {
                val bx = balls[i]; val by = balls[i + 1]; val br = balls[i + 2]; val col = balls[i + 3].toInt()
                p.color = 0xFFB8860B.toInt(); p.style = Paint.Style.STROKE; p.strokeWidth = 1f * d
                c.drawLine(bx, by - br - 3f * d, bx, by - br, p)
                p.style = Paint.Style.FILL
                p.color = col; c.drawCircle(bx, by, br, p)
                p.color = 0x88FFFFFF.toInt(); c.drawCircle(bx - br * 0.35f, by - br * 0.35f, br * 0.3f, p)
                i += 4
            }
        }
    }

    private fun drawIcicles(c: Canvas) {
        p.style = Paint.Style.FILL
        for (i in ix.indices) {
            val len = ilen[i]
            if (len < 1f) continue
            val top = iy[i]
            val sway = if (ifall[i]) 0f else sin(time * 0.7f + i) * 0.6f * d
            path.reset()
            path.moveTo(ix[i] - iw[i] / 2f, top)
            path.lineTo(ix[i] + iw[i] / 2f, top)
            path.lineTo(ix[i] + sway, top + len)
            path.close()
            p.color = 0xD8CFE8FF.toInt(); c.drawPath(path, p)
            p.style = Paint.Style.STROKE; p.strokeWidth = 1f * d; p.color = 0xAAFFFFFF.toInt()
            c.drawLine(ix[i] - iw[i] * 0.2f, top + 1f * d, ix[i] + sway * 0.8f, top + len * 0.85f, p)
            p.style = Paint.Style.FILL
        }
    }

    private fun drawPile(c: Canvas) {
        var any = false
        for (v in pile) if (v > 0.5f) { any = true; break }
        if (!any) return
        for (pass in 0..1) {
            path.reset()
            val dy = if (pass == 0) 2.5f * d else 0f
            path.moveTo(0f, h + 2f * d)
            path.lineTo(0f, h - pile[0] + dy)
            for (i in pile.indices) path.lineTo(i * 4f * d, h - pile[i] + dy)
            path.lineTo(w, h - pile[pile.size - 1] + dy)
            path.lineTo(w, h + 2f * d)
            path.close()
            p.style = Paint.Style.FILL
            p.color = if (pass == 0) 0xFFB3C8E6.toInt() else 0xFFF4F8FF.toInt()
            c.drawPath(path, p)
        }
    }

    private fun drawTrack(c: Canvas) {
        p.style = Paint.Style.STROKE
        if (s.trainSide) {
            val y = trackY()
            p.strokeWidth = 1.8f * d; p.color = 0xFFA0AAB0.toInt()
            c.drawLine(0f, y, w, y, p)
            p.strokeWidth = 3f * d; p.color = 0xFF6B4A2B.toInt()
            var x = 0f
            while (x < w) { c.drawLine(x, y + 2f * d, x, y + 4f * d, p); x += 9f * d }
        } else {
            val rp = trackTop ?: return
            p.strokeWidth = 2f * d; p.color = 0xAA6B4A2B.toInt()
            c.drawLines(sleepers, p)
            p.strokeWidth = 1.4f * d; p.color = 0xCCB0BAC0.toInt()
            for (o in floatArrayOf(-3.5f * d, 3.5f * d)) {
                rect.set(rp.x0 - o, rp.y0 - o, rp.x1 + o, rp.y1 + o)
                val rr = max(0f, rp.r + o)
                c.drawRoundRect(rect, rr, rr, p)
            }
        }
        p.style = Paint.Style.FILL
    }

    // ------------------------------------------------------------------ tekenen (voor de lampjes)

    fun drawFront(c: Canvas) {
        if (w <= 0f) return
        if (on(Decor.WALKERS)) drawWalkers(c)
        if (on(Decor.TRAIN)) drawTrain(c)
        if (on(Decor.MARQUEE)) drawMarquee(c)
        if (on(Decor.SNOW)) drawSnow(c)
        if (on(Decor.SLEIGH) && sleighActive) drawSleigh(c)
        drawStars(c)
        drawFireworks(c)
        drawPuffs(c)
    }

    private fun drawSparkle(c: Canvas, x: Float, y: Float, rad: Float, color: Int) {
        path.reset()
        path.moveTo(x, y - rad); path.quadTo(x, y, x + rad, y)
        path.quadTo(x, y, x, y + rad); path.quadTo(x, y, x - rad, y)
        path.quadTo(x, y, x, y - rad); path.close()
        p.style = Paint.Style.FILL; p.color = color
        c.drawPath(path, p)
    }

    private fun drawSnow(c: Canvas) {
        p.style = Paint.Style.FILL
        p.color = 0xE6FFFFFF.toInt()
        for (i in 0 until nFlakes) c.drawCircle(fx[i], fy[i], fs[i], p)
        for (i in 0 until nSpark) {
            val a = sin(time * 1.7f + sp[i])
            if (a <= 0f) continue
            drawSparkle(c, sx[i], sy[i], ss[i] * (0.4f + 0.6f * a), Color.argb((255 * a).toInt(), 255, 244, 200))
        }
    }

    private fun drawPuffs(c: Canvas) {
        p.style = Paint.Style.FILL
        for (pf in puffs) {
            val f = pf.age / pf.life
            p.color = Color.argb((170 * (1f - f)).toInt(), 235, 235, 240)
            c.drawCircle(pf.x, pf.y, pf.r + f * 7f * d, p)
        }
    }

    private fun drawFireworks(c: Canvas) {
        p.style = Paint.Style.FILL
        for (rk in rockets) {
            p.color = 0xFFFFE08A.toInt(); c.drawCircle(rk.x, rk.y, 2.2f * d, p)
            p.color = 0x66FFE08A; c.drawCircle(rk.x, rk.y + 7f * d, 1.6f * d, p)
            p.color = 0x33FFE08A; c.drawCircle(rk.x, rk.y + 14f * d, 1.2f * d, p)
        }
        for (sp in sparks) {
            val f = 1f - sp.age / sp.life
            val a = (255 * f.coerceIn(0f, 1f)).toInt()
            p.color = (sp.color and 0x00FFFFFF) or (a shl 24)
            c.drawCircle(sp.x, sp.y, sp.size * (0.5f + 0.5f * f), p)
            p.color = (sp.color and 0x00FFFFFF) or ((a / 3) shl 24)
            c.drawCircle(sp.x - sp.vx * 0.03f, sp.y - sp.vy * 0.03f, sp.size * 0.7f * f, p)
        }
    }

    private fun drawStars(c: Canvas) {
        for (st in stars) {
            val fade = (1f - st.age / st.life).coerceIn(0f, 1f)
            val len = sqrt(st.vx * st.vx + st.vy * st.vy)
            val ux = st.vx / len; val uy = st.vy / len
            p.style = Paint.Style.FILL
            for (j in 0 until 14) {
                val t = j * 4f * d
                val a = (230 * (1f - j / 14f) * fade).toInt()
                p.color = Color.argb(a, 255, 250, 225)
                c.drawCircle(st.x - ux * t, st.y - uy * t, (3f - j * 0.17f).coerceAtLeast(0.6f) * d, p)
            }
            drawSparkle(c, st.x, st.y, 9f * d, Color.argb((255 * fade).toInt(), 255, 255, 255))
        }
    }

    private fun drawMarquee(c: Canvas) {
        val text = (s.marquee.ifBlank { Tr.t("Fijne feestdagen!") })
        val y = insetPx + reach + 26f * d
        p.textSize = 22f * d
        p.typeface = android.graphics.Typeface.DEFAULT_BOLD
        val pal = palette()
        var total = 0f
        val widths = FloatArray(text.length)
        for (i in text.indices) { widths[i] = p.measureText(text, i, i + 1); total += widths[i] }
        val cycle = w + total + 40f * d
        var x = w - ((time * 60f * d) % cycle)
        val shift = (time * 2f).toInt()
        for (i in text.indices) {
            if (x > -30f * d && x < w) {
                p.style = Paint.Style.STROKE; p.strokeWidth = 3f * d; p.color = 0x99000000.toInt()
                c.drawText(text, i, i + 1, x, y, p)
                p.style = Paint.Style.FILL; p.color = pal[(i + shift) % pal.size]
                c.drawText(text, i, i + 1, x, y, p)
            }
            x += widths[i]
        }
        p.style = Paint.Style.FILL
        p.typeface = android.graphics.Typeface.DEFAULT
    }

    // ---- trein

    private fun drawTrain(c: Canvas) {
        val n = 1 + s.trainWagons
        val pal = palette()
        for (k in n - 1 downTo 0) {
            carPose(k, pose)
            c.save()
            c.translate(pose[0], pose[1])
            c.rotate(pose[2] * 180f / PI.toFloat())
            if (s.trainSide) {
                if (k == 0) sideLoco(c) else sideWagon(c, k, pal)
            } else {
                if (k == 0) topLoco(c) else topWagon(c, k, pal)
            }
            c.restore()
        }
    }

    private fun box(c: Canvas, l: Float, t: Float, r: Float, b: Float, rad: Float, color: Int) {
        rect.set(l, t, r, b); p.style = Paint.Style.FILL; p.color = color
        c.drawRoundRect(rect, rad, rad, p)
    }

    private fun twinkle(k: Int, j: Int) = 0.35f + 0.65f * (0.5f + 0.5f * sin(time * 3.2f + k * 1.7f + j * 2.3f))

    private fun lamp(c: Canvas, x: Float, y: Float, color: Int, a: Float) {
        p.style = Paint.Style.FILL
        p.color = (color and 0x00FFFFFF) or ((70 * a).toInt() shl 24); c.drawCircle(x, y, 4.2f * d, p)
        p.color = (color and 0x00FFFFFF) or ((255 * a).toInt() shl 24); c.drawCircle(x, y, 1.9f * d, p)
    }

    private fun topLoco(c: Canvas) {
        val l = carLen(); val hw = 7f * d
        box(c, -l / 2, -hw, l / 2, hw, 3f * d, 0xFFC62828.toInt())
        box(c, 0f, -hw * 0.78f, l / 2, hw * 0.78f, 3f * d, 0xFF8E1B1B.toInt())
        box(c, -l / 2, -hw, -l * 0.12f, hw, 2f * d, 0xFF5E1010.toInt())
        p.color = 0xFFE0B040.toInt(); c.drawRect(l * 0.02f, -hw, l * 0.07f, hw, p)
        p.color = 0xFF222222.toInt(); c.drawCircle(l * 0.22f, 0f, 2.7f * d, p)
        p.color = 0xFFFFE08A.toInt(); c.drawCircle(l / 2, 0f, 1.9f * d, p)
        p.color = 0x55FFE08A; c.drawCircle(l / 2 + 2f * d, 0f, 6f * d, p)
        if (s.trainLights) { lamp(c, -l * 0.3f, -hw * 0.9f, 0xFFFFD54F.toInt(), twinkle(0, 0)); lamp(c, -l * 0.3f, hw * 0.9f, 0xFF40A9FF.toInt(), twinkle(0, 1)) }
    }

    private fun topWagon(c: Canvas, k: Int, pal: IntArray) {
        val l = carLen(); val hw = 6.5f * d
        box(c, -l / 2 - carGap(), -1f * d, -l / 2, 1f * d, 0f, 0xFF333333.toInt())
        box(c, -l / 2, -hw, l / 2, hw, 2.5f * d, 0xFF37474F.toInt())
        box(c, -l / 2 + 1.5f * d, -hw + 1.5f * d, l / 2 - 1.5f * d, hw - 1.5f * d, 2f * d, 0xFF263238.toInt())
        // cadeautjes als lading
        for (j in 0..2) {
            val col = pal[(k + j) % pal.size]
            box(c, -l * 0.34f + j * l * 0.24f, -hw * 0.55f, -l * 0.34f + j * l * 0.24f + 6f * d, hw * 0.55f, 1f * d, col)
            p.color = 0xAAFFFFFF.toInt(); c.drawRect(-l * 0.34f + j * l * 0.24f + 2.6f * d, -hw * 0.55f, -l * 0.34f + j * l * 0.24f + 3.4f * d, hw * 0.55f, p)
        }
        if (s.trainLights) {
            lamp(c, -l * 0.42f, -hw, pal[k % pal.size], twinkle(k, 0)); lamp(c, l * 0.42f, -hw, pal[(k + 1) % pal.size], twinkle(k, 1))
            lamp(c, -l * 0.42f, hw, pal[(k + 2) % pal.size], twinkle(k, 2)); lamp(c, l * 0.42f, hw, pal[(k + 3) % pal.size], twinkle(k, 3))
        }
    }

    private fun wheel(c: Canvas, x: Float, y: Float, rad: Float) {
        p.style = Paint.Style.FILL; p.color = 0xFF37393D.toInt(); c.drawCircle(x, y, rad, p)
        p.style = Paint.Style.STROKE; p.strokeWidth = 1f * d; p.color = 0xFFB6BCC2.toInt(); c.drawCircle(x, y, rad - 0.5f * d, p)
        val a = trainS / rad
        c.drawLine(x - cos(a) * rad * 0.8f, y - sin(a) * rad * 0.8f, x + cos(a) * rad * 0.8f, y + sin(a) * rad * 0.8f, p)
        p.style = Paint.Style.FILL
    }

    private fun sideLoco(c: Canvas) {
        val l = carLen(); val hh = sideH
        box(c, -l / 2, hh * 0.2f, l / 2, hh * 0.38f, 1f * d, 0xFF2B2B2B.toInt())
        box(c, -l * 0.08f, -hh * 0.25f, l * 0.5f, hh * 0.25f, 4f * d, 0xFFC62828.toInt())
        box(c, -l * 0.5f, -hh * 0.5f, -l * 0.1f, hh * 0.25f, 2f * d, 0xFF8E1B1B.toInt())
        box(c, -l * 0.54f, -hh * 0.58f, -l * 0.06f, -hh * 0.48f, 1.5f * d, 0xFF3B0B0B.toInt())
        box(c, -l * 0.42f, -hh * 0.38f, -l * 0.2f, -hh * 0.1f, 1.5f * d, 0xFFFFE08A.toInt())
        path.reset()
        path.moveTo(l * 0.26f, -hh * 0.25f); path.lineTo(l * 0.34f, -hh * 0.25f)
        path.lineTo(l * 0.37f, -hh * 0.55f); path.lineTo(l * 0.23f, -hh * 0.55f); path.close()
        p.style = Paint.Style.FILL; p.color = 0xFF222222.toInt(); c.drawPath(path, p)
        p.color = 0xFFE0B040.toInt(); c.drawCircle(l * 0.1f, -hh * 0.28f, 3f * d, p)
        path.reset()
        path.moveTo(l * 0.5f, hh * 0.1f); path.lineTo(l * 0.6f, hh * 0.38f); path.lineTo(l * 0.5f, hh * 0.38f); path.close()
        p.color = 0xFF2B2B2B.toInt(); c.drawPath(path, p)
        p.color = 0xFFFFE08A.toInt(); c.drawCircle(l * 0.5f, -hh * 0.05f, 2f * d, p)
        p.color = 0x55FFE08A; c.drawCircle(l * 0.5f + 3f * d, -hh * 0.05f, 6f * d, p)
        wheel(c, -l * 0.3f, hh * 0.4f, 4.4f * d); wheel(c, -l * 0.02f, hh * 0.4f, 4.4f * d); wheel(c, l * 0.3f, hh * 0.4f, 4.4f * d)
        if (s.trainLights) lamp(c, -l * 0.3f, -hh * 0.62f, 0xFFFFD54F.toInt(), twinkle(0, 0))
    }

    private fun sideWagon(c: Canvas, k: Int, pal: IntArray) {
        val l = carLen(); val hh = sideH
        box(c, -l / 2 - carGap(), hh * 0.12f, -l / 2, hh * 0.18f, 0f, 0xFF333333.toInt())
        box(c, -l / 2, hh * 0.2f, l / 2, hh * 0.36f, 1f * d, 0xFF2B2B2B.toInt())
        box(c, -l / 2, -hh * 0.3f, l / 2, hh * 0.25f, 2.5f * d, 0xFF37474F.toInt())
        p.style = Paint.Style.STROKE; p.strokeWidth = 0.8f * d; p.color = 0xFF263238.toInt()
        var x = -l / 2 + 7f * d
        while (x < l / 2) { c.drawLine(x, -hh * 0.3f, x, hh * 0.25f, p); x += 7f * d }
        for (j in 0..2) {
            val col = pal[(k + j) % pal.size]
            val gx = -l * 0.36f + j * l * 0.25f
            box(c, gx, -hh * 0.55f, gx + 7.5f * d, -hh * 0.3f, 1f * d, col)
            p.style = Paint.Style.FILL; p.color = 0xAAFFFFFF.toInt(); c.drawRect(gx + 3.3f * d, -hh * 0.55f, gx + 4.2f * d, -hh * 0.3f, p)
        }
        wheel(c, -l * 0.3f, hh * 0.4f, 3.9f * d); wheel(c, l * 0.3f, hh * 0.4f, 3.9f * d)
        if (s.trainLights) {
            var j = 0
            var lx = -l * 0.45f
            while (lx <= l * 0.46f) { lamp(c, lx, -hh * 0.32f, pal[(k + j) % pal.size], twinkle(k, j)); lx += l * 0.18f; j++ }
        }
    }

    // ---- slee

    private fun drawSleigh(c: Canvas) {
        c.save()
        c.translate(sleighX, sleighY())
        c.scale(d * sleighDir, d)
        p.style = Paint.Style.STROKE; p.strokeWidth = 0.8f; p.color = 0xFF3B2A1A.toInt()
        c.drawLine(8f, -2f, 108f, -4f, p)
        for (i in 0..2) reindeer(c, 40f + i * 34f, i)
        // slee
        p.style = Paint.Style.STROKE; p.strokeWidth = 1.8f; p.color = 0xFFE0B040.toInt()
        path.reset(); path.moveTo(-24f, 10f); path.lineTo(14f, 10f); path.quadTo(24f, 10f, 22f, 2f)
        c.drawPath(path, p)
        p.style = Paint.Style.FILL; p.color = 0xFFC62828.toInt()
        path.reset(); path.moveTo(-22f, 8f); path.lineTo(-22f, -8f); path.quadTo(-10f, -2f, 8f, -4f); path.lineTo(18f, -6f)
        path.quadTo(18f, 6f, 8f, 8f); path.close()
        c.drawPath(path, p)
        p.color = 0xFF6B4A2B.toInt(); c.drawCircle(-15f, -9f, 7f, p)       // zak met cadeaus
        p.color = 0xFFC62828.toInt(); c.drawCircle(-3f, -10f, 5.5f, p)      // kerstman
        p.color = 0xFFFFD2A8.toInt(); c.drawCircle(-3f, -17f, 3.5f, p)
        p.color = 0xFFC62828.toInt()
        path.reset(); path.moveTo(-6.5f, -18f); path.lineTo(0.5f, -18f); path.lineTo(-5f, -26f); path.close(); c.drawPath(path, p)
        p.color = Color.WHITE; c.drawCircle(-5f, -26f, 1.8f, p)
        c.drawCircle(-3f, -14.5f, 2.6f, p)
        c.restore()
    }

    private fun reindeer(c: Canvas, x: Float, i: Int) {
        val gallop = sin(time * 10f + i * 1.3f)
        val bob = gallop * 1.2f
        c.save(); c.translate(x, bob)
        p.style = Paint.Style.STROKE; p.strokeWidth = 1.6f; p.color = 0xFF6B4423.toInt(); p.strokeCap = Paint.Cap.ROUND
        for (l in 0..3) {
            val side = if (l < 2) -1 else 1
            val a = gallop * 0.7f * (if (l % 2 == 0) 1 else -1)
            c.drawLine(side * 8f, 5f, side * 8f + sin(a) * 6f, 5f + 10f, p)
        }
        p.style = Paint.Style.FILL; p.color = 0xFF8B5A2B.toInt()
        rect.set(-12f, -6f, 12f, 6f); c.drawOval(rect, p)
        c.drawCircle(15f, -9f, 4.6f, p)
        p.style = Paint.Style.STROKE; p.strokeWidth = 4f; c.drawLine(10f, -3f, 14f, -8f, p)
        p.strokeWidth = 1f; p.color = 0xFF5A3A1A.toInt()
        c.drawLine(15f, -13f, 14f, -20f, p); c.drawLine(14f, -20f, 11f, -23f, p); c.drawLine(14f, -18f, 17f, -22f, p)
        p.style = Paint.Style.FILL
        p.color = if (i == 2) 0xFFFF2A2A.toInt() else 0xFF2B1A0E.toInt()
        c.drawCircle(19.4f, -9f, 1.7f, p)
        if (i == 2) { p.color = 0x55FF2A2A; c.drawCircle(19.4f, -9f, 5f, p) }
        c.restore()
    }

    // ---- pinguïns en sneeuwman

    private fun drawWalkers(c: Canvas) {
        for (pg in penguins) {
            val gy = groundY(pg.x) - pg.hopY
            c.save()
            c.translate(pg.x, gy)
            c.scale(d * pg.dir, d)
            val wob = if (pg.pause > 0f || pg.hopY > 0f) 0f else sin(time * 9f + pg.phase) * 7f
            c.rotate(wob)
            p.style = Paint.Style.FILL
            p.color = 0xFFFF9F1C.toInt(); rect.set(-6f, -2f, -1f, 1.5f); c.drawOval(rect, p); rect.set(1f, -2f, 6f, 1.5f); c.drawOval(rect, p)
            p.color = 0xFF1D2733.toInt(); rect.set(-6.5f, -21f, 6.5f, -1f); c.drawOval(rect, p)
            c.drawCircle(0f, -21f, 5f, p)
            p.color = Color.WHITE; rect.set(-4.2f, -17f, 4.2f, -2f); c.drawOval(rect, p)
            c.drawCircle(2f, -22f, 1.3f, p); c.drawCircle(-1.2f, -22f, 1.3f, p)
            p.color = 0xFF111111.toInt(); c.drawCircle(2.3f, -22f, 0.6f, p); c.drawCircle(-0.9f, -22f, 0.6f, p)
            p.color = 0xFFFF9F1C.toInt()
            path.reset(); path.moveTo(1f, -19.5f); path.lineTo(7f, -18.5f); path.lineTo(1f, -17.5f); path.close(); c.drawPath(path, p)
            p.color = 0xFFC62828.toInt(); c.drawRect(-5f, -16.5f, 5f, -14.5f, p)
            c.restore()
        }
        // sneeuwman
        val gy = groundY(snowmanX)
        c.save(); c.translate(snowmanX, gy); c.scale(d, d)
        val sw = if (snowmanWiggle > 0f) sin(time * 25f) * 5f * snowmanWiggle else 0f
        c.rotate(sw)
        p.style = Paint.Style.FILL; p.color = 0xFFF8FBFF.toInt()
        c.drawCircle(0f, -11f, 11f, p); c.drawCircle(0f, -27f, 8f, p); c.drawCircle(0f, -40f, 6f, p)
        p.style = Paint.Style.STROKE; p.strokeWidth = 0.8f; p.color = 0xFFB9CCE6.toInt()
        c.drawCircle(0f, -11f, 11f, p); c.drawCircle(0f, -27f, 8f, p); c.drawCircle(0f, -40f, 6f, p)
        p.strokeWidth = 1.2f; p.color = 0xFF6B4A2B.toInt()
        c.drawLine(-7f, -29f, -17f, -35f, p); c.drawLine(7f, -29f, 17f, -35f, p)
        p.style = Paint.Style.FILL
        p.color = 0xFF222222.toInt(); c.drawCircle(-2.2f, -41.5f, 0.9f, p); c.drawCircle(2.2f, -41.5f, 0.9f, p)
        c.drawCircle(0f, -24f, 0.9f, p); c.drawCircle(0f, -28f, 0.9f, p); c.drawCircle(0f, -11f, 0.9f, p)
        p.color = 0xFFFF7A00.toInt()
        path.reset(); path.moveTo(0f, -39.5f); path.lineTo(7f, -38.5f); path.lineTo(0f, -37.5f); path.close(); c.drawPath(path, p)
        p.color = 0xFF222222.toInt(); c.drawRect(-5f, -50f, 5f, -45.5f, p); c.drawRect(-7.5f, -46f, 7.5f, -44.5f, p)
        p.color = 0xFFC62828.toInt(); c.drawRect(-6.5f, -35f, 6.5f, -32.5f, p)
        val fl = sin(time * 3f) * 2f
        path.reset(); path.moveTo(3f, -33f); path.lineTo(5.5f, -33f); path.lineTo(7f + fl, -24f); path.lineTo(4.5f + fl, -24f); path.close()
        c.drawPath(path, p)
        c.restore()
    }
}
