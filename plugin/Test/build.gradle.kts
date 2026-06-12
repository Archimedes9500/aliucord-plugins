import org.gradle.api.tasks.JavaExec;
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

val scalaResolve = configurations.create("scalaResolve") {
	isCanBeResolved = true;
};

val cScalaClasspath = configurations.create("scalaClasspath");
dependencies{
	implementation("org.scala-lang:scala-library:2.11.12");
	cScalaClasspath("org.scala-lang:scala-compiler:2.11.12");
	cScalaClasspath("org.scala-lang:scala-library:2.11.12");
	scalaResolve("org.scala-lang:scala-compiler:2.11.12");
	scalaResolve("org.scala-lang:scala-library:2.11.12");
};

val scalaCompileDebug = tasks.register("scalaCompileDebug", JavaExec::class.java){
	mainClass.set("scala.tools.nsc.Main");
	classpath = files(
		configurations.getByName("debugCompileClasspath"),
		configurations.getByName("debugRuntimeClasspath"),
		scalaResolve
	);
	val srcFiles = fileTree("src/main/scala"){
		include("**/*.scala");
	}.files.map{it.absolutePath};
	doFirst{
		println("RESOLVED COMPILER CLASSPATH:");
		classpath.files.forEach{println(it)};
		layout.buildDirectory.dir("classes/scala/debug").get().asFile.deleteRecursively();
		layout.buildDirectory.dir("classes/scala/debug").get().asFile.mkdirs();
	};
	standardOutput = System.out;
	errorOutput = System.err;
	if(srcFiles.isEmpty()){
		args = listOf("-version");
	}else{
		args = listOf(
			"-verbose",
			"-g:vars",
			"-d",
			layout.buildDirectory.dir("classes/scala/debug").get().asFile.absolutePath
		)+srcFiles;
	};
	outputs.dir(layout.buildDirectory.dir("classes/scala/debug"));
};

val compileDex = tasks.named<CompileDexTask>("compileDex");
compileDex.configure{
	dependsOn(scalaCompileDebug);
	input.from(layout.buildDirectory.dir("classes/scala/debug"));
};
