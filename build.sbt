import ReleaseTransformations._
import sbtcrossproject.CrossPlugin.autoImport.crossProject

val Scala211 = "2.11.12"

scalaVersion in ThisBuild := Scala211

crossScalaVersions := Seq(Scala211, "2.10.7", "2.12.4", "2.13.0-M2")

organization in ThisBuild := "com.github.xenoby"

scalacOptions in ThisBuild ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) if v <= 11 => List("-target:jvm-1.7")
    case _ => Nil
  }
}

releaseCrossBuild := true

releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining(s";++${Scala211};lensesNative/publishSigned"),
  ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
  setNextVersion,
  commitNextVersion,
  pushChanges,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true)
)

lazy val root = project.in(file("."))
  .aggregate(lensesJS, lensesJVM)
  .settings(
    publish := {},
    publishLocal := {},
    publishArtifact := false
  )

lazy val lenses = crossProject(JSPlatform, JVMPlatform, NativePlatform).in(file("."))
  .settings(
    name := "lenses",
    libraryDependencies ++= Seq(
      // "com.lihaoyi" %%% "utest" % "0.6.3" % "test"
    ),
    resolvers += "Sonatype staging" at "https://oss.sonatype.org/content/repositories/staging",
    publishTo := Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"),
    credentials ++= {
      lazy val credentials = sys.props("credentials")
      val credentialsFile = if (credentials != null) new File(credentials) else null
      if (credentialsFile != null) List(new FileCredentials(credentialsFile))
      else Nil
    }
  )
  .jsSettings(
    scalacOptions += {
      val a = (baseDirectory in LocalRootProject).value.toURI.toString
      val g = "https://raw.githubusercontent.com/scalapb/Lenses/" + sys.process.Process("git rev-parse HEAD").lines_!.head
      s"-P:scalajs:mapSourceURI:$a->$g/"
    }
  )
  .nativeSettings(
    nativeLinkStubs := true // for utest
  )

testFrameworks in ThisBuild += new TestFramework("utest.runner.Framework")

lazy val lensesJVM = lenses.jvm
lazy val lensesJS = lenses.js
lazy val lensesNative = lenses.native
