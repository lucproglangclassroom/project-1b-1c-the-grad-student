name := "topwords"

version := "0.1"

libraryDependencies ++= Seq(
  "com.lihaoyi"       %% "mainargs"        % "0.7.8",
  "org.slf4j"          % "slf4j-api"       % "2.0.9",
  "ch.qos.logback"     % "logback-classic" % "1.4.11",
  "com.github.sbt.junit" % "jupiter-interface" % "0.17.0" % Test,
  "org.scalatest"     %% "scalatest"       % "3.2.19"   % Test,
  "org.scalacheck"    %% "scalacheck"      % "1.19.0"   % Test,
  "org.scalatestplus" %% "scalacheck-1-18" % "3.2.19.0" % Test
)

enablePlugins(JavaAppPackaging)
