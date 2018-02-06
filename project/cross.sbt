addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.21")
addSbtPlugin("org.portable-scala" % "sbt-crossproject"         % "0.3.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.3.0")
addSbtPlugin("com.github.xenoby"  % "sbt-scala-native"         % "0.3.6-20-g0afae98f36")
resolvers += "Sonatype staging" at "https://oss.sonatype.org/content/repositories/staging"
