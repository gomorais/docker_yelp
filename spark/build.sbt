name := "MyTest"

version := "1.0"

organization := "com.morais"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
"org.apache.spark" %% "spark-core" % "2.1.0",
"org.apache.spark" %% "spark-streaming" % "2.1.0",
"com.datastax.spark" % "spark-cassandra-connector_2.11" % "2.0.3"
)
