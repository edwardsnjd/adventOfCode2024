#! /usr/bin/env -S kotlin

inline fun mix(a: Long, b: Long) = a xor b
inline fun prune(a: Long) = a % 16777216 // mod 2^23

fun next(secret: Long) =
  secret
    .let { prune(mix(it, (it shl 6))) } // mul 64
    .let { prune(mix(it, (it shr 5))) } // div 32
    .let { prune(mix(it, (it shl 11))) }// mul 2046

fun price(secret: Long) =
  secret % 10

generateSequence(::readlnOrNull)
  .map(String::toLong)
  .flatMap { secret ->
    val secrets = (0..<2000).runningFold(secret) { s, i -> next(s) }
    val prices = secrets.map(::price)
    val changes = prices.windowed(2).map { (a,b) -> b - a }
    val sequences = changes.windowed(4).mapIndexed { i, seq -> seq to prices[i+4] }
    val firstOutcomes = sequences.groupBy { it.first }.mapValues { it.value.first() }.map { it.value }
    firstOutcomes
  }
  .groupBy { it.first }
  .mapValues { it.value.map { pair -> pair.second }.sum() }
  .toList()
  .sortedByDescending { it.second }
  .first()
