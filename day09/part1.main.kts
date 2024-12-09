#! /usr/bin/env -S kotlin -J-ea

val input = generateSequence(::readlnOrNull).toList().first()

val rawBlocks = input.flatMapIndexed { ind, c ->
  val count = c.toString().toInt()
  val fileInd = ind / 2
  (0..<count).map { if (ind % 2 == 0) fileInd else null }
}

// Pack (working forward through spaces)
val packedBlocks = let {
  var blocks = rawBlocks

  var spaceIdx = blocks.indexOfFirst { c -> c == null }
  var fileIdx = blocks.indexOfLast { c -> c != null }

  val canUseSpace = { idx: Int -> 0 <= idx && idx < fileIdx }
  while (canUseSpace(spaceIdx)) {
    blocks =
      blocks.take(spaceIdx) +
      listOf(blocks[fileIdx]!!) +
      blocks.slice(spaceIdx+1..<fileIdx)

    spaceIdx = blocks.indexOfFirst { c -> c == null }
    fileIdx = blocks.indexOfLast { c -> c != null }
  }
  blocks.filterNotNull()
}

// Checksum
packedBlocks.foldIndexed(0L) { ind, acc, fileId ->
  acc + (fileId.toLong() * ind)
}
