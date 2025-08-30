package alt.archimedes5000.plugins;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.annotation.SuppressLint;
import android.content.Context;
import com.aliucord.patcher.patch;
import com.aliucord.patcher.PreHook;
import com.discord.api.message.Message;
import com.discord.models.message.Message as MessageModel;
import com.discord.stores.StoreMessages;
import com.aliucord.Logger;

@AliucordPlugin(requiresRestart = false)
public class BetterTextReplace extends Plugin {
	@SuppressLint("SetTextI18n")
    @Override
	public void start(Context pluginContext) throws Throwable{
		patcher.patch(
			Message.class
			.getDeclaredMethod(
				"Message",
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
			),
			new PreHook(frame -> {
				Message this = (Message)frame.thisObject;
				//this.content = Utils.replace(frame.args[3]);
				this.content = frame.args[3]+"aaa";
				Logger().debug(this.toString)
			})
		);
	}
	@Override
	public void stop(Context pluginContext){
		patcher.unpatchAll();
	};
}