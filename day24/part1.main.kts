#! /usr/bin/env -S kotlin -J-ea

sealed interface Expr
data class Literal(val value: Long): Expr
data class Reference(val wire: String): Expr
data class Xor(val lhs: Expr, val rhs: Expr): Expr
data class And(val lhs: Expr, val rhs: Expr): Expr
data class Or(val lhs: Expr, val rhs: Expr): Expr

data class Input(
  val wires: MutableMap<String, Expr> = mutableMapOf(),
) {
  fun update(line: String) = apply {
    when {
      line.contains(":") -> handleLiteral(line)
      line.contains(">") -> handleExpr(line)
    }
  }

  private fun handleLiteral(line: String) {
    val (wire, rawValue) = line.split(": ")
    wires[wire] = Literal(rawValue.toLong())
  }

  private fun handleExpr(line: String) {
    val (lhs, op, rhs, _, dst) = line.split(" ")
    wires[dst] = when (op) {
      "XOR" -> Xor(Reference(lhs), Reference(rhs))
      "OR" -> Or(Reference(lhs), Reference(rhs))
      "AND" -> And(Reference(lhs), Reference(rhs))
      else -> error("Unknown")
    }
  }
}

fun calculate(wires: Map<String, Expr>, tsorted: Collection<String>): Map<String, Long> {
  val wireValues: MutableMap<String, Long> = mutableMapOf()

  fun eval(e: Expr): Long =
    when (e) {
      is Literal -> e.value
      is Reference -> wireValues[e.wire] ?: error("missing value for ${e.wire}")
      is Xor -> eval(e.lhs) xor eval(e.rhs)
      is And -> eval(e.lhs) and eval(e.rhs)
      is Or -> eval(e.lhs) or eval(e.rhs)
    }

  for (w in tsorted) {
    wireValues[w] = eval(wires[w] ?: error("missing value for wire $w"))
  }

  return wireValues
}

val input = generateSequence(::readlnOrNull).toList()
val wires: Map<String, Expr> = input.fold(Input(), Input::update).wires

// To save time: topologic sorted input using `topo-sorted.sh`
// val tsorted = listOf( "y02", "x02", "y01", "x01", "y00", "x00", "z02", "z01", "z00",)
// val tsorted = listOf( "x02", "y01", "y04", "pbm", "x04", "y03", "y00", "x03", "x00", "x01", "y02", "fgs", "ntg", "kjc", "psh", "djm", "nrd", "ffh", "vdt", "tgd", "fst", "kpj", "kwq", "tnw", "mjb", "hwm", "qhw", "wpb", "gnj", "bqk", "frj", "bfw", "rvg", "z01", "z05", "z12", "z03", "z07", "z04", "z06", "z09", "z02", "z00", "z11", "z08", "z10",)
val tsorted = listOf( "y27", "x27", "x09", "y09", "vgg", "y42", "x42", "y41", "x41", "spq", "y01", "x01", "x12", "y12", "y07", "x07", "x29", "y29", "x02", "y02", "x17", "y17", "x38", "y38", "fcs", "y33", "x33", "hrh", "x28", "y28", "hvb", "y13", "x13", "x40", "y40", "y23", "x23", "pbr", "dnr", "x25", "y25", "x32", "y32", "y35", "x35", "x34", "y34", "x26", "y26", "x31", "y31", "rbm", "x36", "y36", "y10", "x10", "jcb", "dpj", "dbr", "y18", "x18", "qnw", "x15", "y15", "x11", "y11", "x39", "y39", "tgd", "y44", "x44", "x21", "y21", "y06", "x06", "bcg", "y43", "x43", "kps", "jkm", "x00", "y00", "x20", "y20", "mbp", "fnf", "x37", "y37", "ntr", "qrn", "pgr", "pcv", "btb", "x03", "y03", "cwb", "ssg", "kmk", "y08", "x08", "x16", "y16", "y04", "x04", "twj", "y24", "x24", "rqf", "prr", "ngq", "ftt", "x22", "y22", "x05", "y05", "trp", "npr", "y14", "x14", "tsw", "hqg", "dsb", "hnt", "y19", "x19", "pmq", "vbw", "x30", "y30", "hcp", "bsk", "rjr", "qfw", "jfp", "fjp", "jcr", "jwh", "sgt", "krb", "jqj", "crj", "nwj", "mkh", "qqb", "mkq", "fqh", "wbt", "gck", "z31", "bfs", "wvn", "rfv", "vrj", "jtb", "nmb", "jmh", "jss", "nhg", "kqm", "tst", "bng", "svq", "pqj", "qpj", "mkj", "fnd", "stt", "gbs", "vrh", "dcp", "z00", "qrt", "thv", "bpn", "nkc", "gsg", "rkt", "smm", "cff", "fkm", "wrc", "nww", "z01", "z02", "hnv", "bdp", "z03", "rvq", "cmh", "mwj", "z04", "ngj", "z05", "ckj", "wts", "mcs", "z06", "swp", "jdb", "kpt", "z08", "z07", "rdt", "qvw", "z09", "qfq", "trw", "z10", "cvp", "nvc", "jnn", "z11", "stg", "z12", "fmk", "wnj", "z13", "qdw", "vsd", "z14", "csh", "prp", "ckp", "hdg", "z15", "dhg", "z16", "pch", "rrb", "z17", "mdg", "hmt", "z18", "pfb", "nts", "scv", "tpm", "z19", "z20", "pdc", "fff", "z21", "tmk", "z22", "btw", "kvp", "jnh", "fhw", "z23", "z24", "ngb", "vst", "z25", "jgn", "cbj", "z26", "pnj", "gcc", "bfq", "pms", "z28", "pph", "mcb", "wmd", "z27", "z29", "hhd", "z30", "spj", "ctc", "hkh", "rjt", "qhp", "smg", "z32", "grt", "z33", "hbq", "hkt", "qdd", "z34", "rfw", "wvq", "wgm", "jwd", "cgm", "vdw", "z37", "z35", "z36", "vvr", "mdm", "z38", "hsf", "tkf", "z39", "vbm", "nhq", "rgt", "z41", "z40", "vtn", "swn", "z42", "tbg", "bkf", "z43", "dmk", "tfj", "scp", "z45", "z44",)
val wireValues = calculate(wires, tsorted)

wireValues
  .filter { it.key.startsWith('z') }
  .toList()
  .sortedByDescending { (k,v) -> k }
  .map { (k,v) -> v }
  .fold(0L) { acc, v -> (acc shl 1) + v }
