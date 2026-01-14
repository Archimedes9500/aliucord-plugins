package alt.archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.Utils.showToast
import com.aliucord.SettingsUtilsJSON
import org.json.*

import com.discord.stores.StoreStream
import com.discord.models.user.MeUser

import android.view.View
import java.time.Instant
import com.aliucord.Utils
import com.aliucord.utils.ViewUtils
import com.aliucord.patcher.Hook

import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import org.xmlpull.v1.XmlPullParser
import de.robv.android.xposed.XC_MethodHook

@AliucordPlugin(requiresRestart = true)
class JSOPTest: Plugin(){
	@SuppressLint("SetTextI18n")
	val settings2 = SettingsUtilsJSON("JSOPTest");

	override fun start(pluginContext: Context){/*
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
*/
		patcher.patch(LayoutInflater::class.java, "inflate", arrayOf(XmlPullParser::class.java, ViewGroup::class.java, Boolean::class.java), object : XC_MethodHook(10000) {
			override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam) {
				val recycler: RecyclerView = (param.result as View).findViewById("chat_list_recycler");
				for(i in 0 until recycler.childCount){
					val root: ViewGroup = recycler.getChildAt(i);
					if((root as View).id != Utils.getResId("widget_chat_list_adapter_item_text_root", "id")) continue;
					val view: TextView = root.findViewById("chat_list_adapter_item_text");
					view.setBackgroundColor(4278190080L
						.or(Instant.now()
							.toEpochMilli()
						).rem(16777215)
						.toInt()
					);
				};
			};
		});
	};

	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};