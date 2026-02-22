package alt.archimedes5000.plugins

import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.patcher.*

import com.discord.widgets.chat.list.InlineMediaView

typealias IntIterator = d0.t.c0;
typealias IntProgressionIterator = d0.d0.b;

@AliucordPlugin(requiresRestart = true)
@SuppressLint("SetTextI18n")
class FuckAnimations: Plugin(){
	override fun start(pluginContext: Context){
		patcher.instead<InlineMediaView>("shouldAutoPlay"){frame ->
			return@instead true;
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};