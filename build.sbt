ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.0"

lazy val root = (project in file("."))
  .settings(
    name := "scalagraphics-demo-opengl",
    organization := "com.github.jarlah.scalagraphics",
    idePackagePrefix := Some("com.github.jarlah.scalagraphics"),
    libraryDependencies ++= {
      val version = "3.3.1"
      val os = "linux" // TODO: Change to "linux" or "macos" if necessary
      Seq(
        "lwjgl",
        "lwjgl-glfw",
        "lwjgl-opengl",
        "lwjgl-nanovg"
        // TODO: Add more modules here
      ).flatMap { module =>
        {
          Seq(
            "org.lwjgl" % module % version,
            "org.lwjgl" % module % version % "runtime" classifier s"natives-$os"
          )
        }
      }
    },
    resolvers += "GitHub Package Registry" at "https://maven.pkg.github.com/jarlah/scalagraphics",
    libraryDependencies += "com.github.jarlah.scalagraphics" % "scalagraphics_3" % "0.3.2-SNAPSHOT",
    libraryDependencies += "org.joml" % "joml" % "1.10.5"
  )
