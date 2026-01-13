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
import com.discord.databinding.WidgetChatListAdapterItemTextBinding
import com.aliucord.patcher.Hook
import com.discord.utilities.view.text.LinkifiedTextView

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
		val viewID = Utils.getResId("chat_list_adapter_item_text", "id");
		patcher.patch(
			WidgetChatListAdapterItemTextBinding::class.java
				.getDeclaredMethod("a", View:class.java)
			,
			Hook{frame ->
				val result = frame.getResult() as WidgetChatListAdapterItemTextBinding;
				val view: LinkifiedTextView = result.f2335b;
				val id = view.id;
				if(id != viewID){
					logger.debug("failed: "+id+" ≠ "+viewID);
					return@Hook;
				};
				view.setBackgroundColor(4278190080L
					.or(Instant.now()
						.toEpochMilli()
					).rem(16777215)
					.toInt()
				);
				frame.setResult(result);
			}
		);
	};

	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};