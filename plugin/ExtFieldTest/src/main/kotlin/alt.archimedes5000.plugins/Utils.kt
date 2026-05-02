package alt.archimedes5000.plugins

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import com.github.gfx.util.WeakIdentityHashMap

class ExtField<V>(): ReadWriteProperty<Any, V> {
	private val fields = WeakIdentityHashMap<Any, V>()

	@Suppress("UNCHECKED_CAST")
	override operator fun getValue(thisRef: Any, property: KProperty<*>) = fields[thisRef] as V
	override operator fun setValue(thisRef: Any, property: KProperty<*>, value: V) = fields[thisRef].set(value)
}