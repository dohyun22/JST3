package dohyun22.jst3.loader;

import javax.annotation.Nullable;

import net.minecraftforge.fml.common.Loader;

public abstract class Loadable {

	public boolean canLoad() {
		String s = getRequiredMod();
		return s == null || Loader.isModLoaded(s);
	}

	/** if it returns null, the module will not require any MODs to operate.*/
	@Nullable
	public abstract String getRequiredMod();

	public void preInit() {}

	public void init() {}

	public void postInit() {}
}
