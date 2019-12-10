package dev.acraig.adventofcode.y2019.day9

import dev.acraig.adventofcode.y2019.common.IntCodeComputer
import java.time.Duration
import java.time.Instant

fun main() {
    val computer = IntCodeComputer(Unit::class.java.getResource("/day9_input.txt").readText().trim().split(",").map { it.toLong() }.toLongArray())
    computer.setup(1L)
    var output = 0L
    val part1time = time {
        while (!computer.halted()) {
            computer.run {
                println(it)
                output = it
            }
        }
    }
    println("Final output (1)::: $output")
    computer.reboot()
    computer.setup(2L)
    val part2time = time {
        while (!computer.halted()) {
            computer.run {
                println(it)
                output = it
            }
        }
    }
    println("Final output (2)::: $output")
    println("Times - Part1: $part1time, Part2: $part2time")
}

fun time(op: () -> Unit):Duration {
    val start = Instant.now()
    op()
    val end = Instant.now()
    return Duration.between(start, end)
}