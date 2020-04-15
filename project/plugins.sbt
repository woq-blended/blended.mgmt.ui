//noinspection Annotator,Annotator,Annotator
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.32")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.15.0-0.6")
// Scala source code formatter (also used by Scala-IDE/Eclipse)
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")
//noinspection SpellCheckingInspection
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.6")
addSbtPlugin("com.typesafe.sbt" % "sbt-osgi" % "0.9.5")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.5")

// Strip artifacts to make them more reproducible (same input -> same output, checksum wise)
addSbtPlugin("net.bzzt" % "sbt-reproducible-builds" % "0.16")

// Generate logging config for test execution
addSbtPlugin("de.wayofquality.sbt" % "sbt-testlogconfig" % "0.1.0")

// Generate site with JBake
addSbtPlugin("de.wayofquality.sbt" % "sbt-jbake" % "0.1.2")

// Filter resources (like Maven)
addSbtPlugin("de.wayofquality.sbt" % "sbt-filterresources" % "0.1.2")

addSbtPlugin("de.wayofquality.sbt" % "sbt-phoenix" % "0.1.0")

addSbtPlugin("de.wayofquality.blended" % "sbt-blendedbuild" % "0.1.2")