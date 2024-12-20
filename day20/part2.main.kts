#! /usr/bin/env -S kotlin -J-ea

import Direction.*
import kotlin.math.abs

typealias Coord = Pair<Int, Int>

enum class Direction { Up, Right, Down, Left }
val Directions = listOf(Up, Right, Down, Left)

fun Coord.step(d: Direction) = when(d) {
  Up -> Coord(first, second-1)
  Down -> Coord(first, second+1)
  Left -> Coord(first-1, second)
  Right -> Coord(first+1, second)
}

data class Grid(val cells: List<String>) {
  val width = cells[0]!!.count()
  val height = cells.count()

  fun cell(c: Coord) = c.let { (x, y) -> cells[y]!![x]!! }

  private val positions: Pair<Coord, Coord> by lazy {
    var start: Coord? = null
    var end: Coord? = null
    forCells {
      if (isStart(it)) start = it
      if (isEnd(it)) end = it
    }
    start!! to end!!
  }
  val start: Coord = positions.first
  val end: Coord = positions.second

  val track: Set<Coord> by lazy {
    buildSet() {
      forCells { if (isTrack(it)) add(it) }
    }
  }
  val walls: Set<Coord> by lazy {
    buildSet() {
      forCells { if (isWall(it)) add(it) }
    }
  }

  fun isWall(c: Coord) = cell(c) == '#'
  fun isStart(c: Coord) = cell(c) == 'S'
  fun isEnd(c: Coord) = cell(c) == 'E'
  fun isTrack(c: Coord) = !isWall(c)

  fun forCells(a: (c: Coord) -> Unit) {
    for (y in 0..<height) for (x in 0..<width) a(x to y)
  }
}

fun findTrack(grid: Grid): List<Coord> = buildList {
  var cell = grid.start
  while (cell != grid.end) {
    add(cell)
    cell = Directions
      .map { cell.step(it) }
      .filter(grid::isTrack)
      .filterNot(::contains)
      .first()
  }
  add(cell) // Include end
}

data class Cheat(val startInd: Int, val endInd: Int, val dist: Int) {
  val saving = endInd - startInd - dist
}

fun cheats(g: Grid, track: List<Coord>, maxCheat: Int): Set<Cheat> = buildSet {
  for ((indexA, a) in track.withIndex()) {
    for ((indexB, b) in track.withIndex()) {
      if (indexB <= indexA + 1) continue

      val dist = dist(a, b)
      if (dist > maxCheat) continue

      val c = Cheat(indexA, indexB, dist)
      if (c.saving <= 0) continue

      add(c)
    }
  }
}

fun dist(a: Coord, b: Coord) = abs(b.first-a.first) + abs(b.second-a.second)

val input = generateSequence(::readlnOrNull).toList()
val grid = Grid(input)
val track = findTrack(grid)
val cheats = cheats(grid, track, 20).filter { it.saving >= 100 }
cheats.count()
