#! /usr/bin/env -S kotlin -J-ea

import NumericKey.*
import DirKey.*

enum class NumericKey { Zero, One, Two, Three, Four, Five, Six, Seven, Eight, Nine, NumA }
enum class DirKey { Left, Down, Up, Right, DirA }

typealias KeySequence = List<DirKey>
typealias KeyOptions = List<KeySequence>
typealias Step<T> = Pair<T,T>

class NumericKeypad {
  val adjacencies: Map<NumericKey, List<Pair<DirKey, NumericKey>>> = mapOf(
    NumA  to listOf(               Left to Zero,                   Up to Three),
    Zero  to listOf(                               Right to NumA,  Up to Two),
    One   to listOf(                               Right to Two,   Up to Four),
    Two   to listOf(Down to Zero,  Left to One,    Right to Three, Up to Five),
    Three to listOf(Down to NumA,  Left to Two,                    Up to Six),
    Four  to listOf(Down to One,                   Right to Five,  Up to Seven),
    Five  to listOf(Down to Two,   Left to Four,   Right to Six,   Up to Eight),
    Six   to listOf(Down to Three, Left to Five,                   Up to Nine),
    Seven to listOf(Down to Four,                  Right to Eight),
    Eight to listOf(Down to Five,  Left to Seven,  Right to Nine),
    Nine  to listOf(Down to Six,   Left to Eight),
  )

  val pathsByStep: Map<Step<NumericKey>, KeyOptions> by lazy {
    shortestPaths(NumericKey.values().toList(), adjacencies)
      .mapValues { it.value.map { it + listOf(DirA) } }
  }
}

class DirectionalKeypad {
  val adjacencies: Map<DirKey, List<Pair<DirKey, DirKey>>> = mapOf(
    DirA  to listOf(Left to Up,   Down to Right),
    Up    to listOf(              Down to Down, Right to DirA),
    Down  to listOf(Left to Left,               Right to Right, Up to Up),
    Left  to listOf(                            Right to Down),
    Right to listOf(Left to Down,                               Up to DirA),
  )

  val pathsByStep: Map<Step<DirKey>, KeyOptions> by lazy {
    shortestPaths(DirKey.values().toList(), adjacencies)
      .mapValues { it.value.map { it + listOf(DirA) } }
  }
}

fun <T> shortestPaths(
  nodes: Collection<T>,
  adjacencies: Map<T, List<Pair<DirKey, T>>>,
): Map<Step<T>, KeyOptions> {
  // Floyd Warshall (fill in entire matrix)
  val dists: MutableMap<Step<T>, Int> = mutableMapOf()
  val moves: MutableMap<Step<T>, MutableList<KeySequence>> = mutableMapOf()

  for (i in nodes) {
    dists[i to i] = 0
    moves[i to i] = mutableListOf(emptyList())
  }

  for ((fromKey, adjs) in adjacencies) {
    adjs.forEach { (step, toKey) ->
      dists[fromKey to toKey] = 1
      moves[fromKey to toKey] = mutableListOf(listOf(step))
    }
  }

  for (k in nodes) {
    for (i in nodes) {
      for (j in nodes) {
        if (i == k || j == k) continue

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

// fun KeySequence.repr() = listOf(
//   count(),
//   map {
//     when (it) {
//       DirA -> 'A'
//       Left -> '<'
//       Right -> '>'
//       Up -> '^'
//       Down -> 'v'
//     }
//   }.joinToString(""),
// ).joinToString(": ")

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

fun KeyOptions.shortest(): KeyOptions {
  var minLength = Int.MAX_VALUE
  var results = mutableSetOf<KeySequence>()

  forEach { ks ->
    val length = ks.count()
    when {
      length < minLength -> {
        minLength = length
        results = mutableSetOf(ks)
      }
      length == minLength -> {
        results.add(ks)
      }
    }
  }

  return results.toList()
}

val nk = NumericKeypad()
val dk = DirectionalKeypad()

fun viaRobots(options: KeyOptions, robots: Int): KeyOptions {
  var opts = options

  for (i in (0..<robots)) {
    opts = opts.flatMap { keys ->
      (listOf(DirA) + keys)
        .windowed(2)
        .map { (a,b) -> dk.pathsByStep[a to b]!! }
        .reduce { a, o -> a.flatMap { foo -> o.map { foo + it } } }
    }.shortest()
  }

  return opts.shortest()
}

fun complexity(c: String): Int {
  val codeNum = Regex("""\d+""").find(c)!!.value.toInt()
  val numKeys = c.map(::parse)

  val steps = (listOf(NumA) + numKeys).windowed(2)
  val stepOptions = steps.map { (a,b) -> nk.pathsByStep[a to b]!! }

  val robots = 2

  var total =
    stepOptions
      .map { viaRobots(it, robots).let { it.first().count() } }
      .sum()

  return codeNum * total
}

generateSequence(::readlnOrNull).map(::complexity).sum()
