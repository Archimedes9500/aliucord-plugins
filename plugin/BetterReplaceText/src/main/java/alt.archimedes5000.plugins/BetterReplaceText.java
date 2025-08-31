package alt.archimedes5000.plugins;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.annotation.SuppressLint;
import android.content.Context;
import com.aliucord.patcher.Hook;
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
import com.discord.api.user.User;
import com.discord.api.utcdatetime.UtcDateTime;
import java.util.List;

@AliucordPlugin(requiresRestart = false)
public class BetterReplaceText extends Plugin {
	@SuppressLint("SetTextI18n")
    @Override
	public void start(Context pluginContext) throws Throwable{
		patcher.patch(
			Message.class
				.getDeclaredConstructor(
					long.class,
					long.class,
					User.class,
					String.class,
					UtcDateTime.class,
					UtcDateTime.class,
					Boolean.class,
					Boolean.class,
					List.class,
					List.class,
					List.class,
					List.class,
					List.class,
					String.class,
					Boolean.class,
					Long.class,
					Integer.class,
					MessageActivity.class,
					Application.class,
					Long.class,
					MessageReference.class,
					Long.class,
					List.class,
					List.class,
					Message.class,
					Interaction.class,
					Channel.class,
					List.class,
					MessageCall.class,
					Long.class,
					GuildMember.class,
					Boolean.class,
					int.class
				)
			,
			new Hook(frame -> {
				Message message = (Message)frame.thisObject;
				try{
					ReflectUtils.setField(
						message,
						"content",
						frame.args[3]+"aaa"
					);
					//frame.setResult(message);
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