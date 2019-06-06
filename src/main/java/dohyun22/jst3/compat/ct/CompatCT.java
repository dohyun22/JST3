package dohyun22.jst3.compat.ct;

import crafttweaker.CraftTweakerAPI;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;

public class CompatCT extends Loadable {

	@Override
	public String getRequiredMod() {
		return "crafttweaker";
	}

	@Override
	public void preInit() {
		CTSupport.init();
		CraftTweakerAPI.registerClass(CTSupport.class);
		if (JSTCfg.ic2Loaded) CraftTweakerAPI.registerClass(IC2Support.class);
	}
}
