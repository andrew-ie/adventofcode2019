package dev.acraig.adventofcode.y2019.day4
fun main() {
    val range = 231832..767346 //We could optimize this number, as we know it can't be higher than 699999
    val match = range.filter { validate(it.toString().toCharArray()) }
    println("Part1 - ${match.size}")
    val part2 = match.filter { validatePart2(it.toString()) }
    println("Part2 - ${part2.size}")
}

/**
 * Ensure there is at least one double digit
 */
fun validate(candidate:CharArray):Boolean {
    val check = candidate.asList().subList(1, candidate.size)
    return check.filterIndexed { index, ch -> ch == candidate[index] }.isNotEmpty() &&
            check.filterIndexed { index, ch -> ch < candidate[index] }.isEmpty()
}

val VALID_PART2 = ('0'..'9').toList()
fun validatePart2(candidate:String):Boolean {
    return VALID_PART2.any { digit -> candidate.count { it == digit } == 2 }
}
