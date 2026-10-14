package nl.eric.kerstlampjes

import android.content.Context
import java.util.Locale

/**
 * Eenvoudige vertaling: de Nederlandse tekst is de sleutel.
 * Talen: nl, en, de, fr, es, zh. Onbekende talen krijgen Engels.
 */
object Tr {
    val languages = arrayOf("auto", "nl", "en", "de", "fr", "es", "zh")
    val languageNames = arrayOf("Auto", "Nederlands", "English", "Deutsch", "Français", "Español", "中文")

    @Volatile var lang: String = fromLocale(Locale.getDefault().language)
        private set

    private fun fromLocale(l: String) = when (l) {
        "nl", "de", "fr", "es", "zh" -> l
        else -> "en"
    }

    /** Neemt de gekozen taal uit de instellingen over (auto = taal van het toestel). */
    fun init(ctx: Context) {
        val pref = ctx.getSharedPreferences("kerstlampjes", Context.MODE_PRIVATE).getString("x_lang", "auto") ?: "auto"
        lang = if (pref == "auto") fromLocale(Locale.getDefault().language) else fromLocale(pref)
    }

    /** Zet direct een taal (voor tests). */
    fun use(language: String) { lang = fromLocale(language) }

    fun setPreference(ctx: Context, pref: String) {
        ctx.getSharedPreferences("kerstlampjes", Context.MODE_PRIVATE).edit().putString("x_lang", pref).apply()
        init(ctx)
    }

    fun preference(ctx: Context) =
        ctx.getSharedPreferences("kerstlampjes", Context.MODE_PRIVATE).getString("x_lang", "auto") ?: "auto"

    fun t(nl: String): String = translate(nl, lang)

    fun translate(nl: String, language: String): String {
        if (language == "nl") return nl
        return Translations.forLang(language)[nl] ?: Translations.forLang("en")[nl] ?: nl
    }

    fun f(nl: String, vararg args: Any?): String = String.format(Locale.ROOT, t(nl), *args)
}
