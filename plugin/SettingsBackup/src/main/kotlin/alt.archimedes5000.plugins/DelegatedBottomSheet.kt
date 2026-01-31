package alt.archimedes5000.plugins.utils

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout

import androidx.core.widget.NestedScrollView

import com.discord.app.AppBottomSheet
import com.discord.widgets.channels.WidgetChannelSelector

import com.aliucord.widgets.BottomSheet

val fId = BottomSheet::class.java.getDeclaredField("id").apply{isAccessible = true};
val fView = BottomSheet::class.java.getDeclaredField("view").apply{isAccessible = true};
val fLayout = BottomSheet::class.java.getDeclaredField("layout").apply{isAccessible = true};

/** Delegate to BottomSheet using a wrapped singleton */
@Suppress("unused")
open class DelegatedBottomSheet(val obj: BottomSheet): AppBottomSheet(){
	companion object{
		@JvmStatic
		@get:JvmName("doNotGetIdWithThis") private var id: Int = 0;

		@JvmStatic
		private fun getId(): Int{
			val value = fId.get(null) as Int;
			this.id = value;
			return value;
		};
		@JvmStatic
		private fun setId(value: Int){
			fId.set(null, value);
			this.id = value;
		};
	};

	@get:JvmName("doNotGetViewWithThis") private var view: NestedScrollView? = null;

	private fun getView(): NestedScrollView?{
		val value = fView.get(obj) as? NestedScrollView;
		this.view = value;
		return value;
	};
	private fun setView(value: NestedScrollView){
		fView.set(obj, value);
		this.view = value;
	};

	@get:JvmName("doNotGetLayoutWithThis") private var layout: LinearLayout? = null;

	private fun getLayout(): LinearLayout?{
		val value = fLayout.get(obj) as? LinearLayout;
		this.layout = value;
		return value;
	};
	private fun setLayout(value: LinearLayout){
		fLayout.set(obj, value);
		this.layout = value;
	};

	override fun getContentViewResId(): Int{
		return this.obj.getContentViewResId();
	};

	override fun onViewCreated(view: View, bundle: Bundle?){
		this.obj.onViewCreated(view, bundle);
	};

	val linearLayout = this.obj.linearLayout;

	/** Sets the padding of the LinearLayout associated with this BottomSheet */
	fun setPadding(p: Int){
		this.obj.getLinearLayout().setPadding(p, p, p, p);
	};

	/** Removes all views of the LinearLayout associated with this BottomSheet */
	fun clear(){
		this.obj.getLinearLayout().removeAllViews();
	};

	/** Adds a view to the LinearLayout associated with this BottomSheet */
	fun addView(view: View){
		this.obj.getLinearLayout().addView(view);
	};

	/** Removes a view from the LinearLayout associated with this BottomSheet */
	fun removeView(view: View) {
		this.obj.getLinearLayout().removeView(view);
	};
};