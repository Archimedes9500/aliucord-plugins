@file:OptIn(kotlin.ExperimentalStdlibApi::class)
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

import kotlin.reflect.KType;
import kotlin.properties.ReadOnlyProperty;
//import kotlin.reflect.jvm.jvmErasure;
import d0.e0.p.a.getJvmErasure;
import kotlin.reflect.KClass;
import kotlin.reflect.typeOf;

typealias HookCallback<T> = T.(de.robv.android.xposed.XC_MethodHook.MethodHookParam) -> Unit;

typealias IntIterator = d0.t.c0;
typealias ClosedRange<T> = d0.d0.a<T>;
typealias IntProgressionIterator = d0.d0.b;
typealias KTypeProjection = d0.e0.i;

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
		it.parameterCount == 0
	}.sortedBy{
		it.name.removePrefix("component").toInt();
	};
	val c = T::class.java.constructors.filter{
		it.parameterCount == components.size;
	}.first();

	val args = ArrayList<Any?>();
	for(i in 0 until c.parameterCount){
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

fun interface Invokable<T> {
	operator fun invoke(vararg args: Any?): T;
};
fun f(type: KType): MutableListOf<Class<*>>{
	var r = mutableListOf<KTypeProjection>();

	val args: List<KTypeProjection> = type.arguments;
	for(a in args){
		val ktype: KType? = a.type;
		if(ktype == null) continue;
		val kclassifier: kotlin.reflect.KClassifier<*> = type.classifier;
		val kclass: KClass<*>? = kclassifier as? KClass<*>;
		if(kclass == null) continue;
		val clazz: Class<*> = kclass.java;
		r.add(class);
	};
	return r;
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
				*f(type).dropLast(1).toTypedArray()
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
inline fun <reified T, R> accessMethod(methodName: String? = null) = MethodAccessor<T, R>(methodName, typeOf<T>());
