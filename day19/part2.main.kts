#! /usr/bin/env -S kotlin -J-ea

val input = generateSequence(::readlnOrNull).toList()

val towels = input.get(0).split(", ")
val designs = input.drop(2)

val cache = mutableMapOf<String, Long>()
fun combos(towels: List<String>, design: String): Long = cache.getOrPut(design) {
  if (design.isEmpty()) 1L
  else towels
    .filter(design::startsWith)
    .map { t -> combos(towels, design.drop(t.count())) }
    .sum()
}

designs.map { combos(towels, it) }.sum()
