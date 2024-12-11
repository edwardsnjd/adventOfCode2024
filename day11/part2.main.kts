#! /usr/bin/env -S kotlin -J-ea

val input = generateSequence(::readlnOrNull).toList().first()

var stones = input.split(" ").map { it.toLong() }

fun nextStones(stone: Long) = buildList {
  val str = stone.toString()
  when {
    stone == 0L -> add(1)
    str.count() % 2 == 0 -> {
      add(str.take(str.count() / 2).toLong())
      add(str.drop(str.count() / 2).toLong())
    }
    else -> add(stone * 2024)
  }
}

val cache = mutableMapOf<Pair<Long,Int>, Long>()
fun countStones(value: Long, blinks: Int): Long = cache.getOrPut(value to blinks) {
  when (blinks) {
    0 -> 1
    else -> nextStones(value).map { countStones(it, blinks - 1) }.sum()
  }
}

stones.map { countStones(it, 75) }.sum()
