#! /usr/bin/env -S kotlin -J-ea

data class Machine(var A: Int, var B: Int, var C: Int, var pointer: Int) {
  fun run(program: List<Int>) = buildList<Int> {
    while (pointer < program.count()) {
      val (opcode, operand) = program.drop(pointer).take(2)

      when (opcode) {
        0 /* adv */ -> A = A shr evaluateCombo(operand)
        1 /* bxl */ -> B = B xor operand
        2 /* bst */ -> B = evaluateCombo(operand) % 8
        3 /* jnz */ -> { }
        4 /* bxc */ -> B = B xor C
        5 /* out */ -> add(evaluateCombo(operand) % 8)
        6 /* bdv */ -> B = A shr evaluateCombo(operand)
        7 /* cdv */ -> C = A shr evaluateCombo(operand)
      }

      pointer = if (opcode == 3 && A != 0) operand else pointer + 2
    }
  }

  fun evaluateCombo(operand: Int): Int = when (operand) {
    in 0..3 -> operand
    4 -> A
    5 -> B
    6 -> C
    else -> error("Unknown combo operand: $operand")
  }
}

data class Data(
  var A: Int? = null,
  var B: Int? = null,
  var C: Int? = null,
  val program: MutableList<Int> = mutableListOf(),
) {
  fun update(y: Int, line: String) = apply {
    val nums = Regex("""\d+""").findAll(line).map { it.value.toInt() }
    when (y) {
      0 -> A = nums.first()
      1 -> B = nums.first()
      2 -> C = nums.first()
      4 -> program.addAll(nums)
    }
  }
}

val input = generateSequence(::readlnOrNull).toList()
val data = input.foldIndexed(Data(), { y, data, line -> data.update(y, line) })
val m = Machine(data.A!!, data.B!!, data.C!!, 0)

m.run(data.program).joinToString(",")
