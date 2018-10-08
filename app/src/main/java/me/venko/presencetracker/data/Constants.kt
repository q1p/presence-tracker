package me.venko.presencetracker.data

import java.text.DecimalFormat

/**
 * @author Victor Kosenko
 *
 */
object Map {
    const val DEFAULT_ZOOM_FACTOR = 12.2f
}

object Location {
    val DISTANCE_DECIMAL_FORMAT_SHORT = DecimalFormat("0.#")
    const val KILOMETERS_FACTOR = 1000
}