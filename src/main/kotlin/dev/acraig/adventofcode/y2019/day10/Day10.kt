package dev.acraig.adventofcode.y2019.day10

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

const val PI_TO_DEGREES = 180.0/ PI

fun main() {
    val asteroids = Unit::class.java.getResource("/day10_input.txt").readText().lines().filter { it.isNotBlank() }.mapIndexed {
        rowIndex, row -> Pair(rowIndex, row)
    }.flatMap {
        row -> row.second.mapIndexed { colIndex, ch -> Asteroid (row.first, colIndex, ch == '#') }
    }.filter { it.present }.toSet()
    println(asteroids)
    val answer = asteroids.map { Pair(it, visible(it, asteroids)) }.maxBy { it.second.size }!!
    println("${answer.first} --> ${answer.second.size}")
    val base = answer.first
    val asteroidMap = answer.second.mapValues { asteroidList -> asteroidList.value.sortedBy { distance(base, it) }.toMutableList() }.toSortedMap(
        Comparator {
            a, b -> toDegrees(a).compareTo(toDegrees(b))
        })
    println(asteroidMap)
    val asteroidDeletionOrder = mutableListOf<Asteroid>()
    while (asteroidMap.any {
            it.value.isNotEmpty()
    }) {
        asteroidMap.filterValues { it.isNotEmpty() }.forEach {
            val lastAsteroid = it.value.removeAt(0)
            asteroidDeletionOrder.add(lastAsteroid)
            println("${asteroidDeletionOrder.size} - Removing $lastAsteroid")
        }
    }
    val candidate = asteroidDeletionOrder[199]
    println(candidate)
    println(candidate.colIndex * 100 + candidate.rowIndex)
}

fun visible(base:Asteroid, locations:Collection<Asteroid>):Map<Double, List<Asteroid>> {
    return locations
        .filterNot { it == base }.map { Pair(it, atan2(base.rowIndex - it.rowIndex.toDouble(), base.colIndex - it.colIndex.toDouble())) }.groupBy({it.second}, {it.first}).toMap()

}

fun toDegrees(input:Double):Double {
    val result = (input * PI_TO_DEGREES)
    val degrees = if (result < 0) {
        360 + result
    } else {
        result
    }
    return ((degrees + 270) % 360)
}

fun distance(asteroid1: Asteroid, asteroid2: Asteroid):Double {
    val xDistance = (asteroid1.colIndex - asteroid2.colIndex.toDouble()).pow(2.0)
    val yDistance = (asteroid1.rowIndex - asteroid2.rowIndex.toDouble()).pow(2.0)
    return sqrt(xDistance + yDistance)
}

data class Asteroid(val rowIndex:Int, val colIndex:Int, val present:Boolean) {
    override fun toString(): String {
        return "($colIndex,$rowIndex)"
    }
}