#! /usr/bin/env -S kotlin -J-ea

val inputLines = generateSequence(::readlnOrNull).toList()
fun char(x: Int, y: Int) = inputLines[y]!![x]!!
//inputLines.joinToString("\n").let(::println)

// Grid
val width = inputLines[0].count()
val height = inputLines.count()
fun onMap(x: Int, y: Int) = (0..<width).contains(x) && (0..<height).contains(y)
//(width to height).let(::println)

// Coord for convenience
typealias Coord = Pair<Int,Int>
fun char(c: Coord) = c.let { (x,y) -> char(x,y) }
fun onMap(c: Coord) = c.let { (x,y) -> onMap(x,y) }

// Combinations
fun <T> pairs(items: Collection<T>): Set<Pair<T,T>> = buildSet {
  items.withIndex().forEach { (ax, a) ->
    items.withIndex().forEach { (bx, b) ->
      if (ax != bx) add(a to b)
    }
  }
}
//pairs(listOf(1,2,3))

// Core data for lookup
val antennas: Map<Char, Set<Coord>> = buildMap {
  for (x in 0..<width) {
    for (y in 0..<height) {
      val c = char(x,y)
      if (c == '.') continue
      put(c, getOrElse(c, { setOf() }).plus(x to y))
    }
  }
}
//antennas.also(::println)

fun antiNodes(a: Coord, b: Coord): Set<Coord> = buildSet {
  val (x1,y1) = a
  val (x2,y2) = b
  val (dx, dy) = (x2-x1) to (y2-y1)
  add(x1-dx to y1-dy)
  add(x2+dx to y2+dy)
}
//antiNodes(4 to 4, 5 to 2)

antennas.values
  .flatMap { nodes ->
    pairs(nodes)
      .flatMap { (a,b) -> antiNodes(a,b) }
      .filter(::onMap)
      .toSet()
  }
  .toSet()
  .count()
