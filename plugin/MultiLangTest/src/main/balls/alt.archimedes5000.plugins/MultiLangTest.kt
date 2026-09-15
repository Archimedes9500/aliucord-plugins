package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

@AliucordPlugin(requiresRestart = true)
class MultiLangTest: Plugin(){
	override fun start(pluginContext: Context){
		Test.test(logger);
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};