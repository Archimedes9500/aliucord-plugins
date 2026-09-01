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
import com.aliucord.Logger;

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
				/*
					frame.setResult(null);
					Logger().debug("Hello");
				*/
				before = {mv ->
					mv
						.call(ALOAD, 0)
						.call(ACONST_NULL)
						.call(
							INVOKEVIRTUAL,
							refOf<MethodHookParam>().internalName,
							"setResult",
							MethodType<(Any) -> void>()
						)
						.call(NEW, refOf<Logger>().internalName)
						.call(DUP)
						.call(
							INVOKESPECIAL,
							refOf<Logger>().internalName,
							"<init>",
							"()V"
						)
						.call(LDC, "Hello")
						.call(
							INVOKEVIRTUAL,
							refOf<Logger>().internalName,
							"debug",
							MethodType<(String) -> void>()
						)
						.call(RETURN)
					;
				}
			)
		);
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};