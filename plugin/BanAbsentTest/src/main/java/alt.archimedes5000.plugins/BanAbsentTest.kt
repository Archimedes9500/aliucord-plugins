package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.utilities.permissions.PermissionUtils;
import com.discord.api.role.GuildRole;

import com.discord.stores.StoreStream;
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel;
import com.discord.widgets.user.profile.UserProfileAdminView.ViewState;
import com.discord.stores.StoreBans;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.api.permission.Permission;

@AliucordPlugin(requiresRestart = true)
class BanAbsentUsers: Plugin(){
	val PermissionUtils.applyEveryone by
		accessMethod<Function2<Long, Map<Long,GuildRole>, *>, Long>()
	;
	val PermissionUtils.applyRoles by
		accessMethod<Function2<GuildMember, Map<Long,GuildRole,Long>, *>, Long>()
	;

	override fun start(pluginContext: Context){
		val myId = StoreStream.getUsers().me.id;
		val storeGuilds = StoreStream.getGuilds();

		patcher.after<WidgetUserSheetViewModel.ViewState.Loaded>(
			"getAdminViewState"
		){frame ->
			val state = frame.result as ViewState;
			if(state.isMe || state.isAdminSectionEnabled) return@after;

			val bans = StoreBans.`access$getBannedUsers$p`(
				StoreStream.getBans()
			)?.get(guildId) as? Map<Long, *>;
			val userBanned = (bans?.containsKey(user.id) == true);

			val meMember = storeGuilds.getMember(guildId, myId);
			val guildRoles = storeGuilds
				.`getGuildRolesInternal$app_productionGoogleRelease`()
				.get(guildId)
			;
			val everyonePerms = PermissionUtils.applyEveryone(guildId, guildRoles);
			val perms = PermissionUtils.applyRoles(
				meMember,
				guildRoles,
				everyonePerms
			);
			val canBan = PermissionUtils.can(Permission.BAN_MEMBERS, perms);

			frame.result = state.reconstruct(
				5 to (!userBanned && canBan),
				11 to (!userBanned && canBan)
			);
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};

