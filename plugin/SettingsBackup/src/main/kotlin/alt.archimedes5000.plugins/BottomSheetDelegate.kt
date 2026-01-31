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
		override private var id = BottomSheet::class.java.getDeclaredField("id").get(this.obj);
	};

	override val linearLayout = this.obj.getLinearLayout();

	override fun getContentViewResId(): Int{
		return this.obj.getContentViewResId();
	};

	override fun onViewCreated(view: View, bundle: Bundle?){
		this.obj.onViewCreated(view, bundle);
	};

	override fun getLinearLayout(): LinearLayout{
		return this.obj.getLinearLayout();
	};

	/** Sets the padding of the LinearLayout associated with this BottomSheet */
	override fun setPadding(p: Int){
		this.obj.getLinearLayout().setPadding(p, p, p, p);
	};

	/** Removes all views of the LinearLayout associated with this BottomSheet */
	override fun clear(){
		this.obj.getLinearLayout().removeAllViews();
	};

	/** Adds a view to the LinearLayout associated with this BottomSheet */
	override fun addView(view: View){
		this.obj.getLinearLayout().addView(view);
	};

	/** Removes a view from the LinearLayout associated with this BottomSheet */
	override fun removeView(view: View) {
		this.obj.getLinearLayout().removeView(view);
	};
};