version = "1.1"
description = "Save your scrolling position in a channel by using a message an an anchor you can later jump to, evem after closing the app"

aliucord {
	changelog.set(
		"""
		# 1.1
		* Hold Message->Profile to save instead
		# 1.0
		* Hold Message->Copy ID to save, hold Message->Reply to jump to saved
		""".trimIndent()
	)
	excludeFromUpdaterJson.set(true)
}
