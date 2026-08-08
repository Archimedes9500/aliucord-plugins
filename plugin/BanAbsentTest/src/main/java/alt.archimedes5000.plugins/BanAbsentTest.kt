package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel;
import com.discord.widgets.user.profile.UserProfileAdminView.ViewState;
import com.discord.stores.StoreBans;
import com.discord.stores.StoreStream;

@AliucordPlugin(requiresRestart = true)
class BanAbsentUsers: Plugin(){
	override fun start(pluginContext: Context){
		patcher.after<WidgetUserSheetViewModel.ViewState.Loaded>(
			"getAdminViewState"
		){frame ->
			val state = frame.result as ViewState;
			if(state.isMe || state.isAdminSectionEnabled) return@after;
/*
			val bans = StoreBans.`access$getBannedUsers$p`(
				StoreStream.getBans()
			)[guildId] as Map<Long, *>; 
			val banned = (bans?.containsKey(user.id) == true);
*/
			frame.result = state.reconstruct(
				5 to true,//!banned,
				11 to true//!banned
			).also{
				logger.debug(it.toString());
			};
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};