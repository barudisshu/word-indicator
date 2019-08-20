package info.galudisu.iot.stages

import java.io.{BufferedWriter, FileWriter}

import akka.stream._
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import com.opencsv.CSVWriter
import info.galudisu.iot.modeling.AndroidMsg

import scala.collection.JavaConverters._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Try}

class CsvPersistFlow(fileName: String) extends GraphStage[FlowShape[Seq[AndroidMsg], Future[IOResult]]] {

  val in: Inlet[Seq[AndroidMsg]] = Inlet("IncomingAndroidMsg")
  val out: Outlet[Future[IOResult]] = Outlet("OutgoingCSV")

  override def shape: FlowShape[Seq[AndroidMsg], Future[IOResult]] = FlowShape(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
    new GraphStageLogic(shape) {
      val promise: Promise[IOResult] = Promise[IOResult]()

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val elem = grab(in)
          val bys: List[Array[String]] =
            elem.map{obj =>
              Array(obj.id.toString, obj.msg)
            }.toList

          val tryFut: Try[IOResult] =
            Try(new CSVWriter(new BufferedWriter(new FileWriter(fileName)))).flatMap((csvWriter: CSVWriter) =>
            Try {
              csvWriter.writeAll(bys.asJava)
              csvWriter.close()
            } match {
              case f@Failure(_) =>
                Try(csvWriter.close()).recoverWith {
                  case _ => f
                }
              case success =>
                success
            }
          )
          promise.complete(tryFut)
          push(out, promise.future)
        }
      })

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })
    }
  }
}
