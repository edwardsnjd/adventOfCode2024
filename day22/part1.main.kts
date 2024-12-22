#! /usr/bin/env -S kotlin

inline fun mix(a: Long, b: Long) = a xor b
inline fun prune(a: Long) = a % 16777216 // mod 2^23

fun next(secret: Long) =
  secret
    .let { prune(mix(it, (it shl 6))) } // mul 64
    .let { prune(mix(it, (it shr 5))) } // div 32
    .let { prune(mix(it, (it shl 11))) }// mul 2046

generateSequence(::readlnOrNull)
  .map(String::toLong)
  .map { secret -> (0..<2000).fold(secret) { s, i -> next(s) } }
  .sum()
