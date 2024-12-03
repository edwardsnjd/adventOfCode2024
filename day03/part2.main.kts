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

class State {
  var accept: Boolean = true
  var mults: MutableList<Mul> = mutableListOf()

  operator fun invoke(cmd: Command) = apply {
    when (cmd) {
      Do -> accept = true
      Dont -> accept = false
      is Mul -> if (accept) mults.add(cmd)
    }
  }
}

generateSequence(::readlnOrNull)
  .map(::parseCommands)
  .flatten()
  .fold(State()) { acc, cmd -> acc(cmd) }
  .let { it.mults }
  .map { it.a * it.b }
  .sum()
