package nl.kerstlampjes.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdaterTest {
    @Test
    fun newerVersionIsDetected() {
        assertTrue(Updater.isNewer("2.3", "2.2"))
        assertTrue(Updater.isNewer("v2.10", "2.9"))
        assertTrue(Updater.isNewer("3.0", "2.99"))
        assertTrue(Updater.isNewer("2.2.1", "2.2"))
    }

    @Test
    fun sameOrOlderIsNotNewer() {
        assertFalse(Updater.isNewer("2.2", "2.2"))
        assertFalse(Updater.isNewer("v2.2", "2.2.0"))
        assertFalse(Updater.isNewer("2.1", "2.2"))
    }

    @Test
    fun parsesRelease() {
        val r = Updater.parse("""{"tag_name":"v2.3","html_url":"https://github.com/x/y/releases/tag/v2.3"}""")!!
        assertEquals("2.3", r.version)
        assertEquals("https://github.com/x/y/releases/tag/v2.3", r.page)
        assertNull(Updater.parse("""{"message":"Not Found"}"""))
    }
}
