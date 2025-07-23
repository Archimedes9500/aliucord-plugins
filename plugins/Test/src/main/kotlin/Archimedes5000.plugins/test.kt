package Archimedes5000.plugins
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

import com.discord.api.sticker.Sticker
import com.discord.api.sticker.StickerFormatType
import com.aliucord.Logger

@AliucordPlugin(requiresRestart = true)
@SuppressLint("SetTextI18n")
class Test:Plugin(){
	override fun start(context:Context){
		var c = Sticker::class.java;
		for(method in c.declaredMethods){
			Logger().debug(method.name);
		};
	}
	override fun stop(context: Context) = patcher.unpatchAll();
}