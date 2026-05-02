package alt.archimedes5000.plugins

import com.github.gfx.util.WeakItentityHashMap

class ExtField<V>(): ReadWriteProperty<Any, V> {
	private val fields = WeakIdentityHashMap<Any, V>()

	@Suppress("UNCHECKED_CAST")
	override fun getValue(thisRef: Any, property: KProperty<*>) = fields[thisRef] as V
	override fun setValue(thisRef: Any, property: KProperty<*>, value: V) = fields[thisRef].set(value)
}