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
		private var id: Int = fId.get(null) as Int;
		@JvmStatic
		private fun setId(value: Int){
			fId.set(null, value);
			this.id = value;
		};
	};

	private var view: NestedScrollView? = fView.get(obj) as? NestedScrollView;
	private fun setView(value: NestedScrollView){
		fView.set(obj, value);
		this.view = value;
	};

	private var layout: LinearLayout? = fLayout.get(obj) as? LinearLayout;
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

	val linearLayout = this.obj.getLinearLayout();

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