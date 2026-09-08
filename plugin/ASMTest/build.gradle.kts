version = "0.0"
description = "test"

aliucord{
	changelog.set(
		"""
		""".trimIndent()
	)
}

dependencies{
	implementation("org.ow2.asm:asm-util:9.7.1"){
		exclude(group = "org.jetbrains.kotlin")
	}
	implementation("com.android.tools:r8:9.4.14"){
		exclude(group = "org.jetbrains.kotlin")
	}
}