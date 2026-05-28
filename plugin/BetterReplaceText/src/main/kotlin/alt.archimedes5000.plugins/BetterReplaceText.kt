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


var Message.contentField: String by accessFinalField();

@AliucordPlugin(requiresRestart = true)
class BetterReplaceText: Plugin(){

	val range1 = 0x000000..0x0018FF;
	val PUA = 0x00E000..0x00F8FF;

	val range2 = 0x010000..0x01FFFF;
	val SPUAA = 0x0F0000..0x0FFFFF;
	
	val range3 = 0x020000..0x02FFFF;
	val SPUAB = 0x100000..0x10FFFF;

	fun decode(m: Message){
		val output = StringBuilder(2000);
		val s = m.contentField;
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
		m.contentField = output.toString();
	};
	fun encode(m: Message){
		val output = StringBuilder(2000);
		val s = m.contentField;
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
		m.contentField = output.toString();
	};

	override fun start(pluginContext: Context){
		patcher.before<StoreMessages>(
			"handleMessagesLoaded",
			ChannelChunk::class.java
		){frame ->
			val chunk = frame.args[0] as ChannelChunk;
			for(m in chunk.messages){
				decode(m);
			};
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