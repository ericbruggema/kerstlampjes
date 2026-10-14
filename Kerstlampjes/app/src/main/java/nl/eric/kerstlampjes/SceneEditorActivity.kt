package nl.eric.kerstlampjes

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.SystemClock
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * Het scherm waarin je zelf een scène maakt: achtergrond, effecten (ook vuur), stickers met eigen animaties
 * en lichtsnoeren die je met je vinger tekent. Wat je ziet is precies wat de scène later laat zien.
 */
class EditorView(ctx: Context, val scene: MyScene, private val vw: Float, private val vh: Float) : View(ctx) {
    var recipe: MyRecipe = MyScenes.starter()
    var selKind = 0          // 0 niets, 1 sticker, 2 effect, 3 snoer
    var selIdx = -1
    var drawing = false
    var onSelect: () -> Unit = {}
    var onEdit: () -> Unit = {}

    private val d = resources.displayMetrics.density
    private var scale = 1f
    private var offX = 0f
    private var offY = 0f
    private val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; color = Color.WHITE; strokeWidth = 2f * d
        pathEffect = DashPathEffect(floatArrayOf(10f * d, 6f * d), 0f)
    }
    private val dot = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFFE27A.toInt() }
    private val frame = Paint().apply { style = Paint.Style.STROKE; color = 0x66FFFFFF; strokeWidth = 1f * d }

    init { scene.resize(vw, vh) }

    override fun onDraw(c: Canvas) {
        c.drawColor(0xFF0B0F14.toInt())
        scale = min(width / vw, height / vh)
        offX = (width - vw * scale) / 2f
        offY = (height - vh * scale) / 2f
        c.save()
        c.translate(offX, offY)
        c.scale(scale, scale)
        c.clipRect(0f, 0f, vw, vh)
        scene.draw(c, SystemClock.uptimeMillis())
        drawSelection(c)
        c.restore()
        c.drawRect(offX, offY, offX + vw * scale, offY + vh * scale, frame)
        postInvalidateOnAnimation()
    }

    private fun drawSelection(c: Canvas) {
        when (selKind) {
            1 -> recipe.stickers.getOrNull(selIdx)?.let {
                c.drawCircle(it.x * vw, it.y * vh, max(it.size * vw * 0.62f, 24f * d), ring)
            }
            2 -> recipe.fx.getOrNull(selIdx)?.let {
                if (MyKinds.positioned(it.kind)) c.drawCircle(it.x * vw, it.y * vh, max(it.size / 100f * vw * 0.6f, 24f * d), ring)
            }
            3 -> recipe.strings.getOrNull(selIdx)?.let { s ->
                var i = 0
                while (i < s.pts.size) { c.drawCircle(s.pts[i] * vw, s.pts[i + 1] * vh, 4f * d, dot); i += 2 }
            }
        }
    }

    private fun segDist(px: Float, py: Float, ax: Float, ay: Float, bx: Float, by: Float): Float {
        val dx = bx - ax; val dy = by - ay
        val l2 = dx * dx + dy * dy
        val t = if (l2 == 0f) 0f else (((px - ax) * dx + (py - ay) * dy) / l2).coerceIn(0f, 1f)
        return hypot(px - (ax + t * dx), py - (ay + t * dy))
    }

    private fun hit(x: Float, y: Float) {
        for (i in recipe.stickers.indices.reversed()) {
            val s = recipe.stickers[i]
            if (hypot(x - s.x * vw, y - s.y * vh) <= max(s.size * vw * 0.6f, 26f * d)) { select(1, i); return }
        }
        for (i in recipe.fx.indices.reversed()) {
            val f = recipe.fx[i]
            if (MyKinds.positioned(f.kind) && hypot(x - f.x * vw, y - f.y * vh) <= max(f.size / 100f * vw * 0.6f, 26f * d)) { select(2, i); return }
        }
        for (i in recipe.strings.indices.reversed()) {
            val p = recipe.strings[i].pts
            var k = 0
            while (k + 3 < p.size) {
                if (segDist(x, y, p[k] * vw, p[k + 1] * vh, p[k + 2] * vw, p[k + 3] * vh) <= 22f * d) { select(3, i); return }
                k += 2
            }
        }
        select(0, -1)
    }

    fun select(kind: Int, idx: Int) {
        if (kind == selKind && idx == selIdx) return
        selKind = kind; selIdx = idx
        onSelect()
    }

    private var lastX = 0f
    private var lastY = 0f
    private var moved = false
    private var stroke: MyString? = null

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x = (e.x - offX) / scale
        val y = (e.y - offY) / scale
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                lastX = x; lastY = y; moved = false
                if (drawing) {
                    val s = MyString(floatArrayOf(x / vw, y / vh, x / vw, y / vh))
                    s.style = lastStyle; s.palette = lastPalette
                    recipe.strings.add(s)
                    stroke = s
                    select(3, recipe.strings.lastIndex)
                    scene.invalidate()
                } else {
                    val before = selKind to selIdx
                    hit(x, y)
                    // eerst selecteren, dan pas slepen: een tik op een ander object verplaatst niets
                    if (before != (selKind to selIdx)) moved = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - lastX; val dy = y - lastY
                val s = stroke
                if (s != null) {
                    if (hypot(dx, dy) > 14f * d) {
                        s.pts = s.pts + floatArrayOf(x / vw, y / vh)
                        lastX = x; lastY = y
                        scene.invalidate()
                    }
                } else if (selIdx >= 0 && hypot(dx, dy) > 0.5f) {
                    when (selKind) {
                        1 -> recipe.stickers.getOrNull(selIdx)?.let { it.x = (it.x + dx / vw).coerceIn(0f, 1f); it.y = (it.y + dy / vh).coerceIn(0f, 1f) }
                        2 -> recipe.fx.getOrNull(selIdx)?.let { if (MyKinds.positioned(it.kind)) { it.x = (it.x + dx / vw).coerceIn(0f, 1f); it.y = (it.y + dy / vh).coerceIn(0f, 1f) } }
                        3 -> recipe.strings.getOrNull(selIdx)?.let { st ->
                            for (i in st.pts.indices) st.pts[i] += if (i % 2 == 0) dx / vw else dy / vh
                            scene.invalidate()
                        }
                    }
                    lastX = x; lastY = y
                    onEdit()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val s = stroke
                if (s != null) {
                    // een te korte streep is een misser
                    if (s.pts.size < 6) {
                        recipe.strings.remove(s)
                        select(0, -1)
                    }
                    stroke = null
                    scene.invalidate()
                    onSelect()
                }
                onEdit()
            }
        }
        return true
    }

    var lastStyle = 0
    var lastPalette = 0
}

