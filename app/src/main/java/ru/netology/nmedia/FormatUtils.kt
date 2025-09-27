package ru.netology.nmedia

fun formatCount(count: Int): String {
    if (count < 1000) return count.toString()

    val thousand = 1000
    val million = 1_000_000

    return when {
        count < 10_000 -> {
            val main = count / thousand
            val fraction = (count % thousand) / 100
            "${main}.${fraction}K"
        }
        count < million -> {
            "${count / thousand}K"
        }
        else -> {
            val main = count / million
            val fraction = (count % million) / 100_000
            "${main}.${fraction}M"
        }
    }
}
