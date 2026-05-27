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

@AliucordPlugin(requiresRestart = true)
class BetterReplaceText: Plugin(){

	val range1 = 0x00000..0x01900;
	val PUA = 0x0E000..0x0F8FF;

	val range2 = 0x00001..0x2FFFC;
	val SPUAA = 0xF0000..0xFFFFD;
	val SPUAB = 0x10000..0x10FFFD;

	var Message.contentField: String by accessFinalField();

	override fun start(pluginContext: Context){
		patcher.before<StoreMessages>(
			"handleMessagesLoaded",
			ChannelChunk::class.java
		){frame ->
			val chunk = frame.args[0] as ChannelChunk;
			for(m in chunk.messages){
				val output = StringBuilder(2000);
				val s = m.contentField;
				s.codePoints().forEachOrdered{
					output.appendCodePoint(
						when(it){
							in PUA -> {
								range1.first+(it-PUA.first);
							};
							in SPUAA -> {
								range2.first+(it-SPUAA.first);
							};
							in SPUAB -> {
								range2.first+(it-SPUAB.first);
							};
							else -> it;
						}
					);
				};
				m.contentField = output.toString();
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