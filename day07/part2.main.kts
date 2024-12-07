#! /usr/bin/env -S kotlin -J-ea

data class Entry(val total: Long, val values: List<Long>) {
  companion object {
    fun fromString(line: String) =
      line.split(": ").let {
        Entry(
          total = it[0]!!.toLong(),
          values = it[1]!!.split(" ").map(String::toLong),
        )
      }
  }
}

sealed interface Operation {
  fun eval(l: Long, r: Long): Long

  object Multiply: Operation {
    override fun eval(l: Long, r: Long) = l * r
  }

  object Add: Operation {
    override fun eval(l: Long, r: Long) = l + r
  }

  object Concatenate: Operation {
    override fun eval(l: Long, r: Long) = "${l}${r}".toLong()
  }
}
val operations = listOf(Operation.Multiply, Operation.Add, Operation.Concatenate)

fun isValid(entry: Entry): Boolean {
  return options(entry.values)
    .any { entry.total == it }
}

fun options(values: List<Long>): List<Long> {
  return if (values.count() < 2) listOf(values.sum())
  else operations.flatMap { op ->
    val mergedFirstTwo = op.eval(values[0], values[1])
    val rest = values.drop(2)

    val simplified: List<Long> = buildList {
      add(mergedFirstTwo)
      addAll(rest)
    }

    options(simplified)
  }
}

generateSequence(::readlnOrNull)
  .map(Entry::fromString)
  .toList()
  .filter(::isValid)
  .map(Entry::total)
  .sum()
