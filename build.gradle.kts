import org.gradle.kotlin.dsl.DependencyHandlerScope;

plugins{
	alias(libs.plugins.kotlin.android) apply false;
	alias(libs.plugins.android.library) apply false;
	alias(libs.plugins.aliucord.plugin) apply true;
	alias(libs.plugins.ktlint) apply false;
};

extra["deps"] = mapOf<String, DependencyHandlerScope.() -> Unit>(
	"dexkit" to {
		add(
			"implementation",
			dependencies.create("org.luckypray:dexkit:2.2.0"){
				exclude(group = "org.jetbrains.kotlin")
			}
		);
	},
	"synthetic" to {
		add(
			"implementation",
			dependencies.create("org.ow2.asm:asm-util:9.7.1"){
				exclude(group = "org.jetbrains.kotlin")
			}
		);
		add(
			"implementation",
			dependencies.create("com.android.tools:r8:9.4.14"){
				exclude(group = "org.jetbrains.kotlin")
			}
		);
	}
);