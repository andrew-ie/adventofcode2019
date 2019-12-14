package dev.acraig.adventofcode.y2019.day14

lateinit var reactionTable:Map<String, Reaction>
const val MAX_ORE = 1_000_000_000_000

fun main() {
    val data =
        Unit::class.java.getResource("/day14_input.txt").readText().lines().filterNot { it.isBlank() }.map { parse(it) }
    reactionTable = mapOf("ORE" to Reaction(emptyList(), ChemicalQuantity("ORE", 1))) + data.map { it.output.name to it }.toMap()
    val result = run(1L)
    println(result)
    val min = MAX_ORE / result
    println("At least $min fuel can be made")
    var max = min * 2
    while (run(max) < MAX_ORE) {
        println(max)
        max *= 2
    }
    println("$MAX_ORE units of ore obtains ${search(min, max)} fuel")
}

fun run(size:Long):Long {
    return reactionTable["FUEL"]!!.doReaction(size)
}

tailrec fun search(min:Long, max:Long):Long {
    val split = min + (max - min) / 2
    if (split == min) {
        return min
    }
    val oreCount = run(split)
    val newMin  = if (oreCount < MAX_ORE) split else min
    val newMax = if (oreCount > MAX_ORE) split else max
    return if (newMin >= newMax) {
        split
    } else {
        search(newMin, newMax)
    }
}

private fun parse(line:String):Reaction {
    //53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL
    val inputs = line.substringBefore("=").trim()
    val output = line.substringAfter(">").trim()
    return Reaction(inputs.split(",").map { parseChemical(it.trim()) }, parseChemical(output.trim()))
}

private fun parseChemical(input:String):ChemicalQuantity {
    return ChemicalQuantity(input.substringAfter(' '), input.substringBefore(' ').toLong())
}

data class Reaction(val input:List<ChemicalQuantity>, val output:ChemicalQuantity) {
    var inputs:List<Reaction>? = null
    private fun setupInputs() {
        inputs = input.map { reactionTable[it.name]!! }
    }

    fun doReaction(amountRequired:Long, have: MutableMap<String, Long> = HashMap()):Long {
        var ore = 0L
        if ("ORE" == output.name) {
            val currStore = have.getOrDefault("ORE", 0L)
            if (currStore < amountRequired) {
                ore += amountRequired - currStore
                have["ORE"] = amountRequired
            }
        } else {
            if (inputs == null) {
                setupInputs()
            }
            val currentValue = have.getOrDefault(output.name, 0L)
            if (currentValue < amountRequired) {
                val remaining = (amountRequired - currentValue) % output.amount
                val multiplier = (amountRequired - currentValue) / output.amount + (if (remaining != 0L) 1 else 0)
                input.forEachIndexed() { index, it ->
                    val inputAmountNeeded = it.amount * multiplier
                    if (have.getOrDefault(it.name, 0) < inputAmountNeeded) {
                        ore += inputs!![index].doReaction(inputAmountNeeded, have   )
                    }
                    have[it.name] = have[it.name]!! - inputAmountNeeded
                }
                have[output.name] = output.amount * multiplier + (have[output.name] ?: 0)
            }
        }
        return ore
    }
}

data class ChemicalQuantity(val name:String, val amount:Long)