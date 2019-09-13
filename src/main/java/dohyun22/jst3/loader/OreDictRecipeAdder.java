package dohyun22.jst3.loader;

import dohyun22.jst3.compat.ic2.CompatIC2;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.api.recipe.AdvRecipeItem;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.recipes.ItemList;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictRecipeAdder extends Loadable {

	@Override
	public String getRequiredMod() {
		return null;
	}

	@Override
	public void postInit() {
		String[] ores = OreDictionary.getOreNames();
		for (int i = 0; i < ores.length; i++) {
			String s = ores[i];
			if (s == null) continue;
			if (s.startsWith("ore")) {
				addOreRecipe(s.substring(3, s.length()), 0);
			} else if (s.startsWith("ingot")) {
				addOreRecipe(s.substring(5, s.length()), 1);
			} else if (s.startsWith("gem")) {
				addOreRecipe(s.substring(3, s.length()), 2);
			} else if (s.startsWith("plate")) {
				addOreRecipe(s.substring(5, s.length()), 3);
			} else if (s.startsWith("stick")) {
				addOreRecipe(s.substring(5, s.length()), 4);
			}
		}
	}
	
	private static void addOreRecipe(String name, int mode) {
		String prefix = "";
		int mult = 1;
		switch (mode) {
		case 0:
			prefix = "ore";
			switch (name) {
			case "Redstone":
			case "Nikolite":
				mult = 10;
				break;
			case "Sodalite":
			case "Lapis":
			case "Apatite":
				mult = 12;
				break;
			case "Coal":
				mult = 3;
				break;
			default:
				mult = 2;
			}
			break;
		case 1:
			prefix = "ingot";
			break;
		case 2:
			prefix = "gem";
			break;
		case 3:
			prefix = "plate";
			break;
		case 4:
			OreDictStack ods = new OreDictStack("ingot" + name);
			if (!JSTUtils.oreValid(ods))
				ods = new OreDictStack("gem" + name);
			if (JSTUtils.oreValid(ods))
				MRecipes.addPressRecipe(ods, ItemList.molds[2], JSTUtils.getFirstItem("stick" + name, 2), null, 10, 64);
			return;
		default:
			return;
		}
	    ItemStack crushed = JSTUtils.getFirstItem("crushed" + name);
	    ItemStack dust = JSTUtils.getFirstItem("dust" + name);
	    ItemStack gem = isDustOnly(name) ? ItemStack.EMPTY : JSTUtils.getFirstItem("gem" + name);
	    String str = prefix + name;
		if (JSTUtils.oreValid(str)) {
		    if ((mode == 0 ? crushed.isEmpty() && gem.isEmpty() && dust.isEmpty() : dust.isEmpty())) return;
		    ItemStack ret = mode == 0 ? crushed.isEmpty() ? gem.isEmpty() ? dust : gem : crushed : dust;
	    	ret = ret.copy();
	    	ret.setCount(mult);
	    	if (JSTCfg.ic2Loaded) CompatIC2.addGrindRec(new OreDictStack(str), ret);
	    	if (mode == 1) {
	    		ItemStack pl = JSTUtils.getFirstItem("plate" + name);
	    		if (!pl.isEmpty()) {
	    			OreDictStack ods = new OreDictStack(str);
	    			MRecipes.addPressRecipe(ods, ItemList.molds[1], pl, null, 10, 64);
	    			if (JSTCfg.ic2Loaded) CompatIC2.addFormerRecipe(ods, pl, 2);
	    		}
	    	}
	    }
	}
	
	private static boolean isDustOnly(String name) {
		return "Redstone".equals(name) || "Nikolite".equals(name) || "Glowstone".equals(name) || "Teslatite".equals(name);
	}
}
