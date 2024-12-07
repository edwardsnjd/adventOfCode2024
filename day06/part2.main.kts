#! /usr/bin/env -S kotlin -J-ea

data class Coord(val x: Int, val y: Int) {
  fun right() = Coord(x+1, y)
  fun down() = Coord(x, y+1)
  fun left() = Coord(x-1, y)
  fun up() = Coord(x, y-1)
}

enum class Direction { Right, Down, Left, Up, }

data class Guard(val position: Coord, val direction: Direction)

data class Grid(val rows: List<String>) {
  fun width() = rows[0]!!.count()
  fun height() = rows.count()

  fun isOnMap(c: Coord) =
    c.x >= 0 && c.x < width() &&
    c.y >= 0 && c.y < height()

  fun obstacleAt(c: Coord) =
    rows.getOrNull(c.y)?.getOrNull(c.x) ?: '.' != '.'

  fun with(c: Coord, v: Char) = buildList {
    val before = rows.take(c.y)
    val row = rows[c.y]
    val after = rows.drop(c.y+1)

    addAll(before)
    add(row.take(c.x) + v + row.drop(c.x+1))
    addAll(after)
  }.let(::Grid)

  override fun toString() = rows.joinToString("\n")
}

class Map(
  val grid: MutableList<String> = mutableListOf(),
  var guard: Guard = Guard(Coord(-1, -1), Direction.Up),
) {
  fun update(line: String) = apply {
    grid.add(line.replace("^", "."))

    val ind = line.indexOf("^")
    if (ind != -1) {
      guard = Guard(Coord(ind, grid.count()-1), Direction.Up)
    }
  }
}

enum class Termination { Edge, Loop }
data class Path(val coords: List<Coord>, val termination: Termination)

object Interpreter {
  private fun nextGuard(grid: Grid, g: Guard): Guard {
    val n: Coord = nextPosition(g)
    return if (grid.obstacleAt(n)) Guard(g.position, nextDirection(g))
    else Guard(n, g.direction)
  }

  private fun nextPosition(g: Guard): Coord =
    when (g.direction) {
      Direction.Right -> g.position.right()
      Direction.Down -> g.position.down()
      Direction.Left -> g.position.left()
      Direction.Up -> g.position.up()
    }

  private fun nextDirection(g: Guard): Direction =
    when (g.direction) {
      Direction.Right -> Direction.Down
      Direction.Down -> Direction.Left
      Direction.Left -> Direction.Up
      Direction.Up -> Direction.Right
    }

  fun walk(grid: Grid, guard: Guard): Path {
    val states: MutableList<Guard> = mutableListOf()

    var g = guard

    while (true) {
      if (!grid.isOnMap(g.position))
        return Path(states.map(Guard::position), Termination.Edge)

      if (states.contains(g))
        return Path(states.map(Guard::position), Termination.Loop)

      states.add(g)
      g = nextGuard(grid, g)
    }
  }

  fun generateLoops(grid: Grid, guard: Guard): Set<Coord> =
    walk(grid, guard).let { normalPath ->
      normalPath.coords.drop(1).filter { candidateCoord ->
        val newGrid = grid.with(candidateCoord, 'O')
        walk(newGrid, guard).termination == Termination.Loop
      }
    }.toSet()
}

fun <E> List<E>.centralItem(): E = get(count() / 2)

generateSequence(::readlnOrNull)
  .fold(Map(), Map::update)
  .let { Interpreter.generateLoops(Grid(it.grid), it.guard) }
  .let { it.count() }
