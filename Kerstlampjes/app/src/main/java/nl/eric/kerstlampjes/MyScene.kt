package nl.eric.kerstlampjes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

// ───────────────────────── gegevensmodel ─────────────────────────

/** Soorten effecten in een eigen scène. */
object MyKinds {
    const val SNOW = 0
    const val FALL = 1
    const val CONFETTI = 2
    const val STARS = 3
    const val FIREWORKS = 4
    const val FIRE = 5
    const val SHOOTING = 6
    const val GLOW = 7
    val names = arrayOf(
        "Sneeuw", "Vallende dingen", "Confetti", "Fonkelende sterren", "Vuurwerk", "Vuur", "Vallende sterren", "Gloed"
    )
    val icons = arrayOf("❄️", "🎁", "🎊", "✨", "🎆", "🔥", "🌠", "💡")
    fun positioned(k: Int) = k == FIRE || k == GLOW
    fun sized(k: Int) = k == FIRE || k == GLOW || k == FALL
}

object MyLook {
    val anims = arrayOf("Stil", "Zweven", "Zwaaien", "Pulseren", "Knipperen", "Hupsen", "Ronddraaien", "Voorbijrijden", "Rondjes draaien")
    val taps = arrayOf("Niets", "Hupsen", "Sterretjes", "Draaien", "Belletje", "Zeg iets")
    const val TAP_SAY = 5
    val firePalettes = arrayOf("Oranje", "Blauw", "Groen", "Paars", "Wit")
    val fireColors: Array<IntArray> = arrayOf(
        intArrayOf(0xFFFFE9A0.toInt(), 0xFFFFC04A.toInt(), 0xFFFF8A1F.toInt(), 0xFFE8431A.toInt(), 0xFF7A1A0E.toInt()),
        intArrayOf(0xFFE6F6FF.toInt(), 0xFF8FD3FF.toInt(), 0xFF3D8BFF.toInt(), 0xFF1E4FD6.toInt(), 0xFF0E2A7A.toInt()),
        intArrayOf(0xFFEFFFD8.toInt(), 0xFFB6F25C.toInt(), 0xFF3FD06A.toInt(), 0xFF1E8F4A.toInt(), 0xFF0B4A26.toInt()),
        intArrayOf(0xFFFFE6FF.toInt(), 0xFFE08CFF.toInt(), 0xFFB057FF.toInt(), 0xFF6A2BD6.toInt(), 0xFF3A1580.toInt()),
        intArrayOf(0xFFFFFFFF.toInt(), 0xFFEAF2FF.toInt(), 0xFFC8D8F0.toInt(), 0xFF8FA6C8.toInt(), 0xFF4A5C7A.toInt())
    )
    val swatches = intArrayOf(
        0xFFFFFFFF.toInt(), 0xFFFFD54F.toInt(), 0xFFFF9500.toInt(), 0xFFFF3B30.toInt(),
        0xFFFF4FD8.toInt(), 0xFF40A9FF.toInt(), 0xFF4CD964.toInt(), 0xFFB57BFF.toInt()
    )
    val emojis = listOf(
        "🎄", "🎅", "🤶", "🦌", "⛄", "☃️", "🎁", "⭐", "🌟", "✨", "❄️", "🔔", "🕯️", "🏠", "🛷", "🧦",
        "🍪", "🥛", "🍬", "🍭", "🐧", "🐻", "🦉", "🌙", "☄️", "🎀", "💝", "🧸", "⛪", "🚂", "🌲", "🎶"
    )
    val fallEmojis = listOf("❄️", "🎁", "⭐", "❤️", "🍬", "🎄", "🔔", "🍪", "✨", "🌟")
    /** Achtergronden: (naam, boven, onder). */
    val backgrounds = listOf(
        Triple("Sterrennacht", 0xFF050B24.toInt(), 0xFF16305E.toInt()),
        Triple("Middernacht", 0xFF000000.toInt(), 0xFF0A0A1A.toInt()),
        Triple("Paarse nacht", 0xFF0A0624.toInt(), 0xFF3B1B6B.toInt()),
        Triple("Zonsondergang", 0xFF2B1055.toInt(), 0xFFE0684A.toInt()),
        Triple("Poolnacht", 0xFF031B2B.toInt(), 0xFF0E6B5E.toInt()),
        Triple("Warme kamer", 0xFF1E0F08.toInt(), 0xFF5C2A12.toInt()),
        Triple("Rood", 0xFF3A0610.toInt(), 0xFF9C1B2A.toInt()),
        Triple("Winterochtend", 0xFF6FA8DC.toInt(), 0xFFEAF4FF.toInt())
    )
}

class MyFx(
    var kind: Int, var amount: Int = 50, var speed: Int = 5, var color: Int = 0,
    var emoji: String = "❄️", var x: Float = 0.5f, var y: Float = 0.8f, var size: Int = 30
)

class MySticker(
    var emoji: String, var x: Float = 0.5f, var y: Float = 0.5f, var size: Float = 0.2f,
    var rot: Float = 0f, var anim: Int = 0, var tap: Int = 0, var msg: String = ""
)

