package alt.archimedes5000.plugins;
import java.lang.reflect.*;
import org.json.*;

class JSOP(
	val imports: Map<String, String>,
	val env: Any
){
	lateinit var defaultClass: Class<*>;

	class Expression(
		val name: String,
		val reciever: Pair<String, Any?>,
		val args: List<Pair<String, Any?>>
	);

	//parse utils
	fun parseArg(obj: JSONObject): Pair<String, Any?>?{
		val keys = obj.keys();
		if(!keys.hasNext()) return null;
		val key = keys.next();
		if(keys.hasNext()) return null;
		val rawValue = obj.get(key);
		when(rawValue){
			is JSONArray -> {
				val value = parseExpr(rawValue)?: parseArray(rawValue);
				if(value!= null){
					return Pair(key, value);
				}else{
					return null;
				};
			};
			is JSONObject -> {
				val value = parseObject(rawValue);
				if(value!= null){
					return Pair(key, value);
				}else{
					return null;
				};
			};
			JSONObject.NULL -> return Pair(key, null);
			else -> return Pair(key, rawValue);
		};
	};
	fun parseExpr(expr: JSONArray): Expression?{
		val name = expr.optString(0)?: return null;
		val reciever = parseArg(expr.optJSONObject(1)?: return null);
		if(reciever == null) return null;
		val args = ArrayList<Pair<String, Any?>>();
		for(i in 2 until expr.length()){
			val arg = parseArg(expr.optJSONObject(i)?: return null);
			if(arg == null) return null;
			args.add(arg);
		};
		return Expression(name!!, reciever!!, args!!);
	}
	fun parseArray(array: JSONArray): ArrayList<Pair<String, Any?>>?{
		val types = ArrayList<String>();
		val output = ArrayList<Pair<String, Any?>>();
		for(i in 0 until array.length()){
			val e = parseArg(array.optJSONObject(i)?: return null);
			if(e == null) return null;
			types.add(e.first);
			output.add(e);
		};
		if(types.distinct().size <= 1){
			return output
		}else{
			return null;
		};
	};
	fun parseObject(obj: JSONObject): MutableMap<String, Pair<String, Any?>>?{
		val types = ArrayList<String>();
		val output: MutableMap<String, Pair<String, Any?>> = mutableMapOf();
		val keys = obj.keys();
		while(keys.hasNext()){
			val key = keys.next();
			val arg = parseArg(obj.optJSONObject(key)?: return null);
			if(arg == null) return null;
			types.add(arg.first);
			output.set(key, arg);
		};
		if(types.distinct().size <= 1){
			return output
		}else{
			return null;
		};
	};
	fun parse(body: JSONArray): ArrayList<Expression>{
		val output = ArrayList<Expression>();
		for(i in 0 until body.length()){
			val expr = parseExpr(body.getJSONArray(i));
			if(expr != null){
				output.add(expr);
			};
		};
		return output;
	};

	//exec utils
	fun envSetup(value: Any?): Any?{
		try{
			val field = this.env::class.java
				.getDeclaredField(value.toString())
				.apply{isAccessible = true}
			;
			return field.get(this.env);
		}catch(e: NoSuchFieldException){
			return value;
		};
	};
	fun convert(arg: Pair<String, Any?>): Pair<String, Any?>?{
		val (type, value) = arg;
		if(value == null) return null;
		val o = value::class.java.declaredMethods
			?.find{it.name == "to"+type}
			?.apply{isAccessible = true}
			?.invoke(value)
		;
		if(o != null && o::class == value::class){
			return Pair(type, o);
		}else{
			return null;
		};
	};
	fun processArg(arg: Pair<String, Any?>): Pair<String, Any?>?{
		var (type, value) = arg;
		val setUpValue = envSetup(value);
		return if(value == null){
			arg;
		}else if(setUpValue){
			Pair(type, setUpValue);
		}else if(value is Expression){
			Pair(type, exec(type, value));
		}else if(setUpValue::class.java == Class.forName(imports[type]!!)){
			Pair(type, setUpValue);
		}else{
			convert(Pair(type, setUpValue));
		};
	};
	fun exec(returnType: String, expr: Expression): Any?{
		val name = expr.name;

		val reciever = processArg(expr.reciever);
		val recieverType = expr.reciever.first;
		val args = expr.args.map{processArg(it)?.second}.toTypedArray();

		val returnClass = if(returnType != ""){
			Class.forName(imports[returnType]!!);
		}else{
			defaultClass;
		};
		val recieverClass = Class.forName(imports[recieverType]!!);

		var returnValue: Any? = null;
		val method = recieverClass.declaredMethods
			?.find{it.name == name}
			?.apply{isAccessible = true}
		;
		//try method
		if(method != null){
			returnValue = method.invoke(reciever?.second, *args);
		}else if(args.size == 0){
			val field = recieverClass.declaredFields
				?.find{it.name == name}
				?.apply{isAccessible = true}
			;
			//try field
			if(field != null){
				returnValue = field.get(reciever?.second);
			};
		};
		if(returnValue == null){
			return returnValue;
		}else if(returnValue::class.java == returnClass){
			return returnValue;
		}else{
			return convert(Pair(returnType, returnValue));
		};
	};

	//public interface
	inline fun <reified T>run(line: JSONArray): T?{
		val expr = parseExpr(line);
		if(expr != null){
			this.defaultClass = T::class.java;
			return exec("", expr) as? T;
		}else{
			return null;
		};
	};
	inline fun <reified T>run(body: JSONArray, results: ArrayList<T>){
		val exprs = parse(body);
		this.defaultClass = T::class.java;
		for(expr in exprs){
			results.add(exec("", expr) as T);
		};
	};
};