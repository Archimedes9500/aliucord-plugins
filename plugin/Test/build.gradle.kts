import org.gradle.kotlin.dsl.DependencyHandlerScope;

version = "0.0";
description = "test";

aliucord{
	changelog.set(
		"""
		""".trimIndent()
	);
};

@Suppress("UNCHECKED_CAST")
val deps = rootProject.extra["deps"] as Map<String, DependencyHandlerScope.() -> Unit>;
dependencies{
	file("src/main/java/alt.archimedes5000.plugins/")
		.listFiles()
		?.filter{it.isDirectory}
		?.forEach{
			deps[it.name]?.invoke(this);
		}
	;
};
