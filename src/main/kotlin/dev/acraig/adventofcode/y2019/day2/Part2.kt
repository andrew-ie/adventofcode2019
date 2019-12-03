package dev.acraig.adventofcode.y2019.day2

fun main() {
    val computer = IntCodeComputer()
    for (noun in 0..99) {
        for (verb in 0..99) {
            computer.reboot()
            if (computer.evaluate(noun, verb) == 19690720) {
                println("${noun * 100 + verb}")
            }
        }
    }
}