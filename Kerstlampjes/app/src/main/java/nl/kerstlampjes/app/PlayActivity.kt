package nl.kerstlampjes.app

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

/**
 * Speelscherm: de rand van je scherm met alle versiering, en hier kun je er wél op tikken.
 * (De overlay boven andere apps laat alle tikken door naar de app eronder.)
 */
class PlayActivity : Activity() {

    private lateinit var lights: LightsView
    private lateinit var shake: ShakeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tr.init(this)
        lights = LightsView(this).apply { interactive = true }
        shake = ShakeDetector(this) { runOnUiThread { lights.shake() } }
        val root = FrameLayout(this).apply {
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(0xFF0B1730.toInt(), 0xFF15241C.toInt())
            )
            addView(lights, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            addView(TextView(this@PlayActivity).apply {
                text = Tr.t("Tik op ijspegels en vallende sterren, tik ergens voor vuurwerk en schud je telefoon om de sneeuw weg te schudden.")
                setTextColor(0x99FFFFFF.toInt())
                textSize = 15f
                gravity = Gravity.CENTER
                setPadding(dp(56), 0, dp(56), 0)
            }, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }
        setContentView(root)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        Sound.enabled = ExtraPrefs.sound(this)
    }

    override fun onResume() {
        super.onResume()
        lights.apply(Prefs.load(this))
        shake.start()
    }

    override fun onPause() {
        shake.stop()
        super.onPause()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
