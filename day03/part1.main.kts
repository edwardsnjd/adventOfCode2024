#! /usr/bin/env kotlin -J-ea

val mulRe = Regex("""mul\((\d+),(\d+)\)""")

data class Mul(val a: Int, val b: Int)

fun parseCommands(line: String): List<Mul> =
  mulRe.findAll(line).toList().map {
    val (a,b) = it.destructured; Mul(a.toInt(),b.toInt())
  }

generateSequence(::readlnOrNull)
  .map(::parseCommands)
  .flatten()
  .map { it.a * it.b }
  .sum()
