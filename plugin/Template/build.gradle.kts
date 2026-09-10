import org.gradle.kotlin.dsl.DependencyHandlerScope;
import org.gradle.kotlin.dsl.DependencyHandler;

version = "0.0";
description = "test";

aliucord{
	changelog.set(
		"""
		""".trimIndent()
	);
};

@Suppress("UNCHECKED_CAST")
val deps = rootProject.extra["deps"] as Map<String, /*DependencyHandlerScope.()*/(DependencyHandler) -> Unit>;
dependencies{
	println("AAAAAA\n${deps["dexkit"]!!::invoke}");
	deps["dexkit"]!!.invoke(this);
	file("src/main/java/alt.archimedes5000.plugins/")
		.listFiles()
		?.filter{it.isDirectory}
		?.forEach{deps[it.name]?.invoke(this)}
	;
};