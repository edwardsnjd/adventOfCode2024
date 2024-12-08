data class Coord(val x: Int, val y: Int) {
  operator fun plus(other: Coord): Coord = Coord(x + other.x, y + other.y)
  operator fun minus(other: Coord): Coord = Coord(x - other.x, y - other.y)
}

data class Data(
  var width: Int = 0,
  var height: Int = 0,
) {
  fun update(line: String) = apply {
    width = line.count()
    height += 1
  }

  fun isOnMap(c: Coord) =
    c.x >= 0 && c.x < width &&
    c.y >= 0 && c.y < height
}

generateSequence(::readlnOrNull)
  .fold(Data(), Data::update)
