package alt.archimedes5000.plugins.utils;

import com.aliucord.utils.*;
import java.lang.reflect.*;

import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import com.github.gfx.util.WeakIdentityHashMap;

import de.robv.android.xposed.XposedBridge;

import org.luckypray.dexkit.DexKitBridge;
import com.aliucord.Utils;
import java.io.File;
import java.util.zip.ZipFile;
import com.aliucord.Http;
import org.luckypray.dexkit.query.enums.MatchType;
import org.luckypray.dexkit.util.InstanceUtil;

import com.aliucord.api.PatcherAPI;
import com.aliucord.api.Unpatch;
import com.aliucord.patcher.*;

import kotlin.reflect.KClass;
import kotlin.reflect.KType;
import kotlin.properties.ReadOnlyProperty;
import kotlin.reflect.typeOf;

import com.google.gson.reflect.TypeToken;

typealias IntIterator = d0.t.c0;
typealias ClosedRange<T> = d0.d0.a<T>;
typealias IntProgressionIterator = d0.d0.b;

val logger = com.aliucord.Logger("Utils");

class FakeField<V>(): ReadWriteProperty<Any, V>{
	private val fields = WeakIdentityHashMap<Any, V>();

	@Suppress("UNCHECKED_CAST")
	override operator fun getValue(thisRef: Any, property: KProperty<*>): V{
		return fields[thisRef] as V;
	};
	override operator fun setValue(thisRef: Any, property: KProperty<*>, value: V){
		fields.set(thisRef, value);
	};
};

fun resolveFlagsField(): FinalFieldAccessor<Int>{
	return try{
		FinalFieldAccessor<Int>("accessFlags");
	}catch(_: ReflectiveOperationException){
		FinalFieldAccessor<Int>("modifiers");
	};
};
//has to be wrapped in a function or kotlin 1.5 will shit itself lmao
var Field.accessFlags: Int by resolveFlagsField();
class FinalFieldAccessor<T>(val fieldName: String?): ReadWriteProperty<Any, T>{
	val fields = mutableListOf<Field>();

	fun field(thisRef: Any, property: KProperty<*>): Field {
		val clazz = thisRef::class.java;
		return fields.find{
			it.declaringClass == clazz;
		}?: clazz.getDeclaredField(
			fieldName?: property.name.removeSuffix("Field")
		).apply{
			isAccessible = true;
			fields.add(this);
			accessFlags = modifiers and Modifier.FINAL.inv();
		};
	};

	@Suppress("UNCHECKED_CAST")
	override operator fun getValue(thisRef: Any, property: KProperty<*>): T{
		return field(thisRef, property)[thisRef] as T;
	};
	override operator fun setValue(thisRef: Any, property: KProperty<*>, value: T){
		field(thisRef, property).set(thisRef, value);
	};
};

fun <T> accessFinalField(fieldName: String? = null) = FinalFieldAccessor<T>(fieldName);

fun deoptimize(vararg members: Member): Boolean{
	var allSuccess = true;
	for(member in members){
		if(!XposedBridge.deoptimizeMethod(member)){
			allSuccess = false;
		};
	};
	return allSuccess;
};

fun getJVMClassName(clazz: Class<*>): String{
	val head = clazz.getPackage().name;
	val tail = clazz.name.removePrefix("$head.");
	return head+"."+tail.replace(".", "\$");
};

