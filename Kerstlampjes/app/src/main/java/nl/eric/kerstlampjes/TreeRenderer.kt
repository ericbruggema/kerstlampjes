package nl.eric.kerstlampjes

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

object TreeColors {
    val all = intArrayOf(
        0xFFFF3B30.toInt(), 0xFFFFD54F.toInt(), 0xFF4CD964.toInt(), 0xFF40A9FF.toInt(),
        0xFFFF9500.toInt(), 0xFFFF4FD8.toInt(), 0xFFFFEBC4.toInt()
    )
}

/** Uiterlijk van een boom: lagen (takken), kleuren en schaduw. */
class TreeStyle(
    val name: String,
    val tiers: Int,
    val apexStep: Float,    // afstand tussen toppen van de lagen (fractie van boomhoogte)
    val tierH: Float,       // hoogte van één laag
    val halfBase: Float,    // halve breedte van bovenste laag (fractie van schermbreedte)
    val halfStep: Float,    // extra halve breedte per laag
    val topColor: Int,
    val bottomColor: Int,
    val shading: Int,       // 0 = vlak verloop, 1 = 3D (twee vlakken), 2 = besneeuwd, 3 = strepen, 4 = neon
    val scallops: Int,      // 0 = rustig gebogen onderkant, >0 = golfjes, <0 = zigzag
    val rows: FloatArray,   // hoogtes (fractie van een laag) waar "Versier" snoeren hangt
    val shape: Int = 0,     // 0 = driehoek, 1 = ovaal
    val bgTop: Int = 0xFF08142A.toInt(),
    val bgBottom: Int = 0xFF0F2A1E.toInt(),
    val trunk: Int = 0xFF5B3A22.toInt(),
    val star: Int = 0xFFFFD54F.toInt(),
    val photo: Int = -1,    // >= 0: echte boom, index in Credits.trees
    val cxFrac: Float = 0.5f,
    val topFrac: Float = 0.14f,
    val hFrac: Float = 0.66f
)

object TreeStyles {
    /** Aantal scènes: alle bomen + de open haard. */
    val TREES get() = all.size
    val COUNT get() = all.size + HearthStyles.all.size + SceneHost.COUNT
    /** 0 = bomen, 1 = haarden */
    fun categoryOf(i: Int) = if (i < all.size) 0 else if (i < all.size + HearthStyles.all.size) 1 else 2
    fun nameOf(i: Int) = when (categoryOf(i)) {
        0 -> all[i].name
        1 -> HearthStyles.all[(i - all.size).coerceIn(0, HearthStyles.all.size - 1)].name
        else -> SceneHost.names[(i - all.size - HearthStyles.all.size).coerceIn(0, SceneHost.COUNT - 1)]
    }

    val all = arrayOf(
        TreeStyle("Klassiek", 3, 0.27f, 0.46f, 0.26f, 0.10f, 0xFF2E8B4F.toInt(), 0xFF14572F.toInt(), 0, 0, floatArrayOf(0.5f, 0.82f)),
        TreeStyle("Dennenboom", 5, 0.17f, 0.30f, 0.15f, 0.065f, 0xFF2B7A5E.toInt(), 0xFF0D3F36.toInt(), 0, 3, floatArrayOf(0.55f, 0.88f)),
        TreeStyle("Cartoon 3D", 4, 0.21f, 0.36f, 0.20f, 0.08f, 0xFF48C774.toInt(), 0xFF176B3B.toInt(), 1, 0, floatArrayOf(0.5f, 0.85f)),
        TreeStyle("Besneeuwd", 3, 0.27f, 0.44f, 0.24f, 0.11f, 0xFF3F8F8A.toInt(), 0xFF1B5A5C.toInt(), 2, 4, floatArrayOf(0.55f, 0.85f)),
        TreeStyle("Rond & zacht", 3, 0.22f, 0.40f, 0.17f, 0.10f, 0xFF6BD36F.toInt(), 0xFF2E9B4A.toInt(), 0, 0, floatArrayOf(0.4f, 0.72f), shape = 1),
        TreeStyle("IJsboom", 4, 0.21f, 0.36f, 0.19f, 0.08f, 0xFFBFEAFF.toInt(), 0xFF4A8FD0.toInt(), 1, 0, floatArrayOf(0.5f, 0.85f),
            bgTop = 0xFF031530.toInt(), bgBottom = 0xFF0B3556.toInt(), trunk = 0xFF3A4E66.toInt(), star = 0xFFE6F6FF.toInt()),
        TreeStyle("Zuurstok", 4, 0.19f, 0.34f, 0.15f, 0.08f, 0xFFFF9EC4.toInt(), 0xFFD6336C.toInt(), 3, 0, floatArrayOf(0.4f, 0.7f), shape = 1,
            bgTop = 0xFF2A0F3A.toInt(), bgBottom = 0xFF3F1B4D.toInt(), trunk = 0xFFEFE6D8.toInt(), star = 0xFFFFFFFF.toInt()),
        TreeStyle("Goud", 4, 0.21f, 0.36f, 0.19f, 0.08f, 0xFFFFE27A.toInt(), 0xFFB07A10.toInt(), 0, 3, floatArrayOf(0.55f, 0.87f),
            bgTop = 0xFF110A05.toInt(), bgBottom = 0xFF2A1A0A.toInt(), trunk = 0xFF4A2F18.toInt(), star = 0xFFFFFFFF.toInt()),
        TreeStyle("Paarse nacht", 3, 0.27f, 0.46f, 0.25f, 0.10f, 0xFFB57BFF.toInt(), 0xFF5A2BA8.toInt(), 1, 0, floatArrayOf(0.5f, 0.82f),
            bgTop = 0xFF0A0624.toInt(), bgBottom = 0xFF1D0F3F.toInt(), trunk = 0xFF3B2A52.toInt(), star = 0xFFFFC2F0.toInt()),
        TreeStyle("Zigzag", 4, 0.21f, 0.36f, 0.19f, 0.08f, 0xFF3FD0C0.toInt(), 0xFF157A78.toInt(), 0, -6, floatArrayOf(0.5f, 0.85f),
            bgTop = 0xFF05202A.toInt(), bgBottom = 0xFF0B3A3A.toInt()),
        TreeStyle("Neon", 3, 0.27f, 0.46f, 0.25f, 0.10f, 0xFF29F1FF.toInt(), 0xFF29F1FF.toInt(), 4, 0, floatArrayOf(0.5f, 0.82f),
            bgTop = 0xFF000000.toInt(), bgBottom = 0xFF05050F.toInt(), trunk = 0xFF0B3A44.toInt(), star = 0xFFFF4FD8.toInt()),
        TreeStyle("Rode boom", 3, 0.27f, 0.46f, 0.25f, 0.10f, 0xFFFF6B5E.toInt(), 0xFFA3141F.toInt(), 1, 0, floatArrayOf(0.5f, 0.82f),
            bgTop = 0xFF120B1E.toInt(), bgBottom = 0xFF2A1428.toInt(), trunk = 0xFF3A2A2A.toInt(), star = 0xFFFFE27A.toInt()),
        TreeStyle("Bollenboom", 5, 0.17f, 0.28f, 0.11f, 0.07f, 0xFFB6F25C.toInt(), 0xFF58B31F.toInt(), 0, 0, floatArrayOf(0.5f, 0.78f), shape = 1,
            bgTop = 0xFF0A1F2E.toInt(), bgBottom = 0xFF15402F.toInt()),
        TreeStyle("Zonsondergang", 3, 0.27f, 0.46f, 0.25f, 0.10f, 0xFFFFB347.toInt(), 0xFFD6336C.toInt(), 0, 0, floatArrayOf(0.5f, 0.82f),
            bgTop = 0xFF2B1055.toInt(), bgBottom = 0xFFB4456A.toInt(), trunk = 0xFF3A1D2A.toInt(), star = 0xFFFFF3B0.toInt()),
        // echte bomen (foto's, zie Credits.trees); de kegel is onzichtbaar en bepaalt waar lampjes mogen hangen
        TreeStyle("Echt: Capitool", 1, 0f, 1f, 0.30f, 0f, 0, 0, 0, 0, floatArrayOf(0.2f, 0.4f, 0.6f, 0.8f),
            photo = 0, cxFrac = 0.66f, topFrac = 0.40f, hFrac = 0.56f),
        TreeStyle("Echt: Binnenboom", 1, 0f, 1f, 0.42f, 0f, 0, 0, 0, 0, floatArrayOf(0.2f, 0.4f, 0.6f, 0.8f),
            photo = 1, cxFrac = 0.50f, topFrac = 0.03f, hFrac = 0.88f),
        TreeStyle("Echt: Spar in het park", 1, 0f, 1f, 0.36f, 0f, 0, 0, 0, 0, floatArrayOf(0.2f, 0.4f, 0.6f, 0.8f),
            photo = 2, cxFrac = 0.57f, topFrac = 0.06f, hFrac = 0.78f),
        TreeStyle("Echt: Stadsboom", 1, 0f, 1f, 0.35f, 0f, 0, 0, 0, 0, floatArrayOf(0.2f, 0.4f, 0.6f, 0.8f),
            photo = 3, cxFrac = 0.53f, topFrac = 0.25f, hFrac = 0.61f),
        TreeStyle("Echt: Lichtjesboom", 1, 0f, 1f, 0.33f, 0f, 0, 0, 0, 0, floatArrayOf(0.2f, 0.4f, 0.6f, 0.8f),
            photo = 4, cxFrac = 0.50f, topFrac = 0.27f, hFrac = 0.52f),
        // je eigen foto: lampjes mogen overal hangen
        TreeStyle("Eigen foto", 1, 0f, 1f, 0.47f, 0f, 0, 0, 0, 0, floatArrayOf(0.5f),
            photo = -2, cxFrac = 0.5f, topFrac = 0f, hFrac = 1f)
    )
}

