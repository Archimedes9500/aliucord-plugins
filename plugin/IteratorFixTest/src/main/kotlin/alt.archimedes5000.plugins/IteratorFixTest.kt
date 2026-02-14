package alt.archimedes5000.plugins

import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context

import d0.t.c0
import d0.d0.b
typealias c0 = IntIterator
typealias b = IntProgressionIterator

@AliucordPlugin(requiresRestart = true)
@SuppressLint("SetTextI18n")
class IteratorFixTest: Plugin(){

	override fun start(pluginContext: Context){

		val prog = IntProgression(0, 2, 1);
		val progIter = prog.iterator() as IntIterator;
		for(i in progIter){
			logger.debug("$i");
		};

		logger.debug("".trimIndent());

	};

	override fun stop(pluginContext: Context) = patcher.unpatchAll();

};