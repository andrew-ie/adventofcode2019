package dev.acraig.adventofcode.y2019.day6

fun main() {
    val input = Unit::class.java.getResource("/day6_input.txt").readText().lines().filter { it.isNotBlank() }.map { it.split(')') }.map { it[0] to it[1] }.toList()
    val planets = (input.map { it.first } + input.map { it.second }).map { it to Node(it) }.toMap()
    input.forEach { planets[it.second]!!.orbits.add(planets[it.first]!!)}
    val orbiters = planets.values.map { it.orbits.size }.sum()
    val indirectOrbiters = planets.values.map { it.indirectOrbitCounts() }.sum()
    println("$orbiters + $indirectOrbiters = ${orbiters + indirectOrbiters}")
    //PART2
    val startPlanet = planets["YOU"]!!.orbits.first()
    val destinationPlanet = planets["SAN"]!!.orbits.first()
    val rootPlanet = planets["COM"]!!
    println(startPlanet.value + " -> " + destinationPlanet.value)
    val startPlanetRoute = startPlanet.routeTo(rootPlanet)
    val destinationPlanetRoute = destinationPlanet.routeTo(rootPlanet)
    val common = startPlanetRoute intersect destinationPlanetRoute
    val route = (startPlanetRoute - common) + (destinationPlanetRoute - common).reversed()
    println(route.map { it.value })
    println(route.size)
}

data class Node<T>(val value:T, val orbits:MutableList<Node<T>> = mutableListOf()) {
    fun indirectOrbitCounts():Int {
        val firstLevelIndirectOrbiters = orbits.map { it.orbits.size }.sum()
        val secondLevelIndirectOrbiters = orbits.map { it.indirectOrbitCounts()}.sum()
        return firstLevelIndirectOrbiters + secondLevelIndirectOrbiters
    }

    fun routeTo(dest:Node<T>):List<Node<T>> {
        var position = this
        val result = mutableListOf(this)
        while (position != dest && position.orbits.isNotEmpty()) {
            position = position.orbits[0]
            result.add(position)
        }
        return result
    }
}