/** Opgeslagen boom: lampjes per boomstijl + effect-instellingen. */
object TreeStore {
    private const val FILE = "kerstlampjes"

    fun load(ctx: Context, r: TreeRenderer) {
        val p = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        r.mode = p.getInt("tree_mode", Modes.TWINKLE)
        r.speed = p.getInt("tree_speed", 5)
        r.orient = p.getInt("tree_orient", 0)
        r.style = p.getInt("tree_style", 0)
        loadBulbs(p, r)
    }

    /** Sleutel op naam, zodat het toevoegen van nieuwe bomen de opgeslagen lampjes niet door elkaar haalt. */
    private fun bulbKey(style: Int) = "tree_bulbs_n_" + TreeStyles.nameOf(style)

    private fun loadBulbs(p: android.content.SharedPreferences, r: TreeRenderer) {
        r.bulbs.clear()
        val raw = p.getString(bulbKey(r.style), null)
            ?: if (r.style == 0) p.getString("tree_bulbs_0", null) ?: p.getString("tree_bulbs", null) else null
        if (raw == null) {
            r.autoDecorate()
            return
        }
        raw.split(';').forEach { part ->
            val f = part.split(',')
            if (f.size == 3) {
                val x = f[0].toFloatOrNull(); val y = f[1].toFloatOrNull(); val c = f[2].toIntOrNull()
                if (x != null && y != null && c != null) {
                    r.bulbs.add(TreeRenderer.Bulb(x, y, c.coerceIn(0, TreeColors.all.size - 1)))
                }
            }
        }
    }

    fun save(ctx: Context, r: TreeRenderer) {
        val s = r.bulbs.joinToString(";") {
            String.format(Locale.US, "%.4f,%.4f,%d", it.x, it.y, it.color)
        }
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit()
            .putString(bulbKey(r.style), s)
            .putInt("tree_style", r.style)
            .putInt("last_cat_${r.category}", r.style)
            .putInt("tree_mode", r.mode)
            .putInt("tree_speed", r.speed)
            .putInt("tree_orient", r.orient)
            .apply()
    }

    /** Spring direct naar een stijl (bijv. de scène van vandaag). */
    fun jump(ctx: Context, r: TreeRenderer, style: Int) {
        save(ctx, r)
        r.style = style
        loadBulbs(ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE), r)
        save(ctx, r)
    }

    fun rangeStart(cat: Int) = when (cat) { 0 -> 0; 1 -> TreeStyles.TREES; else -> TreeStyles.TREES + HearthStyles.all.size }
    fun rangeLen(cat: Int) = when (cat) { 0 -> TreeStyles.TREES; 1 -> HearthStyles.all.size; else -> SceneHost.COUNT }

    /** Sla de huidige scène op en ga naar de volgende/vorige binnen dezelfde categorie. */
    fun switchStyle(ctx: Context, r: TreeRenderer, delta: Int): Int {
        save(ctx, r)
        val start = TreeStore.rangeStart(r.category)
        val len = TreeStore.rangeLen(r.category)
        r.style = start + Math.floorMod(r.style - start + delta, len)
        loadBulbs(ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE), r)
        save(ctx, r)
        return r.style
    }

    /** Wissel tussen bomen (0) en haarden (1); onthoudt de laatst gekozen scène per categorie. */
    fun selectCategory(ctx: Context, r: TreeRenderer, cat: Int): Int {
        if (r.category == cat) return r.style
        save(ctx, r)
        val p = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val fallback = rangeStart(cat)
        val target = p.getInt("last_cat_$cat", fallback)
        r.style = if (TreeStyles.categoryOf(target) == cat) target else fallback
        loadBulbs(p, r)
        save(ctx, r)
        return r.style
    }
}

/** Speciale dagen: de boom doet dan iets extra's. */
object TreeEggs {
    const val SINTERKLAAS = 1
    const val KERSTAVOND = 2
    const val KERSTMORGEN = 3
    const val KERSTDAG = 4
    const val TWEEDE_KERSTDAG = 5
    const val OUDEJAARSAVOND = 6
    const val NIEUWJAAR = 7
    const val DRIEKONINGEN = 8
    const val COUNT = 8

