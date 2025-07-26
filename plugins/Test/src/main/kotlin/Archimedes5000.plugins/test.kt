package Archimedes5000.plugins/*
import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.aliucord.Utils
import com.aliucord.utils.ReflectUtils
import com.discord.widgets.chat.input.WidgetChatInputAttachments
import com.discord.widgets.chat.input.sticker.*
import com.discord.utilities.stickers.StickerUtils
import com.aliucord.utils.RxUtils
import java.util.Collections
import com.discord.widgets.chat.input.AppFlexInputViewModel

import com.aliucord.wrappers.stickers.StickerWrapper
import com.discord.api.sticker.Sticker
import com.discord.api.sticker.StickerType
import com.discord.api.sticker.StickerFormatType
import com.aliucord.Logger

@AliucordPlugin(requiresRestart = true)
@SuppressLint("SetTextI18n")
class Test:Plugin(){
	override fun start(context:Context){
		val logger = Logger();
		val c = StickerWrapper::class.java;
		val sticker = StickerWrapper(Sticker(
			927304938470662144,
			1234567890,
			398274632408629250,
			"name",
			"description",
			StickerFormatType.APNG,
			"tags",
			StickerType.GUILD,
			true,
			128
		));
		for(method in c.declaredMethods){
		//for(name in arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l")){
			//logger.debug(method.name);
			logger.debug(method.name);
			/*logger.debug(
				c.getDeclaredMethod(name)
				?.invoke(sticker)
				.toString()
			);*/
		};
	}
	override fun stop(context:Context) = patcher.unpatchAll();
}*/