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
	){
		override fun toString(): String{
			return "[$name, $reciever, ${args.joinToString(", ")}]"
		};
	};

	//error utils
	var line: Int = 0;
	lateinit var lastExpr: JSONArray;
	val errors = ArrayList<String>();

	//errors
	fun INVALID_ARGUMENT(arg: JSONObject, reason: String): String{
		return """
			ERROR invalid argument: ${arg.toString()}
				because it $reason
				on line ${this.line.toString()}:
				${this.lastExpr?.toString()}
		""".trimIndent();
	};
	fun INVALID_VALUE(value: Any?, reason: String): String{
		return """
			ERROR invalid value: ${value?.toString()}
				because it $reason
				on line ${this.line.toString()}:
				${this.lastExpr?.toString()}
		""".trimIndent();
	};
	fun LINE_SKIPPED(value: Any?): String{
		return """
			ERROR skipped line: ${this.line.toString()}
				because it was invalid.
		""".trimIndent();
	};
	fun CONVERSION_FAILED(type: String, value: Any?): String{
		return """
			ERROR failed to convert value: ${value?.toString()}
				to type: $type
				on line ${this.line.toString()}:
				${this.lastExpr?.toString()}
		""".trimIndent();
	};
	fun UNKNOWN_TYPE(type: String): String{
		return """
			ERROR unknown type: $type
				(it was not declared in "imports")
				on line ${this.line.toString()}:
				${this.lastExpr?.toString()}
		""".trimIndent();
	};


	//parse utils
	fun parseArg(obj: JSONObject): Pair<String, Any?>?{
		val keys = obj.keys();
		if(!keys.hasNext()){
			errors.add(INVALID_ARGUMENT(obj, "has no keys"));
			return null;
		};

		val key = keys.next();
		if(keys.hasNext()){
			errors.add(INVALID_ARGUMENT(obj, "has more than 1 key"));
			return null;
		};

		val rawValue = obj.get(key);
		when(rawValue){
			is JSONArray -> {
				val value = parseExpr(rawValue)?: parseArray(rawValue);
				if(value!= null){
					return Pair(key, value);
				}else{
					errors.add(INVALID_VALUE(obj, "is not a valid Expression or Array of Args"));
					return null;
				};
			};
			is JSONObject -> {
				val value = parseObject(rawValue);
				if(value!= null){
					return Pair(key, value);
				}else{
					errors.add(INVALID_VALUE(obj, "is not a valid Object of Args"));
					return null;
				};
			};
			JSONObject.NULL -> return Pair(key, null);
			else -> return Pair(key, rawValue);
		};
	};
	fun parseExpr(expr: JSONArray): Expression?{
		val name = expr.optString(0);
		if(name == null){
			errors.add(INVALID_VALUE(expr, "is not a valid Expression, it has no name"))
			return null;
		};

		val rawReciever = expr.optJSONObject(1);
		if(rawReciever == null){
			errors.add(INVALID_VALUE(expr.opt(1), "is not an Object or doesn't exist"));
			return null;
		};
		val reciever = parseArg(rawReciever)?: return null;

		val args = ArrayList<Pair<String, Any?>>();
		for(i in 2 until expr.length()){
			val rawArg = expr.optJSONObject(i);
			if(rawArg == null){
				errors.add(INVALID_VALUE(expr.opt(i), "is not an Object"));
			};
			val arg = parseArg(rawArg)?: return null;
			args.add(arg);
		};

		return Expression(name!!, reciever!!, args!!);
	}
	fun parseArray(array: JSONArray): ArrayList<Pair<String, Any?>>?{
		val types = ArrayList<String>();
		val output = ArrayList<Pair<String, Any?>>();
		for(i in 0 until array.length()){
			val rawE = array.optJSONObject(i);
			if(rawE == null){
				errors.add(INVALID_VALUE(array.opt(i), "is not an Object"));
			};
			val e = parseArg(rawE)?: return null;
			types.add(e.first);
			output.add(e);
		};

		if(types.distinct().size <= 1){
			return output
		}else{
			errors.add(INVALID_VALUE(array, "is not a valid Array of Args with uniform type"));
			return null;
		};
	};
	fun parseObject(obj: JSONObject): MutableMap<String, Pair<String, Any?>>?{
		val types = ArrayList<String>();
		val output: MutableMap<String, Pair<String, Any?>> = mutableMapOf();
		val keys = obj.keys();
		while(keys.hasNext()){
			val key = keys.next();
			val rawArg = obj.optJSONObject(key);
			if(rawArg == null){
				errors.add(INVALID_VALUE(obj.opt(key), "is not an Object"));
			};
			val arg = parseArg(rawArg)?: return null;
			types.add(arg.first);
			output.set(key, arg);
		};

		if(types.distinct().size <= 1){
			return output
		}else{
			errors.add(INVALID_VALUE(obj, "is not a valid Object of Args with uniform type"));
			return null;
		};
	};
	/*
	fun parse(body: JSONArray): ArrayList<Expression?>{
		val output = ArrayList<Expression>();
		for(i in 0 until body.length()){
			this.line++;
			val expr = parseExpr(body.getJSONArray(i));
			if(expr == null){
				errors.add(LINE_SKIPPED());
			};
			output.add(expr);
		};

		return output;
	};
	*/

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
		if(value == null) return arg;

		val o = value::class.java.declaredMethods
			?.find{
				it.name == "to"+type;
				it.parameterCount == 0;
			}
			?.apply{isAccessible = true}
			?.invoke(value)
		;

		if(o != null && o::class == value::class){
			return Pair(type, o);
		}else{
			errors.add(CONVERSION_FAILED(type, value));
			return null;
		};
	};
	fun processArg(arg: Pair<String, Any?>): Pair<String, Any?>?{
		var (type, value) = arg;

		val setUpValue = envSetup(value);

		val argClass = try{
			 Class.forName(imports[type]?: "");
		}catch(e: ClassNotFoundException){
			null;
		};
		if(argClass == null){
			errors.add(UNKNOWN_TYPE(type));
			return null;
		};

		return if(value == null){
			arg;
		}else if(setUpValue == null){
			Pair(type, setUpValue);
		}else if(value is Expression){
			Pair(type, exec(type, value));
		}else if(setUpValue::class.java == argClass){
			Pair(type, setUpValue);
		}else{
			convert(Pair(type, setUpValue));
		};
	};
	fun exec(returnType: String, expr: Expression): Any?{
		this.lastExpr = JSONArray(expr.toString());

		val name = expr.name;
		val reciever = processArg(expr.reciever);
		val recieverType = expr.reciever.first;

		val types = expr.args
			.map{
				try{
					val fullName = imports[processArg(it)?.first];
					Class.forName(fullName?: "");
				}catch(e: ClassNotFoundException){
					null;
				};
			}
			.toTypedArray()
		;
		val args = expr.args
			.map{
				processArg(it)?.second
			}
			.toTypedArray()
		;

		val returnClass =
			if(returnType != ""){
				try{
					val fullName = imports[returnType];
					Class.forName(fullName?: "");
				}catch(e: ClassNotFoundException){
					null;
				};
			}else{
				defaultClass;
			}
		;
		if(returnClass == null){
			errors.add(UNKNOWN_TYPE(returnType));
			return null;
		};

		val recieverClass = try{
			Class.forName(imports[recieverType]?: "");
		}catch(e: ClassNotFoundException){
			null;
		};
		if(recieverClass == null){
			errors.add(UNKNOWN_TYPE(recieverType));
			return null;
		};

		var returnValue: Any? = null;
		
		//try method
		try{
			val method = recieverClass.getDeclaredMethod(name, *types).apply{isAccessible = true};
			returnValue = method.invoke(reciever?.second, *args);
		}catch(e: NoSuchMethodException){
			//try constructor
			try{
				val thisClass = Class.forName(imports[name]?: "");
				//expr name resolves to a class
				val constr = thisClass.getDeclaredConstructor(*types).apply{isAccessible = true};
				returnValue = constr.newInstance(*args);
			}catch(e: Exception){
				//try field
				if(args.size == 0){
					try{
						val field = recieverClass.getDeclaredField(name).apply{isAccessible = true};
						returnValue = field.get(reciever?.second);
					}catch(e: NoSuchFieldException){
						returnValue = null;
					};
				};
			};
		};

		if(returnValue == null){
			errors.add(INVALID_VALUE(expr, "is not a valid Expression, name did not resolve to method, constructor or field"));
			return returnValue;
		}else if(returnValue::class.java == returnClass){
			return returnValue;
		}else{
			return convert(Pair(returnType, returnValue));
		};
	};

	//public interface
	inline fun <reified T>run(line: JSONArray): Pair<T?, ArrayList<String>>{
		this.line++;
		val expr = parseExpr(line);
		if(expr != null){
			this.defaultClass = T::class.java;
			return Pair(exec("", expr) as? T, this.errors);
		}else{
			return Pair(null, this.errors);
		};
	};
	inline fun <reified T>run(body: JSONArray, results: ArrayList<T?>): ArrayList<String>{
		for(i in 0 until body.length()){
			val line = body.getJSONArray(i);
			this.line++
			val expr = parseExpr(line);
			if(expr != null){
				this.defaultClass = T::class.java;
				results.add(exec("", expr) as? T);
			};
		};
		return this.errors;
	};
};