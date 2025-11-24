package alt._7hereape.plugins

import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import android.annotation.SuppressLint
import android.content.Context
import com.aliucord.patcher.Hook
import com.aliucord.patcher.PreHook
import com.discord.models.user.User
import com.discord.widgets.chat.input.ChatInputViewModel
import com.discord.widgets.chat.input.ChatInputViewModel.ViewState
import com.discord.widgets.chat.MessageManager
import com.discord.widgets.chat.MessageContent
import kotlin.jvm.functions.Function1
import com.discord.api.message.Message
import java.util.regex.Pattern

@AliucordPlugin
class NCP: Plugin(){
	val ver = "nanahira00";
	val versions = mapOf(
		"nanahira00" to Encryption(
			key = intArrayOf(0x4E00),
			charset = Charset(0x4E00..0x9FFF)
		){i ->
			this.key[i]!!;
		},
		"nanahira01" to Encryption(
			key = intArrayOf(0x1500),
			charset = Charset(0x1500..0x15FF, 0x3400..0x4CFF, 0x4E00..0x9EFF, 0xA100..0xA3FF, 0xA500..0xA5FF, 0x10600..0x106FF, 0x12000..0x122FF, 0x13000..0x133FF, 0x14400..0x145FF, 0x16800..0x169FF, 0x20000..0x285FF)
		){i ->
			this.key[i]!!;
		}
	);

	fun encrypt(string: String): String{
		val matcher = Pattern.compile("""<.*?>|[\[\]\(\)*~#-_>\n]""").matcher(string);

		var out = StringBuilder(string);
		var flag = matcher.find(0);
		if(ver in versions){
			val encryption = versions[ver]!!;
			for(i in out.indices){
				if(flag){
					val m = matcher.toMatchResult();
					if(i >= m.start() && i < m.end()) continue;
					if(i == m.end()) flag = matcher.find(i);
				};
				val offset = encryption.offset(i);
				out[i] = encryption.charset.char(out, i, offset);
			};
		}else{
			return string;
		};
		return ver+out.toString();
	};
	fun decrypt(string: String): String{
		val matcher = Pattern.compile("""<.*?>|[\[\]\(\)*~#-_>\n]""").matcher(string.substring(10));

		var out = StringBuilder(string.substring(10));
		val ver = string.substring(0, 10);
		var flag = matcher.find(0);
		if(ver in versions){
			val encryption = versions[ver]!!;
			for(i in out.indices){
				if(flag){
					val m = matcher.toMatchResult();
					if(i >= m.start() && i < m.end()) continue;
					if(i == m.end()) flag = matcher.find(i);
				};
				val offset = encryption.offset(i);
				out[i] = encryption.charset.char(out, i, -offset);
			};
		}else{
			return string;
		};
		return out.toString();
	};

	override fun start(pluginContext: Context){
		patcher.patch(
			ChatInputViewModel::class.java.getDeclaredMethod(
				"sendMessage",
				Context::class.java,
				MessageManager::class.java,
				MessageContent::class.java,
				List::class.java,
				Boolean::class.javaPrimitiveType,
				Function1::class.java
			),
			PreHook{frame ->
				val (text: String, users: List<User>) = frame.args[2] as MessageContent;
				if(text.substring(0, 10) != "nanahirape" || text.length < 11) return@PreHook;
				frame.args[2] = MessageContent(encrypt(text.substring(10)), users);
			}
		);
		patcher.patch(
			Message::class.java.getDeclaredMethod("i"),
			Hook{frame ->
				if(frame.result == null) return@Hook;
				val text = frame.result as String;
				if(text.length < 10) return@Hook;
				frame.result = decrypt(text);
			}
		);
	};

	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};