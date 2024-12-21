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

// NB. Order matters here!  When sorted by this order, jumps are minimised
enum class DirKey { Left, Down, Up, Right, DirA }

object NumericKeypad {
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

  val moves: Map<Pair<NumericKey, NumericKey>, List<DirKey>> by lazy {
    val moves: MutableMap<Pair<NumericKey, NumericKey>, List<DirKey>> = mutableMapOf()

    for (i in NumericKey.values()) {
      moves[i to i] = emptyList()
    }

    for ((fromKey, adjs) in adjacency) {
      adjs.forEach { (dir, toKey) -> moves[fromKey to toKey] = listOf(dir) }
    }

    for (k in NumericKey.values()) {
      for (i in NumericKey.values()) {
        for (j in NumericKey.values()) {
          val v = moves.get(i to k)
          val u = moves.get(k to j)

          if (u == null || v == null) continue

          val throughK = v + u
          val throughKCount = throughK.count()

          val existingCount = moves.get(i to j)?.count() ?: Int.MAX_VALUE

          if (throughKCount < existingCount) {
            moves[i to j] = throughK.sorted()
          }
        }
      }
    }

    moves
  }
}

object DirectionalKeypad {
  val adjacency: Map<DirKey, List<Pair<DirKey, DirKey>>> = mapOf(
    DirA to listOf(Left to Up, Down to Right),
    Up to listOf(Right to DirA, Down to Down),
    Down to listOf(Left to Left, Right to Right, Up to Up),
    Left to listOf(Right to Down),
    Right to listOf(Left to Down, Up to DirA),
  )

  val moves: Map<Pair<DirKey, DirKey>, List<DirKey>> by lazy {
    val moves: MutableMap<Pair<DirKey, DirKey>, List<DirKey>> = mutableMapOf()

    for (i in DirKey.values()) {
      moves[i to i] = emptyList()
    }

    for ((fromKey, adjs) in adjacency) {
      adjs.forEach { (dir, toKey) -> moves[fromKey to toKey] = listOf(dir) }
    }

    for (k in DirKey.values()) {
      for (i in DirKey.values()) {
        for (j in DirKey.values()) {
          val toK = moves.get(i to k)
          val fromK = moves.get(k to j)

          if (toK == null || fromK == null) continue

          val throughK = toK + fromK
          val throughKCount = throughK.count()

          val existingCount = moves.get(i to j)?.count() ?: Int.MAX_VALUE

          if (throughKCount < existingCount) {
            moves[i to j] = throughK.sorted()
          }
        }
      }
    }

    moves
  }
}

fun List<DirKey>.repr() = listOf(
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


fun go(c: String): Int {
  val numKeys = c.map(NumericKey::parse)
  val robot1Dirs = (listOf(NumA) + numKeys).windowed(2).flatMap { (a,b) -> NumericKeypad.moves[a to b]!! + listOf(DirA) }
  val robot2Dirs = (listOf(DirA) + robot1Dirs).windowed(2).flatMap { (a,b) -> DirectionalKeypad.moves[a to b]!! + listOf(DirA) }
  val robot3Dirs = (listOf(DirA) + robot2Dirs).windowed(2).flatMap { (a,b) -> DirectionalKeypad.moves[a to b]!! + listOf(DirA) }
  val codeNum = Regex("""\d+""").find(c)!!.value.toInt()
  // println("$c $codeNum\n\t${robot1Dirs.repr()}\n\t${robot2Dirs.repr()}\n\t${robot3Dirs.repr()}")
  return codeNum * robot3Dirs.count()
}

val codes = generateSequence(::readlnOrNull).toList()
codes.map(::go).sum()
