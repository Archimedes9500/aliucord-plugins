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
	val settings2 = SettingsUtilsJSON("JSOPTest");
	override fun start(pluginContext: Context){
		val imports = settings.getObject<MutableMap<String, String>>("imports", mutableMapOf());
		val jsop = JSOP(imports, object{
			val `$me`: MeUser = StoreStream.getUsers().me;
		});

		val body = settings2.getJSONArray("body", JSONArray())!!;
		val (isBot, errors) = jsop.run<Boolean>(body.getJSONArray(0));
		if(!errors.isEmpty()) logger.debug(errors.joinToString("\n"));
		when(isBot){
			false -> showToast("Yuore not a bot", showLonger = true);
			true-> showToast("Yuore a bot", showLonger = true);
			else -> showToast("Idk wtf yuore", showLonger = true);
		};
	};
	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};