class MyString(
    var pts: FloatArray, var style: Int = 0, var palette: Int = 0, var mode: Int = Modes.TWINKLE,
    var speed: Int = 5, var size: Int = 5
)

class MyRecipe(var id: String, var name: String) {
    var bgKind = 0                 // 0 = kleurverloop, 1 = eigen foto
    var bgTop = MyLook.backgrounds[0].second
    var bgBottom = MyLook.backgrounds[0].third
    var ground = true
    val fx = ArrayList<MyFx>()
    val stickers = ArrayList<MySticker>()
    val strings = ArrayList<MyString>()

    fun toJson(withPhoto: Boolean = true): JSONObject = JSONObject().apply {
        put("v", 1)
        put("id", id)
        put("name", name)
        put("bk", if (withPhoto) bgKind else 0)
        put("bt", bgTop); put("bb", bgBottom); put("g", ground)
        put("fx", JSONArray().also { a ->
            fx.forEach {
                a.put(JSONObject().put("k", it.kind).put("a", it.amount).put("s", it.speed).put("c", it.color)
                    .put("e", it.emoji).put("x", it.x.toDouble()).put("y", it.y.toDouble()).put("z", it.size))
            }
        })
        put("st", JSONArray().also { a ->
            stickers.forEach {
                a.put(JSONObject().put("e", it.emoji).put("x", it.x.toDouble()).put("y", it.y.toDouble())
                    .put("z", it.size.toDouble()).put("r", it.rot.toDouble()).put("an", it.anim).put("tp", it.tap).put("m", it.msg))
            }
        })
        put("ls", JSONArray().also { a ->
            strings.forEach { s ->
                a.put(JSONObject().put("p", JSONArray().also { p -> s.pts.forEach { v -> p.put(v.toDouble()) } })
                    .put("st", s.style).put("pal", s.palette).put("mo", s.mode).put("sp", s.speed).put("sz", s.size))
            }
        })
    }

    fun copy(newId: String, newName: String): MyRecipe = fromJson(toJson()).also { it.id = newId; it.name = newName }

    companion object {
        fun fromJson(o: JSONObject): MyRecipe {
            val r = MyRecipe(o.optString("id", "x"), o.optString("name", "Mijn scène"))
            r.bgKind = o.optInt("bk", 0)
            r.bgTop = o.optInt("bt", r.bgTop); r.bgBottom = o.optInt("bb", r.bgBottom)
            r.ground = o.optBoolean("g", true)
            o.optJSONArray("fx")?.let { a ->
                for (i in 0 until min(a.length(), 40)) {
                    val j = a.getJSONObject(i)
                    r.fx.add(MyFx(
                        j.optInt("k", 0).coerceIn(0, MyKinds.names.size - 1), j.optInt("a", 50).coerceIn(1, 100),
                        j.optInt("s", 5).coerceIn(1, 10), j.optInt("c", 0).coerceIn(0, 7), j.optString("e", "❄️"),
                        j.optDouble("x", 0.5).toFloat().coerceIn(0f, 1f), j.optDouble("y", 0.8).toFloat().coerceIn(0f, 1f),
                        j.optInt("z", 30).coerceIn(3, 100)
                    ))
                }
            }
            o.optJSONArray("st")?.let { a ->
                for (i in 0 until min(a.length(), 80)) {
                    val j = a.getJSONObject(i)
                    r.stickers.add(MySticker(
                        j.optString("e", "⭐"), j.optDouble("x", 0.5).toFloat(), j.optDouble("y", 0.5).toFloat(),
                        j.optDouble("z", 0.2).toFloat().coerceIn(0.03f, 1f), j.optDouble("r", 0.0).toFloat(),
                        j.optInt("an", 0).coerceIn(0, MyLook.anims.size - 1), j.optInt("tp", 0).coerceIn(0, MyLook.taps.size - 1),
                        j.optString("m", "").take(80)
                    ))
                }
            }
            o.optJSONArray("ls")?.let { a ->
                for (i in 0 until min(a.length(), 20)) {
                    val j = a.getJSONObject(i)
                    val p = j.optJSONArray("p") ?: continue
                    val n = min(p.length(), 800) / 2 * 2
                    if (n < 4) continue
                    r.strings.add(MyString(
                        FloatArray(n) { k -> p.optDouble(k, 0.0).toFloat() },
                        j.optInt("st", 0).coerceIn(0, LampStyles.names.size - 1),
                        j.optInt("pal", 0).coerceIn(0, Palettes.colors.size - 1),
                        j.optInt("mo", 1).coerceIn(0, 4), j.optInt("sp", 5).coerceIn(1, 10), j.optInt("sz", 5).coerceIn(1, 10)
                    ))
                }
            }
            return r
        }
    }
}

/** Opslag van eigen scènes: een lijst recepten in de voorkeuren, de actieve is wat je ziet in "Mijn scène". */
object MyScenes {
    private const val FILE = "kerstlampjes"
    private const val KEY = "my_recipes"
    private const val ACTIVE = "my_active"
    private const val PREFIX = "KLS1:"

