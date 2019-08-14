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

public class LoadableLoader {
	private static ArrayList<Loadable> loadables;
	
	public static void preInit() {
		loadables = new ArrayList();

		//Base Modules for JST content.
		addModule(new ValueLoader());
		addModule(new OreDictLoader());
		addModule(new RecipeLoader());
		addModule(new MRecipeLoader());
		
		addModule(new CompatIC2());
		//addModule(new CompatGTCE());
		addModule(new CompatIE());
		addModule(new CompatForestry());
		addModule(new CompatThEx());
		addModule(new CompatTiC());
		addModule(new CompatBWM());
		addModule(new CompatRC());
		addModule(new CompatTC6());
		addModule(new CompatIFG());
		addModule(new CompatAE2());
		addModule(new CompatAR());
		addModule(new CompatTAN());
		addModule(new CompatEIO());
		addModule(new CompatBOP());
		addModule(new CompatCT());
		addModule(new CompatTOP());
		
		addModule(new OreDictRecipeAdder());
		
	    for (Loadable m : loadables) {
	    	try {
	    		m.preInit();
	    	} catch (Throwable t) {
	    		JSTUtils.LOG.error("An error occurred while PreInit in " + m.getClass().getName());
	    		t.printStackTrace();
	    	}
	    }
	}
	
	public static void init() {
	    for (Loadable m : loadables) {
	    	try {
	    		m.init();
	    	} catch (Throwable t) {
	    		JSTUtils.LOG.error("An error occurred while Init in " + m.getClass().getName());
	    		t.printStackTrace();
	    	}
	    }
	}
	
	public static void postInit() {
	    for (Loadable m : loadables) {
	    	try {
	    		m.postInit();
	    	} catch (Throwable t) {
	    		JSTUtils.LOG.error("An error occurred while PostInit in " + m.getClass().getName());
	    		t.printStackTrace();
	    	}
	    }
		loadables = null;
	}
	
	public static void addModule(Loadable par1) {
	    if (loadables != null && par1.canLoad())
	    	loadables.add(par1);
	}
}
