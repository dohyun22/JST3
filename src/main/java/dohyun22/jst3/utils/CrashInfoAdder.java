package dohyun22.jst3.utils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CrashInfoAdder implements ICrashCallable {
	private static boolean loaded;
	private static final String KRMessage = "\uC774\uB7F0! \uD06C\uB798\uC26C\uAC00 \uB09C\uAC83 \uAC19\uAD70\uC694! :( \uC774 \uC624\uB958 \uB85C\uADF8 \uC804\uCCB4\uB97C \uCE74\uD398\uC5D0 \uC788\uB294 \uBC84\uADF8 \uC2E0\uACE0 \uAC8C\uC2DC\uD310\uC5D0 \uC62C\uB824\uC8FC\uC138\uC694!"+
			"\n\thttp://cafe.naver.com/justservero" + 
			"\n\t\u203B\uD06C\uB798\uC26C \uB9AC\uD3EC\uD2B8\uB97C \uC62C\uB9B4\uB54C \uBAA8\uB4DC\uD329 \uBC84\uC804\uACFC \uC0AC\uC591\uC5D0 \uB9DE\uAC8C \uB9D0\uBA38\uB9AC\uB97C \uAF2D \uC9C0\uC815\uD574\uC8FC\uC138\uC694!"+
			"\n\tP.S \uD06C\uB798\uC26C \uBCF4\uACE0\uB97C \uD560 \uB54C \uC77C\uBD80 \uBAA8\uB4DC\uC758 \uD06C\uB798\uC26C \uBCF4\uACE0\uB97C \uAE08\uC9C0\uD558\uB294 \uACBD\uACE0\uBB38(\uC608: DO NOT REPORT THIS CRASH!)\uC740 \uBB34\uC2DC\uD574\uC8FC\uC138\uC694."+
			"\n\t\uC800\uD76C\uC5D0\uAC8C \uD06C\uB798\uC26C \uBCF4\uACE0\uB97C \uD558\uC9C0 \uB9D0\uB77C\uB294 \uB73B\uC774 \uC544\uB2D9\uB2C8\uB2E4.";
	private static final String ENMessage = "Uh oh, It looks like your game has been crashed :( please report this to Team Divine instead of contacting MOD authors." +
			"\n\tP.S Please ignore warning messages that prohibits crash reporting(ex. DO NOT REPORT THIS CRASH!) created by other MODs when reporting this crash." + 
			"\n\tIt does not mean that you should not report this crash to us.";
	
	public CrashInfoAdder() {
		if (!loaded) FMLCommonHandler.instance().registerCrashCallable(this);
		loaded = true;
	}
	
	@Override
	public String call() throws Exception {
		return "ko_kr".equals(JSTUtils.getLang()) ? KRMessage : ENMessage;
	}
	
	@Override
	public String getLabel() {
		return "JST3";
	}
}