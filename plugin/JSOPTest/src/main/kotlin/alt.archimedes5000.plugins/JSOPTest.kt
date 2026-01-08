package alt.archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.Utils.showToast
import com.aliucord.SettingsUtilsJSON
import com.discord.stores.StoreStream
import org.json.*

@AliucordPlugin(requiresRestart = false)
class JSOPTest: Plugin(){
	@SuppressLint("SetTextI18n")
	override val settings = SettingsUtilsJSON("JSOPTest");
	override fun start(pluginContext: Context){
		val imports = settings.getObject("import", Map<String, String>::class);
		val jsop = JSOP(imports, object{
			val `$author`: User = StoreStream.getUsers().me;
		});

		val body = settings.getObject("body", JSONArray::class);
		if(jsop.run<Boolean?>(body.getJSONArray(0))){
			showToast("Yuore not a bot", showLonger = true);
		};
	};
	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};