#! /usr/bin/env -S kotlin -J-ea

data class Coord(val x: Int, val y: Int) {
  fun right() = Coord(x+1, y)
  fun down() = Coord(x, y+1)
  fun left() = Coord(x-1, y)
  fun up() = Coord(x, y-1)
}

enum class Direction { Right, Down, Left, Up, }

data class Guard(val position: Coord, val direction: Direction)

class Map {
  val grid: MutableList<String> = mutableListOf()
  var guard: Guard = Guard(Coord(-1, -1), Direction.Up)

  fun width() = grid[0]!!.count()
  fun height() = grid.count()

  fun isOnMap(c: Coord) =
    c.x >= 0 && c.x < width() &&
    c.y >= 0 && c.y < height()

  fun update(line: String) = apply {
    grid.add(line.replace("^", "."))

    val ind = line.indexOf("^")
    if (ind != -1) {
      guard = Guard(Coord(ind, grid.count()-1), Direction.Up)
    }
  }

  fun obstacleAt(c: Coord) =
    grid.getOrNull(c.y)?.getOrNull(c.x) ?: '.' == '#'

  fun nextPosition(g: Guard): Coord =
    when (g.direction) {
      Direction.Right -> g.position.right()
      Direction.Down -> g.position.down()
      Direction.Left -> g.position.left()
      Direction.Up -> g.position.up()
    }

  fun nextDirection(g: Guard): Direction =
    when (g.direction) {
      Direction.Right -> Direction.Down
      Direction.Down -> Direction.Left
      Direction.Left -> Direction.Up
      Direction.Up -> Direction.Right
    }

  fun walk() = sequence {
    var g = guard
    while (isOnMap(g.position)) {
      yield(g.position)

      val n: Coord = nextPosition(g)
      g = if (obstacleAt(n)) Guard(g.position, nextDirection(g)) else Guard(n, g.direction)
    }
  }
}

fun <E> List<E>.centralItem(): E = get(count() / 2)

generateSequence(::readlnOrNull)
  .fold(Map(), Map::update)
  .let { it.walk().toSet().count() }
