package dev.acraig.adventofcode.y2019.day2

import java.lang.IllegalStateException

class IntCodeComputer {
    private val input = Unit::class.java.getResource("/day2_input.txt").readText().trim().split(",").map { it.toInt() }.toList()

    var state = input.toMutableList()

    fun reboot() {
        state = input.toMutableList()
    }

    fun evaluate(noun:Int, verb:Int):Int {
        state[1] = noun
        state[2] = verb
        var position = 0
        while (input[position] != 99) {
            when {
                state[position] == 1 -> {
                    state[state[position + 3]] = state[state[position + 1]] + state[state[position + 2]]
                }
                input[position] == 2 -> {
                    state[state[position + 3]] = state[state[position + 1]] * state[state[position + 2]]
                }
                else -> {
                    throw IllegalStateException("Invalid opcode ${input[position]}")
                }
            }
            position += 4
        }
        return state[0]
    }

}