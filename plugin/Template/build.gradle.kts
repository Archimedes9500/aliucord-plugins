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
	println("AAAAAA\n${configurations.names}");
	deps["dexkit"]!!.invoke(this);
	println("BBBBBB\n${configurations.names}");
	file("src/main/java/alt.archimedes5000.plugins/")
		.listFiles()
		?.filter{it.isDirectory}
		?.forEach{
			println("adding ${it.name}");
			deps[it.name]?.invoke(this);
		}
	;
};