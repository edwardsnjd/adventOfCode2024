#! /usr/bin/env -S kotlin

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

val triplets: MutableSet<Set<String>> = mutableSetOf()
for (a in computers) {
  for (b in computers) {
    if (a == b) continue

    for (c in computers) {
      if (c == a) continue
      if (c == b) continue

      val acs = connections[a]!!
      val bcs = connections[b]!!

      if (acs.contains(b) && acs.contains(c) && bcs.contains(c))
        triplets.add(setOf(a,b,c))
    }
  }
}

triplets.filter { it.toList().any { it.startsWith("t") } }.count()
