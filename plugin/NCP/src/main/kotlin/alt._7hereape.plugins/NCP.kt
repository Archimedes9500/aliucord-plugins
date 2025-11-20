package alt._7hereape.plugins

import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import android.annotation.SuppressLint
import android.content.Context
import com.aliucord.patcher.PreHook
import com.discord.models.user.User
import com.discord.widgets.chat.input.ChatInputViewModel
import com.discord.widgets.chat.input.ChatInputViewModel.ViewState
import com.discord.widgets.chat.MessageManager
import com.discord.widgets.chat.MessageContent
import kotlin.jvm.functions.Function1
import com.discord.models.message.Message

@AliucordPlugin
class NCP: Plugin(){
	val ver = "nana";
	val key: HashMap<String, Array<Int>> = hashMapOf(
		"nana" to arrayOf()
	);
	fun encrypt(string: String): String{
		var s = StringBuilder(string);
		val r = Regex("""<.*?>|[\[\]\(\)*~#-_>\n]""");
		val matches = r.findAll(string, 0);
	
		var out = StringBuilder(string);
		if(key[ver] != null){
			for(i in out.indices){
				if(matches.any{m -> i in m.range}) continue;
				//out[i] = (s[i]?.toInt()!!+key[ver]?.get(i)!!)?.toChar()!!;
			};
		}else{
			return string;
		};
		return ver+out.toString();
	};
	fun decrypt(string: String): String{
		val v = string.substring(0, 4)
		var s = StringBuilder(string.substring(4));
		val r = Regex("""<.*?>|[\[\]\(\)*~#-_>\n]""");
		val matches = r.findAll(string, 4);
	
		var out = StringBuilder(string);
		if(key[ver] != null){
			for(i in out.indices){
				if(matches.any{m -> i in m.range}) continue;
				//out[i] = (s[i]?.toInt()!!-key[ver]?.get(i)!!)?.toChar()!!;
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
				frame.args[2] = MessageContent(encrypt(text), users);
			}
		);
	};

	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};