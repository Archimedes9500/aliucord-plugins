package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.stores.StoreStream;
import com.discord.widgets.user.profile.UserProfileAdminView;
import com.discord.widgets.user.profile.UserProfileAdminView.ViewState;
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel;

@AliucordPlugin(requiresRestart = true)
class BanAbsentUsers: Plugin(){
	override fun start(pluginContext: Context){
		val storeBans = StoreStream.getBans();

		patcher.after<WidgetUserSheetViewModel.ViewState.Loaded>(
			"getAdminViewState"
		){frame ->
			val state = frame.result as ViewState;
			if(state.isMe || state.isAdminSectionEnabled) return@after;

			val bans = storeBans.`access$getBannedUsers$p`(storeBans)[guildId];
			frame.result = state.reconstruct(
				5 to (true && user !in bans),
				11 to (true && user !in bans)
			).also{
				logger.debug(it.toString());
			};
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};