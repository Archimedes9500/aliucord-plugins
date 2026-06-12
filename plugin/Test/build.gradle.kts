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
		}
	}
}

val androidBootClasspathPaths = run{
	val out = mutableListOf<String>();
	val androidExt = extensions.findByType(com.android.build.gradle.LibraryExtension::class.java);
	if(androidExt != null){
		androidExt.bootClasspath.forEach{p -> out += p.toString()};
	};
	if(out.isEmpty()){
		// fallback to ANDROID_SDK_ROOT/platforms/android-36/android.jar if the Android extension isn't available yet
		val sdkRoot = System.getenv("ANDROID_SDK_ROOT") ?: System.getenv("ANDROID_HOME");
		if(!sdkRoot.isNullOrBlank()){
			val fallback = File(sdkRoot, "platforms/android-36/android.jar");
			if(fallback.exists()) out += fallback.absolutePath;
		}
	}
	out.toList();
};

val scalaCompileDebug = tasks.register("scalaCompileDebug", JavaExec::class.java){
	// Wait for any assemble* task in root or this project to finish to avoid race conditions.
	dependsOn(rootProject.tasks.matching{it.name.startsWith("assemble", ignoreCase = true)});
	dependsOn(tasks.matching{it.name.startsWith("assemble", ignoreCase = true)});

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

		// resolve classpath configurations fully to avoid race conditions
		val cfgs = listOfNotNull(
			configurations.findByName("debugCompileClasspath"),
			configurations.findByName("debugRuntimeClasspath"),
			configurations.findByName("compileClasspath"),
			configurations.findByName("runtimeClasspath")
		);
		cfgs.forEach{try{it.resolve()}catch(_: Exception){/*ignore*/}};

		// extract jars/classes from resolved artifacts
		cfgs.forEach{cfg ->
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
				}
			}
		};

		// include any freshly compiled project outputs (Kotlin/Java) to ensure generated classes (utils, Patcher APIs, discord models) are visible
		val javaClassesDir = layout.buildDirectory.dir("classes/java/debug").get().asFile;
		if(javaClassesDir.exists()){
			extraJars += javaClassesDir;
		}
		val kotlinClassesDir = layout.buildDirectory.dir("tmp/kotlin-classes/debug").get().asFile;
		if(kotlinClassesDir.exists()){
			extraJars += kotlinClassesDir;
		}

		// also include any aliucord/discord/aliuhook artifacts explicitly if present in build outputs (AAR/JAR)
		val buildDepDir = file("build/outputs")
		if(buildDepDir.exists()){
			buildDepDir.walkTopDown().forEach{f ->
				if(f.isFile && (f.name.endsWith(".jar") || f.name.endsWith(".classes.jar") || f.name.endsWith(".aar"))) extraJars += f;
			}
		}

		androidBootClasspathPaths.forEach{p -> extraJars += File(p)};

		scalaCompileResolve.files.forEach{extraJars += it};
		scalaResolve.files.forEach{extraJars += it};
		classpath = files(extraJars);

		val pathSep = System.getProperty("path.separator");
		val cpString = classpath.files.joinToString(pathSep){it.absolutePath};

		val scalaLibJar = classpath.files.find{
			it.name.startsWith("scala-library");
		} ?: throw GradleException("scala-library not found on classpath");

		// add android jars and scala-library to the JVM bootclasspath for the compiler as well
		val bootList = mutableListOf<String>();
		bootList += scalaLibJar.absolutePath;
		androidBootClasspathPaths.forEach{bootList += it};
		jvmArgs = listOf("-Xbootclasspath/a:${bootList.joinToString(pathSep)}");

		layout.buildDirectory.dir("classes/scala/debug").get().asFile.deleteRecursively();
		layout.buildDirectory.dir("classes/scala/debug").get().asFile.mkdirs();

		if(srcFiles.isEmpty()){
			args = listOf("-version");
		}else{
			args = listOf(
				"-verbose",
				"-g:vars",
				"-d",
				layout.buildDirectory.dir("classes/scala/debug").get().asFile.absolutePath,
				"-classpath",
				cpString
			)+srcFiles;
		}
	};
	standardOutput = System.out;
	errorOutput = System.err;
	outputs.dir(layout.buildDirectory.dir("classes/scala/debug"));
};

val compileDex = tasks.named<CompileDexTask>("compileDex");
compileDex.configure{
	dependsOn(scalaCompileDebug);
	input.from(layout.buildDirectory.dir("classes/scala/debug"));
};
