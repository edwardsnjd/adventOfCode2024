#! /usr/bin/env -S kotlin -J-ea

val input = generateSequence(::readlnOrNull).toList()

data class Coord(val x: Int, val y: Int) {
  fun right() = Coord(x+1, y)
  fun down() = Coord(x, y+1)
  fun left() = Coord(x-1, y)
  fun up() = Coord(x, y-1)
}

data class Grid(val lines: List<String>) {
  val width = lines[0].count()
  val height = lines.count()

  fun cell(c: Coord): Int? = when {
    (c.x < 0 || c.x > width-1) -> null
    (c.y < 0 || c.y > height-1) -> null
    else -> lines[c.y][c.x].toString().toInt()
  }

  fun coords() = buildList {
    for (y in 0..<height) for (x in 0..<width) add(Coord(x, y))
  }

  fun onMap(c: Coord): Boolean =
    (0 <= c.x && c.x < width) && (0 <= c.y && c.y < height)

  fun surrounding(c: Coord): List<Coord> =
    listOf(c.right(), c.down(), c.left(), c.up()).filter(::onMap)
}

val g = Grid(input)

fun Grid.trailHeads() =
  coords().filter { cell(it) == 0 }

fun Grid.trailEndsFrom(c: Coord): Set<Coord> = buildSet {
  val h = cell(c)!!
  if (h == 9) add(c)
  else surrounding(c)
    .filter { cell(it) == h + 1 }
    .flatMap { trailEndsFrom(it) }
    .forEach { add(it) }
}

g.trailHeads()
  .map { coord -> g.trailEndsFrom(coord).count() }
  .sum()
