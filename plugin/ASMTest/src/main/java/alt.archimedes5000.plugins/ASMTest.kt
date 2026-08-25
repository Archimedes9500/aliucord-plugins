package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.stores.StoreUserTyping;
import org.objectweb.asm.Opcodes.*;

@AliucordPlugin(requiresRestart = true)
class ASMTest: Plugin(){
	override fun start(pluginContext: Context){
		Patcher.addPatch(
			(StoreUserTyping::class.java
				.getDeclaredMethod(
					"setUserTyping",
					Long::class.java
				)
			),
			runtimeCallback(
				before = {mv ->
					mv.call(ALOAD, 0);
					mv.call(ACONST_NULL);
					mv.call(
						INVOKEVIRTUAL,
						MethodHookParam::class.java.name,
						"setResult",
						"(Ljava.lang.Object;)V"
					);
				}
			)
		);
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};