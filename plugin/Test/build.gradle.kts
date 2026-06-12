import org.gradle.api.tasks.JavaExec;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import com.aliucord.gradle.task.CompileDexTask;
import java.io.File;

version = "0.0";
description = "test";

aliucord{
	changelog.set(
		"""
		""".trimIndent()
	);
};

val scalaResolve = configurations.create("scalaResolve"){
	isCanBeResolved = true;
};

val cScalaClasspath = configurations.create("scalaClasspath");

val scalaCompileResolve = configurations.create("scalaCompileResolve"){
	isCanBeResolved = true;
};

dependencies{
	implementation("org.scala-lang:scala-library:2.11.12");
	cScalaClasspath("org.scala-lang:scala-compiler:2.11.12");
	cScalaClasspath("org.scala-lang:scala-library:2.11.12");
	scalaResolve("org.scala-lang:scala-compiler:2.11.12");
	scalaResolve("org.scala-lang:scala-library:2.11.12");
};

afterEvaluate{
	listOf(
		"debugCompileClasspath",
		"debugRuntimeClasspath",
		"compileClasspath",
		"runtimeClasspath",
		"compileOnly"
	).forEach{name ->
		if(configurations.findByName(name) != null){
			scalaCompileResolve.extendsFrom(configurations.getByName(name));
		};
	}
}

val scalaCompileDebug = tasks.register("scalaCompileDebug", JavaExec::class.java){
	mainClass.set("scala.tools.nsc.Main");
	classpath = files();
	javaLauncher.set(
		javaToolchains.launcherFor{
			languageVersion.set(JavaLanguageVersion.of(8));
		}
	);
	val srcFiles = fileTree("src/main/scala"){
		include("**/*.scala");
	}.files.map{it.absolutePath};
	doFirst{
		val extraJars = mutableListOf<File>();

		listOfNotNull(
			configurations.findByName("debugCompileClasspath"),
			configurations.findByName("debugRuntimeClasspath"),
			configurations.findByName("compileClasspath"),
			configurations.findByName("runtimeClasspath")
		).forEach {cfg ->
			cfg.files.forEach{f ->
				if(f.name.endsWith(".aar")){
					val classesJar = zipTree(f).files.find{it.name == "classes.jar"};
					if(classesJar != null){
						val out = layout.buildDirectory.file("scala-deps/${f.name}.classes.jar").get().asFile;
						out.parentFile.mkdirs();
						classesJar.copyTo(out, overwrite = true);
						extraJars += out;
					}
				}else if(f.name.endsWith(".apk")){
					val dex2jarOut = layout.buildDirectory.file("dex2jar/${f.name}.jar").get().asFile;
					if(dex2jarOut.exists()) extraJars += dex2jarOut;
				}else if(f.name.endsWith(".jar")){
					extraJars += f;
				};
			};
		};

		// include Android bootclasspath (android.jar) so android.* types like Context are available to the scala compiler
		val androidExt = extensions.findByType(com.android.build.gradle.LibraryExtension::class.java);
		if(androidExt != null){
			androidExt.bootClasspath.forEach {path -> extraJars += File(path)};
		};

		scalaCompileResolve.files.forEach{extraJars += it};
		scalaResolve.files.forEach{extraJars += it};
		classpath = files(extraJars);
		val scalaLibJar = classpath.files.find{
			it.name.startsWith("scala-library");
		}?: throw GradleException("scala-library not found on classpath");
		jvmArgs = listOf("-Xbootclasspath/a:${scalaLibJar.absolutePath}");
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
