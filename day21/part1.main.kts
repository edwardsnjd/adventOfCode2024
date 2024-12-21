#! /usr/bin/env -S kotlin -J-ea

import NumericKey.*
import DirKey.*

enum class NumericKey {
  Zero, One, Two, Three, Four, Five, Six, Seven, Eight, Nine, NumA;
  companion object {
    fun parse(c: Char) = when (c) {
      'A' -> NumA
      '0' -> Zero
      '1' -> One
      '2' -> Two
      '3' -> Three
      '4' -> Four
      '5' -> Five
      '6' -> Six
      '7' -> Seven
      '8' -> Eight
      '9' -> Nine
      else -> error("Unrecognised NumericKey code $c")
    }
  }
}

enum class DirKey { Left, Down, Up, Right, DirA }

typealias KeySequence = List<DirKey>

class NumericKeypad {
  val adjacency: Map<NumericKey, List<Pair<DirKey, NumericKey>>> = mapOf(
    NumA  to listOf(               Left to Zero,                   Up to Three),
    Zero  to listOf(                               Right to NumA,  Up to Two),
    One   to listOf(               Up to Four,     Right to Two),
    Two   to listOf(Down to Zero,  Left to One,    Right to Three, Up to Five),
    Three to listOf(Down to NumA,  Left to Two,                    Up to Six),
    Four  to listOf(Down to One,   Right to Five,                  Up to Seven),
    Five  to listOf(Down to Two,   Left to Four,   Right to Six,   Up to Eight),
    Six   to listOf(Down to Three, Left to Five,                   Up to Nine),
    Seven to listOf(Down to Four,  Right to Eight),
    Eight to listOf(Down to Five,  Left to Seven,  Right to Nine),
    Nine  to listOf(Down to Six,   Left to Eight),
  )

  val shortestPaths: Map<Pair<NumericKey, NumericKey>, List<KeySequence>> by lazy {
    shortestPaths(NumericKey.values().toList(), adjacency)
  }
}

class DirectionalKeypad {
  val adjacency: Map<DirKey, List<Pair<DirKey, DirKey>>> = mapOf(
    DirA to listOf(Left to Up, Down to Right),
    Up to listOf(Right to DirA, Down to Down),
    Down to listOf(Left to Left, Right to Right, Up to Up),
    Left to listOf(Right to Down),
    Right to listOf(Left to Down, Up to DirA),
  )

  val shortestPaths: Map<Pair<DirKey, DirKey>, List<KeySequence>> by lazy {
    shortestPaths(DirKey.values().toList(), adjacency)
  }
}

fun <T,U> shortestPaths(
  nodes: Collection<T>,
  adjacency: Map<T, List<Pair<U, T>>>,
): Map<Pair<T, T>, List<List<U>>> {
  val dists: MutableMap<Pair<T, T>, Int> = mutableMapOf()
  val moves: MutableMap<Pair<T, T>, MutableList<List<U>>> = mutableMapOf()

  for (i in nodes) {
    dists[i to i] = 0
    moves[i to i] = mutableListOf(emptyList<U>())
  }

  for ((fromKey, adjs) in adjacency) {
    adjs.forEach { (dir, toKey) ->
      dists[fromKey to toKey] = 1
      moves[fromKey to toKey] = mutableListOf(listOf(dir))
    }
  }

  for (k in nodes) {
    for (i in nodes) {
      for (j in nodes) {
        val distToK = dists.get(i to k)
        val distFromK = dists.get(k to j)
        if (distToK == null || distFromK == null) continue

        val distThroughK = distToK + distFromK
        val existingDist = dists.get(i to j) ?: Int.MAX_VALUE

        if (distThroughK < existingDist) {
          dists[i to j] = distThroughK
          moves[i to j] = mutableListOf()
        }
        if (distThroughK <= existingDist) {
          val movesToK = moves[i to k]!!
          val movesFromK = moves[k to j]!!
          val movesThroughK = movesToK.flatMap { i2k -> movesFromK.map { i2k + it } }
          moves[i to j]!!.addAll(movesThroughK)
        }
      }
    }
  }

  return moves
}

fun KeySequence.repr() = listOf(
  count(),
  map {
    when (it) {
      DirA -> 'A'
      Left -> '<'
      Right -> '>'
      Up -> '^'
      Down -> 'v'
    }
  }.joinToString(""),
).joinToString(": ")

val nk = NumericKeypad()
val dk = DirectionalKeypad()

fun go(c: String): Int {
  val numKeys = c.map(NumericKey::parse)

  val robot1Options: List<KeySequence> = buildList {
    listOf(numKeys)
      .map { listOf(NumA) + it }
      .forEach { it.windowed(2).flatMap { (a,b) -> nk.shortestPaths[a to b]!!.map { optForA2B -> optForA2B + listOf(DirA) } } }
      .let { seqs ->
        val minLength = seqs.map { it.count() }.min()
        val shortest = seqs.filter { it.count() == minLength }
        println("Robot1 filtered from ${seqs.count()} to ${shortest.count()}")
        shortest
      }
  println(robot1Options[0]!!)

  val robot2Options: List<KeySequence> =
    robot1Options
      .map { listOf(DirA) + it }
      .flatMap { it.windowed(2).flatMap { (a,b) -> dk.shortestPaths[a to b]!!.map { it + listOf(DirA) } } }
      .let { seqs ->
        val minLength = seqs.map { it.count() }.min()
        val shortest = seqs.filter { it.count() == minLength }
        println("Robot2 filtered from ${seqs.count()} to ${shortest.count()}")
        shortest
      }
  println(robot2Options[0]!!)

  val robot3Options: List<KeySequence> =
    robot2Options
      .map { listOf(DirA) + it }
      .flatMap { it.windowed(2).flatMap { (a,b) -> dk.shortestPaths[a to b]!!.map { it + listOf(DirA) } } }
      .let { seqs ->
        val minLength = seqs.map { it.count() }.min()
        val shortest = seqs.filter { it.count() == minLength }
        println("Robot3 filtered from ${seqs.count()} to ${shortest.count()}")
        shortest
      }
  println(robot3Options[0]!!)

  val robot3Seq =
    robot3Options
      .sortedBy { it.count() }
      .first()

  println()
  val codeNum = Regex("""\d+""").find(c)!!.value.toInt()
  return codeNum * robot3Seq.count()
}

val codes = generateSequence(::readlnOrNull).toList()
codes.map(::go).sum()
