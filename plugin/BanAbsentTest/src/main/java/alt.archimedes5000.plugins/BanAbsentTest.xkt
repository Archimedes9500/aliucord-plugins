package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel;
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel.StoreState;
import com.discord.widgets.user.profile.UserProfileAdminView.ViewState;
import com.discord.utilities.permissions.ManageUserContext;
import com.discord.stores.StoreBans;
import com.discord.stores.StoreStream;

@AliucordPlugin(requiresRestart = true)
class BanAbsentUsers: Plugin(){
	val WidgetUserSheetViewModel.guildId: Long by accessField();
	val WidgetUserSheetViewModel.userId: Long by accessField();
	val WidgetUserSheetViewModel.storeState: StoreState by accessField("mostRecentStoreState");
	var WidgetUserSheetViewModel.ViewState.userContext: ManageUserContext by accessField();

	override fun start(pluginContext: Context){
		patcher.patch(
			WidgetUserSheetViewModel::class.java
			.declaredMethods.filter{it.name == "createAdminViewState"}.single(),
			Hook{frame ->
				with(frame.thisObject as WidgetUserSheetViewModel){
					val state = frame.result as ViewState;

					if(state.isMe || state.isAdminSectionEnabled || storeState?.guild == null) return@Hook;
	
					var userContext = frame.args[3] as ManageUserContext?;
					if(userContext == null){
						val myRoles = StoreStream.getGuilds().getMember(guildId, storeState.me.id)?.roles;
						val userRoles = StoreStream.getGuilds().getMember(guildId, userId)?.roles;
						userContext = ManageUserContext.from(
							storeState.guild,
							storeState.me,
							storeState.user,
							myRoles?: emptyList<Long?>(),
							userRoles?: emptyList<Long?>(),
							storeState.permissions,
							storeState.guildRoles
						);
					};
					WidgetUserSheetViewModel.`access$getViewState$p`(this).userContext = userContext;

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
		patcher.after<WidgetUserSheetViewModel.ViewState.Loaded>(
			"getAdminViewState"
		){frame ->
			logger.debug("$frame.result\n\n$userContext");
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};