    /** Welke speciale dag is het nu? 0 = geen. */
    fun today(): Int {
        val cal = java.util.Calendar.getInstance()
        val m = cal.get(java.util.Calendar.MONTH) + 1
        val d = cal.get(java.util.Calendar.DAY_OF_MONTH)
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            m == 12 && (d == 5 || d == 6) -> SINTERKLAAS
            m == 12 && d == 24 -> KERSTAVOND
            m == 12 && d == 25 -> if (hour < 12) KERSTMORGEN else KERSTDAG
            m == 12 && d == 26 -> TWEEDE_KERSTDAG
            m == 12 && d == 31 -> OUDEJAARSAVOND
            m == 1 && d == 1 -> NIEUWJAAR
            m == 1 && d == 6 -> DRIEKONINGEN
            else -> 0
        }
    }

    fun banner(egg: Int, preview: Boolean): String? {
        val cal = java.util.Calendar.getInstance()
        val text = when (egg) {
            SINTERKLAAS ->
                if (cal.get(java.util.Calendar.DAY_OF_MONTH) == 5) "🎁 Pakjesavond! Is je schoen al gezet?"
                else "🎁 Vrolijk Sinterklaasfeest!"
            KERSTAVOND -> "✨ Kerstavond — stille nacht, heilige nacht"
            KERSTMORGEN -> "🎅 Goedemorgen! Vrolijk kerstfeest — zijn de cadeautjes al open?"
            KERSTDAG -> "🎄 Eerste kerstdag — vrolijk kerstfeest!"
            TWEEDE_KERSTDAG -> "🎄 Tweede kerstdag — geniet van de rust"
            OUDEJAARSAVOND -> {
                val end = java.util.Calendar.getInstance().apply {
                    add(java.util.Calendar.DAY_OF_YEAR, 1)
                    set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0); set(java.util.Calendar.MILLISECOND, 0)
                }
                val left = ((end.timeInMillis - System.currentTimeMillis()) / 1000).coerceAtLeast(0)
                String.format(Locale.US, "🎆 Nog %02d:%02d:%02d tot het nieuwe jaar", left / 3600, left % 3600 / 60, left % 60)
            }
            NIEUWJAAR -> {
                val y = cal.get(java.util.Calendar.YEAR)
                "🥳 Gelukkig nieuwjaar $y!"
            }
            DRIEKONINGEN -> "⭐ Driekoningen — volg de ster"
            else -> return null
        }
        return if (preview) "$text  (voorbeeld)" else text
    }
}

/**
 * Tekent de kerstboom met lampjes. Wordt gedeeld door de app (TreeView)
 * en de live wallpaper. Lampjes staan als fractie van de breedte/hoogte opgeslagen.
 */
class TreeRenderer(private val density: Float, private val ctx: Context? = null) {

    class Bulb(var x: Float, var y: Float, var color: Int) {
        val phase = Random.nextFloat() * 2f * PI.toFloat()
        val freq = 0.35f + Random.nextFloat() * 0.9f
        var pulseStart = -10_000L
    }

    val bulbs = ArrayList<Bulb>()
    var mode = Modes.TWINKLE
    var speed = 5
    /** 0 = omlaag, 1 = omhoog, 2 = willekeurig */
    var orient = 0

    private var w = 0f
    private var h = 0f
    private val bulbR get() = 7f * density
    private val hitR get() = 24f * density
    private val sz get() = bulbR * 1.9f              // grootte van het lampje
    private val hang get() = sz * 1.0f               // afstand ophangpunt → midden glas

    /** +1 = lampje hangt omlaag, -1 = wijst omhoog. Willekeurig is afgeleid van de x-positie (blijft stabiel). */
    private fun dirOf(x: Float): Float = when (orient) {
        1 -> -1f
        2 -> if (((Math.round(x * 10000f) * 2654435761L) ushr 13) and 1L == 1L) -1f else 1f
        else -> 1f
    }

