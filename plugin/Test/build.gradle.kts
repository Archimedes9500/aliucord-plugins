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
	implementation("org.scala-lang:scala-library:2.12.17");
};

val scalaCompileDebug = tasks.register("scalaCompileDebug", ScalaCompile::class.java){
	source = fileTree("src/main/scala"){
		include("**/*.scala");
	};
	classpath = files(
		provider{
			configurations.getByName("debugCompileClasspath");
		}
	);
	destinationDirectory.set(
		layout.buildDirectory.dir("classes/scala/debug")
	);
	scalaCompileOptions.additionalParameters = listOf("-g:vars,lines,source");
};

val compileDex = tasks.named<CompileDexTask>("compileDex");
compileDex.configure {
	input.from(project.tasks.named("scalaCompileDebug"));
};
