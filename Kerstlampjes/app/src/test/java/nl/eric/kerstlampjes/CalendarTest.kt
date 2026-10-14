package nl.eric.kerstlampjes

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarTest {
    @Test
    fun seasonBoundaries() {
        assertFalse(Calendar2.inSeason(LocalDate.of(2026, 11, 14)))
        assertTrue(Calendar2.inSeason(LocalDate.of(2026, 11, 15)))
        assertTrue(Calendar2.inSeason(LocalDate.of(2026, 12, 31)))
        assertTrue(Calendar2.inSeason(LocalDate.of(2027, 1, 6)))
        assertFalse(Calendar2.inSeason(LocalDate.of(2027, 1, 7)))
        assertFalse(Calendar2.inSeason(LocalDate.of(2026, 7, 1)))
    }

    @Test
    fun sceneOfTheDay() {
        assertEquals(-1, Calendar2.sceneFor(LocalDate.of(2026, 7, 1)))
        assertEquals(3, Calendar2.sceneFor(LocalDate.of(2026, 11, 20)))   // kerstdorp
        assertEquals(8, Calendar2.sceneFor(LocalDate.of(2026, 12, 5)))    // pakjesavond
        assertEquals(7, Calendar2.sceneFor(LocalDate.of(2026, 12, 24)))   // kerststal
        assertEquals(8, Calendar2.sceneFor(LocalDate.of(2026, 12, 25)))
        assertEquals(0, Calendar2.sceneFor(LocalDate.of(2026, 12, 31)))   // sneeuwbol
        assertEquals(0, Calendar2.sceneFor(LocalDate.of(2027, 1, 1)))
        assertEquals(6, Calendar2.sceneFor(LocalDate.of(2027, 1, 3)))     // ijsbaan
    }

    @Test
    fun adventSundayShowsTheWreath() {
        // 4e advent 2026 = zondag 20 december, 1e advent = 29 november
        assertEquals(2, Calendar2.sceneFor(LocalDate.of(2026, 12, 20)))
        assertEquals(2, Calendar2.sceneFor(LocalDate.of(2026, 11, 29)))
    }

    @Test
    fun countdownCountsDays() {
        Tr.use("nl")
        assertEquals("🎄 Nog 10 dagen tot kerst", Calendar2.countdown(LocalDate.of(2026, 12, 15)))
        assertEquals("🎄 Het is kerst! Vrolijk kerstfeest", Calendar2.countdown(LocalDate.of(2026, 12, 25)))
        Tr.use("en")
        assertEquals("🎄 10 days until Christmas", Calendar2.countdown(LocalDate.of(2026, 12, 15)))
    }
}
