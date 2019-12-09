package dev.acraig.adventofcode.y2019.common

class IntCodeComputer(private val input:LongArray, private val name:String = "IntComputer") {
    companion object {
        private val INSTRUCTION_MAP = Instruction.values().map { it.opcode to it }.toMap()
    }
    var state = State(input.toMutableList())

    fun reboot() {
        state = State(input.toMutableList())
    }

    fun setup(input:Long) {
        state.nextInput = input
    }
    fun halted():Boolean {
        return state.getNextOpCode().code == Instruction.HALT
    }

    fun run(outputProcessor: (Long) -> Unit = { }){
        println("$name running with input $state.nextInput")
        var currOpCode = state.getNextOpCode()
        if (currOpCode.code == Instruction.INPUT) {
            state.position = currOpCode.code.operate(currOpCode, state, outputProcessor)
            currOpCode = state.getNextOpCode()
        }
        while (currOpCode.code != Instruction.HALT && currOpCode.code != Instruction.INPUT) {
            state.position = currOpCode.code.operate(currOpCode, state) {
                outputProcessor(it)
            }
            currOpCode = state.getNextOpCode()
        }
    }

    data class OpCode(val value:Int, val mode3rd:Int, val mode2nd:Int, val mode1st:Int, val code:Instruction) {
        constructor(input:Long) : this(input.toInt(),getDigit(input, 0), getDigit(input, 1), getDigit(input, 2), INSTRUCTION_MAP[input.toInt() % 100] ?: Instruction.ERROR)
    }

    data class State(var memory:MutableList<Long>, var nextInput: Long = 0L, var position:Int = 0, var relativeOffset:Int = 0) {
        fun getNextOpCode():OpCode {
            return OpCode(memory[position])
        }

        operator fun get(index:Int):Long {
            if (memory.size <= index) {
                memory.addAll((0..(index - memory.size)).map { 0L })
            }
            return memory[index]
        }

        operator fun set(index:Int, value:Long) {
            if (memory.size <= index) {
                println("Expanding memory to fit to position $index (${memory.size})")
                memory.addAll((0..(index - memory.size)).map { 0L })
            }
            memory[index] = value
        }

    }

    enum class Instruction(val opcode:Int, val parameterCount:Int) {

        ADD(1, 3) {
            override fun execute(opcode: OpCode, state: State, parameters: List<Long>, output: (Long) -> Unit) {
                val val1 = getValue(state, opcode, 0)
                val val2 = getValue(state, opcode, 1)
                val result = val1 + val2
                setValue(state, opcode, parameters, 2, result)
            }
        },
        MULTIPLY(2, 3) {
            override fun execute(opcode: OpCode, state: State, parameters: List<Long>, output: (Long) -> Unit) {
                val val1 = getValue(state, opcode, 0)
                val val2 = getValue(state, opcode, 1)
                val result = val1 * val2
                setValue(state, opcode, parameters, 2, result)
            }
        },
        INPUT(3, 1) {
            override fun execute(opcode: OpCode, state: State, parameters: List<Long>, output: (Long) -> Unit) {
                setValue(state, opcode, parameters, 0, state.nextInput)
            }
        },
        OUTPUT(4, 1) {
            override fun execute(opcode: OpCode, state: State, parameters: List<Long>, output: (Long) -> Unit) {
                val value = getValue(state, opcode, 0)
                output(value)
            }
        },
        JUMP_IF_TRUE(5, 2) {
            override fun operate(opcode: OpCode, state: State, output: (Long) -> Unit):Int {
                return if (getValue(state, opcode, 0) != 0L) {
                    getValue(state, opcode, 1).toInt()
                } else {
                    state.position + parameterCount + 1
                }
            }
        },
        JUMP_IF_FALSE(6, 2) {
            override fun operate(opcode: OpCode, state: State, output: (Long) -> Unit):Int {
                val test = getValue(state, opcode,  0)
                return if (test == 0L) {
                    val destination = getValue(state, opcode, 1).toInt()
                    destination
                } else {
                    state.position + parameterCount + 1
                }
            }
        },
        LESS_THAN(7, 3) {
            override fun execute(opcode: OpCode, state: State, parameters: List<Long>, output: (Long) -> Unit) {
                val result = if (getValue(state, opcode, 0) < getValue(state, opcode, 1)) {
                    1L
                } else {
                    0L
                }
                setValue(state, opcode, parameters, 2, result)
            }
        },
        EQUALS(8, 3) {
            override fun execute(opcode: OpCode, state: State, parameters: List<Long>, output: (Long) -> Unit) {
                val result = if (getValue(state, opcode, 0) == getValue(state, opcode, 1)) {
                    1L
                } else {
                    0L
                }
                setValue(state, opcode, parameters, 2, result)
            }
        },
        RELATIVE_OFFSET(9, 1) {
            override fun execute(opcode: OpCode, state: State, parameters: List<Long>, output: (Long) -> Unit) {
                val result = getValue(state, opcode, 0 ).toInt()
                state.relativeOffset += result
            }
        },
        HALT(99, 0),
        ERROR(-1, 0) {
            override fun execute(opcode: OpCode, state: State, parameters: List<Long>, output: (Long) -> Unit) {
                throw UnsupportedOperationException("Unrecognized opcode $opcode")
            }
        };

        open fun operate(opcode: OpCode, state: State, output: (Long) -> Unit):Int {
            val parameters = state.memory.subList(state.position + 1, state.position + 1 + parameterCount)
            execute(opcode, state, parameters, output)
            return state.position + parameterCount + 1
        }

        open fun execute(opcode: OpCode, state: State, parameters:List<Long>, output: (Long) -> Unit) {
            println("OpCode Not Implemented")
        }

        fun getValue(state:State, opcode:OpCode, parameterIndex:Int):Long {
            val mode = getMode(parameterIndex, opcode)
            val result = when (mode) {
                1 -> state[state.position + parameterIndex + 1]
                2 -> state[state[state.position + parameterIndex + 1].toInt() + state.relativeOffset]
                else -> state[state[state.position + parameterIndex + 1].toInt()]
            }
            return result
        }

        private fun getMode(
            parameterIndex: Int,
            opcode: OpCode
        ): Int {
            val result = when (parameterIndex) {
                0 -> opcode.mode1st
                1 -> opcode.mode2nd
                2 -> opcode.mode3rd
                else -> throw IllegalStateException("Invalid index")
            }
            return result
        }

        fun setValue(state:State, opcode:OpCode, parameter:List<Long>, parameterIndex:Int, value:Long) {
            val mode = getMode(parameterIndex, opcode)
            val offset = if (mode == 2) state.relativeOffset else 0
            val index = state[state.position + parameterIndex + 1].toInt() + offset
            state[index] = value
        }
    }
}
fun getDigit(input:Long, digit:Int):Int {
    return input.toString().padStart(5, '0').substring(digit..digit).toInt()
}

