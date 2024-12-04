#! /usr/bin/env kotlin -J-ea

data class Coord(val x: Int, val y: Int) {
  fun right() = Coord(x+1, y)
  fun downright() = Coord(x+1, y+1)
  fun down() = Coord(x, y+1)
  fun downleft() = Coord(x-1, y+1)
  fun left() = Coord(x-1, y)
  fun upleft() = Coord(x-1, y-1)
  fun up() = Coord(x, y-1)
  fun upright() = Coord(x+1, y-1)
}

data class Grid(val lines: List<String>) {
  val width = lines[0].count()
  val height = lines.count()

  fun cell(c: Coord): Char? {
    if (c.x < 0 || c.x > width-1) return null
    if (c.y < 0 || c.y > height-1) return null
    return lines[c.y][c.x]
  }

  fun coords() = buildList {
    for (y in 0..<height) {
      for (x in 0..<width) {
        add(Coord(x, y))
      }
    }
  }

  fun instancesStartingAt(word: String, c: Coord) = buildList {
    add(foundWithStep(word, c, Coord::right))
    add(foundWithStep(word, c, Coord::downright))
    add(foundWithStep(word, c, Coord::down))
    add(foundWithStep(word, c, Coord::downleft))
    add(foundWithStep(word, c, Coord::left))
    add(foundWithStep(word, c, Coord::upleft))
    add(foundWithStep(word, c, Coord::up))
    add(foundWithStep(word, c, Coord::upright))
  }.count { it }

  fun foundWithStep(word: String, c: Coord, step: (Coord) -> Coord) =
    buildList {
      var current = c
      for (i in 0..<word.length) {
        add(cell(current) ?: "")
        current = step(current)
      }
    }.joinToString("") == word
}

val grid = generateSequence(::readlnOrNull).toList().let { Grid(it) }

grid.coords().map { c -> grid.instancesStartingAt("XMAS", c) }.sum()
