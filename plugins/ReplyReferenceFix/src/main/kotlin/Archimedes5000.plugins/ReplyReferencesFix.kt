package Archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.patcher.Hook
import com.aliucord.Utils
import com.aliucord.Utils.showToast
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import java.net.URL
import java.net.HttpURLConnection
import java.io.OutputStream
import org.json.JSONObject

@AliucordPlugin(requiresRestart = false)
class ReplyReferencesFix: Plugin(){
	@SuppressLint("SetTextI18n")
	override fun start(context: Context){
		val link = URL("https://mediawiki.org/w/api.php");
		val r = link.openConnection() as HttpURLConnection;
		r.requestMethod = "POST";
		r.doOutput = true;
		r.setRequestProperty(
			"Content-Type",
			"application/x-www-form-urlencoded"
		);
		var content = "action=query&format=json&formatversion=2";
		val body = r.outputStream as OutputStream;
		body.write(content.toByteArray());
		body.flush();
		var response = r.inputStream
			.bufferedReader()
			.readText()
		;
		var j = JSONObject(response);
		showToast(
			j.opt("batchcomplete").toString(),
			showLonger = false
		);
	}
	override fun stop(context: Context) = patcher.unpatchAll();
}