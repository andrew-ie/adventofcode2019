package dev.acraig.adventofcode.y2019.day3

import kotlin.math.abs

fun main() {
    val input = Unit::class.java.getResource("/day3_input.txt").readText().lines().filter { it.isNotBlank() }
    val paths = input.map { it.split(",") }.map {
        genCoordinates(
            it
        )
    }
    val uniqueLocations = paths.map { it.toSet() }
    val intersections = uniqueLocations[0].filter { uniqueLocations[1].contains(it) }.filter { it != listOf(0, 0) }
    val closestIntersection =
        findClosest(listOf(0, 0), intersections)
    val distance = manhattanDistance(
        listOf(0, 0),
        closestIntersection.first()
    )
    println("$closestIntersection -- $distance")
}

fun genCoordinates(path:List<String>):List<List<Int>> {
    val currentPosition = mutableListOf(0, 0)
    val retVal = mutableListOf<List<Int>>(currentPosition.toList())
    path.forEach {
        val direction = it[0]
        val amount = it.substring(1).toInt()
        for (i in 0 until amount) {
            when (direction) {
                'U' -> currentPosition[1]++
                'D' -> currentPosition[1]--
                'L' -> currentPosition[0]--
                'R' -> currentPosition[0]++
            }
            retVal.add(currentPosition.toList())
        }
    }
    return retVal
}

private fun findClosest(coordinate:List<Int>, candidates:List<List<Int>>):List<List<Int>> {
    val sorted = candidates.map { Pair(it,
        manhattanDistance(it, coordinate)
    ) }.sortedBy { it.second}
    val closestDistance = sorted[0].second
    return sorted.takeWhile { it.second == closestDistance }.map { it.first }
}

fun manhattanDistance(coordinate1:List<Int>, coordinate2:List<Int>):Int {
    return coordinate1.mapIndexed { index, value -> abs(value - coordinate2[index]) }.sum()
}
