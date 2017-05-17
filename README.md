# scala-fsm

Finite state machine (FSM) implemented in scala. State transitions are being checked in compile-time, which means that invalid transitions will not compile.

## Usage

See the unit tests for more example usages. Here is an example:

* Define your states
```
    case class A(id: String) extends State
    case class B(id: String) extends State
    case class C(id: String) extends State
```

* Define your inputs
```
    case class Iab(id: String) extends Input
    case class Iac(id: String) extends Input
    case class Ibc(id: String) extends Input
```

* Define your transitions
```
    implicit val tAB = Transformation[A, Iab, B]( (a, e) => B(a.id + e.id))
    implicit val tAC = Transformation[A, Iac, C]( (a, e) => C(a.id + e.id))
    implicit val tBC = Transformation[B, Ibc, C]( (b, e) => C(b.id + e.id))
    implicit def tCC[E <: Input] = Transformation[C, E, C]( (c, e) => C(c.id + "i"))
```

* Then you can transition between stages:
```
    Fsm(A("a")).transition(Iab("b")) shouldBe Fsm(B("ab"))
    Fsm(A("a")).transition(Iac("c")) shouldBe Fsm(C("ac"))
    Fsm(B("b")).transition(Ibc("c")) shouldBe Fsm(C("bc"))

    Fsm(C("c")).transition(Iab("")) shouldBe Fsm(C("ci"))
    Fsm(C("c")).transition(Iab("")).transition(Iac("")).transition(Ibc("")) shouldBe Fsm(C("ciii"))

    // This won't compile
    //Fsm(A("a")).transition(Ibc(""))
```