    private val bgPaint = Paint()
    private val starsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val treePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val trunkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF5B3A22.toInt() }
    private val bulbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val capPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF2B3A30.toInt() }
    private val shinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFFD54F.toInt() }
    private val wirePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0xFF0A2214.toInt()
        strokeCap = Paint.Cap.ROUND
    }
    private val wirePath = Path()
    private val glassPath = Path()
    private val tierPath = Path()
    private val starPath = Path()
    private var glow: Array<Shader> = emptyArray()
    private var bgStars = FloatArray(0)

    // boomgeometrie (pixels)
    private var cx = 0f
    private var top = 0f
    private var treeH = 0f
    private val apex = FloatArray(8)
    private val base = FloatArray(8)
    private val half = FloatArray(8)
    private var n = 3
    private val spec get() = TreeStyles.all[style.coerceIn(0, TreeStyles.all.size - 1)]

    var style = 0
        set(v) {
            field = Math.floorMod(v, TreeStyles.COUNT)
            layout()
        }

    /** Halve breedte van laag k op hoogte y (0 als y buiten de laag valt). */
    private fun tierHalf(k: Int, y: Float): Float {
        if (y < apex[k] || y > base[k]) return 0f
        return if (spec.shape == 1) {
            val cy = (apex[k] + base[k]) / 2f
            val hh = (base[k] - apex[k]) / 2f
            val t = (y - cy) / hh
            half[k] * kotlin.math.sqrt(max(0f, 1f - t * t))
        } else {
            half[k] * (y - apex[k]) / (base[k] - apex[k])
        }
    }

    private fun layout() {
        if (w <= 0f || h <= 0f) return
        if (isFireplace) {
            n = 0
            cx = w / 2f
            val hs = HearthStyles.all[(style - TreeStyles.all.size).coerceIn(0, HearthStyles.all.size - 1)]
            bgPaint.shader = LinearGradient(0f, 0f, 0f, h, hs.bgTop, hs.bgBottom, Shader.TileMode.CLAMP)
            roomGlow = RadialGradient(
                cx, h * 0.72f, w * 0.95f,
                intArrayOf(0xAAFF7A1F.toInt(), 0x33FF4A1C.toInt(), 0x00000000),
                floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP
            )
            innerGlow = RadialGradient(
                cx, h * 0.80f, w * 0.34f,
                intArrayOf(0xFFFF9A3A.toInt(), 0x00FF4A1C.toInt()),
                null, Shader.TileMode.CLAMP
            )
            return
        }
        val sp = spec
        n = sp.tiers
        cx = w * sp.cxFrac
        top = h * sp.topFrac
        treeH = h * sp.hFrac
        for (k in 0 until n) {
            apex[k] = top + k * treeH * sp.apexStep
            base[k] = apex[k] + treeH * sp.tierH
            half[k] = minOf(w * (sp.halfBase + sp.halfStep * k), w * 0.47f)
        }
        bgPaint.shader = LinearGradient(0f, 0f, 0f, h, sp.bgTop, sp.bgBottom, Shader.TileMode.CLAMP)
    }

    fun resize(width: Int, height: Int) {
        w = width.toFloat(); h = height.toFloat()
        if (w <= 0f || h <= 0f) return
        layout()
        val r = bulbR * 3.2f
        glow = Array(TreeColors.all.size) { i ->
            val c = TreeColors.all[i]
            RadialGradient(
                0f, 0f, r,
                intArrayOf(alpha(c, 190), alpha(c, 60), alpha(c, 0)),
                floatArrayOf(0f, 0.4f, 1f), Shader.TileMode.CLAMP
            )
        }
        wirePaint.strokeWidth = maxOf(1.5f * density, sz * 0.1f)
        val z = sz
        val bw = z * 0.46f
        val gTop = z * 0.4f
        val bh = z * 1.35f
        glassPath.reset()
        glassPath.moveTo(0f, gTop)
        glassPath.cubicTo(bw * 1.25f, gTop, bw * 1.15f, gTop + bh * 0.62f, 0f, gTop + bh)
        glassPath.cubicTo(-bw * 1.15f, gTop + bh * 0.62f, -bw * 1.25f, gTop, 0f, gTop)
        glassPath.close()
        val rnd = Random(7)
        bgStars = FloatArray(60) { rnd.nextFloat() }
        val rnd2 = Random(11)
        sx = FloatArray(70) { rnd2.nextFloat() }
        sy = FloatArray(70) { rnd2.nextFloat() }
        sv = FloatArray(70) { rnd2.nextFloat() }
        sunPaint.shader = LinearGradient(
            0f, 0f, 0f, h,
            intArrayOf(0xFF24336E.toInt(), 0xFFD9648F.toInt(), 0xFFFFB86B.toInt()),
            floatArrayOf(0f, 0.65f, 1f), Shader.TileMode.CLAMP
        )
    }

    // ---------- easter eggs ----------

    /** -1 = volg de datum; 1..COUNT = toon die speciale dag (tik op de ster). */
    var eggOverride = -1
    private var eggCache = 0
    private var eggCheckedAt = Long.MIN_VALUE
    private val eggPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val sunPaint = Paint()
    private var sx = FloatArray(0)
    private var sy = FloatArray(0)
    private var sv = FloatArray(0)

    fun currentEgg(nowMs: Long): Int {
        if (eggOverride > 0) return eggOverride
        if (eggCheckedAt == Long.MIN_VALUE || nowMs - eggCheckedAt > 20_000L) {
            eggCache = TreeEggs.today()
            eggCheckedAt = nowMs
        }
        return eggCache
    }

    fun banner(nowMs: Long): String? = TreeEggs.banner(currentEgg(nowMs), eggOverride > 0)

    fun cycleEgg() {
        eggOverride = if (eggOverride == -1) 1 else if (eggOverride >= TreeEggs.COUNT) -1 else eggOverride + 1
    }

    fun hitStar(px: Float, py: Float): Boolean {
        if (w <= 0f || isFireplace || spec.photo != -1) return false
        val sr = w * 0.075f
        val dx = px - cx
        val dy = py - (apex[0] + sr * 0.2f)
        return dx * dx + dy * dy <= (sr * 1.6f) * (sr * 1.6f)
    }

    private fun hasSnow(e: Int) = e == TreeEggs.KERSTAVOND || e == TreeEggs.KERSTDAG || e == TreeEggs.TWEEDE_KERSTDAG
    private fun hasPresents(e: Int) =
        e == TreeEggs.SINTERKLAAS || e == TreeEggs.KERSTAVOND || e == TreeEggs.KERSTMORGEN || e == TreeEggs.KERSTDAG

    private fun drawFireworks(c: Canvas, t: Float) {
        for (b in 0 until 5) {
            val tt = t + b * 0.7f
            val cyc = kotlin.math.floor(tt / 3.4f).toInt()
            val ph = (tt % 3.4f) / 3.4f
            if (ph > 0.75f) continue
            val pp = ph / 0.75f
            val rnd = Random(cyc * 31 + b)
            val px = (0.12f + 0.76f * rnd.nextFloat()) * w
            val py = (0.1f + 0.32f * rnd.nextFloat()) * h
            val color = TreeColors.all[rnd.nextInt(TreeColors.all.size)]
            val reach = (0.07f + 0.09f * rnd.nextFloat()) * w
            val e = 1f - (1f - pp) * (1f - pp)
            eggPaint.color = color
            for (i in 0 until 20) {
                val a = (i / 20f * 2f * PI).toFloat()
                val x = px + cos(a) * reach * e
                val y = py + sin(a) * reach * e + pp * pp * 0.05f * h
                eggPaint.alpha = (255 * (1f - pp)).toInt().coerceIn(0, 255)
                c.drawCircle(x, y, 2.4f * density * (1f - pp * 0.5f), eggPaint)
            }
        }
    }

    private fun drawShootingStar(c: Canvas, t: Float) {
        val ph = (t % 5f) / 5f
        if (ph > 0.3f) return
        val p = ph / 0.3f
        val dx = -w * 0.7f
        val dy = h * 0.22f
        val hx = w * 0.95f + dx * p
        val hy = h * 0.06f + dy * p
        eggPaint.color = Color.WHITE
        eggPaint.alpha = (255 * (1f - p)).toInt().coerceIn(0, 255)
        eggPaint.style = Paint.Style.STROKE
        eggPaint.strokeWidth = 2.5f * density
        eggPaint.strokeCap = Paint.Cap.ROUND
        c.drawLine(hx - dx * 0.14f, hy - dy * 0.14f, hx, hy, eggPaint)
        eggPaint.style = Paint.Style.FILL
        c.drawCircle(hx, hy, 3.2f * density, eggPaint)
    }

    private fun drawPresents(c: Canvas) {
        val ground = if (isFireplace) h * 0.90f else base[n - 1] + h * 0.045f
        val boxes = arrayOf(
            floatArrayOf(-0.31f, 0.13f, 0.075f), floatArrayOf(-0.17f, 0.09f, 0.05f),
            floatArrayOf(0.15f, 0.11f, 0.085f), floatArrayOf(0.30f, 0.09f, 0.055f)
        )
        val fills = intArrayOf(0xFFD7263D.toInt(), 0xFF2E86DE.toInt(), 0xFF1E9E57.toInt(), 0xFFF4B400.toInt())
        val ribbons = intArrayOf(0xFFFFE27A.toInt(), 0xFFFFFFFF.toInt(), 0xFFFFD1DC.toInt(), 0xFFD7263D.toInt())
        for (i in boxes.indices) {
            val bx = cx + boxes[i][0] * w
            val bw = boxes[i][1] * w
            val bh = boxes[i][2] * h
            eggPaint.color = fills[i]
            c.drawRoundRect(bx - bw / 2, ground - bh, bx + bw / 2, ground, bw * 0.06f, bw * 0.06f, eggPaint)
            eggPaint.color = ribbons[i]
            c.drawRect(bx - bw * 0.07f, ground - bh, bx + bw * 0.07f, ground, eggPaint)
            c.drawRect(bx - bw / 2, ground - bh * 0.55f, bx + bw / 2, ground - bh * 0.4f, eggPaint)
            c.drawCircle(bx - bw * 0.1f, ground - bh - bw * 0.05f, bw * 0.1f, eggPaint)
            c.drawCircle(bx + bw * 0.1f, ground - bh - bw * 0.05f, bw * 0.1f, eggPaint)
        }
    }

    private fun drawSnow(c: Canvas, t: Float) {
        eggPaint.color = Color.WHITE
        for (i in sx.indices) {
            val spd = 0.03f + sv[i] * 0.05f
            val y = ((sy[i] + t * spd) % 1f) * h
            val x = (sx[i] + 0.015f * sin(t * 0.7f + i)) * w
            eggPaint.alpha = 210
            c.drawCircle(x, y, (0.8f + sv[i] * 1.6f) * density, eggPaint)
        }
    }

    private fun drawConfetti(c: Canvas, t: Float) {
        for (i in 0 until 50) {
            val y = ((sy[i] + t * (0.08f + sv[i] * 0.1f)) % 1f) * h
            val x = (sx[i] + 0.03f * sin(t * 1.3f + i)) * w
            eggPaint.color = TreeColors.all[i % TreeColors.all.size]
            c.save()
            c.translate(x, y)
            c.rotate(t * 180f * (sv[i] + 0.3f) + i * 37f)
            c.drawRect(-2.5f * density, -4.5f * density, 2.5f * density, 4.5f * density, eggPaint)
            c.restore()
        }
    }

    // ---------- open haard ----------

    val isOwnPhoto get() = !isFireplace && !isScene && spec.photo == -2
    val isRealTree get() = !isFireplace && !isScene && spec.photo >= 0
    fun reloadOwn() { if (treeBmpIdx == -2) { treeBmp?.recycle(); treeBmp = null; treeBmpIdx = -3 } }

    val isFireplace get() = style >= TreeStyles.TREES && style < TreeStyles.TREES + HearthStyles.all.size
    val isScene get() = style >= TreeStyles.TREES + HearthStyles.all.size
    val sceneIndex get() = (style - TreeStyles.TREES - HearthStyles.all.size).coerceIn(0, SceneHost.COUNT - 1)
    val isPhotoHearth get() = isFireplace && hearthKind >= HearthStyles.PHOTO
    val category get() = TreeStyles.categoryOf(style)
    private val hearthKind get() = HearthStyles.all[(style - TreeStyles.all.size).coerceIn(0, HearthStyles.all.size - 1)].kind
    private val painter = HearthPainter(density, ctx)

    private var flareStart = -10_000L
    private val fireGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val brickPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val flamePath = Path()
    private val openingPath = Path()
    private var roomGlow: Shader? = null
    private var innerGlow: Shader? = null

    private val openL get() = cx - 0.26f * w
    private val openR get() = cx + 0.26f * w
    private val openT get() = h * 0.43f
    private val openB get() = h * 0.80f
    private val mantelTop get() = h * 0.30f
    private val mantelBottom get() = h * 0.345f

    fun flare(nowMs: Long) { flareStart = nowMs }

    fun hitFire(px: Float, py: Float): Boolean =
        isFireplace && w > 0f && (if (hearthKind == HearthStyles.CARTOON) px in openL..openR && py in openT..openB
            else painter.fireRect(hearthKind, w, h).contains(px, py))

    private fun drawFireplace(c: Canvas, t: Float, nowMs: Long) {
        val sp = speed / 5f
        val fl = ((nowMs - flareStart) / 1400f).let { if (it in 0f..1f) 1f - it else 0f }
        val flick = 0.82f + 0.18f * sin(t * sp * 6.3f) * sin(t * sp * 2.1f + 1f)

        // gloed in de kamer
        fireGlowPaint.shader = roomGlow
        fireGlowPaint.alpha = (230 * flick).toInt().coerceIn(0, 255)
        c.drawRect(0f, 0f, w, h, fireGlowPaint)
        fireGlowPaint.shader = null

        // baksteen
        val bl = cx - 0.42f * w
        val br = cx + 0.42f * w
        val bt = mantelBottom
        val bb = openB
        brickPaint.style = Paint.Style.FILL
        brickPaint.color = 0xFF8C3F2E.toInt()
        c.drawRect(bl, bt, br, bb, brickPaint)
        brickPaint.color = 0xFF5E2A1F.toInt()
        brickPaint.style = Paint.Style.STROKE
        brickPaint.strokeWidth = 1.5f * density
        val rowH = h * 0.03f
        var row = 0
        var y = bt
        while (y < bb) {
            c.drawLine(bl, y, br, y, brickPaint)
            var x = bl + (row % 2) * w * 0.04f
            while (x < br) {
                if (x > bl) c.drawLine(x, y, x, minOf(y + rowH, bb), brickPaint)
                x += w * 0.08f
            }
            y += rowH
            row++
        }
        brickPaint.style = Paint.Style.FILL

        // schoorsteenmantel
        brickPaint.color = 0xFF6B4423.toInt()
        c.drawRect(cx - 0.47f * w, mantelTop, cx + 0.47f * w, mantelBottom, brickPaint)
        brickPaint.color = 0xFF8A5E34.toInt()
        c.drawRect(cx - 0.47f * w, mantelTop, cx + 0.47f * w, mantelTop + h * 0.007f, brickPaint)

        // sokken
        val socks = arrayOf(
            Pair(-0.38f, 0xFFD7263D.toInt()),
            Pair(-0.31f, 0xFF1E9E57.toInt()),
            Pair(0.30f, 0xFF2E86DE.toInt())
        )
        for (s in socks) {
            val x = cx + s.first * w
            val tw = w * 0.06f
            val len = h * 0.11f
            brickPaint.color = s.second
            c.drawRect(x - tw / 2, mantelBottom, x + tw / 2, mantelBottom + len, brickPaint)
            c.drawRoundRect(x - tw / 2, mantelBottom + len - h * 0.035f, x + tw * 1.15f, mantelBottom + len + h * 0.012f,
                tw * 0.4f, tw * 0.4f, brickPaint)
            brickPaint.color = 0xFFFFFFFF.toInt()
            c.drawRect(x - tw * 0.62f, mantelBottom, x + tw * 0.62f, mantelBottom + h * 0.02f, brickPaint)
        }

        // openingen
        val rad = 0.12f * w
        openingPath.reset()
        openingPath.addRoundRect(
            openL, openT, openR, openB,
            floatArrayOf(rad, rad, rad, rad, 0f, 0f, 0f, 0f), Path.Direction.CW
        )
        brickPaint.color = 0xFF0B0504.toInt()
        c.drawPath(openingPath, brickPaint)

        c.save()
        c.clipPath(openingPath)
        fireGlowPaint.shader = innerGlow
        fireGlowPaint.alpha = (255 * (0.7f + 0.3f * flick + 0.3f * fl)).toInt().coerceIn(0, 255)
        c.drawRect(openL, openT, openR, openB, fireGlowPaint)
        fireGlowPaint.shader = null

        val baseY = openB - h * 0.045f
        // houtblokken
        brickPaint.color = 0xFF3B2314.toInt()
        c.save(); c.rotate(-9f, cx, baseY)
        c.drawRoundRect(cx - 0.21f * w, baseY, cx + 0.19f * w, baseY + h * 0.045f, h * 0.022f, h * 0.022f, brickPaint)
        c.restore()
        c.save(); c.rotate(9f, cx, baseY)
        c.drawRoundRect(cx - 0.19f * w, baseY + h * 0.008f, cx + 0.21f * w, baseY + h * 0.05f, h * 0.022f, h * 0.022f, brickPaint)
        c.restore()
        brickPaint.color = 0xFFFF6A1F.toInt()
        brickPaint.alpha = (140 * flick).toInt()
        c.drawRoundRect(cx - 0.15f * w, baseY + h * 0.03f, cx + 0.15f * w, baseY + h * 0.05f, h * 0.01f, h * 0.01f, brickPaint)
        brickPaint.alpha = 255

        // vlammen
        val layers = arrayOf(
            Pair(1f, 0xDDE8431A.toInt()),
            Pair(0.72f, 0xEEFF9A1F.toInt()),
            Pair(0.42f, 0xFFFFE27A.toInt())
        )
        for (l in layers) {
            brickPaint.color = l.second
            for (i in 0 until 7) {
                val mid = 3f - kotlin.math.abs(i - 3f) * 0.18f      // middelste vlammen hoger
                val noise = 0.5f + 0.5f * sin(t * sp * (3f + i * 0.7f) + i * 1.7f) *
                    (0.6f + 0.4f * sin(t * sp * 5.1f + i))
                val hi = (0.13f * h + 0.1f * h * noise) * (mid / 3f) * (1f + 0.7f * fl) * l.first
                val bw = 0.055f * w * (0.5f + 0.5f * l.first)
                val x = cx + (i - 3) * 0.062f * w + sin(t * sp * 2f + i) * 0.01f * w
                val sway = sin(t * sp * 3.3f + i * 2f) * 0.02f * w
                flamePath.reset()
                flamePath.moveTo(x - bw, baseY + h * 0.02f)
                flamePath.quadTo(x - bw * 0.9f, baseY - hi * 0.5f, x + sway, baseY - hi)
                flamePath.quadTo(x + bw * 0.9f, baseY - hi * 0.5f, x + bw, baseY + h * 0.02f)
                flamePath.close()
                c.drawPath(flamePath, brickPaint)
            }
        }

        // vonkjes
        brickPaint.color = 0xFFFFC857.toInt()
        for (i in 0 until 24) {
            val p = (sy[i] + t * 0.15f * (0.5f + sv[i]) * sp) % 1f
            val x = cx + (sx[i] - 0.5f) * 0.4f * w + sin(t * 2f + i) * 0.012f * w
            val yy = baseY - p * 0.33f * h
            brickPaint.alpha = (255 * (1f - p)).toInt().coerceIn(0, 255)
            c.drawCircle(x, yy, (1f + sv[i]) * density, brickPaint)
        }
        brickPaint.alpha = 255
        c.restore()

        // haardplaat + vloer
        brickPaint.color = 0xFF4B4B55.toInt()
        c.drawRect(cx - 0.45f * w, openB, cx + 0.45f * w, openB + h * 0.035f, brickPaint)
        brickPaint.color = 0xFF2A1A12.toInt()
        c.drawRect(0f, openB + h * 0.035f, w, h, brickPaint)
        fireGlowPaint.shader = roomGlow
        fireGlowPaint.alpha = (150 * flick).toInt().coerceIn(0, 255)
        c.drawRect(0f, openB + h * 0.035f, w, h, fireGlowPaint)
        fireGlowPaint.shader = null
    }

    // ---------- interactie ----------

    fun inTree(px: Float, py: Float): Boolean {
        if (!isFireplace && isOwnPhoto) return true
        if (isFireplace) return kotlin.math.abs(px - cx) <= 0.47f * w && py in (h * 0.27f)..(h * 0.40f)
        for (k in 0 until n) {
            if (kotlin.math.abs(px - cx) <= tierHalf(k, py)) return true
        }
        return false
    }

    fun hit(px: Float, py: Float): Int {
        var best = -1
        var bestD = hitR * hitR
        for (i in bulbs.indices) {
            val dx = bulbs[i].x * w - px
            val dy = bulbs[i].y * h + hang * dirOf(bulbs[i].x) - py
            val d = dx * dx + dy * dy
            if (d <= bestD) { bestD = d; best = i }
        }
        return best
    }

    /** Tik op boom: bestaand lampje → volgende kleur, anders nieuw lampje. Geeft true bij wijziging. */
    fun tap(px: Float, py: Float, color: Int, now: Long): Boolean {
        val i = hit(px, py)
        if (i >= 0) {
            val b = bulbs[i]
            b.color = (b.color + 1) % TreeColors.all.size
            b.pulseStart = now
            return true
        }
        if (inTree(px, py)) {
            val fx = px / w
            bulbs.add(Bulb(fx, (py - hang * dirOf(fx)) / h, color).also { it.pulseStart = now })
            return true
        }
        return false
    }

    fun remove(px: Float, py: Float): Boolean {
        val i = hit(px, py)
        if (i < 0) return false
        bulbs.removeAt(i)
        return true
    }

    fun autoDecorate() {
        bulbs.clear()
        if (w <= 0f || h <= 0f) resizeFallback()
        val rnd = Random(System.nanoTime())
        if (isFireplace) {
            // slinger langs de schoorsteenmantel
            val cnt = 9
            for (m in 0 until cnt) {
                val x = cx - 0.40f * w + 0.80f * w * m / (cnt - 1)
                val jy = (rnd.nextFloat() - 0.5f) * bulbR * 0.6f
                bulbs.add(Bulb(x / w, (mantelTop + h * 0.006f + jy) / h, rnd.nextInt(TreeColors.all.size)))
            }
            return
        }
        if (isOwnPhoto) {
            // slinger over de bovenkant van de foto
            for (m in 0 until 9) {
                val x = w * (0.08f + 0.84f * m / 8f)
                bulbs.add(Bulb(x / w, (h * 0.10f + (if (m % 2 == 0) 0f else bulbR * 0.5f)) / h, rnd.nextInt(TreeColors.all.size)))
            }
            return
        }
        val spacing = bulbR * 3.7f
        val margin = bulbR * 1.8f
        val rows = spec.rows
        for (k in 0 until n) {
            for (f in rows) {
                val y = apex[k] + (base[k] - apex[k]) * f
                val hw = tierHalf(k, y) - margin
                if (hw < 0f) continue
                val n = (2f * hw / spacing).toInt() + 1
                for (m in 0 until n) {
                    val x = if (n == 1) cx else cx - hw + (2f * hw) * m / (n - 1)
                    val jy = (rnd.nextFloat() - 0.5f) * bulbR * 0.6f
                    bulbs.add(Bulb(x / w, (y + jy) / h, rnd.nextInt(TreeColors.all.size)))
                }
            }
        }
    }

    /** Groepeer lampjes op hoogte en sorteer op x: elke groep hangt aan één snoer. */
    private fun groups(): List<List<Bulb>> {
        if (bulbs.isEmpty()) return emptyList()
        val byY = bulbs.sortedBy { it.y }
        val out = ArrayList<MutableList<Bulb>>()
        var cur = mutableListOf(byY[0])
        for (i in 1 until byY.size) {
            if ((byY[i].y - cur.last().y) * h > bulbR * 2.4f) { out.add(cur); cur = mutableListOf() }
            cur.add(byY[i])
        }
        out.add(cur)
        return out.map { g -> g.sortedBy { it.x } }
    }

    private fun halfAt(y: Float): Float {
        if (!isFireplace && isOwnPhoto) return 0.5f * w
        if (isFireplace) return if (y in (h * 0.27f)..(h * 0.40f)) 0.45f * w else 0f
        var best = 0f
        for (i in 0 until n) best = max(best, tierHalf(i, y))
        return best
    }

    private fun swag(x0: Float, y0: Float, x1: Float, y1: Float) {
        val dx = x1 - x0
        val sag = (kotlin.math.abs(dx) * 0.22f).coerceIn(bulbR * 0.6f, bulbR * 2.6f)
        wirePath.moveTo(x0, y0)
        wirePath.quadTo((x0 + x1) / 2f, (y0 + y1) / 2f + sag * 2f, x1, y1)
    }

    private fun resizeFallback() {
        // Zonder echte schermmaat: gebruik een standaard 1080x2400-achtig venster.
        resize((360 * density).toInt(), (780 * density).toInt())
    }

    // ---------- tekenen ----------

    private fun intensity(b: Bulb, i: Int, t: Float): Float {
        val sp = speed / 5f
        return when (mode) {
            Modes.STEADY -> 1f
            Modes.ALTERNATE -> {
                val v = 0.5f + 0.5f * sin(2f * PI.toFloat() * t * sp * 0.5f + (i % 2) * PI.toFloat())
                0.12f + 0.88f * ((v - 0.3f) / 0.4f).coerceIn(0f, 1f)
            }
            Modes.CHASE -> { // licht dat langs de boom omlaag loopt
                var k = (b.y * 12f - t * sp * 2f) % 3f
                if (k < 0) k += 3f
                0.12f + 0.88f * exp(-k * k * 1.6f)
            }
            Modes.WAVE -> 0.25f + 0.75f * (0.5f + 0.5f * sin(b.y * 14f - t * sp * 2.5f))
            TreeModes.MUSIC -> {
                // elk lampje heeft een eigen drempel: hoe harder de muziek, hoe meer er aangaan
                val th = ((i * 37) % 100) / 100f
                val lvl = AudioLevel.level
                val k = ((lvl * 1.5f - th * 0.9f) / 0.25f).coerceIn(0f, 1f)
                0.12f + 0.88f * k
            }
            else -> {
                val v = 0.5f + 0.5f * sin(2f * PI.toFloat() * t * sp * b.freq + b.phase)
                0.2f + 0.8f * v.pow(2.2f)
            }
        }
    }

    fun draw(c: Canvas, nowMs: Long) {
        if (w <= 0f) return
        val t = nowMs / 1000f
        val egg = currentEgg(nowMs)
        c.drawRect(0f, 0f, w, h, if (egg == TreeEggs.KERSTMORGEN && !isFireplace) sunPaint else bgPaint)

        // sterretjes
        var j = 0
        while (j + 2 < bgStars.size) {
            val tw = 0.5f + 0.5f * sin(t * 0.8f + j)
            starsPaint.alpha = (60 + 120 * tw).toInt()
            c.drawCircle(bgStars[j] * w, bgStars[j + 1] * h * 0.5f, (0.6f + bgStars[j + 2]) * density, starsPaint)
            j += 3
        }

        if (egg == TreeEggs.OUDEJAARSAVOND || egg == TreeEggs.NIEUWJAAR) drawFireworks(c, t)
        if (egg == TreeEggs.DRIEKONINGEN) drawShootingStar(c, t)

        if (isFireplace) {
            if (hearthKind == HearthStyles.CARTOON) {
                drawFireplace(c, t, nowMs)
            } else {
                val fl = ((nowMs - flareStart) / 1400f).let { if (it in 0f..1f) 1f - it else 0f }
                painter.draw(c, w, h, t, nowMs, hearthKind, fl, speed / 5f)
            }
        } else if (spec.photo != -1) {
            drawPhotoTree(c)
        } else {
        // stam + takken
        val sp = spec
        val tw = w * 0.05f
        trunkPaint.color = sp.trunk
        c.drawRect(cx - tw, base[n - 1] - 2f, cx + tw, base[n - 1] + h * 0.05f, trunkPaint)
        for (k in n - 1 downTo 0) {
            val hw = half[k]
            tierPath.reset()
            if (sp.shape == 1) {
                tierPath.addOval(cx - hw, apex[k], cx + hw, base[k], Path.Direction.CW)
            } else {
                tierPath.moveTo(cx, apex[k])
                tierPath.lineTo(cx + hw, base[k])
                if (sp.scallops > 0) {
                    val s = sp.scallops
                    val step = 2f * hw / s
                    for (i in 0 until s) {
                        val x0 = cx + hw - i * step
                        tierPath.quadTo(x0 - step / 2f, base[k] + treeH * 0.05f, x0 - step, base[k])
                    }
                } else if (sp.scallops < 0) {
                    val s = -sp.scallops
                    val step = 2f * hw / s
                    for (i in 0 until s) {
                        val x0 = cx + hw - i * step
                        tierPath.lineTo(x0 - step / 2f, base[k] + treeH * 0.04f)
                        tierPath.lineTo(x0 - step, base[k])
                    }
                } else {
                    tierPath.quadTo(cx, base[k] - treeH * 0.04f, cx - hw, base[k])
                }
                tierPath.close()
            }
            when (sp.shading) {
                1 -> {
                    // twee vlakken: licht links, donker rechts
                    treePaint.shader = null
                    treePaint.style = Paint.Style.FILL
                    treePaint.color = sp.topColor
                    c.drawPath(tierPath, treePaint)
                    val right = Path().apply {
                        moveTo(cx, apex[k]); lineTo(cx + hw, base[k]); lineTo(cx, base[k] + treeH * 0.02f); close()
                    }
                    treePaint.color = sp.bottomColor
                    c.drawPath(right, treePaint)
                }
                4 -> {
                    // neon: donkere vulling met gloeiende rand
                    treePaint.shader = null
                    treePaint.style = Paint.Style.FILL
                    treePaint.color = 0xFF04060C.toInt()
                    c.drawPath(tierPath, treePaint)
                    treePaint.style = Paint.Style.STROKE
                    treePaint.color = sp.topColor
                    treePaint.strokeJoin = Paint.Join.ROUND
                    val widths = floatArrayOf(10f, 5f, 2f)
                    val alphas = intArrayOf(40, 90, 255)
                    for (i in widths.indices) {
                        treePaint.strokeWidth = widths[i] * density
                        treePaint.alpha = alphas[i]
                        c.drawPath(tierPath, treePaint)
                    }
                    treePaint.style = Paint.Style.FILL
                }
                else -> {
                    treePaint.style = Paint.Style.FILL
                    treePaint.alpha = 255
                    treePaint.shader = LinearGradient(
                        0f, apex[k], 0f, base[k], intArrayOf(sp.topColor, sp.bottomColor), null, Shader.TileMode.CLAMP
                    )
                    c.drawPath(tierPath, treePaint)
                }
            }
            if (sp.shading == 3) {
                // strepen
                treePaint.shader = null
                treePaint.color = 0x66FFFFFF
                c.save()
                c.clipPath(tierPath)
                val band = (base[k] - apex[k]) / 6f
                var y = apex[k] + band * 0.5f
                while (y < base[k]) {
                    c.drawRect(cx - hw, y, cx + hw, y + band * 0.5f, treePaint)
                    y += band
                }
                c.restore()
            }
            if (sp.shading == 2) {
                // sneeuwkap
                val capH = (base[k] - apex[k]) * 0.34f
                val capW = hw * 0.34f
                val cap = Path().apply {
                    moveTo(cx, apex[k]); lineTo(cx + capW, apex[k] + capH)
                    quadTo(cx + capW * 0.5f, apex[k] + capH * 1.25f, cx, apex[k] + capH * 0.9f)
                    quadTo(cx - capW * 0.5f, apex[k] + capH * 1.25f, cx - capW, apex[k] + capH)
                    close()
                }
                treePaint.shader = null
                treePaint.color = 0xFFF2F7FF.toInt()
                c.drawPath(cap, treePaint)
            }
        }
        treePaint.shader = null
        treePaint.style = Paint.Style.FILL

        // ster
        val sr = w * 0.075f * (if (egg == TreeEggs.DRIEKONINGEN) 1.4f else 1f)
        val sy = apex[0] + sr * 0.2f
        starPath.reset()
        for (n in 0 until 10) {
            val r = if (n % 2 == 0) sr else sr * 0.45f
            val a = (-PI / 2 + n * PI / 5).toFloat()
            val x = cx + r * cos(a); val y = sy + r * sin(a)
            if (n == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
        }
        starPath.close()
        starPaint.color = spec.star
        starPaint.alpha = (200 + 55 * sin(t * 2f)).toInt().coerceIn(0, 255)
        c.drawPath(starPath, starPaint)
        }

        if (hasPresents(egg)) drawPresents(c)

        // snoer: per groep van links naar rechts, met uiteinden aan de rand van de boom
        wirePath.reset()
        for (g in groups()) {
            val first = g.first(); val last = g.last()
            val fy = first.y * h; val ly = last.y * h
            swag(cx - halfAt(fy) + bulbR * 0.2f, fy - bulbR * 0.4f, first.x * w, fy)
            for (i in 0 until g.size - 1) swag(g[i].x * w, g[i].y * h, g[i + 1].x * w, g[i + 1].y * h)
            swag(last.x * w, ly, cx + halfAt(ly) - bulbR * 0.2f, ly - bulbR * 0.4f)
        }
        c.drawPath(wirePath, wirePaint)

        // lampjes
        val z = sz
        val glassCy = z * 0.4f + z * 1.35f * 0.5f
        for (i in bulbs.indices) {
            val b = bulbs[i]
            var k = intensity(b, i, t)
            val age = (nowMs - b.pulseStart) / 1200f
            var scale = 1f
            if (age in 0f..1f) {
                val e = 1f - age
                k = max(k, e)
                scale = 1f + 0.4f * e
            }
            val baseC = TreeColors.all[b.color]

            c.save()
            c.translate(b.x * w, b.y * h)
            c.rotate(sin(b.phase) * 9f + if (dirOf(b.x) < 0f) 180f else 0f)
            c.translate(0f, glassCy)
            c.scale(scale, scale)
            glowPaint.shader = glow[b.color]
            glowPaint.alpha = (255 * k * k).toInt().coerceIn(0, 255)
            c.drawCircle(0f, 0f, z * 2.2f, glowPaint)
            c.translate(0f, -glassCy)

            // fitting
            c.drawRoundRect(-z * 0.2f, -z * 0.05f, z * 0.2f, z * 0.5f, z * 0.08f, z * 0.08f, capPaint)
            // glas
            bulbPaint.color = lerp(dark(baseC), baseC, k)
            c.drawPath(glassPath, bulbPaint)
            // glimlicht
            shinePaint.alpha = (60 + 160 * k).toInt()
            c.drawOval(-z * 0.26f, z * 0.62f, -z * 0.08f, z * 1.12f, shinePaint)
            c.restore()
        }

        if (hasSnow(egg) && (!isFireplace || hearthKind == HearthStyles.CAMPFIRE)) drawSnow(c, t)
        if (egg == TreeEggs.NIEUWJAAR) drawConfetti(c, t)
    }

    private var treeBmp: android.graphics.Bitmap? = null
    private var treeBmpIdx = -1
    private val treeImgPaint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val treeCreditPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xB3FFFFFF.toInt(); setShadowLayer(3f, 0f, 1f, 0xFF000000.toInt())
    }
    private val treeDst = android.graphics.RectF()

    private fun drawPhotoTree(c: Canvas) {
        val i = spec.photo
        if (treeBmpIdx != i) {
            treeBmp?.recycle()
            treeBmp = try {
                if (i == -2) ctx?.let { OwnPhoto.load(it) }
                else ctx?.assets?.open("trees/" + Credits.trees[i].asset)?.use { android.graphics.BitmapFactory.decodeStream(it) }
            } catch (e: Exception) {
                null
            }
            treeBmpIdx = i
        }
        val b = treeBmp
        if (b == null) {
            treeCreditPaint.textSize = 15f * density
            treeCreditPaint.textAlign = Paint.Align.CENTER
            c.drawText("📷 Kies een eigen foto met de knop onderaan", w / 2f, h * 0.5f, treeCreditPaint)
            treeCreditPaint.textAlign = Paint.Align.LEFT
            return
        }
        if (i == -2) {
            val s0 = max(w / b.width, h / b.height)
            treeDst.set((w - b.width * s0) / 2f, (h - b.height * s0) / 2f, (w + b.width * s0) / 2f, (h + b.height * s0) / 2f)
            c.drawBitmap(b, null, treeDst, treeImgPaint)
            return
        }
        val s = max(w / b.width, h / b.height)
        val dw = b.width * s
        val dh = b.height * s
        treeDst.set((w - dw) / 2f, (h - dh) / 2f, (w + dw) / 2f, (h + dh) / 2f)
        c.drawBitmap(b, null, treeDst, treeImgPaint)
        treeCreditPaint.textSize = 10.5f * density
        c.drawText(Credits.shortTree(i), 12f * density, h - 10f * density, treeCreditPaint)
    }

    private fun alpha(c: Int, a: Int) = Color.argb(a, Color.red(c), Color.green(c), Color.blue(c))
    private fun dark(c: Int) = Color.rgb(
        (Color.red(c) * 0.25f + 18).toInt(), (Color.green(c) * 0.25f + 18).toInt(), (Color.blue(c) * 0.25f + 18).toInt()
    )
    private fun lerp(a: Int, b: Int, f: Float): Int {
        val x = f.coerceIn(0f, 1f)
        return Color.rgb(
            (Color.red(a) + (Color.red(b) - Color.red(a)) * x).toInt(),
            (Color.green(a) + (Color.green(b) - Color.green(a)) * x).toInt(),
            (Color.blue(a) + (Color.blue(b) - Color.blue(a)) * x).toInt()
        )
    }
}
