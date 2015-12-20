sbtPlugin := true

name := "sbt-native-packager-core"

libraryDependencies ++= Seq(
    "org.apache.commons" % "commons-compress" % "1.4.1",
    "com.spotify" % "docker-client" % "3.2.1",
    "org.vafer" % "jdeb" % "1.3" artifacts (Artifact("jdeb", "jar", "jar")),
    "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

// Release configuration
releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseProcess := Seq.empty[ReleaseStep]

// configure github page
site.settings

com.typesafe.sbt.SbtSite.SiteKeys.siteMappings <+= (baseDirectory) map { dir => 
  val nojekyll = dir / "src" / "site" / ".nojekyll"
  nojekyll -> ".nojekyll"
}
site.sphinxSupport()
site.includeScaladoc()
ghpages.settings
git.remoteRepo := "git@github.com:sbt/sbt-native-packager.git"

// bintray config
bintrayOrganization := Some("sbt")
bintrayRepository := "sbt-plugin-releases"

