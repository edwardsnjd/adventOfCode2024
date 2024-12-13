#! /usr/bin/env -S kotlin -J-ea

data class Coord(val x: Int, val y: Int) {
  fun right() = Coord(x+1, y)
  fun down() = Coord(x, y+1)
  fun left() = Coord(x-1, y)
  fun up() = Coord(x, y-1)
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

val input = generateSequence(::readlnOrNull).toList()
val grid = Grid(input)
val groups = findGroups(grid)
groups.map { group ->
  val area = group.cells.count()
  val perimeter = let {
    val shared = group.cells.map { c ->
      listOf(c.up(), c.down(), c.left(), c.right()).filter(group.cells::contains).count()
    }.sum()
    (area * 4) - shared
  }
  area * perimeter
}.sum()
