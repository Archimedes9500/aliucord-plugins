package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.utilities.permissions.PermissionUtils;
import com.discord.api.role.GuildRole;
import com.discord.models.member.GuildMember;

import com.discord.stores.StoreStream;
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel;
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel.StoreState;
import com.discord.widgets.user.profile.UserProfileAdminView.ViewState;
import com.discord.stores.StoreBans;
import com.discord.utilities.permissions.ManageUserContext;
import com.discord.api.permission.Permission;

@AliucordPlugin(requiresRestart = true)
class BanAbsentUsers: Plugin(){
	val WidgetUserSheetViewModel.storeState: StoreState by
		accessField("mostRecentStoreState")
	;
	val PermissionUtils.applyEveryone by
		accessMethod<Function2<Long, Map<Long,GuildRole>, *>, Long>()
	;
	val PermissionUtils.applyRoles by
		accessMethod<Function3<GuildMember, Map<Long,GuildRole>, Long, *>, Long>()
	;
	val permissionUtils = PermissionUtils.INSTANCE;

	override fun start(pluginContext: Context){
		val me = StoreStream.getUsers().me;
		val myId = me.id;
		val storeGuilds = StoreStream.getGuilds();

		patcher.patch(
			WidgetUserSheetViewModel.ViewState::class.java
			.declaredMethods.filter{it.name == "createAdminViewState"}.single(),
			Hook{(frame, ) ->
				val state = frame.result as ViewState;
				if(state.isMe || state.isAdminSectionEnabled) return@after;

				val userContext = frame.args[3] as ManageUserContext;

				with(frame.thisObject as WidgetUserSheetViewModel.ViewState){
					val bans = StoreBans.`access$getBannedUsers$p`(
						StoreStream.getBans()
					)?.get(guildId) as? Map<Long, *>;
					val userBanned = (bans?.containsKey(user.id) == true);

					frame.result = state.reconstruct(
						5 to (!userBanned && userContext.canBan),
						11 to (!userBanned && userContext.canBan)
					);
				};
			}
		);
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};
