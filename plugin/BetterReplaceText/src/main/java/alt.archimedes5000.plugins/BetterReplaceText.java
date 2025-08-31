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
		var c = Message.class
			.getDeclaredConstructors()[0]
		;
		if(c == null){
			new Logger().debug("aaa");
		}
		patcher.patch(
			c,
			new PreHook(frame -> {
				Message message = (Message)frame.thisObject;
				try{
					/*ReflectUtils.setField(
						message,
						"content",
						frame.args[3]+"aaa"
					);
					//frame.setResult(message);*/
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