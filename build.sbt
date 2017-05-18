import sbt.Keys._

lazy val `scala-fsm` = crossProject
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(
    scalaVersion := Settings.Versions.scala,
    libraryDependencies ++= Settings.sharedDependencies.value
  )

lazy val `scala-fsm-jvm` = `scala-fsm`.jvm
lazy val `scala-fsm-js` = `scala-fsm`.js
