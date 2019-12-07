package dev.acraig.adventofcode.y2019.day5

import dev.acraig.adventofcode.y2019.common.IntCodeComputer

fun main() {
    val sourceCode = Unit::class.java.getResource("/day5_input.txt").readText().trim().split(",").map { it.toInt() }.toIntArray()
    val computer = IntCodeComputer(sourceCode)
    computer.setup(1)
    computer.run() {
        println(it)
    }
    computer.reboot()
    computer.setup(5)
    computer.run() {
        println(it)
    }
}