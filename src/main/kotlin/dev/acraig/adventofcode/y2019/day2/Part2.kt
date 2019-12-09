package dev.acraig.adventofcode.y2019.day2

import dev.acraig.adventofcode.y2019.common.IntCodeComputer

fun main() {
    val sourceCode = Unit::class.java.getResource("/day2_input.txt").readText().trim().split(",").map { it.toLong() }.toLongArray()
    complete@ for (noun in 0..99) {
        sourceCode[1] = noun.toLong()
        for (verb in 0..99) {
            sourceCode[2] = verb.toLong()
            val computer = IntCodeComputer(sourceCode)
            computer.run()
            if (computer.state.memory[0] == 19690720L) {
                println("${noun * 100 + verb}")
                break@complete
            }
        }
    }
}