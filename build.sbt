organization := "me.lessis"

name := "guavapants"

version := "0.1.1"

description := "Seamless transformations between Guava and Scala types"

crossScalaVersions := Seq("2.10.4", "2.11.5")

scalaVersion := crossScalaVersions.value.head

licenses := Seq(
  "MIT" ->
  url("https://github.com/softprops/%s/blob/%s/LICENSE" format(name.value, version.value)))

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "16.0",
  "com.google.code.findbugs" % "jsr305" % "2.0.0", // dep on finbugs fixes this compile issue -- https://issues.scala-lang.org/browse/SI-7751
  "org.scalatest" %% "scalatest" % "2.2.1" % "test")

homepage := Some(url("https://github.com/softprops/%s/#readme".format(name.value)))

publishArtifact in Test := false

lsSettings

LsKeys.tags in LsKeys.lsync := Seq("guava")

bintraySettings

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("guava")

externalResolvers in LsKeys.lsync := (resolvers in bintray.Keys.bintray).value

pomExtra := (
  <scm>
    <url>git@github.com:softprops/guavapants.git</url>
    <connection>scm:git:git@github.com:softprops/guavapants.git</connection>
  </scm>
  <developers>
    <developer>
      <id>softprops</id>
      <name>Doug Tangren</name>
      <url>https://github.com/softprops</url>
    </developer>
  </developers>)
