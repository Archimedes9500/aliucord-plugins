package alt.archimedes5000.plugins;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.annotation.SuppressLint;
import android.content.Context;
import com.aliucord.patcher.PreHook;
//import com.discord.api.message.Message;
import com.discord.models.message.Message;
import com.discord.stores.StoreMessages;
import com.aliucord.Logger;
import com.aliucord.utils.ReflectUtils;
import java.lang.reflect.Constructor;

import com.discord.utilities.textprocessing.DiscordParser;
import android.content.Context;
import b.a.t.b.b.b;
import b.a.t.b.b.e;
import com.discord.simpleast.core.node.Node;
import com.discord.simpleast.core.parser.Parser;
import com.discord.utilities.textprocessing.node.EditedMessageNode;
import com.discord.utilities.textprocessing.node.ZeroSpaceWidthNode;
import com.facebook.drawee.span.DraweeSpanStringBuilder;
import d0.z.d.m;
import java.util.List;

@AliucordPlugin(requiresRestart = false)
public class BetterReplaceText extends Plugin {
	@SuppressLint("SetTextI18n")
    @Override
	public void start(Context pluginContext) throws Throwable{
		patcher.patch(
			DiscordParser.class
			.getDeclaredMethod(
				"parseChannelMessage",
				Context.class,
				String.class,
				MessageRenderContext.class,
				MessagePreprocessor.class,
				ParserOptions.class,
				boolean.class
			),
			new PreHook(frame -> {
				DiscordParser parser = (DiscordParser)frame.thisObject;
				frame.args[1] = "https://youtu.be/7MHYrDk2rG0";
				new Logger().debug(frame.thisObject.toString());
			})
		);
	}
	@Override
	public void stop(Context pluginContext){
		patcher.unpatchAll();
	};
}