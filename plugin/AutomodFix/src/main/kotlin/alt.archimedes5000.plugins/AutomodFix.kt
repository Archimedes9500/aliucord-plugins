package alt.archimedes5000.plugins;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.annotation.SuppressLint;
import android.content.Context;
import com.aliucord.patcher.before;
import com.discord.models.message.Message;
import com.discord.stores.StoreMessages;
import com.aliucord.Utils.Logger;

@AliucordPlugin(requiresRestart = true)
class AutomodFix:Plugin(){
	@SuppressLint("SetTextI18n")
	override fun start(pluginContext:Context){
		patcher.before<StoreMessages>(
			"access$handleLocalMessageCreate",
			StoreMessages::class.java,
			Message::class.java
		)balls@{
			frame ->
			val store = frame.args[0] as StoreMessages;
			val message = frame.args[1] as Message;
			Logger().debug(message);
		}
	}
	override fun stop(pluginContext:Context) = patcher.unpatchAll();
}