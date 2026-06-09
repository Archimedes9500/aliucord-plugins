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

val cScalaClasspath = configurations.create("scalaClasspath");
val cZincClasspath = null;//configurations.create("zincClasspath");
val cScalaCompilerPlugins = null;//configurations.create("scalaCompilerPlugins");
dependencies{
	implementation("org.scala-lang:scala-library:2.11.12");
	cScalaClasspath("org.scala-lang:scala-compiler:2.11.12");
	//cZincClasspath("org.scala-sbt:zinc-compile-core_2.11:1.4.4");
};

val scalaCompileDebug = tasks.register("scalaCompileDebug", ScalaCompile::class.java){
	source = fileTree("src/main/scala"){
		include("**/*.scala");
	};
	classpath = configurations.getByName("debugCompileClasspath").plus(cScalaClasspath);
	doFirst{
		println("scalaClasspath: " + cScalaClasspath.files);
	};
	scalaClasspath = configurations.getByName("scalaClasspath");
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
compileDex.configure{
	dependsOn(scalaCompileDebug);
	input.from(
		input.from(layout.buildDirectory.dir("classes/scala/debug"));
		//scalaCompileDebug.map{it.outputs.files}
	);
};
