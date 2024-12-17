#! /usr/bin/env -S kotlin -J-ea

/**
 * Try to find candidate values for A that produce output values corresponding to the program.
 * NOTE: This is specific to the input program, not a general solution.
 *
 * Observations:
 * - Each loop outputs a single digit
 * - Each loop reduces A by factor of 8
 * - Loop exits once A < 8
 * - Length of program is 16 values, so 8^14 < A < 8^15
 * - Each 3 bits of A gives a power of 8
 * - Final output = fn(first 3 bits of A)
 * - Penultimate output = fn(first 6 bits of A) etc.
 */
fun inputLoopCandidates(position: Int = 15, prev: Long = 0): List<Long> {
  val p = listOf<Long>(2,4,1,2,7,5,1,7,4,4,0,3,5,5,3,0)
  val target = p.get(position)

  val candidates =
    (0..8)
      .map { (prev shl 3) + it }
      .filter { v -> inputLoopOutput(v.toLong()) == target }

  return if (position == 0) candidates
  else candidates.flatMap { inputLoopCandidates(position-1, it) }
}

/**
 * Calculate the output for the input program's inner loop.
 * NOTE: This is specific to the input program, not a general solution.
 *
 * ```
 * bst A -> B = A % 8          // (i.e. RHS 3 bits)
 * bxl 2 -> B = B xor 2        // i.e. toggle second bit (10)
 * cdv B -> C = A shr B        // i.e. div by 2^B
 * bxl 7 -> B = B xor 7        // i.e. toggle bottom 3 bits (111)
 * bxc _ -> B = B xor C        // i.e. toggle bits from C
 * adv 3 -> A = A shr 3        // i.e. div by 8
 * out B -> output(B % 8)      // output bottom 3 bits
 * jnz 0 -> goto 0 if A != 0   // exit or restart
 * ```
 */
fun inputLoopOutput(A: Long): Long =
  (((A and 7) xor 5) xor (A shr ((A and 7) xor 2).toInt())) and 7

inputLoopCandidates().min()
