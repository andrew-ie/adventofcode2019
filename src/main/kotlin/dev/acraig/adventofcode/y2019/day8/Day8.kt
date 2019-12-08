package dev.acraig.adventofcode.y2019.day8

const val WIDTH = 25
const val HEIGHT = 6

fun main() {
    val imageSource = Unit::class.java.getResource("/day8_input.txt").readText().trim()
    val imageLayers = imageSource.chunked(WIDTH * HEIGHT).map { it.toCharArray() }
    val counts = imageLayers.map { it to countDigits(it) }.sortedBy { it.second['0'] }.first()
    println("Sorted ${counts.second} - ${counts.first.joinToString("")}")
    val image = imageLayers.fold(imageLayers.first(), ::combine)
    image.map { if (it == '0') ' ' else '*' }.joinToString("").chunkedSequence(25).forEach { println(it) }
}

fun countDigits(array:CharArray):Map<Char, Int> {
    val counts = ('0'..'2').map { it to 0 }.toMap().toMutableMap()
    array.forEach { counts[it] = counts[it]!! + 1 }
    return counts.toMap()
}

fun combine(first:CharArray, second:CharArray):CharArray {
    return first.mapIndexed() {
        index, ch -> if (ch == '2') {
            second[index]
        } else {
            ch
        }
    }.toCharArray()
}