    /** Telt op bij elke wijziging, zodat tekenaars hun kopie kunnen verversen. */
    @Volatile var version = 0

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun newId() = java.lang.Long.toString(System.currentTimeMillis(), 36) + (Math.random() * 1000).toInt()

    fun all(ctx: Context): MutableList<MyRecipe> {
        val out = ArrayList<MyRecipe>()
        try {
            val a = JSONArray(prefs(ctx).getString(KEY, "[]"))
            for (i in 0 until a.length()) out.add(MyRecipe.fromJson(a.getJSONObject(i)))
        } catch (e: Exception) {
        }
        return out
    }

    fun saveAll(ctx: Context, list: List<MyRecipe>) {
        val a = JSONArray()
        list.forEach { a.put(it.toJson()) }
        prefs(ctx).edit().putString(KEY, a.toString()).apply()
        version++
    }

    fun activeId(ctx: Context) = prefs(ctx).getString(ACTIVE, "") ?: ""
    fun setActive(ctx: Context, id: String) {
        prefs(ctx).edit().putString(ACTIVE, id).apply()
        version++
    }

    fun active(ctx: Context): MyRecipe {
        val list = all(ctx)
        return list.firstOrNull { it.id == activeId(ctx) } ?: list.firstOrNull() ?: starter()
    }

    fun upsert(ctx: Context, r: MyRecipe) {
        val list = all(ctx)
        val i = list.indexOfFirst { it.id == r.id }
        if (i >= 0) list[i] = r else list.add(r)
        saveAll(ctx, list)
    }

    fun delete(ctx: Context, id: String) {
        saveAll(ctx, all(ctx).filter { it.id != id })
        bgFile(ctx, id).delete()
    }

    fun bgFile(ctx: Context, id: String) = File(ctx.filesDir, "my-bg-$id.jpg")

    /** Een mooie beginscène, zodat "Mijn scène" meteen iets laat zien. */
    fun starter(): MyRecipe {
        val r = MyRecipe(newId(), "Mijn eerste scène")
        r.fx.add(MyFx(MyKinds.STARS))
        r.fx.add(MyFx(MyKinds.SNOW, amount = 45, speed = 4))
        r.fx.add(MyFx(MyKinds.SHOOTING, amount = 40, speed = 4))
        r.stickers.add(MySticker("🎄", 0.5f, 0.74f, 0.42f, 0f, 3, 2))
        r.stickers.add(MySticker("🎅", 0.18f, 0.9f, 0.2f, 0f, 1, 5, "Ho ho ho!"))
        r.stickers.add(MySticker("⛄", 0.84f, 0.9f, 0.2f, 0f, 2, 1))
        r.strings.add(MyString(floatArrayOf(0.03f, 0.04f, 0.2f, 0.07f, 0.4f, 0.085f, 0.6f, 0.085f, 0.8f, 0.07f, 0.97f, 0.04f), 0, 0, Modes.TWINKLE))
        return r
    }

    /** Deelbare tekst: de foto zelf wordt niet meegestuurd. */
    fun share(r: MyRecipe): String =
        PREFIX + Base64.encodeToString(r.toJson(withPhoto = false).toString().toByteArray(Charsets.UTF_8), Base64.NO_WRAP or Base64.URL_SAFE)

    fun parseShared(text: String): MyRecipe? = try {
        val s = text.trim()
        val i = s.indexOf(PREFIX)
        if (i < 0) null else {
            val b64 = s.substring(i + PREFIX.length).takeWhile { !it.isWhitespace() }
            val r = MyRecipe.fromJson(JSONObject(String(Base64.decode(b64, Base64.URL_SAFE), Charsets.UTF_8)))
            r.id = newId()
            r.bgKind = 0
            r
        }
    } catch (e: Exception) {
        null
    }

    fun importBackground(ctx: Context, id: String, uri: android.net.Uri) = OwnPhoto.import(ctx, uri, bgFile(ctx, id))
}

// ───────────────────────── de scène ─────────────────────────

/** Tekent een eigen scène (recept). Wordt ook door de editor gebruikt. */
class MyScene(ctx: Context, d: Float) : Scene(ctx, d) {
    override val name = "Mijn scène"
    override val hint = "Tik op je stickers. Maak je eigen scène via ⋯ → Eigen scène maken"

    /** In de editor bepaalt de editor zelf welk recept getekend wordt. */
    var external = false
    var recipe: MyRecipe? = null
        set(v) { field = v; rev++ }
    private var rev = 0
    private var seenRev = -1
    private var seenVersion = -1

    // runtime-toestand
    private var tapAt = LongArray(0)
    private val flare = HashMap<Int, Long>()
    private val fires = HashMap<Int, FireSim>()
    private val popX = FloatArray(12)
    private val popY = FloatArray(12)
    private val popT = LongArray(12) { -10_000L }
    private var popNext = 0
    private var bubbleIdx = -1
    private var bubbleUntil = 0L

