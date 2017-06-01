name := "play-scala-anorm-example"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.11"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += jdbc
libraryDependencies += evolutions
libraryDependencies += cache
libraryDependencies += ws
libraryDependencies +="mysql" % "mysql-connector-java" % "5.1.42"
libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3"
libraryDependencies += "com.typesafe.play" %% "anorm" % "2.5.1"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test

PlayKeys.devSettings := Seq("play.server.http.port" -> "8080")