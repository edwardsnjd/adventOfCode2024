#! /usr/bin/env -S kotlin -J-ea

val input = generateSequence(::readlnOrNull)

typealias Coord = Pair<Int, Int>

fun Coord.step(d: Direction): Coord {
  val (x,y) = this
  return when (d) {
    Direction.Up -> x to y-1
    Direction.Right -> x+1 to y
    Direction.Down -> x to y+1
    Direction.Left -> x-1 to y
  }
}

enum class Direction { Up, Right, Down, Left }

class Data(
  val walls: MutableList<Coord> = mutableListOf(),
  val boxes: MutableList<Coord> = mutableListOf(),
  var maybeRobot: Coord? = null,
  var buildingMap: Boolean = true,
  val moves: MutableList<Direction> = mutableListOf(),
) {
  private val width by lazy { walls.map { (a,b) -> a }.max() + 1 }
  private val height by lazy { walls.map { (a,b) -> b }.max() + 1 }
  private val robot get() = maybeRobot!!

  fun update(y: Int, line: String) = apply {
    if (line == "") buildingMap = false
    if (buildingMap) updateMap(y, line) else updateMoves(line)
  }

  private fun updateMap(y: Int, line: String) {
    line.forEachIndexed { x, c ->
      when (c) {
        '#' -> walls.add(x to y)
        'O' -> boxes.add(x to y)
        '@' -> maybeRobot = x to y
      }
    }
  }

  private fun updateMoves(line: String) {
    line.forEach { c ->
      when (c) {
        '^' -> Direction.Up
        '>' -> Direction.Right
        'v' -> Direction.Down
        '<' -> Direction.Left
        else -> error("Unknown move")
      }.let(moves::add)
    }
  }

  fun move(d: Direction): Data = apply {
    val candidates: MutableSet<Coord> = mutableSetOf(robot)

    var current = robot
    do {
      current = current.step(d)
      when {
        walls.contains(current) -> {
          candidates.clear()
          break
        }
        boxes.contains(current) -> candidates.add(current)
        else -> break
      }
    } while (true)

    if (candidates.any()) {
      maybeRobot = robot.step(d)

      candidates.drop(1)
        .reversed()
        .forEach { box ->
          boxes.remove(box)
          boxes.add(box.step(d))
        }
    }
  }
}

input
  .foldIndexed(Data(), { y, data, line -> data.update(y, line) })
  .also { data -> data.moves.forEach(data::move) }
  .let { it.boxes.map { (x,y) -> x + 100*y }.sum() }
