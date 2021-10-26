package example

import cats.effect._
import org.http4s._
import org.http4s.implicits._
import cats.effect.unsafe.IORuntime
import java.util.concurrent._
import org.http4s.client._
import fs2.text.utf8.encode
import org.http4s.blaze.client.BlazeClientBuilder
import scala.concurrent.ExecutionContext.global

object Hello extends IOApp {

  val blockingPool = Executors.newFixedThreadPool(5)
  val httpClient: Client[IO] = JavaNetClientBuilder[IO].create

  val blazeClient = BlazeClientBuilder.apply[IO].stream
  val request = Request[IO](
    Method.GET,
    uri"http://localhost:5891/topics/TutorialTopic/records"
  )

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      client <- BlazeClientBuilder[IO](global).stream
      _ <- client
        .stream(request)
        .flatMap(_.bodyText)
        .foreach(v => IO(println(s"Chunk $v")))
    } yield ()).compile.drain
      .as(ExitCode.Success)

}
