package alt.archimedes5000.plugins.utils

import com.aliucord.widgets.BottomSheet
import com.aliucord.entities.Plugin.SettingsTab
import android.view.View
import android.os.Bundle
import java.lang.reflect.*

fun createSettings(tab: BottomSheet): SettingsTab{
	return SettingsTab(
		DelegatedBottomSheet(
			object : BottomSheet(){
				override fun onViewCreated(view: View, bundle: Bundle?){
					super.onViewCreated(view, bundle);
				};
			}
		)::class.java as Class<*>,
		SettingsTab.Type.BOTTOM_SHEET
	).withArgs(tab);
};