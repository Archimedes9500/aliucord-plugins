package alt.archimedes5000.plugins.utils;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.enums.MatchType;
import java.lang.reflect.*;
import com.aliucord.Utils;

val bridge by lazy{
	DexKitBridge.create(appContext.packageManager.applicationInfo.sourceDir);
};
val cache = mutableMapOf<Executable, List<Executable>>();

fun getCallersOf(exe: Executable): List<Executable>{
	var result = cache[exe];
	if(result != null) return result;
	bridge.use{bridge ->
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
		}.toList();
		return result;
	};
};