package alt.archimedes5000.plugins

import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context

typealias IntIterator = d0.t.c0;
typealias IntProgressionIterator = d0.d0.b;

@AliucordPlugin(requiresRestart = true)
@SuppressLint("SetTextI18n")
class ExtFieldTest: Plugin(){

	var String.balls: String by ExtField();
	override fun start(pluginContext: Context){
		val test = "test";
		test.balls = "balls";
		logger.debug(test.balls);
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};