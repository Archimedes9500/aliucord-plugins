package alt.archimedes5000.plugins;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.annotation.SuppressLint;
import android.content.Context;
import com.aliucord.patcher.before;
import com.discord.models.message.Message;
import com.discord.stores.StoreMessages;
import com.aliucord.Logger;

@AliucordPlugin(requiresRestart = true)
class AutomodFix:Plugin(){
	@SuppressLint("SetTextI18n")
	override fun start(pluginContext:Context){
		patcher.before<StoreMessages?>(
			"handleMessageCreate",
			List::class.java
		)balls@{
			frame ->
			val messages = frame.args[0] as? List<com.discord.api.message.Message>?;
			Logger().debug(messages.toString());
		}
	}
	override fun stop(pluginContext:Context) = patcher.unpatchAll();
}