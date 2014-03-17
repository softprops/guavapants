organization := "me.lessis"

name := "guavapants"

version := "0.1.0-SNAPSHOT"

description := "Seamless transformations between Guava and Scala types"

licenses := Seq(
  "MIT" ->
  url("https://github.com/softprops/%s/blob/%s/LICENSE" format(name.value, version.value)))

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "16.0",
  "com.google.code.findbugs" % "jsr305" % "2.0.0", // dep on finbugs fixes this compile issue -- https://issues.scala-lang.org/browse/SI-7751
  "org.scalatest" %% "scalatest" % "1.9.1" % "test")

lsSettings

LsKeys.tags in LsKeys.lsync := Seq("guava")

bintraySettings

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("guava")
