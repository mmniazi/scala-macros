import sbt._
import Keys._

object Build extends Build {

  /**
   * A dummy aggregator project.
   */
  lazy val root: Project = project.in(file(".")).aggregate(generator, sample)

  val paradiseDependency: ModuleID = "org.scalamacros" % "paradise" % "2.0.0" cross CrossVersion.full

  lazy val generator: Project = project.in(file("generator"))
                                .settings(
      libraryDependencies ++=Seq(
        // Macro generator dependencies
        "org.scala-lang" % "scala-reflect" % "2.10.4",
        paradiseDependency
      ),
      addCompilerPlugin(paradiseDependency)
    )

  lazy val sample: Project = project.in(file("sample"))
                             .dependsOn(generator)
                             .settings(
      addCompilerPlugin(paradiseDependency)
    )

}
