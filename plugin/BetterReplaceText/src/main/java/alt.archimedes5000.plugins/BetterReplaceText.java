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
import java.lang.reflect.Constructor;

import com.discord.widgets.chat.list.utils.EmbeddedMessageParser;
import com.discord.widgets.chat.list.utils.EmbeddedMessageParser.ParserData;

@AliucordPlugin(requiresRestart = false)
public class BetterReplaceText extends Plugin {
	@SuppressLint("SetTextI18n")
    @Override
	public void start(Context pluginContext) throws Throwable{
		patcher.patch(
			EmbeddedMessageParser.class
			.getDeclaredMethod(
				ParserData.class
			),
			new PreHook(frame -> {
				EmbeddedMessageParser parser = (EmbeddedMessageParser)frame.thisObject;
				ParserData data = (ParserData)frame.args[0];
				String content = data.getMessage().getContent();
				frame.args[0] = new ParserData(
					data.component1(),
					data.component2(),
					data.component3(),
					data.component4(),
					data.component5(),
					data.component6(),
					"https://youtu.be/7MHYrDk2rG0",
					data.component8()
				);
				new Logger().debug(frame.args[0].toString());
			})
		);
	}
	@Override
	public void stop(Context pluginContext){
		patcher.unpatchAll();
	};
}