package import alt.archimedes5000.plugins.utils

import com.aliucord.widgets.BottomSheet
import com.aliucord.entities.Plugin.SettingsTab

fun createSettings(tab: BottomSheet): SettingsTab{
	return SettingsTab(
		DelegatedBottomSheet(object : BottomSheet(){})::class.java as Class<*>,
		SettingsTab.Type.BOTTOM_SHEET
	).withArgs(tab);
};