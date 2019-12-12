package dev.acraig.adventofcode.y2019.day12

import kotlin.math.abs

fun main() {
    val moons = Unit::class.java.getResource("/day12_input.txt").readText().lines().filter { it.isNotBlank() }
        .map { "<x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)>".toRegex().find(it) }
        .map { it!!.groupValues.subList(1, 4) }.mapIndexed { index, it -> Moon(index, listOf(Pair(it[0].toInt(), 0), Pair(it[1].toInt(), 0), Pair(it[2].toInt(), 0))) }
    var updatedMoons = moons
    for (i in 0 until 1000) {
        updatedMoons = step(updatedMoons)
    }
    println("${updatedMoons.map { it.energy() }.sum()}, $updatedMoons")
    val cycles = findCycles(moons)
    val lowestMultiple = lcm(cycles)
    println("$lowestMultiple ($cycles)")
}

private fun lcm(list:List<Long>):Long {
    val toFind = list.toMutableSet()
    var currentMultiplier = list.max()!!
    var currentValue = currentMultiplier
    while (toFind.isNotEmpty()) {
        val match = toFind.find { currentValue % it == 0L }
        if (match != null) {
            toFind.remove(match)
            currentMultiplier = currentValue
        } else {
            currentValue += currentMultiplier
        }
    }
    return currentValue
}

private fun step(
    updatedMoons: List<Moon>): List<Moon> {
    return updatedMoons.map { it.applyGravity(updatedMoons) }.map { it.updatePosition() }
}

fun findCycles(initial:List<Moon>):List<Long> {
    val dimensions = initial.map { it.vectors }
    val start = (0..dimensions[0].size).map { emptyList<Pair<Int, Int>>() }
    val groupedDimensions = dimensions.fold(start) {
        current, moon -> moon.mapIndexed {
            index, vector -> current[index] + vector
        }
    }
    return groupedDimensions.map { findCycle(it) }
}

fun findCycle(initial:List<Pair<Int, Int>>):Long {
    var count = 0L
    var currentState = initial
    do {
        count++
        currentState = currentState.map { applyGravityVector(it, currentState) }.map { updatePositionVector(it) }
    } while (currentState != initial)
    return count
}

fun updatePositionVector(vector:Pair<Int, Int>):Pair<Int, Int> {
    return Pair(vector.first + vector.second, vector.second)
}

fun applyGravityVector(vector: Pair<Int, Int>, allVectors: List<Pair<Int, Int>>):Pair<Int, Int> {
    val updatedVelocity = allVectors.filterNot { it.first == vector.first }.map {
        other -> if (other.first > vector.first) { 1 } else { -1 }
    }.sum() + vector.second
    return Pair(vector.first, updatedVelocity)
}

data class Moon(val identifier:Int, val vectors:List<Pair<Int, Int>>) {

    fun updatePosition():Moon {
        return Moon(identifier, vectors.map {
            updatePositionVector(it)
        })
    }

    fun applyGravity(others:List<Moon>):Moon {
        return Moon(identifier, vectors.mapIndexed {
            index, it -> applyGravityVector(it, others.map { it.vectors[index] })
        })
    }

    fun energy():Int {
        return vectors.map { abs(it.first) }.sum() * vectors.map{ abs(it.second) }.sum()
    }
}