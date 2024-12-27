#! /usr/bin/env -S kotlin -J-ea

typealias Grid = List<Int>
fun Grid.intersectsWith(other: Grid) =
  zip(other).any { (a,b) -> a and b != 0 }

data class Input(
  val locks: MutableList<Grid> = mutableListOf(),
  val keys: MutableList<Grid> = mutableListOf(),
  var partial: MutableList<String> = mutableListOf(),
) {
  fun update(line: String) = apply {
    if (line.isEmpty()) return@apply

    partial.add(line)
    if (partial.count() < 7) return@apply

    val converted = partial.map { line ->
      line
        .map { c -> if (c == '#') 1 else 0 }
        .foldIndexed(0) { ind, acc, c -> acc + (c.toInt() shl (5 - ind - 1)) }
    }
    if (converted.first() == 31) locks.add(converted)
    else keys.add(converted)

    partial = mutableListOf()
  }
}

val input = generateSequence(::readlnOrNull).toList()
val (locks: List<Grid>, keys: List<Grid>) = input.fold(Input(), Input::update)

locks
  .flatMap { lock -> keys.filterNot { it.intersectsWith(lock) } }
  .count()
