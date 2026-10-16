package nl.kerstlampjes.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var s: LightSettings
    private lateinit var preview: LightsView
    private lateinit var toggle: Button
    private lateinit var status: TextView
    private val dp get() = resources.displayMetrics.density

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tr.init(this)
        s = Prefs.load(this)
        setContentView(buildUi())

        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshState()
    }

    // ---------- UI ----------

    private fun buildUi(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(px(20), px(28), px(20), px(28))
        }

        root.addView(TextView(this).apply {
            text = Tr.t("🎄 Kerstlampjes")
            textSize = 28f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(0xFFFFF4E0.toInt())
        })
        status = TextView(this).apply {
            textSize = 14f
            setTextColor(0xFFB8C9BE.toInt())
            setPadding(0, px(4), 0, px(16))
        }
        root.addView(status)

        // Voorbeeld
        preview = LightsView(this)
        val frame = FrameLayout(this).apply {
            background = GradientDrawable().apply {
                setColor(0xFF15241C.toInt())
                cornerRadius = px(18).toFloat()
            }
            clipToOutline = true
            addView(preview, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            addView(TextView(this@MainActivity).apply {
                text = "Voorbeeld"
                setTextColor(0x66FFFFFF)
                gravity = Gravity.CENTER
            }, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }
        root.addView(frame, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px(220)))
        preview.apply(s)

        toggle = Button(this).apply {
            textSize = 17f
            setTextColor(Color.WHITE)
            setOnClickListener { onToggle() }
        }
        root.addView(toggle, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px(56)).apply {
            topMargin = px(18); bottomMargin = px(8)
        })

        root.addView(Button(this).apply {
            text = Tr.t("🎄 Bouw je kerstboom")
            textSize = 16f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                setColor(0xFF8A5A00.toInt()); cornerRadius = px(14).toFloat()
            }
            setOnClickListener { startActivity(Intent(this@MainActivity, TreeActivity::class.java)) }
        }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px(52)))

        root.addView(Button(this).apply {
            text = Tr.t("🎮 Speel met je rand")
            textSize = 16f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                setColor(0xFF1F5A8A.toInt()); cornerRadius = px(14).toFloat()
            }
            setOnClickListener { startActivity(Intent(this@MainActivity, PlayActivity::class.java)) }
        }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px(52)).apply { topMargin = px(10) })

        section(root, Tr.t("Effect"))
        root.addView(spinner(TreeModes.names.map { Tr.t(it) }.toTypedArray(), s.mode) {
            s = s.copy(mode = it)
            if (it == TreeModes.MUSIC && !AudioLevel.hasPermission(this)) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 3)
            }
        })
        section(root, Tr.t("Kleuren"))
        root.addView(spinner(Palettes.names.map { Tr.t(it) }.toTypedArray(), s.palette) { s = s.copy(palette = it) })

        section(root, Tr.t("Uiterlijk"))
        slider(root, Tr.t("Snelheid"), 1, 10, s.speed, "") { s = s.copy(speed = it) }
        slider(root, Tr.t("Grootte lampjes"), 6, 32, s.bulbSizeDp, " dp") { s = s.copy(bulbSizeDp = it) }
        slider(root, Tr.t("Afstand tussen lampjes"), 16, 100, s.spacingDp, " dp") { s = s.copy(spacingDp = it) }
        slider(root, Tr.t("Doorhang van de draad"), 0, 20, s.sagDp, " dp") { s = s.copy(sagDp = it) }
        slider(root, Tr.t("Helderheid"), 30, 100, s.opacity, " %") { s = s.copy(opacity = it) }

        section(root, Tr.t("Pasvorm scherm"))
        slider(root, Tr.t("Afronding hoeken"), 0, 80, s.cornerDp, " dp") { s = s.copy(cornerDp = it) }
        slider(root, Tr.t("Afstand tot rand"), 0, 40, s.insetDp, " dp") { s = s.copy(insetDp = it) }

        check(root, Tr.t("Alleen op het beginscherm (niet boven apps)"), s.homeOnly) { on ->
            s = s.copy(homeOnly = on)
            if (on && !HomeOnly.hasAccess(this)) askUsageAccess()
        }

        section(root, Tr.t("Versiering"))
        check(root, Tr.t("Lampjes tonen"), s.showLights) { s = s.copy(showLights = it) }
        Decor.flags.forEachIndexed { i, f ->
            check(root, Tr.t(Decor.names[i]), (s.decor and f) != 0) { on ->
                s = s.copy(decor = if (on) s.decor or f else s.decor and f.inv())
            }
        }
        section(root, Tr.t("Treintje"))
        root.addView(spinner(Decor.trainViews.map { Tr.t(it) }.toTypedArray(), if (s.trainSide) 1 else 0) {
            s = s.copy(trainSide = it == 1)
        })
        slider(root, Tr.t("Aantal wagons"), 1, 8, s.trainWagons, "") { s = s.copy(trainWagons = it) }
        check(root, Tr.t("Lampjes op de trein"), s.trainLights) { s = s.copy(trainLights = it) }
        check(root, Tr.t("Ballen in de slinger"), s.garlandBalls) { s = s.copy(garlandBalls = it) }
        section(root, Tr.t("Lichtkrant"))
        root.addView(EditText(this).apply {
            setText(s.marquee)
            hint = Tr.t("Fijne feestdagen!")
            setTextColor(0xFFE8EFEA.toInt())
            setHintTextColor(0xFF6F8577.toInt())
            inputType = InputType.TYPE_CLASS_TEXT
            filters = arrayOf(InputFilter.LengthFilter(80))
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(e: Editable?) { s = s.copy(marquee = e?.toString() ?: ""); changed() }
                override fun beforeTextChanged(a: CharSequence?, b: Int, c: Int, d: Int) {}
                override fun onTextChanged(a: CharSequence?, b: Int, c: Int, d: Int) {}
            })
        })

        root.addView(TextView(this).apply {
            text = Tr.t("Tip: voeg de tegel \"Kerstlampjes\" toe aan je snelle instellingen om ze met één veeg aan en uit te zetten.")
            setTextColor(0xFF8FA597.toInt())
            textSize = 13f
            setPadding(0, px(20), 0, 0)
        })

        return ScrollView(this).apply { addView(root) }
    }

    private fun section(parent: LinearLayout, title: String) {
        parent.addView(TextView(this).apply {
            text = title.uppercase()
            textSize = 12f
            letterSpacing = 0.08f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(0xFFE0B35A.toInt())
            setPadding(0, px(20), 0, px(6))
        })
    }

    private fun spinner(items: Array<String>, selected: Int, onPick: (Int) -> Unit): Spinner =
        Spinner(this).apply {
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, items)
            setSelection(selected.coerceIn(0, items.size - 1), false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    onPick(pos); changed()
                }
                override fun onNothingSelected(p: AdapterView<*>?) {}
            }
        }

    private fun askUsageAccess() {
        android.app.AlertDialog.Builder(this)
            .setTitle(Tr.t("Toegang nodig"))
            .setMessage(Tr.t("Om te zien of je op het beginscherm bent, heeft Kerstlampjes toegang tot gebruiksgegevens nodig. Het blijft op je telefoon: er wordt niets opgeslagen of verstuurd. Zet Kerstlampjes aan in het volgende scherm."))
            .setPositiveButton(Tr.t("Toegang geven")) { _, _ ->
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
            .setNegativeButton(Tr.t("Later"), null)
            .show()
    }

    private fun check(parent: LinearLayout, label: String, value: Boolean, onSet: (Boolean) -> Unit) {
        parent.addView(CheckBox(this).apply {
            text = label
            setTextColor(0xFFE8EFEA.toInt())
            textSize = 15f
            isChecked = value
            setOnCheckedChangeListener { _, on -> onSet(on); changed() }
        })
    }

    private fun slider(parent: LinearLayout, label: String, min: Int, max: Int, value: Int, unit: String, onSet: (Int) -> Unit) {
        val tv = TextView(this).apply {
            setTextColor(0xFFE8EFEA.toInt())
            textSize = 15f
            setPadding(0, px(8), 0, 0)
            text = "$label: $value$unit"
        }
        parent.addView(tv)
        parent.addView(SeekBar(this).apply {
            this.max = max - min
            progress = (value - min).coerceIn(0, max - min)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, p: Int, fromUser: Boolean) {
                    val v = p + min
                    tv.text = "$label: $v$unit"
                    if (fromUser) { onSet(v); changed() }
                }
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            })
        })
    }

    // ---------- logica ----------

    private fun changed() {
        Prefs.save(this, s)
        preview.apply(s)
        LightsService.update(this)
    }

    private fun onToggle() {
        if (!Settings.canDrawOverlays(this)) {
            startActivity(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            )
            return
        }
        if (LightsService.isRunning) LightsService.stop(this) else LightsService.start(this)
        toggle.postDelayed({ refreshState() }, 250)
    }

    private fun refreshState() {
        val canDraw = Settings.canDrawOverlays(this)
        val on = LightsService.isRunning
        when {
            !canDraw -> {
                toggle.text = Tr.t("Toestemming geven")
                status.text = Tr.t("Geef toestemming om boven andere apps te tekenen.")
                tint(0xFFB26A00.toInt())
            }
            on -> {
                toggle.text = Tr.t("Lampjes uitzetten")
                status.text = Tr.t("De lampjes branden ✨")
                tint(0xFF7A1F1F.toInt())
            }
            else -> {
                toggle.text = Tr.t("Lampjes aanzetten")
                status.text = Tr.t("Lampjes staan uit.")
                tint(0xFF2E7D32.toInt())
            }
        }
    }

    private fun tint(color: Int) {
        toggle.background = GradientDrawable().apply {
            setColor(color)
            cornerRadius = px(14).toFloat()
        }
    }

    private fun px(v: Int) = (v * dp).toInt()
}