val bridge: DexKitBridge by lazy{
	Utils.threadPool.submit{
		val libdexkit = File(Utils.appContext.filesDir, "libdexkit.so");
		if(!libdexkit.exists()){
			ZipFile(
				File(Utils.appContext.cacheDir, "dexkit.aar").also{
					Http.simpleDownload(
						"https://repo1.maven.org/maven2/org/luckypray/dexkit/2.2.0/dexkit-2.2.0.aar",
						it
					);
				}
			).use{zip ->
				zip.getInputStream(zip.getEntry(
					"jni/${android.os.Build.SUPPORTED_ABIS.first()}/libdexkit.so"
				)).use{input ->
					libdexkit.outputStream().use{output ->
						input.copyTo(output);
					};
				};
			};
		};
		System.load(libdexkit.absolutePath);
	}.get();
	DexKitBridge.create(Utils.appContext.applicationInfo.sourceDir);
};
val cache = mutableMapOf<Executable, Array<out Executable>>();
fun getCallersOf(exe: Executable): Array<out Executable>{
	//com.aliucord.Logger("balls").debug(getJVMClassName(exe.declaringClass));
	var result = cache[exe];
	if(result != null) return result;
	val callee = bridge.findClass{
		matcher{
			className(exe.declaringClass.name);
		};
	}.single().findMethod{
		matcher{
			name = if(exe is Method) exe.name else "<init>";
			paramTypes(*exe.parameterTypes.map{it.name}.toTypedArray());
		};
	}.single();
	result = bridge.findMethod{
		matcher{
			invokeMethods{
				add{
					descriptor = callee.descriptor;//Match by method signature
				};
				matchType = MatchType.Contains;//Only needs to contain that call site
			};
		};
	}.map{
		if(it.isConstructor){
			InstanceUtil.getConstructorInstance(
				Utils.appContext.classLoader,
				it.toDexMethod()
			);
		}else{
			InstanceUtil.getMethodInstance(
				Utils.appContext.classLoader,
				it.toDexMethod()
			);
		};
	}.toTypedArray();
	return result!!;
};

fun deoptimizeCallersOf(exe: Executable): Boolean{
	return deoptimize(*getCallersOf(exe));
};

typealias HookCallback<T> = T.(de.robv.android.xposed.XC_MethodHook.MethodHookParam) -> Unit;
inline fun <reified T> PatcherAPI.before(
	methodName: String,
	vararg paramTypes: Class<*>,
	deoptimize: Array<out Executable>,
	crossinline callback: HookCallback<T>
): Unpatch{
	deoptimize(*deoptimize);
	return this.before<T>(methodName, *paramTypes, callback = callback);
};

inline fun <reified T> PatcherAPI.before(
	methodName: String,
	vararg paramTypes: Class<*>,
	deoptimize: Boolean,
	crossinline callback: HookCallback<T>
): Unpatch{
	return if(deoptimize){
		deoptimizeCallersOf(T::class.java.getDeclaredMethod(methodName, *paramTypes));
		this.before<T>(methodName, *paramTypes, callback = callback);
	}else{
		this.before<T>(methodName, *paramTypes, callback = callback);
	};
};

inline fun <reified T> PatcherAPI.before(
	vararg paramTypes: Class<*>,
	deoptimize: Boolean,
	crossinline callback: HookCallback<T>
): Unpatch{
	return if(deoptimize){
		deoptimizeCallersOf(T::class.java.getDeclaredConstructor(*paramTypes));
		this.before<T>(*paramTypes, callback = callback);
	}else{
		this.before<T>(*paramTypes, callback = callback);
	};
};

inline fun <reified T: Any>T.reconstruct(vararg data: Pair<Int, Any?>): T{
	val new = data.toMap();

	val components = T::class.java.methods.filter{
		it.name.matches(Regex("""component[1-9]\d*"""))
	&&
		//it.parameterCount == 0
		it.parameterTypes.size == 0//Android 7 lmao
	}.sortedBy{
		it.name.removePrefix("component").toInt();
	};
	val c = T::class.java.constructors.filter{
		//it.parameterCount == components.size;
		it.parameterTypes.size == components.size;
	}.first();

	val args = ArrayList<Any?>();
	for(i in 0 until /*c.parameterCount*/c.parameterTypes.size){
		args.add(
			if(i+1 in new){
				new[i+1];
			}else{
				components[i].invoke(this);
			}
		);
	};
	return c.newInstance(*args.toTypedArray()) as T;
};

