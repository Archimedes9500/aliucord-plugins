@file:Suppress("UnstableApiUsage")

import com.aliucord.gradle.AliucordExtension
import com.android.build.gradle.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

subprojects {
	val libs = rootProject.libs

	apply {
		plugin(libs.plugins.android.library.get().pluginId)
		plugin(libs.plugins.aliucord.plugin.get().pluginId)
		plugin(libs.plugins.kotlin.android.get().pluginId)
		plugin(libs.plugins.ktlint.get().pluginId)
	}

	configure<LibraryExtension> {
		namespace = "alt.archimedes5000.plugins"
		compileSdk = 36

		defaultConfig {
			minSdk = 21
		}

		buildFeatures {
			aidl = false
			buildConfig = true
			renderScript = false
			shaders = false
		}

		compileOptions {
			sourceCompatibility = JavaVersion.VERSION_21
			targetCompatibility = JavaVersion.VERSION_21
		}
	}

	configure<AliucordExtension> {
		author("Archimedes5000", 0L)
		github("https://github.com/Archimedes9500/aliucord-plugins")
	}

	configure<KtlintExtension> {
		version.set(libs.versions.ktlint.asProvider())
		coloredOutput.set(true)
		outputColorName.set("RED")
		ignoreFailures.set(true)
	}

	configure<KotlinAndroidExtension> {
		compilerOptions {
			jvmTarget = JvmTarget.JVM_21
			optIn.add("kotlin.RequiresOptIn")
			optIn.add("kotlin.ExperimentalStdlibApi")
			freeCompilerArgs.add("-nowarn")
			//freeCompilerArgs.add("-Xno-stdlib")
		}
	}

	val stdlibConfig = configurations.create("stdlibForStrip") {
		isCanBeResolved = true
		isCanBeConsumed = false
		isTransitive = false
	}
	val discordConfig = configurations.create("discordForScan") {
		isCanBeResolved = true
		isCanBeConsumed = false
		isTransitive = false
	}
	val stripStdlib = tasks.register<Zip>("stripStdlib") {
		val stdlibJar = stdlibConfig.resolve().single()
		val discordJar = discordConfig.resolve().single()

		val discordKotlinEntries = zipTree(discordJar)
			.matching { include("kotlin/**/*.class") }
			.files
			.map { file ->
				file.toPath().toString().replace(File.separatorChar, '/')
			}.toSet()

		from(zipTree(stdlibJar)) {
			exclude { details ->
				details.path in discordKotlinEntries
			}
		}
	
		archiveFileName.set("kotlin-stdlib-stripped.jar")
		destinationDirectory.set(layout.buildDirectory.dir("patched"))
	}

	@Suppress("unused")
	dependencies {
		val compileOnly by configurations
		val implementation by configurations	

		stdlibConfig(libs.kotlin.stdlib)
		discordConfig(libs.discord)

		compileOnly(libs.discord)
		compileOnly(libs.aliucord)
		compileOnly(files(stripStdlib.map { it.archiveFile }))
		//compileOnly(libs.kotlin.stdlib)
		compileOnly("org.jetbrains.kotlin:kotlin-reflect")
		compileOnly("com.aliucord:Aliuhook:1.1.4")
		implementation("com.github.gfx.util:weak-identity-hash-map:2.0.0"){
			exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
			exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
			exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
		}
		implementation("org.luckypray:dexkit:2.2.0"){
			exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
			exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
			exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
		}
	}
}
