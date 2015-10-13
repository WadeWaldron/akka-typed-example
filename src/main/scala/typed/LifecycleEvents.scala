package typed

object LifecycleEvents extends App{

  import akka.typed._
  import akka.typed.ScalaDSL._

  val behavior = Full[Unit] {
    case Sig(ctx, PreStart) =>
      println("Actor Started")
      Same
  }

  val system = ActorSystem("MySystem", Props(behavior))

  system.terminate()
}
