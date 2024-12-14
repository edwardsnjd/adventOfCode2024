#! /usr/bin/env -S kotlin -J-ea

val input = generateSequence(::readlnOrNull).toList()

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
      latestA = Coord(dx.toInt(),dy.toInt())
    }
    buttonBRegex.matchEntire(line)?.destructured?.let { (dx,dy) ->
      latestB = Coord(dx.toInt(),dy.toInt())
    }
    prizeRegex.matchEntire(line)?.destructured?.let { (x,y) ->
      latestPrize = Coord(x.toInt(),y.toInt())
      machines.add(Machine(latestA!!, latestB!!, latestPrize!!))
    }
  }
}

data class Coord(val x: Int, val y: Int) {
  operator fun minus(c: Coord) = Coord(x-c.x, y-c.y)
}

data class Machine(val buttonA: Coord, val buttonB: Coord, val prize: Coord) {
  fun minPath(): Int? = buildList {
    for (i in 0..100) {
      for (j in 0..100) {
        if (
          (buttonA.x * i + buttonB.x * j == prize.x) &&
          (buttonA.y * i + buttonB.y * j == prize.y)
        ) add(3*i +  j)
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
