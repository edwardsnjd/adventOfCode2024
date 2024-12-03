#! /usr/bin/env kotlin -J-ea

val mulRe = Regex("""(do\(\))|(don't\(\))|(mul\((\d+),(\d+)\))""")

sealed interface Command
object Do: Command
object Dont: Command
data class Mul(val a: Int, val b: Int): Command

fun parseCommands(line: String): List<Command> {
  val matches = mulRe.findAll(line).toList()
  return matches.map {
    val (yep,nope,mult,a,b) = it.destructured
    when {
      yep.isNotEmpty() -> Do
      nope.isNotEmpty() -> Dont
      mult.isNotEmpty() -> Mul(a.toInt(),b.toInt())
      else -> error("Unknown match")
    }
  }
}

generateSequence(::readlnOrNull)
  .map(::parseCommands)
  .flatten()
  .fold(
    object {
      var accept: Boolean = true
      var mults: MutableList<Mul> = mutableListOf()
    }
  ) { acc, cmd ->
    when (cmd) {
      Do -> acc.accept = true
      Dont -> acc.accept = false
      is Mul -> if (acc.accept) acc.mults.add(cmd)
      else -> error("Unknown command")
    }
    acc
  }
  .let { it.mults }
  .map { it.a * it.b }
  .sum()
