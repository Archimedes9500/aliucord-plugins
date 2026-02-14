package alt.archimedes5000.plugins

import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context

//import d0.t.c0 as IntIterator
//import d0.d0.b as IntProgressionIterator

@AliucordPlugin(requiresRestart = true)
@SuppressLint("SetTextI18n")
class IteratorFixTest: Plugin(){

	override fun start(pluginContext: Context){

		val autorange = 0..2;
		val autorangeIter = autorange.iterator();
		for(i in autorangeIter){
			logger.debug("$i");
		};

		val range = IntRange(0, 2);
		val rangeIter = range.iterator();
		for(i in rangeIter){
			logger.debug("$i");
		};
		
		val prog = IntProgression(0, 2, 1);
		val progIter = prog.iterator();
		for(i in progIter){
			logger.debug("$i");
		};

		val altIter = d0.d0.b(0, 2, 1) as d0.t.c0;
		for(i in altIter){
			logger.debug("$i");
		};
	};

	override fun stop(pluginContext: Context) = patcher.unpatchAll();

};