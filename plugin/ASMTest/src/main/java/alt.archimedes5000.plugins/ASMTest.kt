package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import android.widget.TextView;
import org.objectweb.asm.*;
import org.objectweb.asm.Opcodes.*;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import com.aliucord.Logger;

@AliucordPlugin(requiresRestart = true)
class ASMTest: Plugin(){
	override fun start(pluginContext: Context){
		try{Patcher.addPatch(
			(TextView::class.java
				.getDeclaredMethod(
					"setText",
					CharSequence::class.java,
					TextView.BufferType::class.java,
					Boolean::class.java,
					Int::class.java
				)
			),
			runtimeCallback(
				/*
					Logger().debug("Hello");
					frame.setResult(null);
				*/
				before = {mv ->
					mv
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
							MethodType<(String) -> void>().descriptor
						)
						.call(ALOAD, 1)
						.call(
							GETFIELD,
							refOf<MethodHookParam>().internalName,
							"args",
							refOf<Array<Object>>().refSignature
						)
						.call(ICONST_0)
						.call(LDC, "balls")
						.call(AASTORE)
						.call(RETURN)
					;
				}
			)
		);}catch(e: Throwable){logger.error("balls", e)};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};