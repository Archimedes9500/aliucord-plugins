package alt.archimedes5000.plugins.utils

import java.lang.reflect.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.reflect.jvm.jvmErasure

class MethodTypes<F>(val value: Array<Class<*>>)

@kotlin.ExperimentalStdlibApi
inline fun <reified F: Any> method(): MethodTypes<F> {
	val types = typeOf<F>().arguments.dropLast(1).map {
		it.type?.jvmErasure?.java?: Any::class.java;
	}.toTypedArray()

	return MethodTypes<F>(types)
}

fun KClass<*>.get(name: String, types: MethodTypes<*>): Method? {
	try {
		return this.java.getDeclaredMethod(name, *holder.types)
	} catch(e: Throwable) {
		return null
	}
}

inline fun <reified T, reified F> PatcherAPI.before(methodName: String, crossinline callback: F) {
	val types = typeOf<F>().arguments.dropLast(1).map {
		it.type?.jvmErasure?.java?: Any::class.java;
	}.toTypedArray()

	return patch(T::class.java.getDeclaredMethod(methodName, *types), object : XC_MethodHook() {
		override fun beforeHookedMethod(param: MethodHookParam) {
			try {
				val retVal = callback::class.java.declaredMethods[0].invoke(param.thisObject as T, *param.args)
				if (retVal !== Unit) param.result = retVal
			} catch (th: Throwable) {
				logger.error("Exception while pre-hooking ${param.method.declaringClass.name}.${param.method.name}", th)
			}
		}
	})
}