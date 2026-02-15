package alt.archimedes5000.plugins

import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context

import com.aliucord.patcher.instead

//import d0.t.c0 as IntIterator
//import d0.d0.b as IntProgressionIterator

@AliucordPlugin(requiresRestart = true)
@SuppressLint("SetTextI18n")
class IteratorFixTest: Plugin(){

	val cIntProgressionIterator = Class.forName("kotlin.ranges.IntProgressionIterator")
		.getDeclaredConstructor(
			Int::class.java,
			Int::class.java,
			Int::class.java
		)
		.apply{isAccessible = true}
	;

	override fun start(pluginContext: Context){

		patcher.instead<IntProgression>("iterator"){frame ->
			return@instead cIntProgressionIterator.newInstance(first, last, step);
		};

		val prog = IntProgression(0, 2, 1);
		val progIter = prog.iterator() as IntIterator;
		for(i in progIter){
			logger.debug("$i");
		};

		logger.debug("".trimIndent());

	};

	override fun stop(pluginContext: Context) = patcher.unpatchAll();

};