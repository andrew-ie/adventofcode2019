package dev.acraig.adventofcode.y2019.day3

fun main() {
    val input = Unit::class.java.getResource("/day3_input.txt").readText().lines().filter { it.isNotBlank() }
    val paths = input.map { it.split(",") }.map {
        genCoordinates(
            it
        )
    }
    val uniqueLocations = paths.map { it.toSet() }
    val intersections = uniqueLocations[0].filter { uniqueLocations[1].contains(it) }.filter { it != listOf(0, 0) }
    println(intersections.map { Pair(it, sumDistance(it, paths)) }.minBy { it.second }!!)

}

fun sumDistance(coordinates: List<Int>, paths: Collection<List<List<Int>>>):Int {
    return paths.map { it.indexOf(coordinates) }.sum()
}