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
val impl = configurations.findByName("implementation");
impl.dependencies?.forEach{d ->
	temp.dependencies.add(d);
};
configurations.remove(impl);
plugins.apply("scala");
configurations.getByName("implementation").extendsFrom(temp);

dependencies{
	implementation("org.scala-lang:scala-library:2.13.12");
};

tasks.withType<ScalaCompile>().configureEach{
	scalaCompileOptions.additionalParameters = listOf("-g:vars,lines,source");
};

val compileDex = tasks.named<CompileDexTask>("compileDex");
compileDex.configure {
	input.from(project.tasks.named("scalaCompile"));
};