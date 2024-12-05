#! /usr/bin/env kotlin -J-ea

sealed interface Entry
data class Order(val before: String, val after: String): Entry
data class Update(val pages: List<String>): Entry

val orderRe = Regex("""(\d+)\|(\d+)""")
fun parse(line: String): Entry? =
  if (line.isEmpty()) null
  else
    orderRe.matchEntire(line)?.destructured?.let { (b, a) -> Order(b, a) }
    ?: line.split(",").let(::Update)

class Data {
  val orderings: MutableMap<String, MutableSet<String>> = mutableMapOf()
  val updates: MutableList<List<String>> = mutableListOf()

  fun update(e: Entry) = apply {
    when (e) {
      is Order -> orderings.getOrPut(e.before, { mutableSetOf() }).add(e.after)
      is Update -> updates.add(e.pages)
    }
  }

  fun isValid(pages: List<String>) =
    !pages.withIndex().any { (idx, page) ->
      pages.drop(idx).any { orderings[it]?.contains(page) ?: false }
    }

  fun corrected(pages: List<String>): List<String> =
    pages.sortedWith { a, b -> if (orderings[a]?.contains(b) ?: false) -1 else 0 }
}

fun <E> List<E>.centralItem(): E = get(count() / 2)

generateSequence(::readlnOrNull)
  .mapNotNull(::parse)
  .fold(Data(), Data::update)
  .let {
    it.updates
      .filter(it::isValid)
      .map { it.centralItem().toInt() }
      .sum()
  }
