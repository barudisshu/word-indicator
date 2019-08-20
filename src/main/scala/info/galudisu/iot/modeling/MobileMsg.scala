package info.galudisu.iot.modeling

trait MobileMsg {
  def id: Int
  def msg: String
  def toGenMsg(origin: String) = GenericMsg(id, msg, origin)
}
case class AndroidMsg(id: Int = genId, msg: String = genMsg) extends MobileMsg
case class IosMsg(id: Int = genId, msg: String = genMsg)     extends MobileMsg

case class GenericMsg(id: Int, msg: String, origin: String)

object GenericMsg {
  implicit class ToRaw(gm: GenericMsg) {
    def toAndroidMsg: Option[AndroidMsg] =
      if (gm.origin.equalsIgnoreCase("android"))
        Some(AndroidMsg(gm.id, gm.msg))
      else None
    def toIosMsg: Option[IosMsg] =
      if (gm.origin.equalsIgnoreCase("ios"))
        Some(IosMsg(gm.id, gm.msg))
      else None
  }
}
