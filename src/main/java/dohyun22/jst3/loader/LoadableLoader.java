package dohyun22.jst3.loader;

import java.util.ArrayList;

import dohyun22.jst3.compat.*;
import dohyun22.jst3.compat.ct.CompatCT;
import dohyun22.jst3.compat.forestry.CompatForestry;
import dohyun22.jst3.compat.ic2.CompatIC2;
import dohyun22.jst3.compat.ifg.CompatIFG;
import dohyun22.jst3.compat.rc.CompatRC;
import dohyun22.jst3.compat.tic.CompatTiC;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraftforge.fml.common.Loader;

public class LoadableLoader {
	private static ArrayList<Loadable> loadables;
	
	public static void preInit() {
		loadables = new ArrayList();

		//Base Modules for JST content.
		addModule(true, ValueLoader.class);
		addModule(true, OreDictLoader.class);
		addModule(true, RecipeLoader.class);
		addModule(true, MRecipeLoader.class);

		//Compat Modules for mod support.
		addModule(JSTCfg.ic2Loaded, CompatIC2.class);
		//addModule(JSTCfg.gtceLoaded, CompatGTCE.class);
		addModule("immersiveengineering", CompatIE.class);
		addModule("forestry", CompatForestry.class);
		addModule(JSTCfg.tfLoaded, CompatThEx.class);
		addModule(JSTCfg.ticLoaded, CompatTiC.class);
		addModule("betterwithmods", CompatBWM.class);
		addModule(JSTCfg.rcLoaded, CompatRC.class);
		addModule(JSTCfg.tcLoaded, CompatTC6.class);
		addModule("industrialforegoing", CompatIFG.class);
		addModule("appliedenergistics2", CompatAE2.class);
		addModule("advancedrocketry", CompatAR.class);
		addModule("galacticraftcore", CompatGC.class);
		addModule("toughasnails", CompatTAN.class);
		addModule("enderio", CompatEIO.class);
		addModule("biomesoplenty", CompatBOP.class);
		addModule("projectred-core", CompatPR.class);
		addModule("crafttweaker", CompatCT.class);
		addModule("theoneprobe", CompatTOP.class);
		
		addModule(true, OreDictRecipeAdder.class);
		
	    for (Loadable m : loadables) {
	    	try {
	    		m.preInit();
	    	} catch (Throwable t) {
	    		JSTUtils.LOG.error("An error occurred while PreInit in " + m.getClass().getName());
	    		JSTUtils.LOG.catching(t);
	    	}
	    }
	}
	
	public static void init() {
	    for (Loadable m : loadables) {
	    	try {
	    		m.init();
	    	} catch (Throwable t) {
	    		JSTUtils.LOG.error("An error occurred while Init in " + m.getClass().getName());
	    		JSTUtils.LOG.catching(t);
	    	}
	    }
	}
	
	public static void postInit() {
	    for (Loadable m : loadables) {
	    	try {
	    		m.postInit();
	    	} catch (Throwable t) {
	    		JSTUtils.LOG.error("An error occurred while PostInit in " + m.getClass().getName());
	    		JSTUtils.LOG.catching(t);
	    	}
	    }
		loadables = null;
	}
	
	public static void addModule(Object l, Class<? extends Loadable> c) {
		boolean flag = false;
		if (l instanceof Boolean)
			flag = (Boolean)l;
		else if (l instanceof String)
			flag = Loader.isModLoaded((String)l);
	    if (flag) {
	    	if (c != null)
				try {
					Loadable m = c.newInstance();
					loadables.add(m);
				} catch (Throwable t) {
					JSTUtils.LOG.error("Can't add module " + c);
					JSTUtils.LOG.catching(t);
				}
	    }
	}
}
