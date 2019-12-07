package dev.acraig.adventofcode.y2019.day2

import dev.acraig.adventofcode.y2019.common.IntCodeComputer

fun main() {
    val sourceCode = Unit::class.java.getResource("/day2_input.txt").readText().trim().split(",").map { it.toInt() }.toIntArray()
    complete@ for (noun in 0..99) {
        sourceCode[1] = noun
        for (verb in 0..99) {
            sourceCode[2] = verb
            val computer = IntCodeComputer(sourceCode)
            computer.run()
            if (computer.state[0] == 19690720) {
                println("${noun * 100 + verb}")
                break@complete
            }
        }
    }
}