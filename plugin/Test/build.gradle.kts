import org.gradle.api.tasks.scala.ScalaCompile;
import com.aliucord.gradle.task.CompileDexTask;

version = "0.0";
description = "test";

aliucord{
	changelog.set(
		"""
		""".trimIndent()
	);
};

val _impl = configurations.create("_impl");
val impl = configurations.getByName("implementation");
impl.dependencies?.forEach{d ->
	_impl.dependencies.add(d);
};
configurations.remove(impl);

val _compile = configurations.create("_compile");
val compile = configurations.getByName("compileOnly");
impl.dependencies?.forEach{d ->
	_compile.dependencies.add(d);
};
configurations.remove(compile);

plugins.apply("scala");
configurations.getByName("implementation").extendsFrom(_impl);
configurations.getByName("compileOnly").extendsFrom(_compile);

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