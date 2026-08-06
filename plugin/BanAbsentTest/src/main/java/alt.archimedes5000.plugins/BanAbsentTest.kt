package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.widgets.user.profile.UserProfileAdminView;
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel.ViewState;

@AliucordPlugin(requiresRestart = true)
class BanAbsentTest: Plugin(){
	override fun start(pluginContext: Context){
		patcher.before<UserProfileAdminView>(
			"updateView",
			ViewState::class.java
		){(frame, state: ViewState) ->
			if(state.isMe() || state.isAdminSectionEnabled() || state.getShowBanButton()) return@before;
			frame.args[0] = state.reconstruct(
				5 to true,
				11 to true
			);
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};