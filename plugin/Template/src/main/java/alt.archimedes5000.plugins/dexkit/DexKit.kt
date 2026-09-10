package alt.archimedes5000.plugins.dexkit;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import java.lang.reflect.*;

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