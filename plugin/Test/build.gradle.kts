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

// Gather Android boot jars early (same as before)
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

// Task that fully resolves classpath and materializes any embedded classes.jar from AARs.
// This avoids doing resolution inside scalaCompileDebug's doFirst (reduces race conditions).
val prepareScalaClasspath = tasks.register("prepareScalaClasspath"){
	// Ensure any Java/Kotlin compile tasks that produce classes are finished before we capture outputs.
	// Match common task name patterns (project-level) so compiled classes are present.
	dependsOn(rootProject.tasks.matching{it.name.startsWith("assemble", ignoreCase = true)});
	dependsOn(tasks.matching{it.name.startsWith("assemble", ignoreCase = true)});
	// depend on language compile tasks if present
	dependsOn(tasks.matching{it.name.startsWith("compile") && (it.name.contains("Java") || it.name.contains("Kotlin") )});

	val preparedDir = layout.buildDirectory.dir("scala-prep").get().asFile;

	doLast{
		preparedDir.deleteRecursively();
		preparedDir.mkdirs();

		val extraJars = mutableListOf<File>();

		// resolve classpath configurations fully to avoid race conditions
		val cfgs = listOfNotNull(
			configurations.findByName("debugCompileClasspath"),
			configurations.findByName("debugRuntimeClasspath"),
			configurations.findByName("compileClasspath"),
			configurations.findByName("runtimeClasspath")
		);
		// force resolution outside of the scala exec task
		cfgs.forEach{try{it.resolve()}catch(_: Exception){/*ignore*/}};

		// extract jars/classes from resolved artifacts
		cfgs.forEach{cfg ->
			cfg.files.forEach{f ->
				if(f.name.endsWith(".aar")){
					val classesJar = zipTree(f).files.find{it.name == "classes.jar"};
					if(classesJar != null){
						val out = File(preparedDir, "${f.name}.classes.jar");
						out.parentFile.mkdirs();
						classesJar.copyTo(out, overwrite = true);
						extraJars += out;
					}
				}else if(f.name.endsWith(".apk")){
					// if a dex2jar'ed jar already exists in build, pick it up
					val dex2jarOut = File(layout.buildDirectory.dir("dex2jar").get().asFile, "${f.name}.jar");
					if(dex2jarOut.exists()) extraJars += dex2jarOut;
				}else if(f.name.endsWith(".jar")){
					extraJars += f;
				}
			}
		};

		// include any freshly compiled project outputs (Kotlin/Java) to ensure generated classes are visible
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

		// include explicit scala resolve outputs
		scalaCompileResolve.files.forEach{extraJars += it};
		scalaResolve.files.forEach{extraJars += it};

		// write a manifest file listing resolved classpath entries (used by scalaCompileDebug)
		val manifest = File(preparedDir, "classpath.txt");
		manifest.bufferedWriter().use{w ->
			val sep = System.getProperty("path.separator");
			extraJars.map{it.absolutePath}.forEach{
				w.write(it);
				w.write(sep);
			};
		};
	};
};

// Scala compile exec task now depends on prepareScalaClasspath and only reads the prepared manifest during execution.
val scalaCompileDebug = tasks.register("scalaCompileDebug", JavaExec::class.java){
	dependsOn(prepareScalaClasspath);
	// also wait for assemble tasks to reduce races with android plugin
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
		val preparedDir = layout.buildDirectory.dir("scala-prep").get().asFile;
		val manifest = File(preparedDir, "classpath.txt");
		if(!manifest.exists()){
			throw GradleException("scala classpath manifest not found; prepareScalaClasspath may have failed");
		}
		val content = manifest.readText();
		val pathSep = System.getProperty("path.separator");
		val entries = if(content.isBlank()) listOf<String>() else content.split(pathSep).filter{it.isNotBlank()}.map{File(it)};

		// ensure scala-library and compiler artifacts exist on classpath
		val extraJars = entries.toMutableList();

		// safety: include scalaResolve files additionally if any missing entries
		scalaResolve.files.forEach{
			if(!extraJars.contains(it)) extraJars += it;
		};

		classpath = files(extraJars);

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
