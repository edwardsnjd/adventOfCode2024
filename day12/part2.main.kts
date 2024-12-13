#! /usr/bin/env -S kotlin -J-ea

data class Coord(val x: Int, val y: Int) {
  fun right() = Coord(x+1, y)
  fun down() = Coord(x, y+1)
  fun left() = Coord(x-1, y)
  fun up() = Coord(x, y-1)

  fun adjacentTo(other: Coord) =
    (other.y == y && (other.x == x-1 || other.x == x+1)) ||
    (other.x == x && (other.y == y-1 || other.y == y+1))
}

data class Grid(val lines: List<String>) {
  val width = lines[0].count()
  val height = lines.count()

  fun cell(c: Coord): Char? = when {
    (c.x < 0 || c.x > width-1) -> null
    (c.y < 0 || c.y > height-1) -> null
    else -> lines[c.y][c.x]
  }

  fun coords() = buildList {
    for (y in 0..<height) for (x in 0..<width) add(Coord(x, y))
  }

  fun onMap(c: Coord): Boolean =
    (0 <= c.x && c.x < width) && (0 <= c.y && c.y < height)
}

data class Group(val type: Char, val cells: Set<Coord>)

fun findGroups(grid: Grid): List<Group> = buildList {
  grid.coords().forEach { c ->
    val inExistingGroup = any { it.cells.contains(c) }
    if (!inExistingGroup) add(createGroup(grid, c))
  }
}

fun createGroup(grid: Grid, c: Coord): Group {
  val cell = grid.cell(c)!!
  val coords = mutableSetOf<Coord>()

  val todo = ArrayDeque<Coord>()
  todo.add(c)

  while (todo.any()) {
    val current = todo.removeLast()
    coords.add(current)

    listOf(current.up(), current.down(), current.left(), current.right())
      .filter(grid::onMap)
      .filterNot(coords::contains)
      .filter { grid.cell(it) == cell }
      .let(todo::addAll)
  }

  return Group(cell, coords)
}

enum class Direction { Right, Down, Left, Up }

fun countSides(cs: Set<Coord>): Int {
  val perimeterGroups = cs
    .sortedBy(Coord::x)
    .sortedBy(Coord::y)
    .fold(mutableMapOf<Direction, MutableSet<Coord>>()) { gs, c ->
      val exposedDirections = mapOf(
        Direction.Up to !cs.contains(c.up()),
        Direction.Right to !cs.contains(c.right()),
        Direction.Down to !cs.contains(c.down()),
        Direction.Left to !cs.contains(c.left()),
      ).mapNotNull { (d, v) -> if (v) d else null }
      exposedDirections.forEach { d -> gs.getOrPut(d, { mutableSetOf() }).add(c) }
      gs
    }

  return perimeterGroups.toList().fold(0) { total, (direction, group) ->
    val sides: MutableSet<MutableSet<Coord>> = mutableSetOf()
    group.forEach { c ->
      val touchingSide = sides.find { it.any(c::adjacentTo) }
      if (touchingSide != null) touchingSide.add(c) else sides.add(mutableSetOf(c))
    }
    total + sides.count()
  }
}

val input = generateSequence(::readlnOrNull).toList()
val grid = Grid(input)
val groups = findGroups(grid)
groups.map { group ->
  val area = group.cells.count()
  var sides = countSides(group.cells)
  area * sides
}.sum()
