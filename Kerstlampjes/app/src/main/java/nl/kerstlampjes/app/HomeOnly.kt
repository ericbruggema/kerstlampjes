package nl.kerstlampjes.app

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process

/**
 * "Alleen op het beginscherm": met toegang tot gebruiksgegevens zien we welke app voorop staat.
 * Er wordt niets opgeslagen of verstuurd; het blijft op je telefoon.
 */
object HomeOnly {
    fun hasAccess(ctx: Context): Boolean {
        val ops = ctx.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= 29)
            ops.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), ctx.packageName)
        else
            @Suppress("DEPRECATION") ops.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), ctx.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    class Tracker(private val ctx: Context) {
        private val usm = ctx.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        private var since = System.currentTimeMillis() - 6 * 3600_000L
        private var last: String? = null
        private val launchers: Set<String> = run {
            val home = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            ctx.packageManager.queryIntentActivities(home, PackageManager.MATCH_ALL).map { it.activityInfo.packageName }.toSet()
        }

        /** true = beginscherm (of onze eigen app), false = een andere app voorop, null = onbekend (geen toegang). */
        fun isHome(): Boolean? {
            if (!hasAccess(ctx)) return null
            val now = System.currentTimeMillis()
            try {
                val ev = usm.queryEvents(since, now)
                val e = UsageEvents.Event()
                while (ev.hasNextEvent()) {
                    ev.getNextEvent(e)
                    if (e.eventType == UsageEvents.Event.ACTIVITY_RESUMED && e.packageName != "com.android.systemui") last = e.packageName
                }
                since = now - 2000
            } catch (ex: Exception) {
                return null
            }
            val p = last ?: return null
            return p in launchers || p == ctx.packageName
        }
    }
}