    private val flakes = Flakes(220, 11)
    private val confX = FloatArray(120) { hash(it * 3 + 1) }
    private val confY = FloatArray(120) { hash(it * 3 + 2) }
    private val confV = FloatArray(120) { hash(it * 3 + 3) }

    private val emoji = HashMap<String, Bitmap>()
    private val bmpPaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    private val mat = Matrix()
    private var bg: Bitmap? = null
    private var bgId = ""
    private var bgFileStamp = 0L

    private val bulb = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cap = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF2B3A30.toInt() }
    private val shine = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val wire = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND; color = 0xFF16221B.toInt()
    }
    private val lamp = Path()
    private val ground = Path()

    private class Geom(val xs: FloatArray, val ys: FloatArray, val wire: Path)
    private var geoms: List<Geom> = emptyList()
    private var geomKey = ""

    private fun hash(n: Int): Float {
        var x = n * -1640531535
        x = x xor (x ushr 15)
        x *= 0x2c1b3c6d
        x = x xor (x ushr 12)
        return (x and 0xFFFF) / 65535f
    }

    fun invalidate() { rev++ }

    private fun sync() {
        if (!external && seenVersion != MyScenes.version) {
            seenVersion = MyScenes.version
            recipe = MyScenes.active(ctx)
        }
        if (seenRev != rev) {
            seenRev = rev
            val r = recipe
            if (tapAt.size != (r?.stickers?.size ?: 0)) tapAt = LongArray(r?.stickers?.size ?: 0)
            fires.keys.retainAll((r?.fx?.indices?.toSet()) ?: emptySet())
            geomKey = ""
        }
    }

    private fun emojiBitmap(e: String): Bitmap = emoji.getOrPut(e) {
        val s = 128
        val b = Bitmap.createBitmap(s, s, Bitmap.Config.ARGB_8888)
        val pt = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 100f; textAlign = Paint.Align.CENTER }
        val fm = pt.fontMetrics
        Canvas(b).drawText(e, s / 2f, s / 2f - (fm.ascent + fm.descent) / 2f, pt)
        b
    }

    private fun background(c: Canvas, r: MyRecipe) {
        if (r.bgKind == 1) {
            val f = MyScenes.bgFile(ctx, r.id)
            if (bgId != r.id || bgFileStamp != f.lastModified()) {
                bg?.recycle()
                bg = try { if (f.exists()) BitmapFactory.decodeFile(f.path) else null } catch (e: Exception) { null }
                bgId = r.id
                bgFileStamp = f.lastModified()
            }
            val b = bg
            if (b != null && w > 0f && h > 0f) {
                val s = max(w / b.width, h / b.height)
                mat.setScale(s, s)
                mat.postTranslate((w - b.width * s) / 2f, (h - b.height * s) / 2f)
                c.drawBitmap(b, mat, bmpPaint)
                return
            }
        }
        sky(c, r.bgTop, r.bgBottom)
    }

    override fun draw(c: Canvas, now: Long) {
        sync()
        val r = recipe ?: return
        val t = now / 1000f
        background(c, r)
        for ((i, f) in r.fx.withIndex()) if (f.kind == MyKinds.STARS) stars(c, t * (0.4f + f.speed * 0.12f), 0.6f)
        for ((i, f) in r.fx.withIndex()) {
            when (f.kind) {
                MyKinds.GLOW -> glowFx(c, f, t)
                MyKinds.SHOOTING -> shooting(c, f, t)
            }
        }
        if (r.ground) drawGround(c)
        for ((i, f) in r.fx.withIndex()) if (f.kind == MyKinds.FIRE) fire(c, i, f, now)
        drawStickers(c, r, now, t)
        drawStrings(c, r, t)
        for (f in r.fx) {
            when (f.kind) {
                MyKinds.SNOW -> snowFx(c, f, t)
                MyKinds.FALL -> fallFx(c, f, t)
                MyKinds.CONFETTI -> confettiFx(c, f, t)
                MyKinds.FIREWORKS -> fireworks(c, f, t)
            }
        }
        drawPops(c, now)
        drawBubble(c, r, now)
    }

    // ----- effecten -----

    private fun snowFx(c: Canvas, f: MyFx, t: Float) {
        val n = (10 + f.amount * 2).coerceAtMost(220)
        val sp = 0.4f + f.speed * 0.2f
        p.style = Paint.Style.FILL
        p.color = Color.WHITE
        p.alpha = 210
        for (i in 0 until n) {
            val yy = ((flakes.y[i] + t * sp * (0.03f + flakes.v[i] * 0.05f)) % 1f) * h
            val xx = (flakes.x[i] + 0.015f * sin(t * 0.7f + i)) * w
            c.drawCircle(xx, yy, (0.8f + flakes.v[i] * 1.6f) * d, p)
        }
        p.alpha = 255
    }

    private fun fallFx(c: Canvas, f: MyFx, t: Float) {
        val n = (3 + f.amount / 5).coerceAtMost(24)
        val sp = 0.4f + f.speed * 0.16f
        val bmp = emojiBitmap(f.emoji)
        val size = f.size / 100f * w
        for (i in 0 until n) {
            val y = ((confY[i] + t * sp * (0.05f + confV[i] * 0.05f)) % 1.2f - 0.1f) * h
            val x = (confX[i] + 0.03f * sin(t * 1.1f + i)) * w
            mat.setScale(size / 128f, size / 128f)
            mat.postRotate(sin(t * 1.4f + i) * 25f, size / 2f, size / 2f)
            mat.postTranslate(x - size / 2f, y - size / 2f)
            c.drawBitmap(bmp, mat, bmpPaint)
        }
    }

    private fun confettiFx(c: Canvas, f: MyFx, t: Float) {
        val n = (10 + f.amount).coerceAtMost(110)
        val sp = 0.5f + f.speed * 0.18f
        p.style = Paint.Style.FILL
        for (i in 0 until n) {
            val y = ((confY[i] + t * sp * (0.08f + confV[i] * 0.1f)) % 1f) * h
            val x = (confX[i] + 0.03f * sin(t * 1.3f + i)) * w
            p.color = TreeColors.all[i % TreeColors.all.size]
            c.save()
            c.translate(x, y)
            c.rotate(t * 180f * (confV[i] + 0.3f) + i * 37f)
            c.drawRect(-2.5f * d, -4.5f * d, 2.5f * d, 4.5f * d, p)
            c.restore()
        }
    }

    private fun fireworks(c: Canvas, f: MyFx, t: Float) {
        val period = 2.2f / (0.4f + f.speed * 0.2f)
        val life = period * 1.5f
        val count = 10 + f.amount / 4
        p.style = Paint.Style.FILL
        val b0 = floor(t / period).toInt()
        for (bi in b0 - 1..b0) {
            val age = t - bi * period
            if (age < 0f || age > life) continue
            val k = age / life
            val cx = (0.15f + 0.7f * hash(bi * 7 + 1)) * w
            val cy = (0.1f + 0.35f * hash(bi * 7 + 2)) * h
            val col = if (f.color == 0) TreeColors.all[Math.floorMod(bi, TreeColors.all.size)] else MyLook.swatches[f.color % 8]
            for (i in 0 until count) {
                val a = i * 2f * PI.toFloat() / count + hash(bi) * 6f
                val rr = (0.05f + 0.12f * hash(bi * 7 + 3)) * w * (1f - (1f - k) * (1f - k)) * 1.6f
                val x = cx + cos(a) * rr
                val y = cy + sin(a) * rr + k * k * 0.05f * h
                p.color = col
                p.alpha = (255 * (1f - k) * (0.6f + 0.4f * sin(t * 30f + i))).toInt().coerceIn(0, 255)
                c.drawCircle(x, y, (1.4f + 1.4f * (1f - k)) * d, p)
            }
        }
        p.alpha = 255
    }

    private fun shooting(c: Canvas, f: MyFx, t: Float) {
        val period = 5f / (0.4f + f.speed * 0.2f)
        val bi = floor(t / period).toInt()
        val k = ((t - bi * period) / 1.1f)
        if (k > 1f) return
        val sx = (0.35f + 0.6f * hash(bi * 5 + 1)) * w
        val sy = (0.04f + 0.3f * hash(bi * 5 + 2)) * h
        val dx = -0.4f * w
        val dy = 0.2f * w
        wire.strokeWidth = 1.8f * d
        for (j in 0 until 8) {
            val kk = (k - j * 0.025f).coerceIn(0f, 1f)
            wire.color = Color.argb(((1f - j / 8f) * 255 * (1f - k * 0.6f)).toInt(), 255, 250, 220)
            c.drawPoint(sx + dx * kk, sy + dy * kk, wire)
        }
        wire.color = 0xFF16221B.toInt()
    }

    private fun glowFx(c: Canvas, f: MyFx, t: Float) {
        val fl = 0.8f + 0.2f * sin(t * (1.5f + f.speed * 0.5f)) * sin(t * 2.3f + 1f)
        glow(c, f.x * w, f.y * h, f.size / 100f * w * 1.6f, MyLook.swatches[f.color % 8], (f.amount * 1.6f * fl).toInt())
    }

    private fun fire(c: Canvas, i: Int, f: MyFx, now: Long) {
        val sim = fires.getOrPut(i) { FireSim(240) }
        val pal = MyLook.fireColors[f.color.coerceIn(0, MyLook.fireColors.size - 1)]
        val halfW = f.size / 100f * w * 0.5f
        val height = halfW * (2.6f + f.amount / 100f * 1.6f)
        val fl = ((now - (flare[i] ?: -10_000L)) / 1400f).let { if (it in 0f..1f) 1f - it else 0f }
        // een donker "vuurbed": het vuur wordt additief getekend en moet ook op sneeuw of een lichte lucht zichtbaar zijn
        glow(c, f.x * w, f.y * h - height * 0.28f, height * 0.62f, 0xFF160703.toInt(), 215)
        glow(c, f.x * w, f.y * h - height * 0.3f, height * (0.9f + 0.4f * fl), pal[2], (50 + 60 * fl).toInt())
        sim.draw(c, now, f.x * w, f.y * h, halfW, height * (1f + 0.5f * fl), halfW * 0.9f, 0.85f + 0.3f * fl, f.speed / 5f, pal)
    }

    private fun drawGround(c: Canvas) {
        ground.reset()
        ground.moveTo(0f, h)
        ground.lineTo(0f, h * 0.92f)
        ground.cubicTo(w * 0.25f, h * 0.88f, w * 0.55f, h * 0.95f, w, h * 0.9f)
        ground.lineTo(w, h)
        ground.close()
        p.style = Paint.Style.FILL
        p.color = 0xFFEAF2FF.toInt()
        c.drawPath(ground, p)
        p.color = 0x33000000
        c.drawRect(0f, h * 0.985f, w, h, p)
    }

    // ----- stickers -----

    private fun addPop(x: Float, y: Float, now: Long) {
        popX[popNext] = x; popY[popNext] = y; popT[popNext] = now
        popNext = (popNext + 1) % popT.size
    }

    private fun drawPops(c: Canvas, now: Long) {
        p.style = Paint.Style.STROKE
        p.strokeCap = Paint.Cap.ROUND
        for (i in popT.indices) {
            val k = (now - popT[i]) / 800f
            if (k !in 0f..1f) continue
            p.color = 0xFFFFE27A.toInt()
            p.alpha = (255 * (1f - k)).toInt()
            p.strokeWidth = 2.2f * d
            for (j in 0 until 8) {
                val a = j * PI.toFloat() / 4f
                val r0 = (6f + 22f * k) * d
                val r1 = (12f + 38f * k) * d
                c.drawLine(popX[i] + cos(a) * r0, popY[i] + sin(a) * r0, popX[i] + cos(a) * r1, popY[i] + sin(a) * r1, p)
            }
        }
        p.alpha = 255
        p.style = Paint.Style.FILL
    }

    private fun drawStickers(c: Canvas, r: MyRecipe, now: Long, t: Float) {
        for ((i, s) in r.stickers.withIndex()) {
            var x = s.x * w
            var y = s.y * h
            var rot = s.rot
            val size = s.size * w
            var sc = size / 128f
            var alpha = 255
            when (s.anim) {
                1 -> y += sin(t * 1.6f + i) * 0.012f * h
                2 -> rot += sin(t * 2f + i) * 14f
                3 -> sc *= 1f + 0.12f * sin(t * 3f + i)
                4 -> alpha = (90 + 165 * abs(sin(t * 2.2f + i))).toInt()
                5 -> y -= abs(sin(t * 3f + i)) * 0.04f * h
                6 -> rot += t * 90f
                7 -> x = (((s.x + 0.15f + t * 0.06f) % 1.3f) - 0.15f) * w
                8 -> { x += cos(t * 1.2f + i) * 0.06f * w; y += sin(t * 1.2f + i) * 0.06f * w }
            }
            val since = if (i < tapAt.size) (now - tapAt[i]) / 1000f else 9f
            if (since in 0f..1f) when (s.tap) {
                1 -> y -= sin(since * PI.toFloat()) * 0.08f * h
                3 -> rot += since * 360f
            }
            mat.setScale(sc, sc)
            mat.postRotate(rot, size / 2f, size / 2f)
            mat.postTranslate(x - size / 2f, y - size / 2f)
            bmpPaint.alpha = alpha
            c.drawBitmap(emojiBitmap(s.emoji), mat, bmpPaint)
        }
        bmpPaint.alpha = 255
    }

    private fun drawBubble(c: Canvas, r: MyRecipe, now: Long) {
        if (now > bubbleUntil || bubbleIdx !in r.stickers.indices) return
        val s = r.stickers[bubbleIdx]
        val msg = s.msg.ifBlank { return }
        textPaint.textSize = 14f * d
        textPaint.typeface = android.graphics.Typeface.DEFAULT_BOLD
        val half = msg.length / 2
        val split = if (msg.length > 22) msg.indexOf(' ', half).takeIf { it > 0 } else null
        val lines = if (split != null) listOf(msg.substring(0, split), msg.substring(split + 1)) else listOf(msg)
        val tw = lines.maxOf { textPaint.measureText(it) } + 24f * d
        val th = lines.size * 20f * d + 14f * d
        val cx = (s.x * w).coerceIn(tw / 2f + 4f * d, w - tw / 2f - 4f * d)
        val bottom = s.y * h - s.size * w * 0.55f
        rect.set(cx - tw / 2f, bottom - th, cx + tw / 2f, bottom)
        p.color = Color.WHITE
        c.drawRoundRect(rect, 14f * d, 14f * d, p)
        path.reset()
        path.moveTo(s.x * w - 6f * d, bottom - 1f)
        path.lineTo(s.x * w, bottom + 9f * d)
        path.lineTo(s.x * w + 6f * d, bottom - 1f)
        path.close()
        c.drawPath(path, p)
        for ((k, ln) in lines.withIndex()) {
            textPaint.color = 0xFF2A2A33.toInt()
            c.drawText(ln, cx, rect.top + 10f * d + 15f * d + k * 20f * d - 2f * d, textPaint)
        }
        textPaint.color = Color.WHITE
    }

    // ----- lichtsnoeren -----

    private fun geometry(r: MyRecipe) {
        val key = "$w/$h/$rev"
        if (key == geomKey) return
        geomKey = key
        geoms = r.strings.map { s ->
            val z = (6f + s.size * 1.2f) * d
            val spacing = z * 3.2f
            val n = s.pts.size / 2
            val xs = ArrayList<Float>()
            val ys = ArrayList<Float>()
            val wp = Path()
            var carry = 0f
            for (k in 0 until n) {
                val x = s.pts[k * 2] * w
                val y = s.pts[k * 2 + 1] * h
                if (k == 0) {
                    wp.moveTo(x, y); xs.add(x); ys.add(y)
                    continue
                }
                wp.lineTo(x, y)
                val px = s.pts[k * 2 - 2] * w
                val py = s.pts[k * 2 - 1] * h
                val len = hypot(x - px, y - py)
                var pos = spacing - carry
                while (pos <= len && xs.size < 400) {
                    val u = pos / len
                    xs.add(px + (x - px) * u); ys.add(py + (y - py) * u)
                    pos += spacing
                }
                carry = len - (pos - spacing)
            }
            Geom(xs.toFloatArray(), ys.toFloatArray(), wp)
        }
    }

    private fun brightness(mode: Int, i: Int, n: Int, t: Float, sp: Float): Float = when (mode) {
        Modes.STEADY -> 1f
        Modes.ALTERNATE -> if (((floor(t * sp * 1.5f).toInt() + i) and 1) == 0) 1f else 0.25f
        Modes.CHASE -> {
            val head = (t * sp * 4f) % n
            val dd = ((head - i) % n + n) % n
            max(0.2f, 1f - dd / (n * 0.3f + 1f))
        }
        Modes.WAVE -> 0.25f + 0.75f * (0.5f + 0.5f * sin(i * 0.5f - t * sp * 3f))
        else -> 0.45f + 0.55f * (0.5f + 0.5f * sin(t * sp * 3f + i * 1.7f + i * i * 0.3f))
    }

    private fun drawStrings(c: Canvas, r: MyRecipe, t: Float) {
        geometry(r)
        for ((si, s) in r.strings.withIndex()) {
            val g = geoms.getOrNull(si) ?: continue
            wire.style = Paint.Style.STROKE
            wire.strokeWidth = 1.6f * d
            c.drawPath(g.wire, wire)
            val z = (6f + s.size * 1.2f) * d
            val pal = Palettes.colors[s.palette.coerceIn(0, Palettes.colors.size - 1)]
            val sp = s.speed / 5f
            val n = g.xs.size
            for (i in 0 until n) {
                val k = brightness(s.mode, i, n, t, sp)
                val base = pal[i % pal.size]
                val col = mix(0xFF303030.toInt(), base, k)
                val st = if (s.style == LampStyles.MIX) Math.floorMod(i * 31 + si, LampStyles.BASIC) else s.style
                c.save()
                c.translate(g.xs[i], g.ys[i])
                glow(c, 0f, z * 0.9f, z * 2.3f, base, (150 * k * k).toInt())
                bulbShape(c, z, col, k, st)
                c.restore()
            }
        }
    }

    private fun mix(a: Int, b: Int, k: Float): Int {
        val kk = k.coerceIn(0f, 1f)
        fun ch(sh: Int) = (((a shr sh) and 255) * (1f - kk) + ((b shr sh) and 255) * kk).toInt()
        return Color.argb(255, ch(16), ch(8), ch(0))
    }

    /** Eén lampje aan de draad bij (0,0), in een van de acht vormen. */
    private fun bulbShape(c: Canvas, z: Float, col: Int, k: Float, style: Int) {
        bulb.color = col
        bulb.style = Paint.Style.FILL
        shine.alpha = (60 + 160 * k).toInt()
        when (style) {
            1 -> {
                c.drawRoundRect(-z * 0.12f, -z * 0.05f, z * 0.12f, z * 0.32f, z * 0.05f, z * 0.05f, cap)
                c.drawCircle(0f, z * 0.62f, z * 0.34f, bulb)
                c.drawCircle(-z * 0.1f, z * 0.52f, z * 0.08f, shine)
            }
            2 -> {
                c.drawRoundRect(-z * 0.22f, -z * 0.05f, z * 0.22f, z * 0.38f, z * 0.08f, z * 0.08f, cap)
                c.drawCircle(0f, z * 1.02f, z * 0.66f, bulb)
                c.drawOval(-z * 0.42f, z * 0.68f, -z * 0.12f, z * 1.12f, shine)
            }
            3 -> {
                c.drawRoundRect(-z * 0.16f, -z * 0.05f, z * 0.16f, z * 0.42f, z * 0.06f, z * 0.06f, cap)
                lamp.reset()
                lamp.moveTo(0f, z * 1.9f)
                lamp.cubicTo(z * 0.62f, z * 1.25f, z * 0.5f, z * 0.6f, 0f, z * 0.42f)
                lamp.cubicTo(-z * 0.5f, z * 0.6f, -z * 0.62f, z * 1.25f, 0f, z * 1.9f)
                lamp.close()
                c.drawPath(lamp, bulb)
                c.drawOval(-z * 0.2f, z * 0.8f, -z * 0.06f, z * 1.25f, shine)
            }
            4 -> {
                c.drawRoundRect(-z * 0.1f, -z * 0.05f, z * 0.1f, z * 0.3f, z * 0.05f, z * 0.05f, cap)
                lamp.reset()
                for (i in 0 until 10) {
                    val r = if (i % 2 == 0) z * 0.78f else z * 0.34f
                    val a = (-PI / 2 + i * PI / 5).toFloat()
                    val x = r * cos(a); val y = z * 1.05f + r * sin(a)
                    if (i == 0) lamp.moveTo(x, y) else lamp.lineTo(x, y)
                }
                lamp.close()
                c.drawPath(lamp, bulb)
                c.drawCircle(-z * 0.08f, z * 0.95f, z * 0.08f, shine)
            }
            5 -> {
                c.drawRoundRect(-z * 0.1f, -z * 0.05f, z * 0.1f, z * 0.3f, z * 0.05f, z * 0.05f, cap)
                val r = z * 0.62f
                val cy = z * 1.0f
                lamp.reset()
                lamp.moveTo(0f, cy + r * 0.95f)
                lamp.cubicTo(-r * 1.4f, cy + r * 0.05f, -r * 0.8f, cy - r * 0.95f, 0f, cy - r * 0.35f)
                lamp.cubicTo(r * 0.8f, cy - r * 0.95f, r * 1.4f, cy + r * 0.05f, 0f, cy + r * 0.95f)
                lamp.close()
                c.drawPath(lamp, bulb)
                c.drawCircle(-r * 0.45f, cy - r * 0.15f, r * 0.13f, shine)
            }
            6 -> {
                c.drawRoundRect(-z * 0.14f, -z * 0.05f, z * 0.14f, z * 0.3f, z * 0.05f, z * 0.05f, cap)
                lamp.reset()
                lamp.moveTo(-z * 0.3f, z * 0.3f)
                lamp.lineTo(z * 0.3f, z * 0.3f)
                lamp.lineTo(z * 0.06f, z * 2.1f)
                lamp.lineTo(0f, z * 2.25f)
                lamp.lineTo(-z * 0.06f, z * 2.1f)
                lamp.close()
                c.drawPath(lamp, bulb)
                c.drawRect(-z * 0.2f, z * 0.45f, -z * 0.1f, z * 1.2f, shine)
            }
            7 -> {
                c.drawRoundRect(-z * 0.1f, -z * 0.05f, z * 0.1f, z * 0.3f, z * 0.05f, z * 0.05f, cap)
                val cy = z * 1.05f
                val r = z * 0.72f
                bulb.style = Paint.Style.STROKE
                bulb.strokeWidth = z * 0.14f
                bulb.strokeCap = Paint.Cap.ROUND
                for (i in 0 until 3) {
                    val a = (i * PI / 3).toFloat()
                    c.drawLine(-r * cos(a), cy - r * sin(a), r * cos(a), cy + r * sin(a), bulb)
                }
                bulb.style = Paint.Style.FILL
                c.drawCircle(0f, cy, z * 0.16f, shine)
            }
            else -> {
                c.drawRoundRect(-z * 0.2f, -z * 0.05f, z * 0.2f, z * 0.5f, z * 0.08f, z * 0.08f, cap)
                c.drawOval(-z * 0.5f, z * 0.45f, z * 0.5f, z * 1.65f, bulb)
                c.drawOval(-z * 0.26f, z * 0.62f, -z * 0.08f, z * 1.12f, shine)
            }
        }
    }

    // ----- aanraken -----

    override fun tap(x: Float, y: Float, now: Long): Boolean {
        val r = recipe ?: return false
        for (i in r.stickers.indices.reversed()) {
            val s = r.stickers[i]
            if (hypot(x - s.x * w, y - s.y * h) > max(s.size * w * 0.6f, 24f * d)) continue
            if (i < tapAt.size) tapAt[i] = now
            when (s.tap) {
                2 -> addPop(s.x * w, s.y * h, now)
                4 -> { Sound.ding(); addPop(s.x * w, s.y * h, now) }
                MyLook.TAP_SAY -> { bubbleIdx = i; bubbleUntil = now + 2800L }
            }
            return true
        }
        for ((i, f) in r.fx.withIndex()) {
            if (f.kind == MyKinds.FIRE && hypot(x - f.x * w, y - f.y * h + f.size / 100f * w) < f.size / 100f * w) {
                flare[i] = now
                return true
            }
        }
        addPop(x, y, now)
        return true
    }
}
