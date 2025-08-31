package alt.archimedes5000.plugins;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.annotation.SuppressLint;
import android.content.Context;
import com.aliucord.patcher.PreHook;
import com.discord.api.message.Message;
//import com.discord.models.message.Message;
import com.discord.stores.StoreMessages;
import com.aliucord.Logger;
import com.aliucord.utils.ReflectUtils;

import com.discord.api.application.Application;
import com.discord.api.botuikit.Component;
import com.discord.api.channel.Channel;
import com.discord.api.guildmember.GuildMember;
import com.discord.api.interaction.Interaction;
import com.discord.api.message.activity.MessageActivity;
import com.discord.api.message.attachment.MessageAttachment;
import com.discord.api.message.call.MessageCall;
import com.discord.api.message.embed.MessageEmbed;
import com.discord.api.message.reaction.MessageReaction;
import com.discord.api.message.MessageReference;
import com.discord.api.user.User;
import com.discord.api.utcdatetime.UtcDateTime;
import java.util.List;

@AliucordPlugin(requiresRestart = false)
public class BetterReplaceText extends Plugin {
	@SuppressLint("SetTextI18n")
    @Override
	public void start(Context pluginContext) throws Throwable{
		patcher.patch(
			Message.class.getDeclaredConstructors()[0],
			new PreHook(frame -> {
				Message message = (Message)frame.thisObject;
				try{
					frame.args[3] = "aaa";
				}catch(Throwable e){
					new Logger().error(e);
				}
			})
		);
	}
	@Override
	public void stop(Context pluginContext){
		patcher.unpatchAll();
	};
}