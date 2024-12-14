#! /usr/bin/env -S kotlin -J-ea

val input = generateSequence(::readlnOrNull).toList()

data class Robot(val x: Long, val y: Long, val vx: Long, val vy: Long) {
  fun gridPositionAfter(t: Int, width: Int, height: Int): Pair<Int, Int> {
    val tx = x + (vx * t)
    val ty = y + (vy * t)
    val finalX = (tx % width).toInt().let { if (it < 0) it + width else it }
    val finalY = (ty % height).toInt().let { if (it < 0) it + height else it }
    return finalX to finalY
  }
}

class Data(
  val robots: MutableList<Robot> = mutableListOf(),
) {
  val lineRe = Regex("""\-?\d+""")
  fun update(line: String) = apply {
    val numbers = lineRe.findAll(line).map { it.value.toLong() }.toList()
    robots.add(Robot(numbers[0], numbers[1], numbers[2], numbers[3]))
  }
}

val width = 101
val height = 103

fun render(robots: Set<Pair<Int, Int>>, width: Int, height: Int) {
  for (y in 0..<height)
    (0..<width)
      .map { x-> if (robots.contains(x to y)) 'X' else '.' }
      .joinToString("")
      .also(::println)
}

val robots = input
  .fold(Data(), Data::update)
  .let { it.robots }

// Eye ball it in less!
// Find lines with lots of cells in a row: ./part2.main.kts < input.txt | less +/XXXXXXXX
for (time in 0..<10000) {
  println("TIME: $time  *****************************************************")
  val positions = robots.map { it.gridPositionAfter(time, width, height) }
  render(positions.toSet(), width, height)
}
