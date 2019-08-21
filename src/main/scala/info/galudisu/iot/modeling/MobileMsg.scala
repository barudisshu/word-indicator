package info.galudisu.iot.modeling

import org.apache.commons.lang3.builder.{ToStringBuilder, ToStringStyle}

import scala.util.Random

trait MobileMsg {
  def id: Int                  = Random.nextInt(1000)
  def msg: String              = Random.alphanumeric.take(20).mkString
  def toGenMsg(origin: String) = GenericMsg(id, msg, origin)
}
class AndroidMsg extends MobileMsg
class IosMsg     extends MobileMsg
case class GenericMsg(id: Int, msg: String, origin: String) {
  override def toString: String = ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE)
}
