import org.gradle.api.tasks.scala.ScalaCompile;
import com.aliucord.gradle.task.CompileDexTask;
import org.gradle.api.file.ConfigurableFileCollection;

version = "0.0";
description = "test";

aliucord{
	changelog.set(
		"""
		""".trimIndent()
	);
};

repositories{
	mavenCentral();
	google();
};

val cScalaClasspath = configurations.create("scalaClasspath");
val cZincClasspath = configurations.create("zincClasspath");
val cScalaCompilerPlugins = configurations.create("scalaCompilerPlugins");
dependencies{
	implementation("org.scala-lang:scala-library:2.11.12");
	cScalaClasspath("org.scala-lang:scala-compiler:2.11.12");
	cZincClasspath("org.scala-sbt:zinc-compile-core_2.11:1.4.4");
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
			configurations.getByName("scalaClasspath").resolve();
		}
	);
	zincClasspath = objects.fileCollection();
	scalaCompilerPlugins = objects.fileCollection();
	scalaCompileOptions.keepAliveMode.set(
		org.gradle.language.scala.tasks.KeepAliveMode.SESSION
	);
	scalaCompileOptions.incrementalOptions{
		analysisFile.set(
			layout.buildDirectory.file("scala/incremental/debug.analysis")
		);
		classfileBackupDir.set(
			layout.buildDirectory.file("scala/classfile-backup/debug.backup")
		);
	};
	scalaCompileOptions.additionalParameters = listOf("-g:vars,lines,source");
	
	destinationDirectory.set(
		layout.buildDirectory.dir("classes/scala/debug")
	);
	outputs.dir(destinationDirectory);
};

val compileDex = tasks.named<CompileDexTask>("compileDex");
compileDex.configure {
	input.from(project.tasks.named("scalaCompileDebug"));
};