fun String.toBoxedName(): String{
	return when(this){
		"byte" -> "java.lang.Byte";
		"char" -> "java.lang.Character";
		"double" -> "java.lang.Double";
		"float" -> "java.lang.Float";
		"int" -> "java.lang.Integer";
		"long" -> "java.lang.Long";
		"short" -> "java.lang.Short";
		"boolean" -> "java.lang.Boolean";
		else -> this;
	};
};
fun String.toJavaName(): String{
	return when(this){
		"kotlin.Any" -> "java.lang.Object";
		"kotlin.Byte?" -> "java.lang.Byte";
		"kotlin.Short?" -> "java.lang.Short";
		"kotlin.Int?" -> "java.lang.Integer";
		"kotlin.Long?" -> "java.lang.Long";
		"kotlin.Char?" -> "java.lang.Character";
		"kotlin.Float?" -> "java.lang.Float";
		"kotlin.Double?" -> "java.lang.Double";
		"kotlin.Boolean?" -> "java.lang.Boolean";
		"kotlin.Cloneable" -> "java.lang.Cloneable";
		"kotlin.Comparable" -> "java.lang.Comparable";
		"kotlin.Enum" -> "java.lang.Enum";
		"kotlin.Annotation" -> "java.lang.annotation.Annotation";
		"kotlin.CharSequence" -> "java.lang.CharSequence";
		"kotlin.String" -> "java.lang.String";
		"kotlin.Number" -> "java.lang.Number";
		"kotlin.Throwable" -> "java.lang.Throwable";
		"kotlin.collections.Iterator" -> "java.util.Iterator";
		"kotlin.collections.Iterable" -> "java.lang.Iterable";
		"kotlin.collections.Collection" -> "java.util.Collection";
		"kotlin.collections.Set" -> "java.util.Set";
		"kotlin.collections.List" -> "java.util.List";
		"kotlin.collections.ListIterator" -> "java.util.ListIterator";
		"kotlin.collections.Map" -> "java.util.Map";
		"kotlin.collections.Map.Entry" -> "java.util.Map.Entry";
		"kotlin.ByteArray" -> "[B";
		"kotlin.CharArray" -> "[C";
		"kotlin.DoubleArray" -> "[D";
		"kotlin.FloatArray" -> "[F";
		"kotlin.IntArray" -> "[I";
		"kotlin.LongArray" -> "[J";
		"kotlin.ShortArray" -> "[S";
		"kotlin.BooleanArray" -> "[Z";
		//will crash Class.forName, not real classes
		"kotlin.Byte" -> "byte";
		"kotlin.Char" -> "char";
		"kotlin.Double" -> "double";
		"kotlin.Float" -> "float";
		"kotlin.Int" -> "int";
		"kotlin.Long" -> "long";
		"kotlin.Short" -> "short";
		"kotlin.Boolean" -> "boolean";
		else -> {//generic Array<T>, requires regex, recursion and boxing
			val m = Regex("kotlin.Array<(.*)>").matchEntire(this);
			if(m != null){
				"[L${"${m.groupValues[1]}".toJavaName().toBoxedName()};";
			}else{//it's a real class and not a fake kotlin one
				this;
			};
		};
	};
};
fun removeTypeParams(name: String): String{
	//balanced <> matcher (except for kotlin.Array)
	val regex = Regex("""(?<!kotlin\.Array)(?=<)(?:(?=.*?<(?!.*?\1)(.*>(?!.*\2).*))(?=.*?>(?!.*?\2)(.*)).)+?.*?(?=\1)[^<]*(?=\2$)""");

	return regex.replace(name, "");
};
fun classOrPrimitiveForName(name: String, boxed: Boolean = false): Class<*>?{
	if(name == "*") return null;
	if(boxed) return Class.forName(name.toBoxedName());
	return when(name){
		"byte" -> Byte::class.java;
		"char" -> Char::class.java;
		"double" -> Double::class.java;
		"float" -> Float::class.java;
		"int" -> Int::class.java;
		"long" -> Long::class.java;
		"short" -> Short::class.java;
		"boolean" -> Boolean::class.java;
		else -> Class.forName(name);
	};
};
fun classForKotlinName(name: String, boxed: Boolean = false): Class<*>?{
	if(name == "*") return null;
	if(boxed) return classOrPrimitiveForName(
		name.toJavaName(),
		boxed = true
	);
	return when(name){
		"kotlin.Byte" -> Byte::class.java;
		"kotlin.Char" -> Char::class.java;
		"kotlin.Double" -> Double::class.java;
		"kotlin.Float" -> Float::class.java;
		"kotlin.Int" -> Int::class.java;
		"kotlin.Long" -> Long::class.java;
		"kotlin.Short" -> Short::class.java;
		"kotlin.Boolean" -> Boolean::class.java;
		else -> classOrPrimitiveForName(
			name.toJavaName()
		);
	};
};
fun kClassForName(name: String): KClass<*>?{
	return classForKotlinName(name)?.kotlin;
};

val kotlinReflectAvailable = runCatching{
	Class.forName("kotlin.reflect.full.KClasses");
}.isSuccess;

