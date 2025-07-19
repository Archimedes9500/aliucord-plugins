version = "1.0"
description = "Save your scrolling position in a channel by using a message an an anchor you can later jump to, evem after closing the app"

aliucord {
	changelog.set(
		"""
		1.0 hold Message->Copy ID to save, hold Message->Reply to jump to saved
		""".trimIndent()
	)
	excludeFromUpdaterJson.set(true)
}
