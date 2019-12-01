package dev.acraig.adventcalendar.y2019.day1

fun main() {
    val input = Unit::class.java.getResource("/day1_input.csv").readText().lines().filter { it.isNotEmpty() }
    val result = input.map { it.toLong() }.map { fuel(it) }.sum()
    println(result)
}

private fun fuel(amt:Long):Long {
    return amt / 3 - 2
}