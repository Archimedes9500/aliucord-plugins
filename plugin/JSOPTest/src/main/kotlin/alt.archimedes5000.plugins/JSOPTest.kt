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
		val imports = settings2.getObject<Map<String, String>>("import", mapOf());
		val jsop = JSOP(imports, object{
			val `$me`: MeUser = StoreStream.getUsers().me;
		});

		val body = settings2.getObject<JSONArray>("body", JSONArray());
		if(jsop.run<Boolean>(body.getJSONArray(0)) == true){
			showToast("Yuore a bot", showLonger = true);
		}else{
			showToast("Yuore not a bot", showLonger = true);
		};
	};
	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};