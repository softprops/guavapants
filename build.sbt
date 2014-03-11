libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "16.0",
  "com.google.code.findbugs" % "jsr305" % "2.0.0", // dep on finbugs fixes this compile issue -- https://issues.scala-lang.org/browse/SI-7751
  "org.scalatest" %% "scalatest" % "1.9.1" % "test")
