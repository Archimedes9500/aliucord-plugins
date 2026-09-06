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

data class Patch(
	val owner: String,
	val method: String? = null,
	val args: List<String>,
	val before: List<JSONArray>? = null,
	val after: List<JSONArray>? = null
);
data class JVMCall(
	val opcode: String,
	val args: Array<Any?> = emptyArray()
);

@AliucordPlugin(requiresRestart = true)
class ASMTest: Plugin(){
	val opcodes: Map<String, Int> = Opcodes::class.java.fields
		.filter{it.type == Int::class.java}
		.associate{it.name to it.getInt(null)}
	;
	val imports = settings.getObject("imports", emptyMap<String, String>());
	val patches = settings.getObject("patches", emptyList<Patch>());

	override fun start(pluginContext: Context){
		for(patch in patches){
			val (owner, method, args, before, after) = patch;
			Patcher.addPatch(
				when(method){
					null, "<init>" -> {
						Class.forName(owner)
							.getDeclaredConstructor(
								*args.map{classOrPrimitiveForName(it)}.toTypedArray()
							)
						;
					}
					else -> {
						Class.forName(owner)
							.getDeclaredMethod(
								method,
								*args.map{classOrPrimitiveForName(it)}.toTypedArray()
							)
						;
					}
				},
				runtimeCallback(
					before = {
						before?.forEach{
							val (op, args) = parse(it);
							call(opcodes[op]!!, *args);
						};
					},
					after = {
						after?.forEach{
							val (op, args) = parse(it);
							call(opcodes[op]!!, *args);
						};
					}
				)
			);
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};

	fun parse(args: JSONArray): JVMCall{
		return args.toList().map{
			if(it is String && it.startsWith("#")){
				it.removePrefix("#")
					.If({setOf('@', ';').none{it in this}}){
						classOrPrimitiveForName(
							imports[this]?: replace('/', '.')
						)!!.internalName;
					}.Else{
						Regex("""@([^@]+?)(;|<|$)""")
							.replace(this){
								it.groupValues[1].filterNot(Char::isWhitespace).run{
									classOrPrimitiveForName(
										imports[this]?: replace('/', '.')
									)!!.descriptorStart;
								}+it.groupValues[2];
							}
						;
					}
				;
			}else{
				it;
			};
		}.run{
			JVMCall(first() as String, drop(1).toTypedArray());
		};
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
