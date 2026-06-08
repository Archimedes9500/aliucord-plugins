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

dependencies{
	implementation("org.scala-lang:scala-library:2.13.12");
};

val scalaCompileDebug = tasks.register("scalaCompile", ScalaCompile::class.java){
	source = fileTree("src/main/scala"){
		include("**/*.scala");
	};
	classpath = files(
		provider{
			configurations.getByName("compileClasspath");
		}
	);
	destinationDirectory.set(
		layout.buildDirectory.dir("classes/scala/main")
	);
	scalaCompileOptions.additionalParameters = listOf("-g:vars,lines,source");
};

val compileDex = tasks.named<CompileDexTask>("compileDex");
compileDex.configure {
	input.from(project.tasks.named("scalaCompile"));
};