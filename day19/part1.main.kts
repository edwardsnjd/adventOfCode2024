#! /usr/bin/env -S kotlin -J-ea

val input = generateSequence(::readlnOrNull).toList()

val towels = input.get(0).split(", ")
val designs = input.drop(2)

val cache = mutableMapOf<String, Boolean>()
fun combos(towels: List<String>, design: String): Boolean = cache.getOrPut(design) {
  if (design.isEmpty()) true
  else towels
    .filter(design::startsWith)
    .any { t -> combos(towels, design.drop(t.count())) }
}

designs.filter { combos(towels, it) }.count()
