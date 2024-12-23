#! /usr/bin/env -S kotlin -J-ea

val input = generateSequence(::readlnOrNull).toList()

val connections: MutableMap<String, MutableSet<String>> = mutableMapOf()
input.forEach { line ->
  val (a,b) = line.split("-")
  if (!connections.contains(a)) connections.set(a, mutableSetOf())
  if (!connections.contains(b)) connections.set(b, mutableSetOf())
  connections[a]!!.add(b)
  connections[b]!!.add(a)
}

val computers = connections.keys.sorted()

fun mutualConnections(subset: Set<String>): Set<String> =
  subset
    .map { connections[it]!!.toSet() }
    .reduce { acc, i -> acc.intersect(i) }

var subsetsByLength = mutableMapOf<Int, Set<Set<String>>>()

var length = 1
subsetsByLength[length] = computers.map { setOf(it) }.toSet()

while (subsetsByLength[length]!!.any()) {
  length += 1

  val prevLength = length - 1
  val prevSubsets = subsetsByLength[prevLength]!!

  subsetsByLength[length] = prevSubsets
    .flatMap { subset -> mutualConnections(subset).map { subset + it } }
    .toSet()
}

val largestPool = subsetsByLength[length-1]!!.first()

largestPool.sorted().joinToString(",")
