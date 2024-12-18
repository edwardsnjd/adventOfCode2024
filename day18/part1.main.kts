#! /usr/bin/env -S kotlin -J-ea

import java.util.PriorityQueue
import Direction.*

typealias Coord = Pair<Int, Int>

enum class Direction { Up, Right, Down, Left }
val Directions = listOf(Up, Right, Down, Left)

fun Coord.step(d: Direction): Coord {
  val (x,y) = this
  return when (d) {
    Up -> x to y-1
    Right -> x+1 to y
    Down -> x to y+1
    Left -> x-1 to y
  }
}

data class Data(
  val corruptions: MutableList<Coord> = mutableListOf(),
) {
  fun update(line: String) = apply {
    val nums = Regex("""\d+""").findAll(line).map { it.value.toInt() }.toList()
    corruptions.add(nums[0]!! to nums[1]!!)
  }
}

data class Grid(var width: Int, var height: Int, val corruptions: List<Coord>) {
  fun isCorrupt(c: Coord) = corruptions.contains(c)

  fun isOnMap(c: Coord) =
    c.let { (x,y) -> (0 <= x && x < width) && (0 <= y && y < height) }

  fun adjacency(c: Coord) =
    Directions.map { c.step(it) }.filter(::isOnMap).filterNot(::isCorrupt)

  fun with(corruptions: List<Coord>) = Grid(width, height, corruptions)

  fun findPath() = djikstra(
    start = 0 to 0,
    end = (width-1) to (height-1),
    adjacency = ::adjacency,
  )
}

fun <T> djikstra(start: T, end: T, adjacency: (T) -> Collection<T>): List<T> {
  val visited: MutableSet<T> = mutableSetOf()
  val todo = PriorityQueue<Pair<T, Int>>(compareBy { (_, cost) -> cost })

  val predecessor: MutableMap<T, T> = mutableMapOf()
  val cost: MutableMap<T, Int> = mutableMapOf()

  cost[start] = 0
  todo.add(start to 0)

  var endNode: T? = null

  while (todo.any()) {
    val (node, _) = todo.poll()

    if (visited.contains(node)) continue
    if (node == end) { endNode = end; break }

    for (neighbour in adjacency(node)) {
      if (visited.contains(neighbour)) continue

      val newCost = cost[node]!! + 1
      if (cost[neighbour]?.let { it > newCost } ?: true) {
        cost[neighbour] = newCost
        predecessor[neighbour] = node
      }

      todo.add(neighbour to newCost)
    }

    visited.add(node)
  }

  return buildList {
    var n = endNode
    while (n != null) {
      add(n)
      n = predecessor[n]
    }
  }.reversed()
}

val take = 1024 // 12
val input = generateSequence(::readlnOrNull).take(take)
val data = input.fold(Data(), Data::update)

val dimensions = 71 // 7
val grid = Grid(dimensions, dimensions, data.corruptions)
val path = grid.findPath()

path.count() - 1
