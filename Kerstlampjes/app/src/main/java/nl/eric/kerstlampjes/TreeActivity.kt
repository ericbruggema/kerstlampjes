package nl.eric.kerstlampjes

import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Het hoofdvlak: bomen (interactief), haarden en scènes.
 * Tik/lang indrukken/swipen wordt aan de juiste kant doorgegeven.
 */
class TreeView(
    context: Context,
    private val onChanged: () -> Unit,
    private val onSwitch: (Int) -> Unit,
    private val onEgg: () -> Unit = {}
) : View(context) {

    val renderer = TreeRenderer(resources.displayMetrics.density, context)
    val scenes = SceneHost(context)
    var selectedColor = 0
    private var running = false

    private val frame = object : Runnable {
        override fun run() {
            if (!running) return
            invalidate()
            postDelayed(this, 33L)
        }
    }

    private fun scene() = scenes.scene(renderer.sceneIndex)

    private val gestures = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent) = true
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val now = SystemClock.uptimeMillis()
            if (renderer.isScene) {
                scene().tap(e.x, e.y, now)
                return true
            }
            if (renderer.hitFire(e.x, e.y)) {
                renderer.flare(now)
                return true
            }
            if (renderer.hitStar(e.x, e.y)) {
                renderer.cycleEgg()
                onEgg()
                return true
            }
            if (renderer.tap(e.x, e.y, selectedColor, now)) onChanged()
            return true
        }
        override fun onFling(e1: MotionEvent?, e2: MotionEvent, vx: Float, vy: Float): Boolean {
            if (e1 == null) return false
            val dx = e2.x - e1.x
            if (abs(dx) > 120f * resources.displayMetrics.density && abs(vx) > abs(vy)) {
                // swipe naar links = volgende
                switchTree(if (dx < 0) 1 else -1)
                return true
            }
            return false
        }
        override fun onLongPress(e: MotionEvent) {
            if (renderer.isScene) {
                if (scene().longPress(e.x, e.y, SystemClock.uptimeMillis())) {
                    performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
                }
                return
            }
            if (renderer.remove(e.x, e.y)) {
                performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
                onChanged()
            }
        }
    })

    private var slideStart = -1_000L
    private var slideDir = 0

    fun switchTree(delta: Int) {
        slideDir = delta
        slideStart = SystemClock.uptimeMillis()
        onSwitch(delta)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean = gestures.onTouchEvent(event)

    override fun onSizeChanged(w: Int, h: Int, ow: Int, oh: Int) {
        renderer.resize(w, h)
        scenes.resize(w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        val now = SystemClock.uptimeMillis()
        val a = ((now - slideStart) / 280f).coerceIn(0f, 1f)
        canvas.save()
        if (a < 1f) {
            val e = 1f - a
            canvas.translate(slideDir * width * 0.6f * e * e, 0f)
        }
        if (renderer.isScene) scene().draw(canvas, now) else renderer.draw(canvas, now)
        canvas.restore()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        running = true
        post(frame)
    }

    override fun onDetachedFromWindow() {
        running = false
        removeCallbacks(frame)
        super.onDetachedFromWindow()
    }

    fun shake() {
        if (renderer.isScene) scene().shake(SystemClock.uptimeMillis())
    }
}

class TreeActivity : Activity() {

    private lateinit var tree: TreeView
    private val dp get() = resources.displayMetrics.density
    private val swatches = ArrayList<View>()
    private lateinit var treeLabel: TextView
    private lateinit var banner: TextView
    private lateinit var countdown: TextView
    private lateinit var photoBtn: Button
    private lateinit var hintView: TextView
    private val chips = ArrayList<Button>()
    private val treeOnly = ArrayList<View>()     // bedieningselementen die alleen bij bomen/haarden horen
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val bannerTick = object : Runnable {
        override fun run() {
            refreshBanner()
            handler.postDelayed(this, 1000L)
        }
    }

    private lateinit var sensors: SensorManager
    private val shakeListener = object : SensorEventListener {
        private var last = 0L
        override fun onSensorChanged(e: SensorEvent) {
            val g = sqrt(e.values[0] * e.values[0] + e.values[1] * e.values[1] + e.values[2] * e.values[2])
            val now = SystemClock.uptimeMillis()
            if (abs(g - 9.81f) > 9f && now - last > 1200) {
                last = now
                tree.shake()
            }
        }
        override fun onAccuracyChanged(s: Sensor?, a: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensors = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        tree = TreeView(this,
            { TreeStore.save(this, tree.renderer); Sound.ding() },
            { delta ->
                TreeStore.switchStyle(this, tree.renderer, delta)
                refreshTreeLabel()
            },
            { refreshBanner() })
        tree.scenes.gifts.editor = { index, msg, unlock, done -> runOnUiThread { editGift(index, msg, unlock, done) } }
        setContentView(buildUi())
    }

    override fun onResume() {
        super.onResume()
        handler.post(bannerTick)
        sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensors.registerListener(shakeListener, it, SensorManager.SENSOR_DELAY_UI)
        }
        // De boom wordt pas geladen als de afmetingen bekend zijn
        tree.post {
            TreeStore.load(this, tree.renderer)
            val today = LocalDate.now()
            val wanted = intent.getBooleanExtra("today", false)
            if (Calendar2.todayStyle() >= 0 && (wanted ||
                    (ExtraPrefs.dailyAuto(this) && ExtraPrefs.lastDay(this) != today.toString()))
            ) {
                ExtraPrefs.setLastDay(this, today.toString())
                intent.removeExtra("today")
                TreeStore.jump(this, tree.renderer, Calendar2.todayStyle())
            }
            TreeStore.save(this, tree.renderer)
            refreshTreeLabel()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onPause() {
        Sound.set(Sound.OFF)
        AudioLevel.sync(this, false)
        handler.removeCallbacks(bannerTick)
        sensors.unregisterListener(shakeListener)
        super.onPause()
    }

    private fun buildUi(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF08142A.toInt())
            setOnApplyWindowInsetsListener { v, ins ->
                v.setPadding(0, ins.systemWindowInsetTop, 0, ins.systemWindowInsetBottom)
                ins
            }
        }

        hintView = TextView(this).apply {
            setTextColor(0xCCFFFFFF.toInt())
            textSize = 12f
            gravity = Gravity.CENTER
            setPadding(px(16), px(12), px(16), px(4))
        }
        root.addView(hintView)

        treeLabel = TextView(this).apply {
            setTextColor(0xFFFFE8B0.toInt())
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(px(16), px(4), px(16), px(4))
        }
        val chipRow = LinearLayout(this).apply { gravity = Gravity.CENTER }
        listOf("🎄 Bomen", "🔥 Haarden", "✨ Scènes").forEachIndexed { i, label ->
            val b = Button(this).apply {
                text = label
                isAllCaps = false
                textSize = 13f
                setTextColor(Color.WHITE)
                setOnClickListener {
                    TreeStore.selectCategory(this@TreeActivity, tree.renderer, i)
                    refreshTreeLabel()
                }
            }
            chips.add(b)
            chipRow.addView(b, LinearLayout.LayoutParams(px(112), px(44)).apply { setMargins(px(4), 0, px(4), 0) })
        }
        root.addView(chipRow, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = px(4)
        })
        root.addView(treeLabel)
        countdown = TextView(this).apply {
            setTextColor(0xFFB8E0C0.toInt())
            textSize = 12f
            gravity = Gravity.CENTER
            text = Calendar2.countdown()
        }
        root.addView(countdown)
        banner = TextView(this).apply {
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(px(16), px(2), px(16), px(2))
            visibility = View.GONE
        }
        root.addView(banner)
        root.addView(tree, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF101C16.toInt())
            setPadding(px(16), px(12), px(16), px(12))
        }

        // kleurkeuze
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER }
        TreeColors.all.forEachIndexed { i, c ->
            val v = View(this)
            v.setOnClickListener { tree.selectedColor = i; refreshSwatches() }
            swatches.add(v)
            row.addView(v, LinearLayout.LayoutParams(px(38), px(38)).apply { setMargins(px(5), 0, px(5), 0) })
            v.tag = c
        }
        panel.addView(row)
        treeOnly.add(row)
        refreshSwatches()

        // effect
        val rendererRef = tree.renderer
        fun picker(items: Array<String>, selected: Int, onPick: (Int) -> Unit) = Spinner(this).apply {
            adapter = ArrayAdapter(this@TreeActivity, android.R.layout.simple_spinner_dropdown_item, items)
            setSelection(selected.coerceIn(0, items.size - 1), false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    onPick(pos)
                    TreeStore.save(this@TreeActivity, rendererRef)
                }
                override fun onNothingSelected(p: AdapterView<*>?) {}
            }
        }
        val pickers = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        pickers.addView(
            picker(TreeModes.names, Prefs.treeMode(this)) {
                rendererRef.mode = it
                if (it == TreeModes.MUSIC) askMic() else applyAudio()
            },
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )
        pickers.addView(
            picker(arrayOf("Lampjes omlaag", "Lampjes omhoog", "Willekeurig"), Prefs.treeOrient(this)) { rendererRef.orient = it },
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )
        panel.addView(pickers, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = px(8)
        })
        treeOnly.add(pickers)
        val lampPicker = picker(LampStyles.names, getSharedPreferences("kerstlampjes", MODE_PRIVATE).getInt("lamp_style", 0)) {
            rendererRef.lamp = it
            TreeStore.save(this@TreeActivity, rendererRef)
        }
        lampPicker.contentDescription = "Soort lampjes"
        panel.addView(lampPicker, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        treeOnly.add(lampPicker)

        val speedLabel = TextView(this).apply {
            setTextColor(0xFFE8EFEA.toInt()); textSize = 14f
            text = "Snelheid: ${Prefs.treeSpeed(this@TreeActivity)}"
        }
        val speedBar = SeekBar(this).apply {
            max = 9
            progress = Prefs.treeSpeed(this@TreeActivity) - 1
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, p: Int, fromUser: Boolean) {
                    speedLabel.text = "Snelheid: ${p + 1}"
                    if (fromUser) {
                        rendererRef.speed = p + 1
                        TreeStore.save(this@TreeActivity, rendererRef)
                    }
                }
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            })
        }
        panel.addView(speedLabel)
        panel.addView(speedBar)
        treeOnly.add(speedLabel)
        treeOnly.add(speedBar)

        // knoppen
        val buttons = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val versier = button("Versier") {
            tree.renderer.autoDecorate(); TreeStore.save(this, tree.renderer)
        }
        val wissen = button("Wissen") {
            tree.renderer.bulbs.clear(); TreeStore.save(this, tree.renderer)
        }
        buttons.addView(versier, LinearLayout.LayoutParams(0, px(48), 1f).apply { rightMargin = px(6) })
        buttons.addView(wissen, LinearLayout.LayoutParams(0, px(48), 1f).apply { rightMargin = px(6) })
        treeOnly.add(versier)
        treeOnly.add(wissen)
        buttons.addView(button("Achtergrond") { setAsWallpaper() },
            LinearLayout.LayoutParams(0, px(48), 1.3f).apply { rightMargin = px(6) })
        buttons.addView(button("⋯") { showMore() },
            LinearLayout.LayoutParams(px(48), px(48)).apply { rightMargin = px(6) })
        buttons.addView(button("ⓘ") { showCredits() }, LinearLayout.LayoutParams(px(48), px(48)))
        photoBtn = button("📷 Kies je eigen foto") { pickPhoto() }
        photoBtn.visibility = View.GONE
        panel.addView(photoBtn, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px(48)).apply {
            topMargin = px(10)
        })
        panel.addView(buttons, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = px(10)
        })

        root.addView(panel)
        return root
    }

    private fun button(label: String, onClick: () -> Unit) = Button(this).apply {
        text = label
        isAllCaps = false
        textSize = 13f
        setPadding(px(4), 0, px(4), 0)
        setTextColor(Color.WHITE)
        setTypeface(typeface, Typeface.BOLD)
        background = GradientDrawable().apply {
            setColor(0xFF2E7D32.toInt()); cornerRadius = px(12).toFloat()
        }
        setOnClickListener { onClick() }
    }

    private fun refreshBanner() {
        val text = tree.renderer.banner(SystemClock.uptimeMillis())
        banner.visibility = if (text == null || tree.renderer.isScene) View.GONE else View.VISIBLE
        if (text != null) banner.text = text
    }

    private fun refreshTreeLabel() {
        val r = tree.renderer
        val i = r.style
        val cat = r.category
        val start = when (cat) { 0 -> 0; 1 -> TreeStyles.TREES; else -> TreeStyles.TREES + HearthStyles.all.size }
        val len = when (cat) { 0 -> TreeStyles.TREES; 1 -> HearthStyles.all.size; else -> SceneHost.COUNT }
        val dots = (0 until len).joinToString(" ") { if (start + it == i) "●" else "○" }
        chips.forEachIndexed { k, b ->
            b.background = GradientDrawable().apply {
                setColor(if (k == cat) 0xFF8A5A00.toInt() else 0xFF1F2F28.toInt())
                cornerRadius = px(12).toFloat()
            }
        }
        treeLabel.text = "◀   ${TreeStyles.nameOf(i)}   ▶\n$dots"
        treeLabel.setOnTouchListener { v, ev ->
            if (ev.action == MotionEvent.ACTION_UP) {
                tree.switchTree(if (ev.x < v.width / 2f) -1 else 1)
            }
            true
        }
        // bediening per categorie
        treeOnly.forEach { it.visibility = if (cat == 2) View.GONE else View.VISIBLE }
        hintView.text = when (cat) {
            0 -> "Veeg om een andere boom te kiezen • tik op de boom om een lampje te hangen • tik op een lampje voor een andere kleur • houd ingedrukt om te verwijderen"
            1 -> if (r.isPhotoHearth) "Veeg om een andere haard te kiezen • tik op het vuur voor extra gloed"
                 else "Veeg om een andere haard te kiezen • tik op het vuur: het laait op • tik op de mantel om lampjes te hangen"
            else -> "Veeg om een andere scène te kiezen • " + tree.scenes.scene(r.sceneIndex).hint
        }
        photoBtn.visibility = if (r.isOwnPhoto) View.VISIBLE else View.GONE
        countdown.text = Calendar2.countdown()
        applyAudio()
        refreshBanner()
    }

    /** Geluid en microfoon volgen de gekozen scène en instellingen. */
    private fun applyAudio() {
        val r = tree.renderer
        Sound.enabled = ExtraPrefs.sound(this)
        Sound.set(if (r.isFireplace) Sound.FIRE else Sound.MUSIC)
        AudioLevel.sync(this, r.mode == TreeModes.MUSIC && !r.isScene && !r.isFireplace)
    }

    private fun askMic() {
        if (AudioLevel.hasPermission(this)) {
            applyAudio()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 31)
        }
    }

    private fun pickPhoto() {
        val i = Intent(Intent.ACTION_GET_CONTENT).setType("image/*")
        try {
            startActivityForResult(Intent.createChooser(i, "Kies een foto"), 21)
        } catch (e: Exception) {
            Toast.makeText(this, "Geen fotokiezer gevonden.", Toast.LENGTH_LONG).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 21 && resultCode == RESULT_OK && data?.data != null) {
            if (OwnPhoto.import(this, data.data!!)) {
                tree.renderer.reloadOwn()
                if (tree.renderer.bulbs.isEmpty()) tree.renderer.autoDecorate()
                TreeStore.save(this, tree.renderer)
                Toast.makeText(this, "Foto ingesteld — tik om lampjes te hangen", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Die foto kon niet worden gelezen.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(code: Int, perms: Array<out String>, results: IntArray) {
        super.onRequestPermissionsResult(code, perms, results)
        val ok = results.isNotEmpty() && results[0] == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (code == 31) {
            if (!ok) Toast.makeText(this, "Zonder microfoon kunnen de lampjes niet op de muziek bewegen.", Toast.LENGTH_LONG).show()
            applyAudio()
        } else if (code == 32) {
            if (ok) { ExtraPrefs.setNotify(this, true); DailyReceiver.schedule(this) }
            else Toast.makeText(this, "Zonder toestemming kan de app geen melding sturen.", Toast.LENGTH_LONG).show()
        }
    }

    private fun showMore() {
        val items = arrayOf(
            "🔊 Geluid: " + if (ExtraPrefs.sound(this)) "aan" else "uit",
            "📅 Automatisch naar de scène van de dag: " + if (ExtraPrefs.dailyAuto(this)) "aan" else "uit",
            "🔔 Dagelijkse melding (18:00): " + if (ExtraPrefs.notify(this)) "aan" else "uit",
            "🎯 Ga nu naar de scène van vandaag",
            "📷 Eigen foto kiezen"
        )
        AlertDialog.Builder(this)
            .setTitle("Meer")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> { ExtraPrefs.setSound(this, !ExtraPrefs.sound(this)); applyAudio() }
                    1 -> ExtraPrefs.setDailyAuto(this, !ExtraPrefs.dailyAuto(this))
                    2 -> {
                        if (ExtraPrefs.notify(this)) {
                            ExtraPrefs.setNotify(this, false); DailyReceiver.cancel(this)
                        } else if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                            android.content.pm.PackageManager.PERMISSION_GRANTED
                        ) {
                            ExtraPrefs.setNotify(this, true); DailyReceiver.schedule(this)
                        } else {
                            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 32)
                        }
                    }
                    3 -> {
                        val st = Calendar2.todayStyle()
                        if (st < 0) {
                            Toast.makeText(this, "Buiten het kerstseizoen (15 nov – 6 jan) is er geen scène van de dag.", Toast.LENGTH_LONG).show()
                        } else {
                            TreeStore.jump(this, tree.renderer, st)
                            refreshTreeLabel()
                        }
                    }
                    4 -> {
                        val idx = TreeStyles.all.indexOfFirst { it.photo == -2 }
                        TreeStore.jump(this, tree.renderer, idx)
                        refreshTreeLabel()
                        pickPhoto()
                    }
                }
            }
            .setNegativeButton("Sluiten", null)
            .show()
    }

    private fun refreshSwatches() {
        swatches.forEachIndexed { i, v ->
            v.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(v.tag as Int)
                if (i == tree.selectedColor) setStroke(px(3), Color.WHITE) else setStroke(px(1), 0x55FFFFFF)
            }
        }
    }

    private fun editGift(index: Int, message: String, unlock: Int, done: (String, Int) -> Unit) {
        val gifts = tree.scenes.gifts
        val choices = gifts.unlockChoices()
        val et = EditText(this).apply {
            setText(message)
            setSelection(text.length)
            hint = "Je bericht"
        }
        val spinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@TreeActivity, android.R.layout.simple_spinner_dropdown_item,
                choices.map { if (it == 0) "Altijd te openen" else "Te openen op ${gifts.unlockLabel(it)}" }
            )
            setSelection(choices.indexOf(unlock).coerceAtLeast(0))
        }
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(px(20), px(8), px(20), 0)
            addView(et)
            addView(spinner)
        }
        AlertDialog.Builder(this)
            .setTitle("Cadeautje ${index + 1}")
            .setView(box)
            .setPositiveButton("Bewaar") { _, _ ->
                done(et.text.toString().ifBlank { message }, choices[spinner.selectedItemPosition])
            }
            .setNegativeButton("Annuleer", null)
            .show()
    }

    private fun showCredits() {
        val tv = TextView(this).apply {
            text = Credits.asText()
            setPadding(px(20), px(12), px(20), px(12))
            textSize = 13f
            setTextIsSelectable(true)
        }
        val sv = ScrollView(this).apply { addView(tv) }
        AlertDialog.Builder(this).setTitle("Bronnen").setView(sv).setPositiveButton("OK", null).show()
    }

    private fun setAsWallpaper() {
        TreeStore.save(this, tree.renderer)
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(
            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
            ComponentName(this, TreeWallpaperService::class.java)
        )
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Kies 'Kerstboom' via Achtergronden in je instellingen.", Toast.LENGTH_LONG).show()
        }
    }

    private fun px(v: Int) = (v * dp).toInt()
}
