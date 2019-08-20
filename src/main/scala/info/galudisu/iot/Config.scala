package info.galudisu.iot

import com.typesafe.config.{Config => AkkaConfig, ConfigFactory => AkkaConfigFactory}

trait Config {
  protected lazy val config: AkkaConfig = AkkaConfigFactory.load()

  val clusterName: String = config.getString("clustering.cluster.name")

}
