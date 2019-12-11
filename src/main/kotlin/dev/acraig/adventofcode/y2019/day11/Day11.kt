package dev.acraig.adventofcode.y2019.day11

import dev.acraig.adventofcode.y2019.common.IntCodeComputer

fun main() {
    paint(0, false)
    paint(1, true)
}

private fun paint(init: Int, printDrawing: Boolean) {
    val ship = Ship()
    ship.paint(init)
    val computer =
        IntCodeComputer(Unit::class.java.getResource("/day11_input.txt").readText().trim().split(",").map { it.toLong() }.toLongArray())
    var currMode = Mode.PAINT
    while (!computer.halted()) {
        computer.setup(ship.currColour().toLong())
        computer.run {
            currMode = if (currMode == Mode.PAINT) {
                ship.paint(it.toInt())
                Mode.ROTATE
            } else {
                ship.rotate(it.toInt())
                Mode.PAINT
            }
        }
    }
    if (printDrawing) {
        val minX = ship.colour.keys.minBy { it.x }!!.x
        val maxX = ship.colour.keys.maxBy { it.x }!!.x
        val minY = ship.colour.keys.minBy { it.y }!!.y
        val maxY = ship.colour.keys.maxBy { it.y }!!.y
        for (x in minX..maxX) {
            val string = StringBuilder()
            for (y in minY..maxY) {
                if (ship.colour.getOrDefault(Position(x, y), 0) == 0) {
                    string.append(" ")
                } else {
                    string.append("#")
                }
            }
            println(string)
        }
    } else {
        println("${ship.visited.size}")
    }
}


data class Position(val x:Int, val y:Int)
enum class Orientation {
    UP,
    RIGHT,
    DOWN,
    LEFT
}

enum class Mode {
    PAINT,
    ROTATE
}
class Ship() {
    val visited = mutableSetOf<Position>()
    val colour = mutableMapOf<Position, Int>()
    private var robotPosition = Position(0, 0)
    private var robotOrientation = Orientation.UP
    fun paint(brush:Int) {
        colour[robotPosition] = brush
        visited.add(robotPosition)
    }

    fun currColour():Int {
        return colour.getOrDefault(robotPosition, 0)
    }

    fun rotate(direction:Int) {
        val values = Orientation.values()
        val offset = if (direction == 0) {
            -1
        } else {
            1
        }
        val currPosition = values.indexOf(robotOrientation)
        var newPosition = (currPosition + offset) % values.size
        if (newPosition < 0) {
           newPosition = values.size - 1
        }
        robotOrientation = values[newPosition]
        robotPosition = when (robotOrientation) {
            Orientation.UP -> Position(robotPosition.x, robotPosition.y + 1)
            Orientation.DOWN -> Position(robotPosition.x, robotPosition.y - 1)
            Orientation.LEFT -> Position(robotPosition.x - 1, robotPosition.y)
            Orientation.RIGHT -> Position(robotPosition.x + 1, robotPosition.y)
        }

    }
}