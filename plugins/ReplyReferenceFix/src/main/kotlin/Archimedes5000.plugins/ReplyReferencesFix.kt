package Archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.patcher.Hook
import com.aliucord.Utils
import com.aliucord.Utils.showToast
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

@AliucordPlugin(requiresRestart = false)
class ReplyReferencesFix: Plugin(){
	@SuppressLint("SetTextI18n")
	override fun start(context: Context){
		
	}
	override fun stop(context: Context) = patcher.unpatchAll();
}