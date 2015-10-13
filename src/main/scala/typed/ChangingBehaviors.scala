package typed



object ChangingBehaviors extends App {
  import scala.concurrent.Await
  import scala.concurrent.duration._

  import akka.typed._
  import akka.typed.ScalaDSL._

  sealed trait Command
  case class Deposit(amount: Int) extends Command {
    require(amount > 0)
  }
  case class Withdraw(amount: Int) extends Command {
    require(amount > 0)
  }

  def positiveBalance(balance: Int): Behavior[Command] = Total[Command] {
    case Deposit(amount) =>
      println(s"Your balance is now: $balance")
      positiveBalance(balance + amount)
    case Withdraw(amount) if amount < balance =>
      println(s"Your balance is now: $balance")
      positiveBalance(balance - amount)
    case Withdraw(amount) if amount == balance =>
      println("Your account is now empty.")
      zeroBalance
    case Withdraw(amount) if amount > balance =>
      println(s"Insufficient Funds.  Balance: $balance")
      Same
  }

  val zeroBalance = Total[Command] {
    case Deposit(amount) =>
      println(s"Your balance is now: $amount")
      positiveBalance(amount)
    case _: Withdraw =>
      println(s"Insufficient Funds.  Balance is zero.")
      Same
  }

  val account = ActorSystem("AccountBalance", Props(zeroBalance))

  account ! Withdraw(10)
  account ! Deposit(5)
  account ! Deposit(5)
  account ! Withdraw(5)
  account ! Withdraw(6)
  account ! Withdraw(5)
  account ! Withdraw(1)

  Thread.sleep(1000)

  Await.result(account.terminate(), 1.second)
}
