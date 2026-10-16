package nl.kerstlampjes.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RimDecorTest {
    @Test
    fun pathIsClosedAndStartsOnTopEdge() {
        val rp = RimPath(10f, 10f, 110f, 210f, 20f)
        rp.at(0f)
        val sx = rp.x; val sy = rp.y
        assertEquals(30f, sx, 0.01f); assertEquals(10f, sy, 0.01f)
        rp.at(rp.length)
        assertEquals(sx, rp.x, 0.01f); assertEquals(sy, rp.y, 0.01f)
    }

    @Test
    fun lengthMatchesRoundedRectangle() {
        val rp = RimPath(0f, 0f, 100f, 200f, 20f)
        val expected = 2 * (100 - 40) + 2 * (200 - 40) + 2 * Math.PI.toFloat() * 20f
        assertEquals(expected, rp.length, 0.01f)
    }

    @Test
    fun pointsStayInsideAndDirectionIsUnit() {
        val rp = RimPath(5f, 5f, 95f, 155f, 30f)
        var s = 0f
        while (s < rp.length) {
            rp.at(s)
            assertTrue("punt $s ligt buiten de rechthoek",
                rp.x in 4.99f..95.01f && rp.y in 4.99f..155.01f)
            assertEquals(1f, rp.tx * rp.tx + rp.ty * rp.ty, 0.01f)
            s += 7f
        }
    }

    @Test
    fun everyDecorationHasAName() {
        assertEquals(Decor.flags.size, Decor.names.size)
        assertEquals(Decor.ALL, Decor.flags.fold(0) { a, b -> a or b })
    }
}
