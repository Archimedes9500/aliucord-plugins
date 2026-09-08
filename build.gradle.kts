import org.gradle.kotlin.dsl.DependencyHandlerScope;

plugins{
	alias(libs.plugins.kotlin.android) apply false;
	alias(libs.plugins.android.library) apply false;
	alias(libs.plugins.aliucord.plugin) apply true;
	alias(libs.plugins.ktlint) apply false;
};

//deps
val DependencyHandlerScope.dexkit get() = add(
	"implementation",
	dependencies.create("org.luckypray:dexkit:2.2.0"){
		exclude(group = "org.jetbrains.kotlin")
	}
);
