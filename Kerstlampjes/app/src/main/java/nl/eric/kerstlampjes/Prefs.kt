package nl.eric.kerstlampjes

import android.content.Context

/** Alle instellingen van de lampjes. */
data class LightSettings(
    val bulbSizeDp: Int = 14,      // grootte lampje
    val spacingDp: Int = 44,       // afstand tussen lampjes
    val speed: Int = 5,            // 1..10
    val mode: Int = 1,             // zie Modes
    val palette: Int = 0,          // zie Palettes
    val cornerDp: Int = 28,        // afronding hoeken (volg je schermhoeken)
    val insetDp: Int = 4,          // afstand van de schermrand
    val opacity: Int = 100,        // 30..100 %
    val sagDp: Int = 6             // doorhang van de draad
)

object Modes {
    const val STEADY = 0
    const val TWINKLE = 1
    const val ALTERNATE = 2
    const val CHASE = 3
    const val WAVE = 4
    val names = arrayOf("Vast aan", "Fonkelen", "Om en om knipperen", "Lopend licht", "Kleurgolf")
}

/** Effecten in het boom-scherm: de lamp-effecten plus "Op de muziek" (microfoon). */
object TreeModes {
    const val MUSIC = 5
    val names = Modes.names + "Op de muziek"
}

object Palettes {
    val names = arrayOf("Klassiek bont", "Warm wit", "Rood / groen / goud", "IJsblauw / wit", "Roze / paars")
    val colors: Array<IntArray> = arrayOf(
        intArrayOf(0xFFFF3B30.toInt(), 0xFFFFD54F.toInt(), 0xFF4CD964.toInt(), 0xFF40A9FF.toInt(), 0xFFFF9500.toInt(), 0xFFFF4FD8.toInt()),
        intArrayOf(0xFFFFE3A8.toInt(), 0xFFFFD27F.toInt(), 0xFFFFEBC4.toInt()),
        intArrayOf(0xFFFF3B30.toInt(), 0xFF34C759.toInt(), 0xFFFFC94A.toInt()),
        intArrayOf(0xFF7FD4FF.toInt(), 0xFFFFFFFF.toInt(), 0xFF3D7BFF.toInt()),
        intArrayOf(0xFFFF6FB5.toInt(), 0xFFB36BFF.toInt(), 0xFFFFC2E2.toInt())
    )
}

object Prefs {
    private const val FILE = "kerstlampjes"

    fun treeMode(ctx: Context) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).getInt("tree_mode", Modes.TWINKLE)

    fun treeOrient(ctx: Context) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).getInt("tree_orient", 0)

    fun treeSpeed(ctx: Context) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).getInt("tree_speed", 5)

    fun load(ctx: Context): LightSettings {
        val p = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val d = LightSettings()
        return LightSettings(
            bulbSizeDp = p.getInt("size", d.bulbSizeDp),
            spacingDp = p.getInt("spacing", d.spacingDp),
            speed = p.getInt("speed", d.speed),
            mode = p.getInt("mode", d.mode),
            palette = p.getInt("palette", d.palette),
            cornerDp = p.getInt("corner", d.cornerDp),
            insetDp = p.getInt("inset", d.insetDp),
            opacity = p.getInt("opacity", d.opacity),
            sagDp = p.getInt("sag", d.sagDp)
        )
    }

    fun save(ctx: Context, s: LightSettings) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit()
            .putInt("size", s.bulbSizeDp)
            .putInt("spacing", s.spacingDp)
            .putInt("speed", s.speed)
            .putInt("mode", s.mode)
            .putInt("palette", s.palette)
            .putInt("corner", s.cornerDp)
            .putInt("inset", s.insetDp)
            .putInt("opacity", s.opacity)
            .putInt("sag", s.sagDp)
            .apply()
    }
}
