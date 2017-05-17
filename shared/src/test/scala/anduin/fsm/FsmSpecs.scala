// Copyright (C) 2014-2017 Anduin Transactions Inc.

package anduin.fsm

// scalastyle:off underscore.import
import org.scalatest._
// scalastyle:on underscore.import

final class FsmSpecs extends FreeSpec with Matchers with OptionValues with Inside with Inspectors {
  "Playback state machine" - {
    /**
      * A playback state machine that looks like this:
      *                 --------------------------
      *                |                         |
      *                |                         V
      * Initial --> Playing <==> Buffering --> Stopped
      *               A                         A
      *              ||                         |
      *              V                          |
      *            Paused ----------------------
      */
    // States
    case object Initial extends State
    case object Playing extends State
    case object Paused extends State
    case object Buffering extends State
    case object Stopped extends State

    // Inputs
    case object StartPlayback extends Input
    case object StopPlayback extends Input
    case object PausePlayback extends Input
    case object UnPausePlayback extends Input
    case object NotEnoughBuffer extends Input
    case object EnoughBuffer extends Input

    // Transformations
    implicit val tInitialPlaying = Transformation[Initial.type, StartPlayback.type, Playing.type]((_, _) => Playing)
    implicit val tPlayingPaused = Transformation[Playing.type, PausePlayback.type, Paused.type]((_, _) => Paused)
    implicit val tPausedPlaying = Transformation[Paused.type, UnPausePlayback.type, Playing.type]((_, _) => Playing)
    implicit val tPlayingBuffering = Transformation[Playing.type, NotEnoughBuffer.type, Buffering.type]((_, _) => Buffering)
    implicit val tBufferingPlaying = Transformation[Buffering.type, EnoughBuffer.type, Playing.type]((_, _) => Playing)
    implicit val tPlayingStopped = Transformation[Playing.type, StopPlayback.type, Stopped.type]((_, _) => Stopped)
    implicit val tPausedStopped = Transformation[Paused.type, StopPlayback.type, Stopped.type]((_, _) => Stopped)

    "Verify state transitions" in {
      Fsm(Initial).transition(StartPlayback) shouldBe Fsm(Playing)

      Fsm(Playing).transition(StopPlayback) shouldBe Fsm(Stopped)
      Fsm(Playing).transition(NotEnoughBuffer) shouldBe Fsm(Buffering)
      Fsm(Playing).transition(PausePlayback) shouldBe Fsm(Paused)

      Fsm(Paused).transition(UnPausePlayback) shouldBe Fsm(Playing)
      Fsm(Paused).transition(StopPlayback) shouldBe Fsm(Stopped)

      Fsm(Buffering).transition(EnoughBuffer) shouldBe Fsm(Playing)

      // This won't compile
      //Fsm(Initial).transition(StopPlayback)
    }
  }

  "Abstract machine" - {
    /**
      * An abstract state machine:
      *
      *                           ------\
      *                           \      \
      *                           V       \
      *    A --------------------> C  -----
      *     \                      *
      *      \                    |
      *      V                   |
      *      B ------------------
      */
    // States
    case class A(id: String) extends State
    case class B(id: String) extends State
    case class C(id: String) extends State

    // Inputs
    case class Iab(id: String) extends Input
    case class Iac(id: String) extends Input
    case class Ibc(id: String) extends Input

    // Transformations
    implicit val tAB = Transformation[A, Iab, B]( (a, e) => B(a.id + e.id))
    implicit val tAC = Transformation[A, Iac, C]( (a, e) => C(a.id + e.id))
    implicit val tBC = Transformation[B, Ibc, C]( (b, e) => C(b.id + e.id))
    implicit def tCC[E <: Input] = Transformation[C, E, C]( (c, e) => C(c.id + "i"))

    "Verify state transitions" in {
      Fsm(A("a")).transition(Iab("b")) shouldBe Fsm(B("ab"))
      Fsm(A("a")).transition(Iac("c")) shouldBe Fsm(C("ac"))
      Fsm(B("b")).transition(Ibc("c")) shouldBe Fsm(C("bc"))

      Fsm(C("c")).transition(Iab("")) shouldBe Fsm(C("ci"))
      Fsm(C("c")).transition(Iab("")).transition(Iac("")).transition(Ibc("")) shouldBe Fsm(C("ciii"))

      // This won't compile
      //Fsm(A("a")).transition(Ibc(""))
    }
  }
}

