package nl.eric.kerstlampjes

import android.content.Context
import android.os.BatteryManager
import android.os.PowerManager
import android.os.SystemClock

/** Beeldsnelheid: normaal 30 fps, zuinig 15 fps; "auto" schakelt bij batterijbesparing of een lege batterij. */
object Perf {
    private var checkedAt = -100_000L
    private var cached = 33L

    fun frameDelay(ctx: Context): Long {
        val now = SystemClock.uptimeMillis()
        if (now - checkedAt < 5000) return cached
        checkedAt = now
        cached = when (ExtraPrefs.fps(ctx)) {
            0 -> 33L
            1 -> 66L
            else -> if (lowPower(ctx)) 66L else 33L
        }
        return cached
    }

    private fun lowPower(ctx: Context): Boolean = try {
        val pm = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
        val bm = ctx.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val charging = bm.isCharging
        pm.isPowerSaveMode || (level in 1..20 && !charging)
    } catch (e: Exception) {
        false
    }
}
