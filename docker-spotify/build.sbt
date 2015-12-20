sbtPlugin := true

name := "sbt-native-packager-docker-spotify"

libraryDependencies ++= Seq(
    "com.spotify" % "docker-client" % "3.2.1"
)

// Release configuration
releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseProcess := Seq.empty[ReleaseStep]
