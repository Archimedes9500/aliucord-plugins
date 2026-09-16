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
	file("src/main/")
		.listFiles().first()
		.resolve("alt.archimedes5000.plugins/")
		.listFiles()
		?.filter{it.isDirectory}
		?.forEach{
			deps[it.name]?.invoke(this);
		}
	;
};

if(project.name == "Template"){
	android{
		sourceSets{
			getByName("main").java.srcDirs("src/main/all");
		};
	};
};
