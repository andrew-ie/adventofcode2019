package dev.acraig.adventofcode.y2019.day1

fun main() {
    val input = Unit::class.java.getResource("/day1_input.csv").readText().lines().filter { it.isNotEmpty() }
    val result = input.map { it.toLong() }.map {
        accumulatedFuel(
            it
        )
    }.sum()
    println(result)
}

private tailrec fun accumulatedFuel(amt:Long, accumulator:Long = 0):Long {
    val fuelRequired = amt / 3 - 2
    if (fuelRequired <= 0) {
        return accumulator
    }
    return accumulatedFuel(
        fuelRequired,
        accumulator + fuelRequired
    )
}