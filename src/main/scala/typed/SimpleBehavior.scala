package typed


object SimpleBehavior extends App {

  import akka.typed.ScalaDSL.Static
  case class Message(value: String)

  val behavior = Static[Message] {
    case Message(value) => println(value)
  }

  import akka.typed.{Props, ActorSystem}

  val system = ActorSystem("MySystem", Props(behavior))

  system ! Message("Hello World")

  system.terminate()
}


