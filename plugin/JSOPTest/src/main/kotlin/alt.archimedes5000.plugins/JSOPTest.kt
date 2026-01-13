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

	override fun start(pluginContext: Context){
		val viewID = Utils.getResId("chat_list_adapter_item_text", "id");
		patcher.patch(
			WidgetChatListAdapterItemTextBinding::class.java
				.getDeclaredMethod("a", View::class.java)
			,
			Hook{
				frame ->
				val result = frame.getResult() as WidgetChatListAdapterItemTextBinding;
				val view: LinkifiedTextView = result.b;
				val id = view.id;
				if(id != viewID){
					logger.debug("failed: "+id+" ≠ "+viewID);
					return@Hook;
				};
				logger.debug("success "+view.toString());
			}
		);
	};

	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};