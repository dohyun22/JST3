package dohyun22.jst3.compat.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.energy.MT_StirlingGen;
import dohyun22.jst3.utils.JSTUtils;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemFuelRecipeWrapper implements IRecipeWrapper {
	@Nonnull
	protected final List<List<ItemStack>> stack;
	protected final int energy;
	protected final boolean isComp;

	private ItemFuelRecipeWrapper(@Nonnull List<List<ItemStack>> in, int ev, boolean cv) {
		stack = in;
		energy = ev;
		isComp = cv;
	}

	@Override
	public void getIngredients(IIngredients ing) {
		ing.setInputLists(ItemStack.class, stack);
	}

	public static List<ItemFuelRecipeWrapper> make(HashMap<Object, Integer> recipe, boolean cv) {
	    List<ItemFuelRecipeWrapper> ret = new LinkedList();
	    for (Entry<Object, Integer> e : recipe.entrySet()) {
	    	Object k = e.getKey();
	    	Integer v = e.getValue();
	    	if (k == null || v == null) continue;
	    	List<ItemStack> stl = new ArrayList();
			if (k instanceof OreDictStack) {
				for (ItemStack st : OreDictionary.getOres(((OreDictStack)k).name)) {
					ItemStack st2 = st.copy();
					st2.setCount(((OreDictStack)k).count);
					stl.add(st2);
				}
			} else if (k instanceof ItemStack) {
				stl.add((ItemStack) k);
			}
			List<List<ItemStack>> ls2 = new ArrayList();
			ls2.add(stl);
	    	if (stl != null)
	    		ret.add(new ItemFuelRecipeWrapper(ls2, v.intValue(), cv));
	    }
	    return ret;
	}
	
	public static List<ItemFuelRecipeWrapper> makeFGFuelList(IModRegistry rg) {
		List<ItemStack> ls = rg.getIngredientRegistry().getFuels();
		List<ItemFuelRecipeWrapper> ret = new LinkedList();
		for (ItemStack st : ls) {
			int bt = MT_StirlingGen.getFuelValue(st);
			if (bt > 0) {
				List<List<ItemStack>> ls2 = new ArrayList();
				List<ItemStack> ls3 = new ArrayList();
				ls3.add(st);
				ls2.add(ls3);
				ret.add(new ItemFuelRecipeWrapper(ls2, (int)(bt * 2.5), false));
			}
		}
		return ret;
	}
	
	@Override
	public void drawInfo(Minecraft mc, int w, int h, int mx, int my) {
		String s = energy < 0 ? I18n.format("jst.msg.com.blacklist") : isComp ? Integer.toString(energy) : energy + " EU / " +  (energy * JSTCfg.RFPerEU) + " RF";
		mc.fontRenderer.drawString(s, 36, 9, 0);
	}
}
