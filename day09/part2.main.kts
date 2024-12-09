#! /usr/bin/env -S kotlin -J-ea

val input = generateSequence(::readlnOrNull).toList().first()

val rawGroups = input.mapIndexed { ind, c ->
  val count = c.toString().toInt()
  val fileInd = ind / 2
  (if (ind % 2 == 0) fileInd else null) to count
}

// Pack (working backwards from top file id)
val packedGroups = let {
  val highestFileId = rawGroups.mapNotNull { (f, _) -> f }.max()
  highestFileId
    .downTo(0)
    .fold(rawGroups) { acc, fileId ->
      val fileIdx = acc.indexOfFirst { (f, _) -> f == fileId }
      val (_, fileCount) = acc[fileIdx]
      val spaceIdx = acc.indexOfFirst { (f, c) -> f == null && c >= fileCount }

      val canUseSpace = { idx: Int -> 0 <= idx && idx < fileIdx }
      if (canUseSpace(spaceIdx)) {
        val (_, spaceCount) = acc[spaceIdx]
        (
          acc.take(spaceIdx) +
          listOf(acc[fileIdx]!!) +
          listOf((null to (spaceCount - fileCount))) +
          acc.slice(spaceIdx+1..<fileIdx) +
          listOf((null to fileCount)) +
          acc.drop(fileIdx+1)
        ).filter { (_, c) -> c > 0 }
      }
      else acc
    }
}

val packedBlocks = packedGroups.flatMap { (f, c) -> (0..<c).map { f } }

// Checksum
packedBlocks.foldIndexed(0L) { ind, acc, fileId ->
  if (fileId == null) acc else acc + (fileId.toLong() * ind)
}
