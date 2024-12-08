#! /usr/bin/env -S kotlin -J-ea

data class Coord(val x: Int, val y: Int) {
  operator fun plus(other: Coord): Coord =
    Coord(x + other.x, y + other.y)

  operator fun minus(other: Coord): Coord =
    Coord(x - other.x, y - other.y)
}

assert(Coord(0,1) + Coord(1,2) == Coord(1,3))
assert(Coord(0,1) - Coord(1,2) == Coord(-1,-1))

class Data(
  var width: Int = 0,
  var height: Int = 0,
  val antenna: MutableMap<Char, MutableSet<Coord>> = mutableMapOf()
) {
  fun update(line: String) = apply {
    line
      .withIndex()
      .filter { (ind, char) -> char != '.' }
      .forEach { (ind, freq) ->
        antenna.getOrPut(freq, { mutableSetOf() }).add(Coord(ind, height))
      }

    width = line.count()
    height += 1
  }

  fun isOnMap(c: Coord) =
    c.x >= 0 && c.x < width &&
    c.y >= 0 && c.y < height

  fun antiNodes(): Map<Char, Set<Coord>> =
    antenna.map { (freq, nodes) ->
      antiNodesFor(nodes)
        .filter(this::isOnMap)
        .toSet()
        .let { freq to it }
    }.toMap()

  fun antiNodesFor(nodes: Set<Coord>): Set<Coord> =
    pairs(nodes).flatMap { (a, b) -> antiNodeCoordsFor(a, b) }.toSet()

  fun antiNodeCoordsFor(a: Coord, b: Coord): Set<Coord> =
    (b-a).let { diff -> setOf(a - diff,  b + diff)}
}

fun <T> pairs(items: Collection<T>): Sequence<Pair<T, T>> = sequence {
  items.forEachIndexed { indA, a ->
    items.forEachIndexed { indB, b ->
      if (indB != indA) yield(a to b)
    }
  }
}

generateSequence(::readlnOrNull)
  .fold(Data(), Data::update)
  .let { it.antiNodes() }
  .let { it.values.fold(mutableSetOf<Coord>(), Set<Coord>::plus) }
  .also(::println)
  .let { it.count() }
