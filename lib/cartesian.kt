// data class Pair   <T,U>
// data class Triple <T,U,V>
data class Quartet<T,U,V,W  >(val t: T, val u: U, val v: V, val w: W)
data class Quintet<T,U,V,W,X>(val t: T, val u: U, val v: V, val w: W, val x: X)
data class Hextet <T,U,V,W,X,Y>(val t: T, val u: U, val v: V, val w: W, val x: X, val y: Y)

fun <T,U> cartesianProduct(
  ts: Collection<T>,
  us: Collection<U>,
) = sequence {
  for (t in ts)
    for (u in us)
      yield(Pair(t,u))
}
fun <T,U,V> cartesianProduct(
  ts: Collection<T>,
  us: Collection<U>,
  vs: Collection<V>,
) = sequence {
  for ((t,u) in cartesianProduct(ts, us))
    for (v in vs)
      yield(Triple(t,u,v))
}
fun <T,U,V,W> cartesianProduct(
  ts: Collection<T>,
  us: Collection<U>,
  vs: Collection<V>,
  ws: Collection<W>,
) = sequence {
  for ((t,u,v) in cartesianProduct(ts, us, vs))
    for (d in ws)
      yield(Quartet(t,u,v,d))
}
fun <T,U,V,W,X> cartesianProduct(
  ts: Collection<T>,
  us: Collection<U>,
  vs: Collection<V>,
  ws: Collection<W>,
  xs: Collection<X>,
) = sequence {
  for ((t,u,v,w) in cartesianProduct(ts, us, vs, ws))
    for (x in xs)
      yield(Quintet(t,u,v,w,x))
}
fun <T,U,V,W,X,Y> cartesianProduct(
  ts: Collection<T>,
  us: Collection<U>,
  vs: Collection<V>,
  ws: Collection<W>,
  xs: Collection<X>,
  ys: Collection<Y>,
) = sequence {
  for ((t,u,v,w,x) in cartesianProduct(ts, us, vs, ws, xs))
    for (y in ys)
      yield(Hextet(t,u,v,w,x,y))
}

fun <T,U> Collection<T>.pairsWith(us: Collection<U>) = sequence {
  for (t in this@pairsWith)
    for (u in us)
      yield(Pair(t,u))
}
fun <T,U,V> Sequence<Pair<T,U>>.triplesWith(vs: Collection<V>) = sequence {
  for ((t,u) in this@triplesWith)
    for (v in vs)
      yield(Triple(t,u,v))
}
fun <T,U,V,W> Sequence<Triple<T,U,V>>.quartetsWith(ws: Collection<W>) = sequence {
  for ((t,u,v) in this@quartetsWith)
    for (w in ws)
      yield(Quartet(t,u,v,w))
}
fun <T,U,V,W,X> Sequence<Quartet<T,U,V,W>>.quintetsWith(xs: Collection<X>) = sequence {
  for ((t,u,v,w) in this@quintetsWith)
    for (x in xs)
      yield(Quintet(t,u,v,w,x))
}

