package alt.archimedes5000.plugins

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.stores.StoreMessages;
import com.discord.stores.StoreMessagesLoader.ChannelChunk;
import com.discord.models.message.Message;
import com.discord.widgets.chat.input.ChatInputViewModel;
import com.discord.widgets.chat.MessageManager;
import com.discord.models.user.User;
import com.discord.widgets.chat.MessageContent;
import kotlin.jvm.functions.Function1;

@AliucordPlugin(requiresRestart = true)
class BetterReplaceText: Plugin(){
	val range1 = 0x000000..0x0018FF;
	val PUA = 0x00E000..0x00F8FF;

	val range2 = 0x010000..0x01FFFF;
	val SPUAA = 0x0F0000..0x0FFFFF;
	
	val range3 = 0x020000..0x02FFFF;
	val SPUAB = 0x100000..0x10FFFF;

	var Message.contentField: String by accessFinalField();

	fun decode(s: String): String{
		val output = StringBuilder(2000);
		s.codePoints().forEachOrdered{
			output.appendCodePoint(
				when{
					PUA.contains(it) -> range1.first+(it-PUA.first);
					SPUAA.contains(it) -> range2.first+(it-SPUAA.first);
					SPUAB.contains(it) -> range3.first+(it-SPUAB.first);
					else -> it;
				}
			);
		};
		return output.toString();
	};
	fun encode(s: String): String{
		val output = StringBuilder(2000);
		s.codePoints().forEachOrdered{
			output.appendCodePoint(
				when{
					range1.contains(it) -> PUA.first+(it-range1.first);
					range2.contains(it) -> SPUAA.first+(it-range2.first);
					range3.contains(it) -> SPUAB.first+(it-range3.first);
					else -> it;
				}
			);
		};
		return output.toString();
	};

	override fun start(pluginContext: Context){
		patcher.before<StoreMessages>(
			"handleMessagesLoaded",
			ChannelChunk::class.java
		){frame ->
			val chunk = frame.args[0] as ChannelChunk;
			for(m in chunk.messages){
				m.contentField = decode(m.contentFiled);
			};
		};
		patcher.before<ChatInputViewModel>(
			"sendMessage",
			Context::class.java,
			MessageManager::class.java,
			MessageContent::class.java,
			List::class.java,
			Boolean::class.java,
			Function1::class.java
		){frame ->
			val (text: String, users: List<*>) = (frame.args[2] as MessageContent);
			frame.args[2] = MessageContent(encode(text), users);
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};
/*
class UString(val value: String){
	constructor(chars: List<String>):
		this(chars.joinToString(""))
	;

	val chars = value.codePoints()
		.mapToObj{String(Character.toChars(it))}
		.toArray{Array(it, {""})}
	;
	operator fun get(i: Int) = chars[i];
	val indices = chars.indices;
	val lastIndex = chars.lastIndex;
	val length = chars.size;
	val iterator = chars::iterator;
	val all = chars::all;

	fun substring(start: Int, end: Int = length): UString{
		return UString(chars.slice(start..end));
	};
	fun substring(range: IntRange): UString{
		return UString(chars.slice(range));
	};
};
fun Any.toUString() = UString(this.toString());
fun String.toUString() = UString(this);
*/
