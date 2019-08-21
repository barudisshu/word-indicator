package info.galudisu.iot

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Balance, Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import info.galudisu.iot.modeling.{AndroidMsg, GenericMsg, IosMsg}
import info.galudisu.iot.stages.{CsvPersistFlow, StatefulCounterFlow}

import scala.concurrent.duration._
import scala.language.postfixOps

class ServerPrime extends Config with LazyLogging {
  implicit val system: ActorSystem    = ActorSystem(clusterName)
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val ORIGIN_ANDROID = "android"
  val ORIGIN_IOS     = "ios"

  val graph: RunnableGraph[NotUsed] = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    //Sources
    val androidNotification = Source.tick(2 seconds, 500 millis, new AndroidMsg)
    val iOSNotification     = Source.tick(700 millis, 600 millis, new IosMsg)

    //Flow
    val groupAndroid = Flow[AndroidMsg].map(_.toGenMsg(ORIGIN_ANDROID)).groupedWithin(5, 2 seconds).async
    val groupIos     = Flow[IosMsg].map(_.toGenMsg(ORIGIN_IOS)).groupedWithin(5, 3 seconds).async

    def counter = Flow[Seq[GenericMsg]].via(new StatefulCounterFlow())

    def mapper = Flow[Seq[GenericMsg]].mapConcat(_.toList)

    def csv = Flow[GenericMsg].via(new CsvPersistFlow("src/main/resources/android.csv"))

    //Junctions
    val aBroadcast               = builder.add(Broadcast[Seq[GenericMsg]](2))
    val iBroadcast               = builder.add(Broadcast[Seq[GenericMsg]](2))
    val balancer                 = builder.add(Balance[Seq[GenericMsg]](2))
    val notificationMerge        = builder.add(Merge[Seq[GenericMsg]](2))
    val genericNotificationMerge = builder.add(Merge[GenericMsg](2))

    val notificationUnzip = builder.add(Broadcast[GenericMsg](2))

    def counterSink(s: String) = Sink.foreach[Int](x => logger.debug(s"==> $s: [$x]"))

    //Graph
    androidNotification ~> groupAndroid ~> aBroadcast ~> counter ~> counterSink(ORIGIN_ANDROID)
    aBroadcast ~> notificationMerge
    iBroadcast ~> notificationMerge
    iOSNotification ~> groupIos ~> iBroadcast ~> counter ~> counterSink(ORIGIN_IOS)

    notificationMerge ~> balancer ~> mapper.async ~> genericNotificationMerge
    balancer ~> mapper.async ~> genericNotificationMerge

    genericNotificationMerge ~> notificationUnzip.in

    notificationUnzip ~> csv ~> Sink.ignore
    notificationUnzip ~> Sink.foreach[GenericMsg](x => logger.debug(s"==> ${x.toString}"))

    ClosedShape
  })

  graph.run()
}
