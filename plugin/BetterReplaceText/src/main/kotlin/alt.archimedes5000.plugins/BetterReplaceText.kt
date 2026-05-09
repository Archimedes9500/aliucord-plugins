package alt.archimedes5000.plugins

import alt.archimedes5000.plugins.utils.*
import com.aliucord.utils.*
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context

@AliucordPlugin(requiresRestart = true)
@SuppressLint("SetTextI18n")
class BetterReplaceText: Plugin(){

	//var String.balls: String by ExtField();
	override fun start(pluginContext: Context){
		logger.debug("".trimIndent());
		logger.debug(appContext.toString());
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};