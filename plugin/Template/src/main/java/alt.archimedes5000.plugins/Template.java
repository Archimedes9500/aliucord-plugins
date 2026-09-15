package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

@AliucordPlugin(true)
class Template extends Plugin{
	@Override void start(Context pluginContext){
	};
	@Override void stop(Context pluginContext){
		patcher.unpatchAll();
	};
};