package alt.archimedes5000.plugins.utils

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout

import androidx.core.widget.NestedScrollView

import com.discord.app.AppBottomSheet
import com.discord.widgets.channels.WidgetChannelSelector

import com.aliucord.widgets.BottomSheet

/** Delegate to BottomSheet using a wrapped singleton */
@Suppress("unused")
open class BottomSheetDelegate(val obj: BottomSheet): BottomSheet(){
	companion object{
		@JvmStatic
		private var id = BottomSheet::class.java.getDeclaredField("id").get(obj);
	};

	val linearLayout = obj.getLinearLayout();

	fun getContentViewResId(): Int{
		return obj.getContentViewResId();
	};

	fun onViewCreated(view: View, bundle: Bundle?){
		obj.onViewCreated(view, bundle);
	};

	fun getLinearLayout(): LinearLayout{
		return obj.getLinearLayout();
	};

	/** Sets the padding of the LinearLayout associated with this BottomSheet */
	fun setPadding(p: Int){
		obj.getLinearLayout().setPadding(p, p, p, p);
	};

	/** Removes all views of the LinearLayout associated with this BottomSheet */
	fun clear(){
		obj.getLinearLayout().removeAllViews();
	};

	/** Adds a view to the LinearLayout associated with this BottomSheet */
	fun addView(view: View){
		obj.getLinearLayout().addView(view);
	};

	/** Removes a view from the LinearLayout associated with this BottomSheet */
	fun removeView(view: View) {
		obj.getLinearLayout().removeView(view);
	};
};