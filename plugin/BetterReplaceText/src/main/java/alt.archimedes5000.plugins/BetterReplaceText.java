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
				Message message = data.getMessage();
				frame.args[0] = new ParserData(
					data.component1(),
					data.component2(),
					data.component3(),
					data.component4(),
					data.component5(),
					data.component6(),
					new Message(
						message.component1(),
						message.component2(),
						message.component3(),
						message.component4(),
						"https://youtu.be/7MHYrDk2rG0",
						message.component6(),
						message.component7(),
						message.component8(),
						message.component9(),
						message.component10(),
						message.component11(),
						message.component12(),
						message.component13(),
						message.component14(),
						message.component15(),
						message.component16(),
						message.component17(),
						message.component18(),
						message.component19(),
						message.component20(),
						message.component21(),
						message.component22(),
						message.component23(),
						message.component24(),
						message.component25(),
						message.component26(),
						message.component27(),
						message.component28(),
						message.component29(),
						message.component30(),
						message.component31(),
						message.component32(),
						message.component33(),
						message.component34(),
						message.component35(),
						message.component36(),
						message.component37(),
						"aaa",
						message.component38()
					),
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