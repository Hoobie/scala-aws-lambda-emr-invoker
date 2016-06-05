javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

lazy val root = (project in file(".")).
  settings(
    name := "scala-aws-lambda-emr-invoker",
    version := "0.1",
    scalaVersion := "2.11.8",
    retrieveManaged := true,
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-core" % "1.0.0",
      "com.amazonaws" % "aws-lambda-java-events" % "1.0.0",
      "com.github.seratch" %% "awscala" % "0.5.+",
      "ch.qos.logback" %  "logback-classic" % "1.1.7",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
    ),
    dependencyOverrides ++= Set(
      "com.amazonaws" % "aws-java-sdk" % "1.10.33",
      "com.amazonaws" % "aws-java-sdk-core" % "1.10.33"
    )  )

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
