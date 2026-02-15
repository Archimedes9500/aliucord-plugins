package alt.archimedes5000.plugins

import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context

import.com.aliucord.patcher.instead;

//import d0.t.c0 as IntIterator
//import d0.d0.b as IntProgressionIterator

/*
fun IntProgression.iterator(): IntIterator{
	val first = this.first;
	val last = this.last;
	val step = this.step;
	return object : IntIterator(){
		private val finalElement: Int = last;
		private var hasNext: Boolean = if(step>0) first<=last else first>=last;
		private var next: Int = if(hasNext) first else finalElement;
		
		override fun hasNext(): Boolean = hasNext;
		
		override fun nextInt(): Int{
			val value = next;
			if(value == finalElement){
				if(!hasNext) throw NoSuchElementException();
				hasNext = false;
			}else{
				next += step;
			};
			return value;
		};
	};
};
*/

@AliucordPlugin(requiresRestart = true)
@SuppressLint("SetTextI18n")
class IteratorFixTest: Plugin(){

	val cIntProgressionIterator = IntProgressionIterator
		.getDeclaredConstructor(Int::class.java, Int::class.java, Int::class.java)
	;

	override fun start(pluginContext: Context){

		patcher.instead<IntProgression>("iterator"){
			(frame, first: Int, last: Int, step: Int) ->
			frame.result = cIntProgressionIterator.newInstance(first, last, step);
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