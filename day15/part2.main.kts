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

fun Coord.boxCoords() = buildSet<Coord> {
  val (x,y) = this@boxCoords
  add(x to y)
  add(x+1 to y)
}

fun Coord.wallCoords() = buildSet<Coord> {
  val (x,y) = this@wallCoords
  add(x to y)
  add(x+1 to y)
}

class Data(
  val walls: MutableList<Coord> = mutableListOf(),
  val boxes: MutableList<Coord> = mutableListOf(),
  var maybeRobot: Coord? = null,
  var buildingMap: Boolean = true,
  val moves: MutableList<Direction> = mutableListOf(),
) {
  private val width by lazy { walls.map { (a,b) -> a }.max() + 2 }
  private val height by lazy { walls.map { (a,b) -> b }.max() + 1 }
  private val robot get() = maybeRobot!!

  fun update(y: Int, line: String) = apply {
    if (line == "") buildingMap = false
    if (buildingMap) updateMap(y, line) else updateMoves(line)
  }

  private fun updateMap(y: Int, line: String) {
    line.forEachIndexed { x, c ->
      when (c) {
        '#' -> walls.add(2*x to y)
        'O' -> boxes.add(2*x to y)
        '@' -> maybeRobot = 2*x to y
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
    val candidateRobots: MutableSet<Coord> = mutableSetOf(robot)
    val candidateBoxes: MutableSet<Coord> = mutableSetOf()

    do {
      val candidates = (
        candidateRobots +
        candidateBoxes.flatMap { it.boxCoords() }.toSet()
      ).map { it.step(d) }

      val intersectingWalls =
        walls
          .map { it to it.wallCoords() }
          .toMap()
          .filter { (wall, coords) -> coords.intersect(candidates).any() }
          .keys
      val intersectingBoxes =
        boxes
          .filterNot { candidateBoxes.contains(it) }
          .map { it to it.boxCoords() }
          .toMap()
          .filter { (box, coords) -> coords.intersect(candidates).any() }
          .keys

      when {
        intersectingWalls.any() -> {
          candidateRobots.clear()
          candidateBoxes.clear()
          break
        }
        intersectingBoxes.any() -> candidateBoxes.addAll(intersectingBoxes)
        else -> break
      }
    } while (true)

    if (candidateRobots.any()) {
      maybeRobot = robot.step(d)
    }

    if (candidateBoxes.any()) {
      candidateBoxes
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
