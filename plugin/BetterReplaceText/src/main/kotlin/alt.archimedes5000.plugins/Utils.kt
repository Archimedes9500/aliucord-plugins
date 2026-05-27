package alt.archimedes5000.plugins.utils;

import com.aliucord.utils.*;
import java.lang.reflect.*;
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import com.github.gfx.util.WeakIdentityHashMap;

typealias IntIterator = d0.t.c0;
typealias IntProgressionIterator = d0.d0.b;

class FakeField<V>(): ReadWriteProperty<Any, V> {
	private val fields = WeakIdentityHashMap<Any, V>();

	@Suppress("UNCHECKED_CAST")
	override operator fun getValue(thisRef: Any, property: KProperty<*>): V{
		return fields[thisRef] as V;
	};
	override operator fun setValue(thisRef: Any, property: KProperty<*>, value: V){
		fields.set(thisRef, value);
	};
};

fun resolveFlagsField(): FieldAccessor<Int>{
	return try{
		FieldAccessor<Int>("accessFlags");
	}catch(_: ReflectiveOperationException){
		FieldAccessor<Int>("modifiers");
	};
};

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
 