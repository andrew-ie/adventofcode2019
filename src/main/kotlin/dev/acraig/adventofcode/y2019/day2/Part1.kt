package dev.acraig.adventofcode.y2019.day2

import dev.acraig.adventofcode.y2019.common.IntCodeComputer

fun main() {
    val sourceCode = Unit::class.java.getResource("/day2_input.txt").readText().trim().split(",").map { it.toLong() }.toLongArray()
    sourceCode[1] = 12
    sourceCode[2] = 2
    val computer = IntCodeComputer(sourceCode)
    computer.run()
    println(computer.state.memory[0])
}