package info.galudisu.iot

import scala.util.Random

package object modeling {
  def genId: Int = Random.nextInt(1000)
  def genMsg: String = Random.alphanumeric.take(4).mkString
}
