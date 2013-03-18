organization := "io.mth"

name := "unfiltered-cors"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.2"

crossScalaVersions := Seq("2.9.2", "2.10.0")

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered" % "0.6.7",
  "net.databinder" %% "unfiltered-filter" % "0.6.7" % "test",
  "net.databinder" %% "unfiltered-jetty" % "0.6.7" % "test",
  "io.argonaut" %% "argonaut" % "6.0-M3" % "test" cross(CrossVersion.full)
)

resolvers ++= Seq(
  "java m2" at "http://download.java.net/maven/2",
  "oss snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "oss releases" at "http://oss.sonatype.org/content/repositories/releases"
)

publishTo <<= version.apply(v => {
  val flavour = if (v.trim.endsWith("SNAPSHOT")) "snapshots" else "releases"
  Some(Resolver.sftp("repo.mth.io","repo.mth.io", "repo.mth.io/data/" + flavour) as ("web", new java.io.File(System.getProperty("user.home") + "/.ssh/id_dsa_publish")))
})
