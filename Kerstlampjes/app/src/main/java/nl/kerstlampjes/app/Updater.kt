package nl.kerstlampjes.app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Controleert op een nieuwe versie door de nieuwste release op GitHub op te vragen.
 * Alleen op verzoek van de gebruiker (menu), nooit op de achtergrond. De app downloadt of
 * installeert zelf niets: de releasepagina opent in de browser.
 */
object Updater {
    const val REPO = "ericbruggema/kerstlampjes"
    const val REPO_URL = "https://github.com/$REPO"
    private const val API = "https://api.github.com/repos/$REPO/releases/latest"

    class Latest(val version: String, val page: String)

    /** "v2.10" -> [2, 10]; onbekende delen tellen als 0. */
    fun parts(v: String): List<Int> =
        v.trim().removePrefix("v").removePrefix("V").split('.', '-', '+')
            .map { p -> p.takeWhile { it.isDigit() }.toIntOrNull() ?: 0 }

    /** True als [latest] hoger is dan [current]. */
    fun isNewer(latest: String, current: String): Boolean {
        val a = parts(latest)
        val b = parts(current)
        for (i in 0 until maxOf(a.size, b.size)) {
            val x = a.getOrElse(i) { 0 }
            val y = b.getOrElse(i) { 0 }
            if (x != y) return x > y
        }
        return false
    }

    fun parse(json: String): Latest? {
        val o = JSONObject(json)
        val tag = o.optString("tag_name")
        if (tag.isBlank()) return null
        return Latest(tag.removePrefix("v"), o.optString("html_url", "$REPO_URL/releases"))
    }

    private fun fetch(): Latest? {
        val c = URL(API).openConnection() as HttpURLConnection
        try {
            c.connectTimeout = 8000
            c.readTimeout = 8000
            c.setRequestProperty("Accept", "application/vnd.github+json")
            c.setRequestProperty("User-Agent", "Kerstlampjes")
            if (c.responseCode != 200) return null
            return parse(c.inputStream.bufferedReader().use { it.readText() })
        } finally {
            c.disconnect()
        }
    }

    fun check(activity: Activity) {
        val current = try {
            activity.packageManager.getPackageInfo(activity.packageName, 0).versionName ?: "0"
        } catch (e: Exception) {
            "0"
        }
        Toast.makeText(activity, Tr.t("Controleren…"), Toast.LENGTH_SHORT).show()
        Thread {
            val latest = try { fetch() } catch (e: Exception) { null }
            activity.runOnUiThread {
                if (activity.isFinishing || activity.isDestroyed) return@runOnUiThread
                show(activity, latest, current)
            }
        }.start()
    }

    private fun show(activity: Activity, latest: Latest?, current: String) {
        val b = AlertDialog.Builder(activity).setTitle(Tr.t("Updates"))
        when {
            latest == null ->
                b.setMessage(Tr.t("Controleren is niet gelukt. Ben je online?"))
                    .setPositiveButton("OK", null)
            isNewer(latest.version, current) ->
                b.setMessage(Tr.f("Er is een nieuwe versie: %s (jij hebt %s).", latest.version, current))
                    .setPositiveButton(Tr.t("Downloaden")) { _, _ ->
                        try {
                            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(latest.page)))
                        } catch (e: Exception) {
                            Toast.makeText(activity, latest.page, Toast.LENGTH_LONG).show()
                        }
                    }
                    .setNegativeButton(Tr.t("Later"), null)
            else ->
                b.setMessage(Tr.f("Je hebt de nieuwste versie (%s).", current))
                    .setPositiveButton("OK", null)
        }
        b.show()
    }
}
