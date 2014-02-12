import NativePackagerKeys._

name := "native-lib-test"

version := "0.1.0"

packageArchetype.java_application

val os = sys.props("os.name").toLowerCase

val isOSX = os.indexOf("mac") >= 0
  
val isWindows = os.indexOf("win") >= 0

val nativeLibForThisPlatform = {
  if(isOSX) "com.almworks.sqlite4java" % "libsqlite4java-osx" % "0.282" artifacts(Artifact("libsqlite4java-osx", "jnilib", "jnilib")) 
  else sys.props("sun.arch.data.model") match {
    case "32" =>  
       if(isWindows) "com.almworks.sqlite4java" % "libsqlite4java-win32-x86" % "0.282" artifacts(Artifact("libsqlite4java-win32-x86", "so", "so"))
       else "com.almworks.sqlite4java" % "libsqlite4java-linux-i386" % "0.282" artifacts(Artifact("libsqlite4java-linux-i386", "so", "so"))
    case "64" =>  
       if(isWindows) "com.almworks.sqlite4java" % "libsqlite4java-win32-x64" % "0.282" artifacts(Artifact("libsqlite4java-win32-x64", "so", "so"))
       else "com.almworks.sqlite4java" % "libsqlite4java-linux-amd64" % "0.282" artifacts(Artifact("libsqlite4java-linux-amd64", "so", "so"))
  }
}


libraryDependencies ++= Seq(
  "com.almworks.sqlite4java" % "sqlite4java" % "0.282",
  nativeLibForThisPlatform
)

classpathTypes ++= Set("jnilib", "dll", "so")

val checkNatives = taskKey[Unit]("checks native dlls/so existence in resulting package and their *names*")

checkNatives := {
   val log = streams.value.log
   println("Checking native files")
   for {
    file  <- (baseDirectory.value / "target/universal/stage").***.get
  }  println(file.getCanonicalPath)
}


TaskKey[Unit]("checkScript") <<= (stagingDirectory in Universal, name, streams) map { (dir, name, streams) =>
  // TODO - Check for windows and run the BASH instead.
  val script = dir / "bin" / name
  System.out.synchronized {
    System.err.println("---SCIRPT---")
    val scriptContents = IO.read(script)
    System.err.println(scriptContents)
    System.err.println("---END SCIRPT---")
    for(file <- (dir.***).get)
      System.err.println("\t"+file)
  }
  val cmd = "bash " + script.getAbsolutePath + " -d"
  val result =
    Process(cmd) ! streams.log match {
      case 0 => ()
      case n => sys.error("Failed to run script: " + script.getAbsolutePath + " error code: " + n)
    }
}
