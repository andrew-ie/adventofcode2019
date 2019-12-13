package dev.acraig.adventofcode.y2019.day13

import dev.acraig.adventofcode.y2019.common.IntCodeComputer

fun main() {
    val computerInput = Unit::class.java.getResource("/day13_input.txt").readText().trim().split(",").map { it.toLong() }.toLongArray()
    val computer = IntCodeComputer(computerInput)
    val screen = Screen()
    runGame(computer, screen)
    screen.tiles.values.groupBy { it }.map { Pair(it.key, it.value.size) }.forEach { println(it)}
    computer.reboot()
    computer.state[0] = 2L
    runGame(computer, screen)
    println(screen.score)
}

fun runGame(computer:IntCodeComputer, screen: Screen) {
    var currMode = OutputMode.X
    var x = 0L
    var y = 0L
    var paddleX = 0L
    var ballX = 0L
    while (!computer.halted()) {
        computer.run { output ->
            currMode = when (currMode) {
                OutputMode.X -> {
                    x = output
                    OutputMode.Y
                }
                OutputMode.Y -> {
                    y = output
                    OutputMode.TYPE
                }
                else -> {
                    if (TileMode.BALL.ordinal == output.toInt()) {
                        ballX = x
                    } else if (TileMode.PADDLE.ordinal == output.toInt()) {
                        paddleX = x
                    }
                    screen.update(x, y, output)
                    OutputMode.X
                }
            }
        }
//        println("$screen")
        val joystick = ballX.compareTo(paddleX).toLong()
        computer.setup(joystick)
    }
}
class Screen {
    val tiles = mutableMapOf<Pair<Long, Long>, TileMode>()
    var score = 0L
    fun update(x:Long, y:Long, value:Long) {
        if (x == -1L && y == 0L) {
            score = value
        } else {
            tiles[Pair(x, y)] = TileMode.values()[value.toInt()]
        }
    }

    override fun toString():String {
        val minX = tiles.keys.map { it.first }.min()!!
        val maxX = tiles.keys.map { it.first }.max()!!
        val minY = tiles.keys.map { it.second }.min()!!
        val maxY = tiles.keys.map { it.second }.max()!!
        val screen = StringBuilder("Score: $score\n")
        for (y in minY..maxY) {
            for (x in (minX..maxX)) {
                val mode = tiles[Pair(x, y)]
                screen.append(when (mode) {
                    TileMode.EMPTY -> ' '
                    TileMode.BALL -> '.'
                    TileMode.BLOCK -> '█'
                    TileMode.PADDLE -> '_'
                    TileMode.WALL -> '|'
                    null -> '?'
                })
            }
            screen.append('\n')
        }
        return screen.toString()
    }
}
enum class OutputMode {
    X,
    Y,
    TYPE
}

enum class TileMode {
    EMPTY,
    WALL,
    BLOCK,
    PADDLE,
    BALL
}