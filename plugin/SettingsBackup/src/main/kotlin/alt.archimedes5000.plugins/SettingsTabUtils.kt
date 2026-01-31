package alt.archimedes5000.plugins.utils

import com.aliucord.widgets.BottomSheet
import com.aliucord.entities.Plugin.SettingsTab
import java.lang.reflect.*

fun createSettings(tab: BottomSheet): SettingsTab{
	return SettingsTab(
		DelegatedBottomSheet::class.java as Class<*>,
		SettingsTab.Type.BOTTOM_SHEET
	).withArgs(tab);
};