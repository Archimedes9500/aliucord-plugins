package alt.archimedes5000.plugins;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.annotation.SuppressLint;
import android.content.Context;
import com.aliucord.patcher.before;
import com.discord.views.sticker.StickerView;
import com.discord.api.sticker.BaseSticker;
import com.discord.api.sticker.Sticker;
import com.discord.api.sticker.StickerFormatType;
import com.discord.api.sticker.StickerPartial;
import com.discord.rlottie.RLottieImageView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.drawable.`ScalingUtils$ScaleType`;
import com.discord.utilities.rx.ObservableExtensionsKt;
import com.discord.utilities.stickers.StickerUtils;
import com.discord.rlottie.RLottieImageView;
import com.discord.stores.StoreStream;
import com.discord.stores.StoreUserSettings;
import d0.z.d.m;
import java.lang.Object;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlinx.coroutines.Job;
import rx.Observable;
import rx.Subscription;

AliucordPlugin(requiresRestart = true)
class GifStickersFix:Plugin(){
	@SuppressLint("SetTextI18n")
	override fun start(pluginContext:Context){
		patcher.before<StickerView>(
			"d",
			BaseSticker::class.java,
			Int::class.java
		)balls@{
			frame ->
			val baseSticker = frame.args[0] as BaseSticker;
			val num = frame.args[1] as Int?;

			val baseSticker2: BaseSticker? = this.k;
			if(baseSticker2 != null && baseSticker2.d() == baseSticker.d()){
				if(this.l != null){
					return@balls;
				}
			}
			val baseSticker3: BaseSticker? = this.k;
			if(baseSticker3 != null && (baseSticker3 == null || baseSticker3.d() != baseSticker.d())){
				val subscription: Subscription = this.l;
				if (subscription != null) {
					subscription.unsubscribe();
				}
				this.l = null;
			}
			this.k = baseSticker;
			val ordinal: Int? = baseSticker.a().ordinal();
			if(ordinal == 4){
				val simpleDraweeView5: SimpleDraweeView = this.j.f188b;
				m.checkNotNullExpressionValue(simpleDraweeView5, "binding.stickerViewImageview");
				simpleDraweeView5.setVisibility(0);
				val imageView2: ImageView = this.j.d;
				m.checkNotNullExpressionValue(imageView2, "binding.stickerViewPlaceholder");
				imageView2.setVisibility(0);
				val rLottieImageView2: RLottieImageView = this.j.c;
				m.checkNotNullExpressionValue(rLottieImageView2, "binding.stickerViewLottie");
				rLottieImageView2.setVisibility(8);
				this.j.f188b.setImageDrawable(null);
				val simpleDraweeView6: SimpleDraweeView = this.j.f188b;
				m.checkNotNullExpressionValue(simpleDraweeView6, "binding.stickerViewImageview");
				val hierarchy2: GenericDraweeHierarchy = simpleDraweeView6.getHierarchy();
				m.checkNotNullExpressionValue(hierarchy2, "binding.stickerViewImageview.hierarchy");
				val `scalingUtils$ScaleType2`: `ScalingUtils$ScaleType` = `ScalingUtils$ScaleType`.a;
				hierarchy2.n(v.l);
				val stickerUtils: StickerUtils = StickerUtils.INSTANCE;
				val context: Context = getContext();
				val j: Observable = Observable.j(ObservableExtensionsKt.`restSubscribeOn$default`(stickerUtils.fetchSticker(context, baseSticker), false, 1, null), StoreUserSettings.`observeStickerAnimationSettings$default`(StoreStream.Companion.getUserSettings(), false, 1, null), a.j);
				m.checkNotNullExpressionValue(j, "Observable.combineLatest…lobalAnimationSettings) }");
				ObservableExtensionsKt.`appSubscribe$default`(ObservableExtensionsKt.ui(j), StickerView.class.java, null as Context?, new b.a.y.q0.b(this), null as Function1, null as Function0, null as Function0, new d(this, num, baseSticker), 58, null as Object);
		
				it.result = null;
			}else{
				//balls
			}
		}
	}
	override fun stop(pluginContext:Context) = patcher.unpatchAll();
}