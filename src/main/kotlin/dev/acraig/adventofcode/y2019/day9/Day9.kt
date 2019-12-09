package dev.acraig.adventofcode.y2019.day9

import dev.acraig.adventofcode.y2019.common.IntCodeComputer

fun main() {
    val computer = IntCodeComputer(Unit::class.java.getResource("/day9_input.txt").readText().trim().split(",").map { it.toLong() }.toLongArray())
    computer.setup(1L)
    var output = 0L
    while (!computer.halted()) {
        computer.run {
            println(it)
            output = it
        }
    }
    println("Final output (1)::: $output")
    computer.reboot()
    computer.setup(2L)
    while (!computer.halted()) {
        computer.run {
            println(it)
            output = it
        }
    }
    println("Final output (2)::: $output")

}