package Archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.patcher.Hook
import com.aliucord.Utils
import com.aliucord.Utils.showToast
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import org.json.JSONObject
import com.aliucord.Http
import com.google.gson.Gson
import java.net.URL

@AliucordPlugin(requiresRestart = false)
class ReplyReferencesFix: Plugin(){
	@SuppressLint("SetTextI18n")
	override fun start(context: Context){
		var response = try{
			URL("https://www.mediawiki.org/w/api.php?action=query?format=json").readText();
		};
		response = new JSONObject(response);
		showToast(response["batchcomplete"], showLonger = false);
	}
	override fun stop(context: Context) = patcher.unpatchAll();
}