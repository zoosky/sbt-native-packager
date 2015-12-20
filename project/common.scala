import sbt._
import sbt.Keys._
import sbt.ScriptedPlugin._
import scalariform.formatter.preferences._

object CommonSettings extends AutoPlugin {

  override def trigger = AllRequirements
  
  override def projectSettings: Seq[Setting[_]] = scriptedSettings ++ Seq(
    organization := "com.typesafe.sbt",
    scalaVersion in Global := "2.10.5",
    scalacOptions in Compile ++= Seq("-deprecation", "-target:jvm-1.7"),
    publishMavenStyle := false,
    scriptedLaunchOpts <+= version apply { v => "-Dproject.version="+v }
  )
}