class SceneEditorActivity : Activity() {

    private lateinit var view: EditorView
    private lateinit var panel: LinearLayout
    private lateinit var titleView: TextView
    private val dp get() = resources.displayMetrics.density
    private var tab = 0
    private fun px(v: Int) = (v * dp).toInt()
    private val recipe get() = view.recipe
    private val tabNames get() = arrayOf(
        "🖼️ " + Tr.t("Achtergrond"), "✨ " + Tr.t("Effecten"), "🎅 " + Tr.t("Stickers"), "💡 " + Tr.t("Lampjes"), "💾 " + Tr.t("Meer")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tr.init(this)
        val dm = resources.displayMetrics
        val scene = MyScene(this, dm.density).apply { external = true }
        view = EditorView(this, scene, dm.widthPixels.toFloat(), dm.heightPixels.toFloat())
        val r = MyScenes.active(this)
        view.recipe = r
        scene.recipe = r

        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(0xFF0B0F14.toInt()) }
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(px(8), px(6), px(8), px(2))
        }
        titleView = TextView(this).apply { setTextColor(Color.WHITE); textSize = 15f; setTypeface(typeface, Typeface.BOLD) }
        top.addView(titleView, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        top.addView(btn("✅ " + Tr.t("Klaar")) { finishAndShow() })
        @Suppress("DEPRECATION")
        root.setOnApplyWindowInsetsListener { v, ins ->
            v.setPadding(0, ins.systemWindowInsetTop, 0, ins.systemWindowInsetBottom)
            ins
        }
        root.addView(top)
        root.addView(view, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

        val tabs = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(px(4), px(4), px(4), px(2)) }
        for ((i, n) in tabNames.withIndex()) {
            tabs.addView(btn(n) { tab = i; rebuild() },
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, px(40)).apply { setMargins(px(3), 0, px(3), 0) })
        }
        root.addView(HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false; addView(tabs) })

        panel = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(px(10), px(4), px(10), px(10)) }
        root.addView(ScrollView(this).apply { addView(panel) },
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (dm.heightPixels * 0.34f).toInt()))
        setContentView(root)

        view.onSelect = {
            if (view.selKind != 0) tab = when (view.selKind) { 1 -> 2; 2 -> 1; else -> 3 }
            rebuild()
        }
        rebuild()
    }

    override fun onPause() {
        persist()
        super.onPause()
    }

    private fun persist() {
        MyScenes.upsert(this, recipe)
        MyScenes.setActive(this, recipe.id)
    }

    private fun finishAndShow() {
        persist()
        getSharedPreferences("kerstlampjes", MODE_PRIVATE).edit()
            .putInt("tree_style", TreeStyles.TREES + HearthStyles.all.size + SceneHost.MINE).apply()
        finish()
    }

    // ───── kleine UI-bouwstenen ─────

    private fun btn(label: String, onClick: () -> Unit) = Button(this).apply {
        text = label
        isAllCaps = false
        textSize = 13f
        setPadding(px(10), 0, px(10), 0)
        setTextColor(Color.WHITE)
        setTypeface(typeface, Typeface.BOLD)
        minHeight = 0; minimumHeight = 0
        background = GradientDrawable().apply { setColor(0xFF2E7D32.toInt()); cornerRadius = px(12).toFloat() }
        setOnClickListener { onClick() }
    }

    private fun label(s: String) = TextView(this).apply {
        text = s; setTextColor(0xFFCFD8DC.toInt()); textSize = 13f; setPadding(0, px(8), 0, px(2))
    }

    private fun add(v: View, h: Int = ViewGroup.LayoutParams.WRAP_CONTENT) {
        val hh = if (v is Button && h == ViewGroup.LayoutParams.WRAP_CONTENT) px(44) else h
        panel.addView(v, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hh).apply { topMargin = px(4) })
    }

    private fun slider(title: String, min: Int, max: Int, value: Int, onChange: (Int) -> Unit) {
        val shown = TextView(this).apply { setTextColor(0xFFCFD8DC.toInt()); textSize = 13f; text = "$title: $value" }
        val bar = SeekBar(this).apply {
            this.max = max - min
            progress = (value - min).coerceIn(0, max - min)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) {
                    shown.text = "$title: ${p + min}"
                    if (fromUser) onChange(p + min)
                }
                override fun onStartTrackingTouch(s: SeekBar?) {}
                override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        }
        add(shown)
        add(bar)
    }

    private fun choose(title: String, items: List<String>, onPick: (Int) -> Unit) {
        AlertDialog.Builder(this).setTitle(title)
            .setItems(items.toTypedArray()) { _, i -> onPick(i) }
            .setNegativeButton(Tr.t("Annuleer"), null).show()
    }

    private fun swatches(colors: IntArray, selected: Int, onPick: (Int) -> Unit) {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        for ((i, col) in colors.withIndex()) {
            row.addView(View(this).apply {
                background = GradientDrawable().apply {
                    setColor(col); cornerRadius = px(10).toFloat()
                    setStroke(px(if (i == selected) 3 else 1), if (i == selected) Color.WHITE else 0x66FFFFFF)
                }
                setOnClickListener { onPick(i) }
            }, LinearLayout.LayoutParams(px(40), px(40)).apply { setMargins(px(3), px(3), px(3), px(3)) })
        }
        add(HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false; addView(row) })
    }

    private fun emojiGrid(items: List<String>, onPick: (String) -> Unit) {
        val rows = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val per = (items.size + 1) / 2
        for (r in 0..1) {
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            for (e in items.drop(r * per).take(per)) {
                row.addView(TextView(this).apply {
                    text = e; textSize = 26f; gravity = android.view.Gravity.CENTER
                    setOnClickListener { onPick(e) }
                }, LinearLayout.LayoutParams(px(46), px(46)))
            }
            rows.addView(row)
        }
        add(HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false; addView(rows) })
    }

    private fun askText(title: String, current: String, onOk: (String) -> Unit) {
        val et = EditText(this).apply {
            setText(current); setSelection(text.length); inputType = InputType.TYPE_CLASS_TEXT
        }
        AlertDialog.Builder(this).setTitle(title)
            .setView(LinearLayout(this).apply { setPadding(px(20), px(8), px(20), 0); addView(et) })
            .setPositiveButton(Tr.t("Bewaar")) { _, _ -> onOk(et.text.toString().trim()) }
            .setNegativeButton(Tr.t("Annuleer"), null).show()
    }

    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_LONG).show()

    private fun changed() { view.scene.invalidate(); view.invalidate() }

    // ───── tabbladen ─────

    private fun rebuild() {
        panel.removeAllViews()
        titleView.text = "🎨 " + recipe.name
        when (tab) {
            0 -> backgroundTab()
            1 -> effectsTab()
            2 -> stickersTab()
            3 -> lightsTab()
            else -> moreTab()
        }
    }

    private fun backgroundTab() {
        add(label(Tr.t("Kies een achtergrond")))
        val cols = MyLook.backgrounds.map { it.third }.toIntArray()
        val sel = if (recipe.bgKind == 0) MyLook.backgrounds.indexOfFirst { it.second == recipe.bgTop && it.third == recipe.bgBottom } else -1
        swatches(cols, sel) { i ->
            recipe.bgKind = 0
            recipe.bgTop = MyLook.backgrounds[i].second
            recipe.bgBottom = MyLook.backgrounds[i].third
            changed(); rebuild()
        }
        add(btn("📷 " + Tr.t("Eigen foto als achtergrond")) {
            try {
                startActivityForResult(Intent.createChooser(Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), Tr.t("Kies een foto")), 41)
            } catch (e: Exception) {
                toast(Tr.t("Geen fotokiezer gevonden."))
            }
        })
        add(btn("🏔️ " + Tr.t("Sneeuwgrond") + ": " + (if (recipe.ground) Tr.t("aan") else Tr.t("uit"))) {
            recipe.ground = !recipe.ground; changed(); rebuild()
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 41 && resultCode == RESULT_OK && data?.data != null) {
            if (MyScenes.importBackground(this, recipe.id, data.data!!)) {
                recipe.bgKind = 1
                changed(); rebuild()
            } else {
                toast(Tr.t("Die foto kon niet worden gelezen."))
            }
        }
    }

    private fun effectsTab() {
        add(btn("➕ " + Tr.t("Effect toevoegen")) {
            choose(Tr.t("Welk effect?"), MyKinds.names.indices.map { MyKinds.icons[it] + " " + Tr.t(MyKinds.names[it]) }) { k ->
                val f = MyFx(k)
                when (k) {
                    MyKinds.FIRE -> { f.x = 0.5f; f.y = 0.88f; f.size = 34; f.color = 0 }
                    MyKinds.GLOW -> { f.x = 0.5f; f.y = 0.5f; f.size = 30; f.color = 1; f.amount = 60 }
                    MyKinds.FALL -> { f.size = 9; f.amount = 40 }
                    MyKinds.FIREWORKS -> f.color = 0
                }
                recipe.fx.add(f)
                view.selKind = 0; view.selIdx = -1
                view.select(2, recipe.fx.lastIndex)
                changed()
            }
        })
        if (recipe.fx.isEmpty()) add(label(Tr.t("Nog geen effecten.")))
        for ((i, f) in recipe.fx.withIndex()) {
            val mark = if (view.selKind == 2 && view.selIdx == i) "▶ " else ""
            add(btn(mark + MyKinds.icons[f.kind] + " " + Tr.t(MyKinds.names[f.kind])) { view.select(2, i); rebuild() })
        }
        val f = recipe.fx.getOrNull(view.selIdx)?.takeIf { view.selKind == 2 } ?: return
        add(label(Tr.t("Instellingen") + ": " + Tr.t(MyKinds.names[f.kind])))
        if (f.kind != MyKinds.STARS) slider(if (f.kind == MyKinds.FIRE) Tr.t("Hoogte") else Tr.t("Hoeveelheid"), 1, 100, f.amount) { f.amount = it; changed() }
        slider(Tr.t("Snelheid"), 1, 10, f.speed) { f.speed = it; changed() }
        if (MyKinds.sized(f.kind)) {
            val mx = if (f.kind == MyKinds.FALL) 30 else 100
            slider(if (f.kind == MyKinds.FIRE) Tr.t("Breedte") else Tr.t("Grootte"), 3, mx, f.size.coerceAtMost(mx)) { f.size = it; changed() }
        }
        when (f.kind) {
            MyKinds.FIRE -> {
                add(label(Tr.t("Kleur van het vuur")))
                swatches(IntArray(MyLook.fireColors.size) { MyLook.fireColors[it][2] }, f.color) { f.color = it; changed(); rebuild() }
            }
            MyKinds.GLOW, MyKinds.FIREWORKS -> {
                add(label(if (f.kind == MyKinds.FIREWORKS) Tr.t("Kleur (wit = bont)") else Tr.t("Kleur")))
                swatches(MyLook.swatches, f.color) { f.color = it; changed(); rebuild() }
            }
            MyKinds.FALL -> {
                add(label(Tr.t("Wat valt er?")))
                emojiGrid(MyLook.fallEmojis) { f.emoji = it; changed() }
            }
        }
        if (MyKinds.positioned(f.kind)) add(label(Tr.t("Sleep het effect op het scherm om het te verplaatsen.")))
        add(btn("🗑️ " + Tr.t("Verwijder")) {
            recipe.fx.remove(f); view.select(0, -1); view.selKind = 0; changed(); rebuild()
        })
    }

    private fun stickersTab() {
        add(label(Tr.t("Tik op een plaatje om het toe te voegen")))
        emojiGrid(MyLook.emojis) { e ->
            val s = MySticker(e, 0.5f, 0.45f, 0.2f)
            recipe.stickers.add(s)
            view.selKind = 0; view.selIdx = -1
            view.select(1, recipe.stickers.lastIndex)
            changed()
        }
        val s = recipe.stickers.getOrNull(view.selIdx)?.takeIf { view.selKind == 1 }
        if (s == null) {
            add(label(Tr.t("Tik op een sticker in het scherm om hem aan te passen. Sleep om te verplaatsen.")))
            return
        }
        slider(Tr.t("Grootte"), 3, 100, (s.size * 100).toInt()) { s.size = it / 100f; changed() }
        slider(Tr.t("Draaiing"), -180, 180, s.rot.toInt()) { s.rot = it.toFloat(); changed() }
        add(btn("🎬 " + Tr.t("Animatie") + ": " + Tr.t(MyLook.anims[s.anim])) {
            choose(Tr.t("Animatie"), MyLook.anims.map { Tr.t(it) }) { s.anim = it; changed(); rebuild() }
        })
        add(btn("👆 " + Tr.t("Bij een tik") + ": " + Tr.t(MyLook.taps[s.tap])) {
            choose(Tr.t("Bij een tik"), MyLook.taps.map { Tr.t(it) }) { k ->
                s.tap = k
                if (k == MyLook.TAP_SAY && s.msg.isBlank()) {
                    askText(Tr.t("Wat zegt hij of zij?"), s.msg) { s.msg = it; rebuild() }
                } else rebuild()
            }
        })
        if (s.tap == MyLook.TAP_SAY) {
            add(btn("💬 " + (s.msg.ifBlank { Tr.t("Tekst…") })) {
                askText(Tr.t("Wat zegt hij of zij?"), s.msg) { s.msg = it; rebuild() }
            })
        }
        add(btn("📄 " + Tr.t("Dupliceren")) {
            val c = MySticker(s.emoji, (s.x + 0.08f).coerceAtMost(1f), (s.y + 0.05f).coerceAtMost(1f), s.size, s.rot, s.anim, s.tap, s.msg)
            recipe.stickers.add(c)
            view.selKind = 0; view.selIdx = -1
            view.select(1, recipe.stickers.lastIndex); changed()
        })
        add(btn("⬆️ " + Tr.t("Naar voren")) {
            recipe.stickers.remove(s); recipe.stickers.add(s)
            view.selKind = 0; view.selIdx = -1
            view.select(1, recipe.stickers.lastIndex); changed()
        })
        add(btn("🗑️ " + Tr.t("Verwijder")) {
            recipe.stickers.remove(s); view.selKind = 0; view.select(0, -1); changed(); rebuild()
        })
    }

    private fun presetString(kind: Int) {
        val n = 12
        val pts = FloatArray(n * 2)
        for (i in 0 until n) {
            val u = i / (n - 1f)
            pts[i * 2] = 0.03f + 0.94f * u
            pts[i * 2 + 1] = if (kind == 0) 0.035f + 0.05f * 4f * u * (1f - u) else 0.2f + 0.17f * 4f * u * (1f - u)
        }
        val s = MyString(if (kind == 2) floatArrayOf(0.03f, 0.03f, 0.97f, 0.03f, 0.97f, 0.97f, 0.03f, 0.97f, 0.03f, 0.03f) else pts)
        s.style = view.lastStyle; s.palette = view.lastPalette
        recipe.strings.add(s)
        view.selKind = 0; view.selIdx = -1
        view.select(3, recipe.strings.lastIndex)
        changed()
    }

    private fun lightsTab() {
        add(btn("✏️ " + Tr.t("Teken een snoer met je vinger") + ": " + (if (view.drawing) Tr.t("aan") else Tr.t("uit"))) {
            view.drawing = !view.drawing
            if (view.drawing) toast(Tr.t("Veeg over het scherm om een snoer te tekenen."))
            rebuild()
        })
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        fun cell() = LinearLayout.LayoutParams(0, px(44), 1f).apply { setMargins(px(2), 0, px(2), 0) }
        row.addView(btn(Tr.t("Bovenkant")) { presetString(0) }, cell())
        row.addView(btn(Tr.t("Boog")) { presetString(1) }, cell())
        row.addView(btn(Tr.t("Rondom")) { presetString(2) }, cell())
        add(row, px(44))
        for ((i, _) in recipe.strings.withIndex()) {
            val mark = if (view.selKind == 3 && view.selIdx == i) "▶ " else ""
            add(btn(mark + "💡 " + Tr.f("Snoer %d", i + 1)) { view.drawing = false; view.select(3, i); rebuild() })
        }
        val s = recipe.strings.getOrNull(view.selIdx)?.takeIf { view.selKind == 3 } ?: return
        add(btn("🔦 " + Tr.t("Soort lampjes") + ": " + Tr.t(LampStyles.names[s.style.coerceIn(0, LampStyles.names.size - 1)])) {
            choose(Tr.t("Soort lampjes"), LampStyles.names.map { Tr.t(it) }) { s.style = it; view.lastStyle = it; changed(); rebuild() }
        })
        add(btn("🎨 " + Tr.t("Kleuren") + ": " + Tr.t(Palettes.names[s.palette.coerceIn(0, Palettes.names.size - 1)])) {
            choose(Tr.t("Kleuren"), Palettes.names.map { Tr.t(it) }) { s.palette = it; view.lastPalette = it; changed(); rebuild() }
        })
        add(btn("✨ " + Tr.t("Effect") + ": " + Tr.t(Modes.names[s.mode.coerceIn(0, Modes.names.size - 1)])) {
            choose(Tr.t("Effect"), Modes.names.map { Tr.t(it) }) { s.mode = it; changed(); rebuild() }
        })
        slider(Tr.t("Snelheid"), 1, 10, s.speed) { s.speed = it; changed() }
        slider(Tr.t("Grootte lampjes"), 1, 10, s.size) { s.size = it; changed() }
        add(btn("🗑️ " + Tr.t("Verwijder")) {
            recipe.strings.remove(s); view.select(0, -1); view.selKind = 0; changed(); rebuild()
        })
    }

    private fun loadRecipe(r: MyRecipe) {
        persist()
        view.recipe = r
        view.scene.recipe = r
        view.selKind = 0; view.selIdx = -1; view.drawing = false
        MyScenes.setActive(this, r.id)
        tab = 0
        rebuild()
    }

    private fun moreTab() {
        add(btn("✏️ " + Tr.t("Naam") + ": " + recipe.name) {
            askText(Tr.t("Naam van je scène"), recipe.name) { if (it.isNotBlank()) { recipe.name = it; rebuild() } }
        })
        add(btn("🆕 " + Tr.t("Nieuwe scène")) {
            val r = MyRecipe(MyScenes.newId(), Tr.t("Mijn scène") + " " + (MyScenes.all(this).size + 1))
            r.fx.add(MyFx(MyKinds.STARS))
            loadRecipe(r)
        })
        add(btn("📄 " + Tr.t("Dupliceren")) {
            val r = recipe.copy(MyScenes.newId(), recipe.name + " 2")
            if (recipe.bgKind == 1) try { MyScenes.bgFile(this, recipe.id).copyTo(MyScenes.bgFile(this, r.id), true) } catch (e: Exception) { r.bgKind = 0 }
            loadRecipe(r)
        })
        add(btn("📚 " + Tr.t("Mijn scènes")) {
            persist()
            val list = MyScenes.all(this)
            choose(Tr.t("Mijn scènes"), list.map { (if (it.id == recipe.id) "✓ " else "") + it.name }) { loadRecipe(list[it]) }
        })
        add(btn("📤 " + Tr.t("Delen")) {
            val text = Tr.t("Mijn kerstscène voor de app Kerstlampjes. Kopieer deze tekst en kies in de app Meer → Plakken.") + "\n\n" + MyScenes.share(recipe)
            val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
            try { startActivity(Intent.createChooser(send, Tr.t("Delen"))) } catch (e: Exception) { toast(Tr.t("Delen is niet mogelijk.")) }
        })
        add(btn("📥 " + Tr.t("Plakken (scène van iemand anders)")) {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = cm.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(this)?.toString() ?: ""
            val r = MyScenes.parseShared(text)
            if (r == null) toast(Tr.t("Geen scène gevonden op het klembord. Kopieer eerst de gedeelde tekst."))
            else { loadRecipe(r); toast(Tr.f("Scène \"%s\" geladen.", r.name)) }
        })
        add(btn("🗑️ " + Tr.t("Deze scène verwijderen")) {
            AlertDialog.Builder(this).setTitle(Tr.f("\"%s\" verwijderen?", recipe.name))
                .setPositiveButton(Tr.t("Verwijder")) { _, _ ->
                    val gone = recipe.id
                    val rest = MyScenes.all(this).filter { it.id != gone }
                    MyScenes.delete(this, gone)
                    loadRecipe(rest.firstOrNull() ?: MyScenes.starter())
                }
                .setNegativeButton(Tr.t("Annuleer"), null).show()
        })
        add(label(Tr.t("Je scène wordt automatisch bewaard. Je ziet hem terug als scène \"Mijn scène\", als wallpaper en in je favorieten.")))
    }
}
