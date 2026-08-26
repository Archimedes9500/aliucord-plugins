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
		val clazz = SynthClass(
			data = ClassData("test.Class"),
			fields = setOf(
				//public String hello = "Hello from ASM";
				FieldData(
					name = "hello",
					type = ClassRef("I"),
					value = 1
				)
			),
			methods = setOf(
				/*
				public String hello(){
					return hello;
				};
				*/
				MethodData(
					name = "hello",
					type = MethodType(
						emptyList<ClassRef>(),
						ClassRef("I")
					),
					body = {mv ->
						mv.visit(ALOAD, 0);
						mv.visit(
							GETFIELD,
							data.internalName,
							"hello",
							Fields["hello"].type.identifier
						);
						mv.visit(IRETURN);
					}
				)
			)
		).value;
		val instance = clazz.getConstructor().newInstance();
		val result = clazz.getMethod("hello").invoke(instance);
		logger.debug("${result as Int}");

/*
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
				*\/
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