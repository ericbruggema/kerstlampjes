package nl.kerstlampjes.app

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Controleert dat elke zichtbare tekst in alle talen is vertaald en dat plaatsaanduidingen kloppen. */
class TranslationsTest {
    private val langs = listOf("en", "de", "fr", "es", "zh")

    private fun sourceDir(): File {
        val candidates = listOf(
            File("src/main/java/nl/kerstlampjes/app"),
            File("app/src/main/java/nl/kerstlampjes/app")
        )
        return candidates.first { it.isDirectory }
    }

    private fun literalKeys(): Set<String> {
        val re = Regex("Tr\\.[tf]\\(\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
        val keys = LinkedHashSet<String>()
        sourceDir().listFiles { f -> f.extension == "kt" && f.name != "Translations.kt" }!!.forEach { f ->
            re.findAll(f.readText()).forEach { keys.add(it.groupValues[1].replace("\\\"", "\"")) }
        }
        return keys
    }

    private fun namedKeys(): Set<String> {
        val keys = LinkedHashSet<String>()
        keys.addAll(Modes.names)
        keys.addAll(TreeModes.names)
        keys.addAll(Palettes.names)
        keys.addAll(LampStyles.names)
        keys.addAll(Sound.songNames)
        keys.addAll(MyKinds.names)
        keys.addAll(MyLook.anims)
        keys.addAll(MyLook.taps)
        keys.addAll(MyLook.firePalettes)
        for (i in 0 until TreeStyles.COUNT) keys.add(TreeStyles.keyOf(i))
        keys.addAll(listOf("Lampjes omlaag", "Lampjes omhoog", "Willekeurig"))
        // lijsten in Scenes.kt (berichten, labels, hints)
        val scenes = File(sourceDir(), "Scenes.kt").readText()
        Regex("private val (?:messages|labels) = arrayOf\\((.*?)\\n    \\)", RegexOption.DOT_MATCHES_ALL)
            .findAll(scenes).forEach { block ->
                Regex("\"([^\"]+)\"").findAll(block.groupValues[1]).forEach { keys.add(it.groupValues[1]) }
            }
        Regex("override val hint = \"([^\"]+)\"").findAll(scenes).forEach { keys.add(it.groupValues[1]) }
        return keys
    }

    private fun placeholders(s: String) =
        Regex("%[0-9.$]*[a-zA-Z]").findAll(s).map { it.value }.toList().sorted()

    @Test
    fun everyKeyIsTranslatedInEveryLanguage() {
        val keys = literalKeys() + namedKeys()
        assertTrue("geen sleutels gevonden", keys.size > 100)
        val problems = ArrayList<String>()
        for (lang in langs) {
            val map = Translations.forLang(lang)
            for (k in keys) {
                val v = map[k]
                if (v == null || v.isBlank()) problems.add("$lang mist: $k")
            }
        }
        assertTrue(problems.joinToString("\n"), problems.isEmpty())
    }

    @Test
    fun placeholdersMatchTheDutchOriginal() {
        val problems = ArrayList<String>()
        for (lang in langs) {
            for ((k, v) in Translations.forLang(lang)) {
                if (placeholders(k) != placeholders(v)) problems.add("$lang: '$k' -> '$v'")
            }
        }
        assertTrue(problems.joinToString("\n"), problems.isEmpty())
    }

    @Test
    fun unknownLanguageFallsBackToEnglish() {
        assertEquals(Tr.translate("Wissen", "en"), Tr.translate("Wissen", "xx").let { Translations.forLang("xx")["Wissen"] })
        assertEquals("Wissen", Tr.translate("Wissen", "nl"))
    }
}
