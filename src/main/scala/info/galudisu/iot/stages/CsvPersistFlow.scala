package info.galudisu.iot.stages

import java.io.{BufferedWriter, FileWriter}

import akka.stream._
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import com.opencsv.CSVWriter
import com.typesafe.scalalogging.LazyLogging
import info.galudisu.iot.modeling.GenericMsg

import scala.collection.JavaConverters._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Try}

class CsvPersistFlow(fileName: String) extends GraphStage[FlowShape[GenericMsg, Future[IOResult]]] with LazyLogging {

  val in: Inlet[GenericMsg]         = Inlet("IncomingAndroidMsg")
  val out: Outlet[Future[IOResult]] = Outlet("OutgoingCSV")

  override def shape: FlowShape[GenericMsg, Future[IOResult]] = FlowShape(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
    new GraphStageLogic(shape) {
      val promise: Promise[IOResult] = Promise[IOResult]()

      setHandler(
        in,
        new InHandler {
          override def onPush(): Unit = {
            val elem                     = grab(in)
            val bys: List[Array[String]] = List(Array(elem.id.toString, elem.msg, elem.origin))
            val tryFut: Try[IOResult] =
              Try(new CSVWriter(new BufferedWriter(new FileWriter(fileName, true)))).flatMap((csvWriter: CSVWriter) =>
                Try {
                  csvWriter.writeAll(bys.asJava)
                  csvWriter.close()
                } match {
                  case f @ Failure(_) =>
                    logger.debug("error occur: {}", f.exception.getMessage)
                    Try {
                      csvWriter.close()
                    }.recoverWith {
                      case _ => f
                    }
                    Try(IOResult.createFailed(0, f.exception))
                  case _ => Try(IOResult.createSuccessful(bys.length))
              })
            promise.tryComplete(tryFut)
            push(out, promise.future)
          }
        }
      )

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })
    }
  }
}
