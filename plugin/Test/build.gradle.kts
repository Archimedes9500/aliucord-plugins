import org.gradle.api.tasks.scala.ScalaCompile

version = "0.0";
description = "test";

aliucord{
	changelog.set(
		"""
		""".trimIndent()
	);
};

val temp = configurations.create("temp");
val implementation = configurations.findByName("implementation");
implementation.dependencies?.forEach{d ->
	temp.dependencies.add(d);
};
configurations.remove(implementation);
configurations.getByName("implementation").extendsFrom(temp);

plugins.apply("scala");

dependencies{
	imolementation("org.scala-lang:scala-library:2.13.12");
};

tasks.withType<ScalaCompile>().configureEach{
	scalaCompileOptions.additionalParameters = listOf("-g:vars,lines,source");
};

val compileDex = tasks.named<CompileDexTask>("compileDex");
compileDex.configure {
	input.from(project.tasks.named("scalaCompile"));
};