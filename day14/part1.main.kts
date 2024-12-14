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

fun safetyScore(positions: Collection<Pair<Int, Int>>, width: Int, height: Int): Int {
  val dividingX = width / 2
  val dividingY = height / 2
  return positions
    .filterNot { (x,y) -> x == dividingX || y == dividingY }
    .groupBy { (x,y) -> (x < dividingX) to (y < dividingY) }
    .values
    .map { it.count() }
    .fold(1, { a,b -> a*b })
}

val time = 100
val width = 101
val height = 103

input
  .fold(Data(), Data::update)
  .let { it.robots.map { it.gridPositionAfter(time, width, height) } }
  .let { safetyScore(it, width, height) }
