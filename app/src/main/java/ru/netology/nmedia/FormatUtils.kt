package ru.netology.nmedia

import java.math.RoundingMode
import java.text.DecimalFormat

fun formatCount(count: Long): String {
    if (count < 1000) return count.toString()

    val df = DecimalFormat("#.0")
    df.roundingMode = RoundingMode.DOWN

    return when {
        count < 10_000 -> {
            df.format(count / 1000.0) + "K"
        }
        count < 1_000_000 -> {
            (count / 1000).toString() + "K"
        }
        count < 10_000_000 -> {
            df.format(count / 1_000_000.0) + "M"
        }
        else -> {
            (count / 1_000_000).toString() + "M"
        }
    }
}
