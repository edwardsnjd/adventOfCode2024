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
      latestA = Coord(dx.toLong(),dy.toLong())
    }
    buttonBRegex.matchEntire(line)?.destructured?.let { (dx,dy) ->
      latestB = Coord(dx.toLong(),dy.toLong())
    }
    prizeRegex.matchEntire(line)?.destructured?.let { (x,y) ->
      latestPrize = Coord(x.toLong(),y.toLong())
      machines.add(Machine(latestA!!, latestB!!, latestPrize!!))
    }
  }
}

data class Coord(val x: Long, val y: Long)

data class Machine(val buttonA: Coord, val buttonB: Coord, val prize: Coord) {
  fun minPath(): Long? = buildList<Long> {
    // For each matching combo:
    //   p.A + q.B = P
    // Form eq for each dimension:
    //   p.Ax + q.Bx = Px
    //   p.Ay + q.By = Py
    // Rearrange and solve for p and q:
    val q =
      ((prize.y.toDouble() * buttonA.x) - (prize.x * buttonA.y)) /
      ((buttonB.y * buttonA.x) - (buttonB.x * buttonA.y))
    val p =
      (prize.x.toDouble() - (q * buttonB.x)) / buttonA.x
    if (kotlin.math.abs(q) % 1.0 < 1E-10 && kotlin.math.abs(p) % 1.0 < 1E-10) {
      add(3L*p.toLong() + q.toLong())
    }
  }.minOrNull()
}

val extra = 10000000000000L

input
  .fold(Data(), Data::update)
  .machines
  .map { Machine(it.buttonA, it.buttonB, Coord(it.prize.x+extra, it.prize.y+extra)) }
  .map(Machine::minPath)
  .filterNotNull()
  .sum()
