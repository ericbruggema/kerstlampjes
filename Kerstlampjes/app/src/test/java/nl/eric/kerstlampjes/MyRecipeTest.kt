package nl.eric.kerstlampjes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MyRecipeTest {
    @Test
    fun recipeSurvivesJsonRoundTrip() {
        val r = MyScenes.starter()
        r.fx.add(MyFx(MyKinds.FIRE, 70, 6, 2, "🔥", 0.3f, 0.9f, 40))
        val back = MyRecipe.fromJson(r.toJson())
        assertEquals(r.name, back.name)
        assertEquals(r.fx.size, back.fx.size)
        assertEquals(MyKinds.FIRE, back.fx.last().kind)
        assertEquals(2, back.fx.last().color)
        assertEquals(r.stickers.size, back.stickers.size)
        assertEquals(r.stickers[1].msg, back.stickers[1].msg)
        assertEquals(r.strings[0].pts.size, back.strings[0].pts.size)
    }

    @Test
    fun brokenValuesAreClamped() {
        val o = org.json.JSONObject("""{"fx":[{"k":99,"a":5000,"s":-3,"c":42}],"st":[{"e":"x","z":50,"an":99}],"ls":[{"p":[0.1,0.1]}]}""")
        val r = MyRecipe.fromJson(o)
        assertTrue(r.fx[0].kind in MyKinds.names.indices)
        assertEquals(100, r.fx[0].amount)
        assertEquals(1, r.fx[0].speed)
        assertTrue(r.stickers[0].size <= 1f)
        assertTrue(r.stickers[0].anim in MyLook.anims.indices)
        assertEquals(0, r.strings.size)   // te weinig punten
    }
}
