package dev.acraig.adventofcode.y2019.day7

import dev.acraig.adventofcode.y2019.common.IntCodeComputer
import kotlin.math.pow

fun main() {
    val sourceCode = Unit::class.java.getResource("/day7_input.txt").readText().trim().split(",").map { it.toInt() }.toIntArray()
    val thrusters = (0..4).map { Thruster(it, sourceCode) }
    var position = 0
    val phases = generateSequence {
        if (position > (5.0.pow(thrusters.size.toDouble()).toInt())) {
            null
        } else {
            val retVal = listOf(position / 625 % 5, position / 125 % 5, position / 25 % 5, position / 5 % 5, position % 5)
            position++
            retVal
        }
    }.filter { it.toSet().size == it.size }.map { phase -> phase.map { it + 5 } }
    phases.map { it to runChain(it, thrusters) }.sortedByDescending { it.second.last() }.take(5).forEach { println(it) }
}

fun runChain(phase:List<Int>, thrusters:List<Thruster>):List<Int> {
    thrusters.forEachIndexed {
        index, it -> if (index == thrusters.lastIndex) {
            it.nextThruster = thrusters[0]
        } else {
            it.nextThruster = thrusters[index + 1]
        }
    }
    phase.forEachIndexed {
        index, it -> thrusters[index].computer.setup(it)
    }
    thrusters.forEach { it.run()}
    thrusters[0].computer.setup(0)
    while (thrusters.any {!it.computer.halted() }) {
        thrusters.forEach { it.run()}
    }

    val output = thrusters.map { it.lastOutput }
    thrusters.forEach { it.reboot() }
    return output
}

class Thruster(index:Int, source:IntArray) {
    val computer:IntCodeComputer = IntCodeComputer(source, "$index")
    lateinit var nextThruster:Thruster
    var lastOutput = 0
    fun run() {
        computer.run() {
            lastOutput = it
            nextThruster.computer.setup(it)
        }
    }
    fun reboot() {
        computer.reboot()
    }
}