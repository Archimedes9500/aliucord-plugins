package alt.archimedes5000.plugins

import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context

typealias IntIterator = d0.t.c0;
typealias IntProgressionIterator = d0.d0.b;

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

		for(i in 0..2){
			logger.debug("$i");
		};

		for(i in 0L..2L){
			logger.debug("$i");
		};
	};
	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};