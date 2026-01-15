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
import com.aliucord.utils.ViewUtils.findViewById
import com.aliucord.patcher.Hook

import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import org.xmlpull.v1.XmlPullParser
import de.robv.android.xposed.XC_MethodHook
import android.graphics.drawable.ColorDrawable

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
		logger.debug("start");
		patcher.patch(
			RecyclerView.Adapter::class.java,
			"onBindViewHolder",
			arrayOf(RecyclerView.ViewHolder::class.java, Int::class.java, List::class.java),
			object: XC_MethodHook(10000){
				override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam){
					val root: View = ((param.args[0] as RecyclerView.ViewHolder).itemView).findViewById("widget_chat_list_adapter_item_text_root");
					logger.debug("hooked: "+root.resources.getResourceName(root.id));
					val view: TextView = (root as ViewGroup).findViewById("chat_list_adapter_item_text");
					logger.debug("found: "+view.toString());
					view.setBackgroundColor(4278190080L
						.or(Instant.now()
							.toEpochMilli()
						).rem(16777215L)
						.toInt()
					);
					logger.debug("set to: "+(view.background as ColorDrawable).color.toString(16));
				};
			}
		);
	};

	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};