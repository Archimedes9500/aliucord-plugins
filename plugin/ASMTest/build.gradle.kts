version = "0.0";
description = "test";

aliucord{
	changelog.set(
		"""
		""".trimIndent()
	);
};

dependencies{
	file("src/main/java/alt.archimedes5000.plugins/utils/")
		.listFiles()
		?.filter{it.isDirectory}
		?.forEach{Deps[it.name]?.invoke(this)}
	;
};