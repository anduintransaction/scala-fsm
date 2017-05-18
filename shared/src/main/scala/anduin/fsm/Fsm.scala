// Copyright (C) 2014-2017 Anduin Transactions Inc.

package anduin.fsm

trait State
trait Input

final case class Transformation[FROM <: State, I <: Input, TO <: State](fn: (FROM, I) => TO) {
  def apply(s: FROM, i: I): TO = fn(s, i)
}

final case class Fsm[S <: State](s: S) {
  def transition[I <: Input, TO <: State](i: I)(implicit t: Transformation[S, I, TO]): Fsm[TO] = {
    Fsm(t(s, i))
  }
}
