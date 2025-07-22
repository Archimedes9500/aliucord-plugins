version = "2.0.0"
description = "Posts a sticker as an image if the sticker is unavailable normally (Usually when you don't have Nitro). Lottie stickers are unsupported."

aliucord {
    changelogMedia.set("https://cdn.discordapp.com/stickers/883809297216192573.png")
    changelog.set(
        """
			# 2.0.0
			* rewrite in kotlin and removed auto message send
            # 1.1.1
            * Support Discord 105.12
            # 1.1.0
            * Make sticker picker automatically close after selecting a sticker
            * Do not mark stickers as unusable (monochrome filter)
        """.trimIndent()
    )
    author("", 0L)
}