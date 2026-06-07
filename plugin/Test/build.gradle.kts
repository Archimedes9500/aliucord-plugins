import org.gradle.api.artifacts.Configuration;
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

val tempMap = mutableMapOf<String, Configuration?>(
	"implementation" to null,
	//"api" to null,
	"compileOnly" to null,
	"runtimeOnly" to null,
	"annotationProcessor" to null,
	//"compileClasspath" to null,
	//"runtimeClasspath" to null,
	//"compileOnlyApi" to null,
	"testImplementation" to null,
	"testApi" to null,
	"testCompileOnly" to null,
	"testRuntimeOnly" to null,
	"testAnnotationProcessor" to null,
	//"testCompileClasspath" to null,
	//"testRuntimeClasspath" to null,
	//"testCompileOnlyApi" to null,
);
tempMap.entries.forEach{(name, _) ->
	val backup = configurations.create("_$name");
	val current = configurations.getByName(name);
	current.dependencies?.forEach{d ->
		backup.dependencies.add(d);
	};
	configurations.remove(current);
	tempMap[name] = backup;
};
plugins.apply("scala");
tempMap.entries.forEach{(name, backup) ->
	configurations.getByName(name).extendsFrom(backup!!);
};

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