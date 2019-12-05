package dev.acraig.adventofcode.y2019.common

import java.util.*
import java.util.concurrent.ArrayBlockingQueue

class IntCodeComputer(private val input:IntArray) {
    companion object {
        private val INSTRUCTION_MAP = Instruction.values().map { it.opcode to it }.toMap()
    }
    private var state = input.toMutableList()

    fun reboot() {
        state = input.toMutableList()
    }

    fun run(input:Queue<Int> = ArrayBlockingQueue(1)):Int {
        var position = 0
        var currOpCode = OpCode(state[position])
        while (currOpCode.code != Instruction.HALT) {
            position = currOpCode.code.operate(position, currOpCode, state, input)
            currOpCode = OpCode(state[position])
        }
        println("<<HALT>>")
        return state[0]
    }

    data class OpCode(val value:Int, val mode3rd:Int, val mode2nd:Int, val mode1st:Int, val code:Instruction) {
        constructor(input:Int) : this(input,input / 10000, input / 1000 % 2, input / 100 % 2, INSTRUCTION_MAP[input % 100] ?: Instruction.ERROR)
    }

    enum class Instruction(val opcode:Int, val parameterCount:Int) {

        ADD(1, 3) {
            override fun execute(opcode: OpCode, state: MutableList<Int>, parameters: List<Int>, input:Queue<Int>) {
                val result = getValue(state, opcode, parameters, 0) + getValue(state, opcode, parameters, 1)
                state[parameters[2]] = result
            }
        },
        MULTIPLY(2, 3) {
            override fun execute(opcode: OpCode, state: MutableList<Int>, parameters: List<Int>, input:Queue<Int>) {
                state[parameters[2]] = getValue(state, opcode, parameters, 0) * getValue(state, opcode, parameters, 1)
            }
        },
        INPUT(3, 1) {
            override fun execute(opcode: OpCode, state: MutableList<Int>, parameters: List<Int>, input: Queue<Int>) {
                state[parameters[0]] = input.poll()
            }
        },
        OUTPUT(4, 1) {
            override fun execute(opcode: OpCode, state: MutableList<Int>, parameters: List<Int>, input: Queue<Int>) {
                println("<<OUTPUT -- ${getValue(state, opcode, parameters, 0)}>>")
            }
        },
        JUMP_IF_TRUE(5, 2) {
            override fun operate(currentIndex: Int, opcode: OpCode, state: MutableList<Int>, input: Queue<Int>): Int {
                val parameters = state.subList(currentIndex + 1, currentIndex + 1 + parameterCount)
                return if (getValue(state, opcode, parameters,  0) != 0) {
                    getValue(state, opcode, parameters, 1)
                } else {
                    currentIndex + parameterCount + 1
                }
            }
        },
        JUMP_IF_FALSE(6, 2) {
            override fun operate(currentIndex: Int, opcode: OpCode, state: MutableList<Int>, input: Queue<Int>): Int {
                val parameters = state.subList(currentIndex + 1, currentIndex + 1 + parameterCount)
                return if (getValue(state, opcode, parameters,  0) == 0) {
                    getValue(state, opcode, parameters, 1)
                } else {
                    currentIndex + parameterCount + 1
                }
            }
        },
        LESS_THAN(7, 3) {
            override fun execute(opcode: OpCode, state: MutableList<Int>, parameters: List<Int>, input: Queue<Int>) {
                val result = if (getValue(state, opcode, parameters, 0) < getValue(state, opcode, parameters, 1)) {
                    1
                } else {
                    0
                }
                state[parameters[2]] = result
            }
        },
        EQUALS(8, 3) {
            override fun execute(opcode: OpCode, state: MutableList<Int>, parameters: List<Int>, input: Queue<Int>) {
                val result = if (getValue(state, opcode, parameters, 0) == getValue(state, opcode, parameters, 1)) {
                    1
                } else {
                    0
                }
                state[parameters[2]] = result
            }
        },
        HALT(99, 0) {
            override fun execute(opcode: OpCode, state: MutableList<Int>, parameters: List<Int>, input: Queue<Int>) {
                println("<HALT>")
            }
        },
        ERROR(-1, 0) {
            override fun execute(opcode: OpCode, state: MutableList<Int>, parameters: List<Int>, input: Queue<Int>) {
                throw UnsupportedOperationException("Unrecognized opcode")
            }
        };

        open fun operate(currentIndex: Int, opcode: OpCode, state: MutableList<Int>, input: Queue<Int>):Int {
            val parameters = state.subList(currentIndex + 1, currentIndex + 1 + parameterCount)
            execute(opcode, state, parameters, input)
            return currentIndex + parameterCount + 1
        }

        open fun execute(opcode: OpCode, state: MutableList<Int>, parameters:List<Int>, input:Queue<Int>) {
            println("OpCode Not Implemented")
        }

        fun getValue(state:List<Int>, opcode:OpCode, parameter:List<Int>, parameterIndex:Int):Int {
            val mode = when (parameterIndex) {
                0 -> opcode.mode1st
                1 -> opcode.mode2nd
                2 -> opcode.mode3rd
                else -> throw IllegalStateException("Invalid index")
            }
            return if (mode == 1) {
                parameter[parameterIndex]
            } else {
                state[parameter[parameterIndex]]
            }
        }

    }
}