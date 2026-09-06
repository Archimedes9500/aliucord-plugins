package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.aliucord.SettingsUtilsJSON;
import org.json.JSONArray;
import org.json.JSONObject.NULL;
import org.objectweb.asm.Opcodes;

data class Patch(
	val owner: String,
	val method: String? = null,
	val args: List<String>,
	val before: List<List<Any>>? = null,
	val after: List<List<Any>>? = null
);
data class JVMCall(
	val opcode: String,
	val args: Array<Any?> = emptyArray()
);

@AliucordPlugin(requiresRestart = true)
class ASMTest: Plugin(){
	val rawSettings = SettingsUtilsJSON("ASMTest");
	val opcodes: Map<String, Int> = Opcodes::class.java.fields
		.filter{it.type == Int::class.java}
		.associate{it.name to it.getInt(null)}
	;

	override fun start(pluginContext: Context){
		val imports = rawSettings.get("imports", mutableMapOf<String, String>());
		val patches = rawSettings.get("patches", ArrayList<Patch>());
		logger.debug(patches.joinToString("\n"));

		for(patch in patches){
			val (owner, method, args, before, after) = patch;
			Patcher.addPatch(
				when(method){
					null, "<init>" -> {
						Class.forName(imports[owner]?: owner)
							.getDeclaredConstructor(
								*args.map{classOrPrimitiveForName(imports[it]?: it)}.toTypedArray()
							)
						;
					}
					else -> {
						Class.forName(imports[owner]?: owner)
							.getDeclaredMethod(
								method,
								*args.map{classOrPrimitiveForName(imports[it]?: it)}.toTypedArray()
							)
						;
					}
				},
				runtimeCallback(
					before = before?.takeIf{it.isNotEmpty()}?.let{{
						it.forEach{
							val (op, args) = parse(it, imports);
							call(opcodes[op]!!, *args);
						};
					}},
					after = after?.takeIf{it.isNotEmpty()}?.let{{
						it.forEach{
							val (op, args) = parse(it, imports);
							call(opcodes[op]!!, *args);
						};
					}}
				)
			);
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};

	fun parse(args: List<Any>, imports: Map<String, String>): JVMCall{
		return args.map{if(it == NULL) null else it}.map{
			if(it is String && it.startsWith("#")){
				it.removePrefix("#")
					.If({setOf('@', ';').none{it in this}}){
						classOrPrimitiveForName(
							imports[this]?: replace('/', '.')
						)!!.internalName;
					}.Else{
						Regex("""@([^@]+?)([;<])""")
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
			}else if(it is Double && it.isInteger){
				it.toInt();
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
"#@Map<@Integer;@Integer;>;" ->
	"Ljava/util/Map<Ljava/lang/Integer;Ljava/lang/Integer;>;"
"#(II)@Map;" -> "(II)Ljava/util/Map;"
*/
