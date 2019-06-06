package dohyun22.jst3.compat;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;

public class CompatGTCE extends Loadable {
	
	@Override
	public boolean canLoad() {
		return JSTCfg.gtceLoaded;
	}

	@Override
	public String getRequiredMod() {
		return null;
	}
}