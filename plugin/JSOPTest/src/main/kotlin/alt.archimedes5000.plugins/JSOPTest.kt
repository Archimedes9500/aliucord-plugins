package alt.archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.Utils.showToast
import com.aliucord.SettingsUtilsJSON
import com.discord.stores.StoreStream
import com.discord.models.user.MeUser
import org.json.*

@AliucordPlugin(requiresRestart = true)
class JSOPTest: Plugin(){
	@SuppressLint("SetTextI18n")
	override fun start(pluginContext: Context){
		val imports = settings.getObject<Map<String, String>>("import", mapOf());
		val jsop = JSOP(imports, object{
			val `$me`: MeUser = StoreStream.getUsers().me;
		});

		val body = settings.getObject<JSONArray>("balls", JSONArray());
		val isBot = jsop.run<Boolean>(body.optJSONArray(0));
		when(isBot){
			false -> showToast("Yuore not a bot", showLonger = true);
			true-> showToast("Yuore a bot", showLonger = true);
			else -> showToast("Idk wtf yuore", showLonger = true);
		};
	};
	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};