fun getArgs(type: KType): List<Class<*>?>?{
	return if(kotlinReflectAvailable){
		Regex("""^\((.*)\) -> (.*)$""")
			.find(type.toString(), 0)
			?.groupValues
			?.get(1)
			?.let{removeTypeParams(it)}
			?.run{split(", ")}
			?.map{
				classForKotlinName(it);
			}
		;
	}else{
		Regex("""^kotlin.jvm.functions.Function(?:N|\d+)<(.*)>""")
			.find(type.toString(), 0)
			?.groupValues
			?.get(1)
			?.let{removeTypeParams(it)}
			?.run{split(", ")}
			?.dropLast(1)
			?.map{
				classOrPrimitiveForName(
					it.removeSuffix("?")//due to kotlin retardation
				);
			}
		;
	};
};

fun Type.toClass(): Class<*> = when(this){
	is Class<*> -> this;
	is ParameterizedType -> rawType as Class<*>;
	is GenericArrayType ->{
		val component = genericComponentType.toClass();
		java.lang.reflect.Array.newInstance(component, 0).javaClass;
	};
	else -> throw IllegalArgumentException("The Type is not a Class");
};
val Type.arguments: Array<Type> get() = when(this){
	is ParameterizedType -> this.actualTypeArguments;
	is GenericArrayType -> arrayOf(this.genericComponentType);
	else -> emptyArray<Type>();
};
fun <T>javaTypeOf(): Type{
	return (object : TypeToken<T>(){}).type;
};

fun interface Invokable<T>{
	operator fun invoke(vararg args: Any?): T;
};
class MethodAccessor<T, R>(private val methodName: String?, val type: KType): ReadOnlyProperty<Any, Invokable<R>>{
	private val methods = mutableListOf<Method>();

	private fun method(thisRef: Any, property: KProperty<*>): Method{
		val clazz = thisRef::class.java
		return methods.find{it.declaringClass == clazz}
			?: clazz.getDeclaredMethod(
				methodName?: property.name.removePrefix("access").replaceFirstChar{
					it.lowercaseChar();
				},
				*getArgs(type)!!.toTypedArray()
			).apply{
				isAccessible = true;
				methods.add(this);
			};
	};

	@Suppress("UNCHECKED_CAST")
	override fun getValue(thisRef: Any, property: KProperty<*>): Invokable<R>{
		return Invokable<R>{args -> method(thisRef, property).invoke(thisRef, *args) as R};
	};
};
inline fun <reified T, R>accessMethod(methodName: String? = null) =
	MethodAccessor<T, R>(methodName, typeOf<T>())
;

fun <T>Class<T>.getAnyField(name: String, firstCall: Boolean = true): Field{
	return runCatching{
		this.getDeclaredField(name);
	}.getOrElse{
		runCatching{
			if(firstCall){
				this.getField(name);
			}else{
				this.superclass.getAnyField(name, false);
			};
		}.getOrThrow();
	}.apply{
		isAccessible = true;
	};
};

fun <T>Iterable<T>.pickByMin(comp: (T) -> Int): Iterable<T>{
	var min = Int.MAX_VALUE;
	val mins = ArrayList<T>();
	this.forEach{
		if(comp(it) == min){
			mins.add(it);
		}else if(comp(it) < min){
			min = comp(it);
			mins.clear();
			mins.add(it);
		};
	};
	return mins;
};

/*
fun <T>Class<T>.getAnyMethod(name: String, vararg args:Any?, depth: Int = 0): Method{
	return runCatching{
		this.getDeclaredMethod(name, *args);
	}.getOrElse{
		if(depth == 0){
				this.getMethod(name);
		}else{
			(this.interfaces+this.superclass).map{
				runCatching{
					it!!.getAnyMethod(name, *args, depth+1);
				};
			}.filter{
				it.isSuccess;
			}.run{
				var min = Int.MAX_VALUE;
				val mins = ArrayList<T>();
				val comp = 
				this.forEach{
					if(comp(it) == min){
						mins.add(it);
					}else if(comp(it) < min){
						min = comp(it);
						mins.clear();
						mins.add(it);
					};
				};
				return@run mins;
			}.single().getOrThrow();
		};
	}.apply{
		isAccessible = true;
	};
};
*/
