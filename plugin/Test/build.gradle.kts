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

val scalaCompiler = configurations.create("scalaCompiler");
val zinc = configurations.create("zinc");
val scalaCompilerPlugins = configurations.create("scalaCompilerPlugins");
dependencies{
	implementation("org.scala-lang:scala-library:2.11.12");
	scalaCompiler("org.scala-lang:scala-compiler:2.11.12");
	zinc("com.typesafe.zinc:zinc:1.5.0");
};

val scalaCompileDebug = tasks.register("scalaCompileDebug", ScalaCompile::class.java){
	source = fileTree("src/main/java"){
		include("**/*.scala");
	};
	classpath = files(
		provider{
			configurations.getByName("debugCompileClasspath");
		}
	);
	scalaClasspath = files(
		provider{
			scalaCompiler;
		}
	);
	zinc = files(
		provider{
			zinc;
		}
	);
	scalaCompilerPlugins = files(
		provider{
			scalaCompilerPlugins;
		}
	);
	destinationDirectory.set(
		layout.buildDirectory.dir("classes/scala/debug")
	);
	scalaCompileOptions.keepAliveMode.set(scalaCompileOptions.KeepAliveMode.DISABLED);
	scalaCompileOptions.additionalParameters = listOf("-g:vars,lines,source");
};

val compileDex = tasks.named<CompileDexTask>("compileDex");
compileDex.configure {
	input.from(project.tasks.named("scalaCompileDebug"));
};

