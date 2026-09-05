package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import org.json.JSONArray;
import android.widget.TextView;
import org.objectweb.asm.*;
import org.objectweb.asm.Type as ASMType;
import org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.Opcodes;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import com.aliucord.Logger;

data class Patch(
	val owner: String,
	val method: String? = null,
	val args: List<String>,
	val before: List<JSONArray>? = null,
	val after: List<JSONArray>? = null
);

val Class<*>.internalName: String get(){
	return if(isPrimitive){
		ASMType.getDescriptor(this);
	}else{
		ASMType.getInternalName(this);
	};
};
val Class<*>.descriptorStart: String get(){
	return if(isPrimitive){
		this.internalName;
	}else{
		"L"+this.internalName;
	};
};

context(imports: Map<String, String>)
fun parse(args: JSONArray): List<Any>{
	return args.map{
		it.If({it is String && it.startsWith('#')}){
			removePrefix('#')
				.If({setOf('@', ';').none{it in this}}){
					classOrPrimitiveForName(
						imports[this]?: replace('/', '.')
					).internalName;
				}.Else{
					Regex("""@([^@]+?)(;|<|$)""")
						.replace(this){
							it[1].filterNot{Char::isWhitespace}.run{
								classOrPrimitiveForName(
									imports[this]?: replace('/', '.')
								).descriptorStart;
							}+it[2];
						}
					;
				}
			;
		}();
	};
};

@AliucordPlugin(requiresRestart = true)
class ASMTest: Plugin(){
	override fun start(pluginContext: Context){
		val imports = settings.getObject("imports", emptyMap<<String, String>>());
		val patches = settings.getObject("patches", emptyList<Patch>());
		for(patch in patches){
			val (owner, method, args, before, after) = patch;
			Patcher.addPatch(
				when(method){
					null, "<init>" -> {
						classOrPrimitiveForName(owner)
							.getDeclaredConstructor(
								*args.map{Class.forName(it)}.toTypedArray()
							)
						;
					}
					else -> {
						classOrPrimitiveForName(owner)
							.getDeclaredMethod(
								method,
								*args.map{Class.forName(it)}.toTypedArray()
							)
						;
					}
				},
				runtimeCallback(
					before = {
						before?.forEach{args ->
							call(Opcodes.valueOf(args[0] as String), *args);
						};
					},
					after = {
						after?.forEach{(op, args) ->
							call(Opcodes.valueOf(op), *args);
						};
					}
				)
			);
		};
		Patcher.addPatch(
			(TextView::class.java
				.getDeclaredMethod(
					"setText",
					CharSequence::class.java,
					TextView.BufferType::class.java,
					Boolean::class.java,
					Int::class.java
				)
			),
			/*
				PreHook{frame -> 
					frame.args[0] = "balls";
					return@PreHook;
				}
				((frame) |> getField(<MethodHookParam>, "args", <Array<Object>>), 0, "hello") |> setArrayField
			*/
			runtimeCallback(
				before = {
					call(ALOAD, 1);//frame
					call(
						GETFIELD,
						refOf<MethodHookParam>().internalName,
						"args",
						refOf<Array<Object>>().descriptor
					);//.args
					call(ICONST_0);//0
					call(LDC, "balls");//"balls"
					call(AASTORE);//[] =
					call(RETURN);//return
				}
			)
		);
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};
/*
"imports":{
	"Map":"java.util.Map",
	"Integer":"java.lang.Integer"
},
"#Map" -> "java/util/Map"
"#@Map;" -> "Ljava/util/Map;"
"#@@Map<@Integer;@Integer;>;" ->
	"Ljava/util/Map<Ljava/lang/Integer;Ljava/lang/Integer;>;"
"#@@@Map;" ->
	"<K:Ljava/lang/Object;V:Ljava/lang/Object;>Ljava/lang/Object;"
"#(II)@Map;" -> "(II)Ljava/util/Map;"
*/