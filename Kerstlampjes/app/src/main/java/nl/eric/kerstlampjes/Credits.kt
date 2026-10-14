package nl.eric.kerstlampjes

/** Bronnen en licenties van de gebruikte foto's (alle van Wikimedia Commons). */
object Credits {
    class Photo(
        val asset: String,       // bestand in assets/hearths/
        val title: String,       // titel van het bestand op Commons
        val author: String,
        val license: String,
        val page: String         // pagina op Commons
    )

    const val RETRIEVED = "14 oktober 2026"

    val photos = arrayOf(
        Photo(
            "fire-burning.jpg", "Fire burning in a fireplace.jpg", "Pöllö", "CC BY 3.0",
            "https://commons.wikimedia.org/wiki/File:Fire_burning_in_a_fireplace.jpg"
        ),
        Photo(
            "cozy-christmas.jpg", "Cozy Christmas Fireplace with stockings.jpg", "Lindsey Turner", "CC BY 2.0",
            "https://commons.wikimedia.org/wiki/File:Cozy_Christmas_Fireplace_with_stockings.jpg"
        ),
        Photo(
            "rostov.jpg", "Fireplace, Wood fire, Rostov-on-Don, Russia.jpg", "Vyacheslav Argenberg", "CC BY 4.0",
            "https://commons.wikimedia.org/wiki/File:Fireplace,_Wood_fire,_Rostov-on-Don,_Russia.jpg"
        ),
        Photo(
            "fireplace-burning.jpg", "Fireplace Burning.jpg", "onbekend (publiek domein)", "Publiek domein",
            "https://commons.wikimedia.org/wiki/File:Fireplace_Burning.jpg"
        )
    )

    val trees = arrayOf(
        Photo(
            "capitol.jpg", "2020 Capitol Christmas Tree with Lights (50676367266).jpg", "USCapitol", "Publiek domein",
            "https://commons.wikimedia.org/wiki/File:2020_Capitol_Christmas_Tree_with_Lights_(50676367266).jpg"
        ),
        Photo(
            "china.jpg", "Weihnachtsbaum im China Garten 20241225.jpg", "PantheraLeo1359531", "CC BY 4.0",
            "https://commons.wikimedia.org/wiki/File:Weihnachtsbaum_im_China_Garten_20241225.jpg"
        ),
        Photo(
            "lorenz.jpg", "Weihnachtsbaum am Lorenzpark 20241130 HOF8432 RAW-Export.jpg", "PantheraLeo1359531", "CC BY 4.0",
            "https://commons.wikimedia.org/wiki/File:Weihnachtsbaum_am_Lorenzpark_20241130_HOF8432_RAW-Export.jpg"
        ),
        Photo(
            "park.jpg", "Christmas Tree in the Park - Flickr - Corey Leopold.jpg", "Corey Leopold", "CC BY 2.0",
            "https://commons.wikimedia.org/wiki/File:Christmas_Tree_in_the_Park_-_Flickr_-_Corey_Leopold.jpg"
        ),
        Photo(
            "white.jpg", "White Christmas Tree Lights at Night.jpg", "photos-public-domain.com", "CC0",
            "https://commons.wikimedia.org/wiki/File:White_Christmas_Tree_Lights_at_Night.jpg"
        )
    )

    fun shortTree(i: Int): String = "Foto: ${trees[i].author} · ${trees[i].license}"

    fun short(i: Int): String = "Foto: ${photos[i].author} · ${photos[i].license}"

    fun asText(): String = buildString {
        append("Foto's van Wikimedia Commons (opgehaald $RETRIEVED). ")
        append("Voor gebruik in de app zijn ze verkleind en bijgesneden.\n\n")
        photos.forEach {
            append("• ${it.title}\n  ${it.author} — ${it.license}\n  ${it.page}\n\n")
        }
    }
}
