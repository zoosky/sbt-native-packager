import ReleaseTransformations._

lazy val nativePackager = project.in(file(".")).settings(
  sbtPlugin := true,
  name := "sbt-native-packager",
  bintrayOrganization := Some("sbt"),
  bintrayRepository := "sbt-plugin-releases",
  
  // Release configuration
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,

  releaseProcess := Seq[ReleaseStep](
    //checkSnapshotDependencies,
    inquireVersions,
    runTest,
    releaseStepInputTask(scripted, " universal/* debian/* rpm/* docker/* ash/* jar/* bash/* jdkpackager/*"),
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    pushChanges
  ),
  
  publishArtifact := false
  
).aggregate(core, dockerSpotify)

lazy val core = project.in(file("core"))

lazy val dockerSpotify = project.in(file("docker-spotify"))
