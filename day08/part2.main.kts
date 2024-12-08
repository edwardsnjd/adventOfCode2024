#! /usr/bin/env -S kotlin -J-ea

data class Coord(val x: Int, val y: Int) {
  operator fun plus(other: Coord): Coord =
    Coord(x + other.x, y + other.y)

  operator fun minus(other: Coord): Coord =
    Coord(x - other.x, y - other.y)
}

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
      antiNodesFor(nodes).let { freq to it }
    }.toMap()

  fun antiNodesFor(nodes: Set<Coord>): Set<Coord> =
    pairs(nodes)
      .flatMap { (a, b) -> antiNodeCoordsFor(a, b) }
      .filter(::isOnMap)
      .toSet()

  fun antiNodeCoordsFor(a: Coord, b: Coord): Set<Coord> =
    (b-a).let { diff ->
      sequence {
        var curr = a
        while (isOnMap(curr)) {
          yield(curr)
          curr -= diff
        }
        curr = b
        while (isOnMap(curr)) {
          yield(curr)
          curr += diff
        }
      }
    }
    .toSet()
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
  .let { it.values.fold(mutableSetOf(), Set<Coord>::plus) }
  .let { it.count() }
