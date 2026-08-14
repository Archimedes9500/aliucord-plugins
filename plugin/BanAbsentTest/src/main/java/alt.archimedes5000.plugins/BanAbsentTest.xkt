package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel;
import com.discord.widgets.user.profile.UserProfileAdminView.ViewState;
import com.discord.utilities.permissions.ManageUserContext;
import com.discord.stores.StoreBans;
import com.discord.stores.StoreStream;

@AliucordPlugin(requiresRestart = true)
class BanAbsentUsers: Plugin(){
	val WidgetUserSheetViewModel.guildId: Long by accessField();
	val WidgetUserSheetViewModel.userId: Long by accessField();

	override fun start(pluginContext: Context){
		patcher.patch(
			WidgetUserSheetViewModel::class.java
			.declaredMethods.filter{it.name == "createAdminViewState"}.single(),
			Hook{frame ->
				with(frame.thisObject as WidgetUserSheetViewModel){
					val state = frame.result as ViewState;

					if(state.isMe || state.isAdminSectionEnabled) return@Hook;
	
					val userContext = frame.args[3] as ManageUserContext?;
					logger.debug("$userContext");

					val bans = StoreBans.`access$getBannedUsers$p`(
						StoreStream.getBans()
					)?.get(guildId) as? Map<Long, *>;
					val userBanned = (bans?.containsKey(userId) == true);

					frame.result = state.reconstruct(
						5 to (!userBanned && (userContext?.canBan?: state.showBanButton)),
						11 to (!userBanned && (userContext?.canBan?: state.isAdminSectionEnabled))
					);
				};
			}
		);
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};
