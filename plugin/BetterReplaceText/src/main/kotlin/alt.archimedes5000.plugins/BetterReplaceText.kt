package alt.archimedes5000.plugins

import alt.archimedes5000.plugins.utils.*
import com.aliucord.utils.*
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import android.content.Context

import com.aliucord.api.NotificationsAPI
import com.aliucord.entities.NotificationData
import com.aliucord.screens.UpdaterScreen

@AliucordPlugin(requiresRestart = true)
class BetterReplaceText: Plugin(){

	override fun start(pluginContext: Context){
		logger.debug("".trimIndent());
		logger.debug(appContext.toString());
		
		var notification = NotificationData()
			.setTitle("Updater")
			.setBody("M".repeat(500))
			.setAutoDismissPeriodSecs(30)
			.setOnClick{view ->
				openPage(view.getContext(), UpdaterScreen::class.java);
			}
		;
		NotificationsAPI.display(notification);
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};