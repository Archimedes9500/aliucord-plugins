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

val cScalaClasspath = configurations.create("scalaClasspath");
dependencies{
	implementation("org.scala-lang:scala-library:2.11.12");
	cScalaClasspath("org.scala-lang:scala-compiler:2.11.12");
	cScalaClasspath("org.scala-lang:scala-library:2.11.12");
};

val scalaCompileDebug = tasks.register("scalaCompileDebug", JavaExec::class.java){
	mainClass.set("scala.tools.nsc.Main");
	classpath = configurations.getByName("debugCompileClasspath")
		.plus(configurations.getByName("debugRuntimeClasspath"))
		.plus(cScalaClasspath)
	;
	println("CLASSPATH:\n${classpath}");
	val srcFiles = fileTree("src/main/scala"){
		include("**/*.scala");
	}.files.map{it.absolutePath};
	doFirst{
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
			"-g:vars",//"-g:vars,lines,source",
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