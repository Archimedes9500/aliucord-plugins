package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.stores.StoreUserTyping;
import org.objectweb.asm.*;
import org.objectweb.asm.Opcodes.*;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

@AliucordPlugin(requiresRestart = true)
class ASMTest: Plugin(){
	override fun start(pluginContext: Context){
		val clazz = test();
		logger.debug("${clazz}");

/*
		Patcher.addPatch(
			(StoreUserTyping::class.java
				.getDeclaredMethod(
					"setUserTyping",
					Long::class.java
				)
			),
			runtimeCallback(
				//
					frame.setResult(null);
					Logger().debug("Hello");
				//
				before = {mv ->
					mv
						.call(ALOAD, 0)
						.call(ACONST_NULL)
						.call(
							INVOKEVIRTUAL,
							MethodHookParam::class.java.name,
							"setResult",
							"(Ljava.lang.Object;)V"
						)
						.call(
							INVOKESPECIAL,
							"com.aliucord.Logger",
							"<init>",
							"()V"
						)
						.call(LDC, "Hello")
						.call(
							INVOKEVIRTUAL,
							"com.aliucord.Logger",
							"debug",
							"(Ljava.lang.String;)V"
						)
					;
				}
			)
		);
*/
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};