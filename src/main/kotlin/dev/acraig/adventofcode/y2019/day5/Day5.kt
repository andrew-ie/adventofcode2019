package dev.acraig.adventofcode.y2019.day5

import dev.acraig.adventofcode.y2019.common.IntCodeComputer
import java.util.concurrent.ArrayBlockingQueue

fun main() {
    val sourceCode = Unit::class.java.getResource("/day5_input.txt").readText().trim().split(",").map { it.toInt() }.toIntArray()
    val computer = IntCodeComputer(sourceCode)
    computer.run(ArrayBlockingQueue(1, true, listOf(1)))
    computer.reboot()
    computer.run(ArrayBlockingQueue(1, true, listOf(5)))
}