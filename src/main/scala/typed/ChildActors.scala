package typed

object ChildActors extends App {
  import akka.typed.ScalaDSL.{ Static, ContextAware }
  import akka.typed.{ ActorSystem, Props, Behavior }

  case class Spawn(name: String)

  val behavior:Behavior[Spawn] = ContextAware[Spawn] { ctx =>
    Static[Spawn] {
      case Spawn(name: String) =>
        println("Spawning a child: "+name)
        ctx.spawn(Props(behavior), name)
    }
  }

  val system = ActorSystem("MySystem", Props(behavior))

  system ! Spawn("Child")

  system.terminate()
}
