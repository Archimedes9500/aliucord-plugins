package Archimedes5000.plugins
import android.content.Context
import com.aliucord.annotations.*
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.aliucord.Utils
import com.aliucord.utils.ReflectUtils
import com.discord.widgets.chat.input.WidgetChatInputAttachments
import com.discord.widgets.chat.input.sticker.*
import com.discord.utilities.stickers.StickerUtils
import com.aliucord.utils.RxUtils
import java.util.Collections
import com.discord.stores.StoreStream

@AliucordPlugin(requiresRestart = true)
@SuppressWarnings("unused")
@SuppressLint("SetTextI18n")
class FakeStickers : Plugin(){
	override fun start(context: Context){
		// Do not mark stickers as unsendable (grey overlay)
		with(StickerItem::class.java){
			patcher.patch(
				getDeclaredMethod(
					"getSendability"
				),
				Hook{
					callFrame ->
					InsteadHook.returnConstant(
						StickerUtils.StickerSendability.SENDABLE
					);
				}
			);
		};
		//Patch onClick to send sticker
		with(WidgetStickerPicker::class.java){
			patcher.patch(
				getDeclaredMethod(
					"onStickerItemSelected",
					StickerItem::class.java
				),
				PreHook{
					callFrame ->
					try{
						// getSendability is patched above to always return SENDABLE so get the real value via reflect
						if(
							ReflectUtils.getField(
								callFrame.args[0],
								"sendability"
							)
							=! StickerUtils.StickerSendability.SENDABLE
						){
							var sticker =
								(
									callFrame.args[0]
									as StickerItem
								)
								.getSticker()
							;
							var link =
								"https://media.discordapp.net/stickers/"
								+sticker.d()
								+sticker.b()
								+"?size=160"
							;
							// Skip original method
							param.setResult(null);
							// Dismiss sticker picker
							val dismisser = WidgetChatInputAttachments::class.java
								.getDeclaredMethod("dismiss")
							; //because cannot access shit again
							dismisser.invoke(
								(
									callFrame.thisObject
									as WidgetChatInputAttachments
								)
							);
						}
					}catch(e: Exception){ //yes generic maybe works idk
						e.printStackTrace();
					}
				}
			)
		}
	}
	override fun stop(context: Context) = patcher.unpatchAll();
}

/*
This plugin was mainly written as a learning exercise in order to
figure out how to send links on tap since it would be required to
send custom stickers. Whether or not custom stickers will ever
happen remains to be seen...


Copyright (C) Rhythm Lunatic 2021

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
