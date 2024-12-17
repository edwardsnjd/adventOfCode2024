#! /usr/bin/env -S kotlin -J-ea

import kotlin.streams.asStream

data class Machine(var A: Long, var B: Long, var C: Long, var pointer: Int) {
  fun run(program: List<Long>) = sequence<Long> {
    while (pointer < program.count()) {
      val (opcode, operand) = program.drop(pointer).take(2)

      when (opcode) {
        0L /* adv */ -> A = A shr evaluateCombo(operand).toInt()
        1L /* bxl */ -> B = B xor operand
        2L /* bst */ -> B = evaluateCombo(operand) % 8
        3L /* jnz */ -> { }
        4L /* bxc */ -> B = B xor C
        5L /* out */ -> yield(evaluateCombo(operand) % 8)
        6L /* bdv */ -> B = A shr evaluateCombo(operand).toInt()
        7L /* cdv */ -> C = A shr evaluateCombo(operand).toInt()
      }

      pointer = if (opcode == 3L && A != 0L) operand.toInt() else pointer + 2
    }
  }

  fun evaluateCombo(operand: Long): Long = when (operand) {
    in 0..3 -> operand
    4L -> A
    5L -> B
    6L -> C
    else -> error("Unknown combo operand: $operand")
  }
}

data class Data(
  var A: Long? = null,
  var B: Long? = null,
  var C: Long? = null,
  val program: MutableList<Long> = mutableListOf(),
) {
  fun update(y: Int, line: String) = apply {
    val nums = Regex("""\d+""").findAll(line).map { it.value.toLong() }
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
val p = data.program

// Attempt brute force to find A
// ❌ This didn't work on input because too large (2^42 < A < 2^45)
(0L..1_000_000_000).asSequence().asStream().parallel().filter { a ->
  val m = Machine(a, data.B!!, data.C!!, 0)
  val resultSeq = m.run(p).zip(p.asSequence())

  var count = 0
  var indMismatch = -1
  for ((ind, pair) in resultSeq.withIndex()) {
    // Fail as fast as possible
    if (pair.first != pair.second) {
      indMismatch = ind
      break
    }
    count += 1
  }

  indMismatch == -1 && count == p.count()
}.findFirst()
