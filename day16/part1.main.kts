#! /usr/bin/env -S kotlin -J-ea

import java.util.PriorityQueue
import Direction.*

typealias Coord = Pair<Int, Int>

enum class Direction { Up, Right, Down, Left }

fun Coord.step(d: Direction): Coord {
  val (x,y) = this
  return when (d) {
    Up -> x to y-1
    Right -> x+1 to y
    Down -> x to y+1
    Left -> x-1 to y
  }
}

class Data(
  val grid: MutableList<String> = mutableListOf(),
  val walls: MutableList<Coord> = mutableListOf(),
  var maybeStart: Coord? = null,
  var maybeEnd: Coord? = null,
) {
  val width by lazy { walls.map { (a,b) -> a }.max() + 1 }
  val height by lazy { walls.map { (a,b) -> b }.max() + 1 }
  val start get() = maybeStart!!
  val end get() = maybeEnd!!

  fun update(y: Int, line: String) = apply {
    grid.add(line.replace("S", ".").replace("E", "."))
    line.forEachIndexed { x, c ->
      when (c) {
        '#' -> walls.add(x to y)
        'S' -> maybeStart = x to y
        'E' -> maybeEnd = x to y
      }
    }
  }

  fun cell(p: Coord) = p.let { (x,y) -> grid[y]!![x]!! }
}

data class Result<T>(
  val end: T?,
  val cost: Int,
  val path: List<T>,
)

fun <T> aStarSearch(
  start: T,
  end: (T) -> Boolean,
  adjacency: (T) -> Collection<T>,
  stepCost: (T,T) -> Int,
  estimate: (T) -> Int,
): Result<T>
{
  val visited: MutableSet<T> = mutableSetOf()
  val todo = PriorityQueue<Pair<T, Int>>(compareBy { (_, estimate) -> estimate })

  var endNode: T? = null // because end is a predicate, might be multiple end states

  val predecessor: MutableMap<T, T> = mutableMapOf()
  val cost: MutableMap<T, Int> = mutableMapOf()

  cost[start] = 0
  todo.add(start to 0)

  while (todo.any()) {
    val (node, _) = todo.poll()

    if (visited.contains(node)) continue
    if (end(node)) { endNode = node; break }

    for (neighbour in adjacency(node)) {
      if (visited.contains(neighbour)) continue

      val newCost = cost[node]!! + stepCost(node, neighbour)
      if (cost[neighbour]?.let { it > newCost } ?: true) {
        cost[neighbour] = newCost
        predecessor[neighbour] = node
      }

      val neighbourEstimate = newCost + estimate(neighbour)
      todo.add(neighbour to neighbourEstimate)
    }

    visited.add(node)
  }

  return Result<T>(
    end = endNode,
    cost = cost[endNode] ?: Int.MAX_VALUE, // If did not find a path
    path = findPath(endNode, predecessor),
  )
}

data class State(val position: Coord, val direction: Direction)

fun Data.adjacency(s: State) = buildList<State> {
  val (pos,dir) = s

  // Turns
  when (dir) {
    Up, Down -> listOf(Left, Right)
    Left, Right -> listOf(Up, Down)
  }.forEach { add(State(pos, it)) }

  // Step in current direction if possible
  val nextPos = pos.step(dir)
  if (cell(nextPos) != '#') add(State(nextPos, dir))
}

fun <T> findPath(target: T?, predecessor: Map<T, T>) = buildList<T> {
  var n = target
  while (n != null) {
    add(n)
    n = predecessor[n]
  }
}.reversed()

val input = generateSequence(::readlnOrNull)
val data = input.foldIndexed(Data(), { y, data, line -> data.update(y, line) })
aStarSearch(
  start = State(data.start, Right),
  end = { s -> s.position == data.end },
  adjacency = { s -> data.adjacency(s) },
  stepCost = { from, to -> if (from.position == to.position) 1000 else 1 },
  estimate = { s -> 1 },
).cost
