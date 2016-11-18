package io.scalawave.workshop.free

import io.scalawave.workshop.common.ActionType

import scalaz._

object ScalazProgram {

  type Program[A] = Coproduct[ScalazCommand, ScalazCalculation, A]
  val commands = new ScalazCommand.Ops[Program]
  val calculations = new ScalazCalculation.Ops[Program]

  val convert = for {
    from <- calculations.getCurrency("From what currency you want to convert")
    to <- calculations.getCurrency("Into what currency you want to convert")
    amount <- calculations.getAmount("How much do you want to convert")
    result <- calculations.convert(from, to, amount)
    _ <- calculations.displayValue(result)
  } yield ()

  def mainLoop: Free[Program, Unit] =
    commands.getNextAction("What do you want to do next") flatMap {
      case ActionType.Convert =>
        convert flatMap { _ => mainLoop }
      case ActionType.ChangeSettings =>
        commands.configure("Change configuration (amount, data source)") flatMap { _ => mainLoop }
      case ActionType.Quit =>
        commands.quit
    }

  val program = for {
    _ <- commands.configure("Set initial configuration (amount, data source)")
    _ <- mainLoop
  } yield ()
}