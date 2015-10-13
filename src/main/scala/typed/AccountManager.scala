package typed

object AccountManagerApp extends App {

  import akka.typed._
  import akka.typed.ScalaDSL._

  object AccountManager {
    case class AccountCommand(accountId: String, command: Account.Command)
    def manageAccounts(accounts: Map[String, ActorRef[Account.Command]]):Behavior[AccountCommand] = ContextAware[AccountCommand] { ctx =>
      Total[AccountCommand] {
        case AccountCommand(accountId, command) =>
          val account = accounts.getOrElse(accountId, ctx.spawn(Props(Account.zeroBalance(accountId)), accountId))
          account ! command
          manageAccounts(accounts.updated(accountId, account))
      }
    }
  }

  object Account {
    trait Command
    case class Deposit(amount: Int) extends Command {
      require(amount > 0)
    }
    case class Withdraw(amount: Int) extends Command {
      require(amount > 0)
    }

    def positiveBalance(accountId: String, balance: Int): Behavior[Command] = Total[Command] {
      case Deposit(amount) =>
        println(s"[$accountId] Your balance is now: $balance")
        positiveBalance(accountId, balance + amount)
      case Withdraw(amount) if amount < balance =>
        println(s"[$accountId] Your balance is now: $balance")
        positiveBalance(accountId, balance - amount)
      case Withdraw(amount) if amount == balance =>
        println(s"[$accountId] Your account is now empty.")
        zeroBalance(accountId)
      case Withdraw(amount) if amount > balance =>
        println(s"[$accountId] Insufficient Funds.  Balance: $balance")
        Same
    }

    def zeroBalance(accountId: String) = Total[Command] {
      case Deposit(amount) =>
        println(s"[$accountId] Your balance is now: $amount")
        positiveBalance(accountId, amount)
      case _: Withdraw =>
        println(s"[$accountId] Insufficient Funds.  Balance is zero.")
        Same
    }
  }

  object Root {
    val run = Full[Unit] {
      case Sig(ctx, PreStart) =>
        val accountManager = ctx.spawn(Props(AccountManager.manageAccounts(Map.empty)), "AccountManager")
        accountManager ! AccountManager.AccountCommand("Account1", Account.Deposit(10))
        accountManager ! AccountManager.AccountCommand("Account2", Account.Deposit(5))
        accountManager ! AccountManager.AccountCommand("Account1", Account.Withdraw(10))
        accountManager ! AccountManager.AccountCommand("Account2", Account.Deposit(5))
        accountManager ! AccountManager.AccountCommand("Account1", Account.Withdraw(5))
        Stopped
    }
  }

  val system = ActorSystem("Root", Props(Root.run))
}

trait Metrics {
  def tic(name: String): Unit
  def timed[T](name: String)(f: => T): T
  def measured(name: String, value: Int): Unit
}

class MyClass extends Metrics {
  tic("MyTic")
  val x = timed("MyTimed") {
    // Some Logic
  }
  measured("MyMeasured", 1000)
}
