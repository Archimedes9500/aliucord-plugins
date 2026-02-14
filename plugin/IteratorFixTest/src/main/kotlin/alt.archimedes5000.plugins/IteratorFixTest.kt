package alt.archimedes5000.plugins

import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin

//fun d0.d0.b.


@AliucordPlugin(requiresRestart = true)
@SuppressLint("SetTextI18n")
class IteratorFixTest: Plugin(){
	init{
		val data = arrayOf(0, 1, 2);
		val iter = object : IntIterator(){
			var idx = 0;
			override fun nextInt(): Int{
				if (!hasNext()) throw NoSuchElementException();
				return data[idx++];
			};
			override fun hasNext(): Boolean = idx<data.size;
		};
	};

	override fun stop(pluginContext: Context) = patcher.unpatchAll();

};