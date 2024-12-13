#! /usr/bin/env -S kotlin -J-ea

val input = generateSequence(::readlnOrNull).toList()

val extra = 10000000000000

data class Data(
  var latestA: Coord? = null,
  var latestB: Coord? = null,
  var latestPrize: Coord? = null,
  val machines: MutableList<Machine> = mutableListOf(),
) {
  val buttonARegex = Regex("""Button A: X\+(\d+), Y\+(\d+)""")
  val buttonBRegex = Regex("""Button B: X\+(\d+), Y\+(\d+)""")
  val prizeRegex = Regex("""Prize: X=(\d+), Y=(\d+)""")

  fun update(line: String) = apply {
    buttonARegex.matchEntire(line)?.destructured?.let { (dx,dy) ->
      latestA = Coord(dx.toLong(),dy.toLong())
    }
    buttonBRegex.matchEntire(line)?.destructured?.let { (dx,dy) ->
      latestB = Coord(dx.toLong(),dy.toLong())
    }
    prizeRegex.matchEntire(line)?.destructured?.let { (x,y) ->
      latestPrize = Coord(extra + x.toLong(),extra + y.toLong())
      machines.add(Machine(latestA!!, latestB!!, latestPrize!!))
    }
  }
}

data class Coord(val x: Long, val y: Long) {
  operator fun minus(c: Coord) = Coord(x-c.x, y-c.y)
}

data class Machine(val buttonA: Coord, val buttonB: Coord, val prize: Coord) {
  fun minPath(): Long? = buildList {
    val maxA = prize.x / buttonA.x
    val maxB = prize.x / buttonB.x
    println("0..$maxA and 0..$maxB")

    for (aPresses in 0..maxA) {
      for (bPresses in 0..maxB) {
        if (
          (buttonA.x * aPresses + buttonB.x * bPresses == prize.x) &&
          (buttonA.y * aPresses + buttonB.y * bPresses == prize.y)
        ) add(3L*aPresses +  bPresses)
      }
    }
  }.minOrNull()
}

input
  .fold(Data(), Data::update)
  .machines
  .map(Machine::minPath)
  .filterNotNull()
  .sum()
