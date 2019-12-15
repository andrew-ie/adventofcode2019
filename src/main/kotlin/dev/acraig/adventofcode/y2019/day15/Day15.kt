package dev.acraig.adventofcode.y2019.day15

import dev.acraig.adventofcode.y2019.common.IntCodeComputer

fun main() {
    val input = Unit::class.java.getResource("/day15_input.txt").readText().trim().split(",").map { it.toLong() }.toLongArray()
    val robot = Robot(IntCodeComputer(input))
    val distance = mutableMapOf<Position, Long>()
    val start = robot.position
    distance[start] = 0L
    val tovisit = mutableSetOf<Position>()
    val visited = mutableSetOf<Position>()
    val vertices = mutableMapOf(start to robot.validDirections())
    vertices[start] = robot.validDirections()
    tovisit.add(start)
    var printedLeak = false
    while (tovisit.isNotEmpty()) {
        val node = tovisit.minBy { distance.getOrDefault(it, Long.MAX_VALUE) }!!
        val newDistance = distance[node]!! + 1
        visited.add(node)
        tovisit.remove(node)
        robot.move(node)
        val newNeighbours = robot.validDirections()
        vertices[node] = newNeighbours
        newNeighbours.filterNot { visited.contains(it) }.forEach {
            if (distance.getOrDefault(it, Long.MAX_VALUE) > newDistance) {
                distance[it] = newDistance
            }
            tovisit.add(it)
        }
        if (!printedLeak && robot.leakPosition != null) {
            printedLeak = true
            val route = generateSequence(robot.leakPosition) {
                robot.previous[it]
            }.takeWhile { it != start }.toList()
            println("${route.size} - $route")
        }
    }
    val hasOxygen = vertices.keys.map { it to (it == robot.leakPosition) }.toMap().toMutableMap()
    var minute = 0
    while (hasOxygen.any { !it.value }) {
        minute++
        val thisRound = hasOxygen.filter { it.value }.flatMap { vertices[it.key]!! }.filter { hasOxygen[it] == false }
        thisRound.forEach {
            hasOxygen[it] = true
        }
    }
    println("Total $minute")

}

data class Position(val x:Long, val y:Long, val type:Type)

enum class Type(val code:Long) {
    WALL(0L),
    PATH(1L),
    FOUND(2L)
}

enum class Direction(val command:Long) {
    NORTH(1) {
        override fun reverse(): Direction {
            return SOUTH
        }

        override fun apply(currPosition: Position, type:Type): Position {
            return Position(currPosition.x, currPosition.y - 1, type)
        }
    },
    SOUTH(2) {
        override fun reverse(): Direction {
            return NORTH
        }

        override fun apply(currPosition: Position, type:Type): Position {
            return Position(currPosition.x, currPosition.y + 1, type)
        }
    },
    WEST(3) {
        override fun reverse(): Direction {
            return EAST
        }

        override fun apply(currPosition: Position, type:Type): Position {
            return Position(currPosition.x - 1, currPosition.y, type)
        }
    },
    EAST(4) {
        override fun reverse(): Direction {
            return WEST
        }

        override fun apply(currPosition: Position, type:Type): Position {
            return Position(currPosition.x + 1, currPosition.y, type)
        }
    };

    abstract fun reverse():Direction
    abstract fun apply(currPosition: Position, type:Type):Position
}

class Robot(private val computer:IntCodeComputer) {

    var position = Position(0, 0, Type.PATH)
    private val distance = mutableMapOf(position to 0L)
    val previous = mutableMapOf<Position, Position>()
    var leakPosition:Position? = null
    private val directionCache = HashMap<Position, List<Position>>()

    init {
        validDirections().filter { distance.getOrDefault(it, Long.MAX_VALUE) > 1 }.forEach {
            distance[it] = 1
            previous[it] = position
        }
    }

    private fun move(direction: Direction):Position {
        val newDistance = distance[position]!! + 1
        computer.setup(direction.command)
        var result:Type = Type.WALL
        computer.run {
            code -> result = Type.values().find{ it.code == code }!!
        }
        if (result != Type.WALL) {
            position = direction.apply(position, result)
        } else {
            throw Exception("Can't move from $position in the direction of $direction")
        }
        validDirections().filter { distance.getOrDefault(it, Long.MAX_VALUE) > newDistance }.forEach {
            distance[it] = newDistance
            previous[it] = position
        }
        if (position.type == Type.FOUND) {
            leakPosition = position
        }
        return position
    }

    fun move(newPosition:Position):Position {
        if (newPosition == position) {
            return position
        }
        else if (validDirections().contains(newPosition)) {
            move(calculateDirection(newPosition))
        }
        else if (previous.containsKey(newPosition)) {
            moveBack(newPosition)
        }
        else {
            val location = directionCache.entries.find { it.value.contains(newPosition) && previous.containsKey(it.key) }?.key
            if (location != null) {
                moveBack(location)
            } else {
                throw Exception("Don't know how to get from $position to $newPosition ($directionCache)")
            }
        }
        return position
    }

    private fun moveBack(returnedPosition:Position):Position {
        val pathToReturnedPosition = generateSequence(returnedPosition) {
            previous[it]
        }.toList().reversed()
        val elementsPresent = pathToReturnedPosition.toSet()
        val pathFromCurrentPosition = generateSequence(position) {
            previous[it]
        }.takeWhile { !elementsPresent.contains(it) }.toList()
        val intersection = previous[pathFromCurrentPosition.last()]
        val path = pathFromCurrentPosition.filter { it != position } + pathToReturnedPosition.subList(pathToReturnedPosition.indexOf(intersection), pathToReturnedPosition.size)
        path.forEach {
            move(calculateDirection(it))
        }
        if (returnedPosition != position) {
            throw Exception("Was supposed to arrive at $returnedPosition, but arrived at $position")
        }
        return position
    }

    private fun calculateDirection(newPosition:Position, oldPosition:Position = position):Direction {
        return when {
            oldPosition.x < newPosition.x -> {
                Direction.EAST
            }
            oldPosition.x > newPosition.x -> {
                Direction.WEST
            }
            oldPosition.y < newPosition.y -> {
                Direction.SOUTH
            }
            oldPosition.y > newPosition.y -> {
                Direction.NORTH
            }
            else -> {
                throw Exception("Can't figure out which way to go from $position to $newPosition")
            }
        }
    }
    private fun testDirection(direction:Direction):Type {
        computer.setup(direction.command)
        var result:Type = Type.WALL
        computer.run {
                code -> result = Type.values().find { it.code == code }!!
        }
        if (result != Type.WALL) {
            computer.setup(direction.reverse().command)
            computer.run()
        }
        return result
    }

    fun validDirections():List<Position> {
        return directionCache.computeIfAbsent(position) {
            Direction.values().map {
                direction -> direction.apply(position, testDirection(direction))
            }.filterNot { it.type == Type.WALL}
        }
    }
}