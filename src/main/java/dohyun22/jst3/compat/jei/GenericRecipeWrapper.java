package dohyun22.jst3.compat.jei;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.api.recipe.IRecipeItem;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import mezz.jei.Internal;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class GenericRecipeWrapper implements IRecipeWrapper {
	protected final RecipeContainer container;
	
	public GenericRecipeWrapper(RecipeContainer rec) {
		this.container = rec;
	}
	    
	@Override
	public void getIngredients(IIngredients ing) {
		ing.setInputLists(ItemStack.class, getInputs());
		ing.setInputLists(FluidStack.class, this.getFluidInputs());
		ing.setOutputs(ItemStack.class, this.getOutputs());
		ing.setOutputs(FluidStack.class, this.getFluidOutputs());
	}
	    
	public List<List<ItemStack>> getInputs() {
		List<List<ItemStack>> ret = new LinkedList<List<ItemStack>>();
		Object[] objlist = this.container.getInputItems();
		for (int n = 0; n < getInputNum(); n++) {
			LinkedList<ItemStack> al = new LinkedList();
			if (objlist == null || n >= objlist.length) {
				ret.add(al);
				continue;
			}
			Object obj = objlist[n];
			if (obj == null) {
				ret.add(al);
				continue;
			} else if (obj instanceof ItemStack) {
				al.add((ItemStack) obj);
			} else if (obj instanceof IRecipeItem) {
				al.addAll(((IRecipeItem)obj).getAllMatchingItems());
			}
			ret.add(al);
		}
		return ret;
	}
	    
	public List<ItemStack> getOutputs() {
		LinkedList<ItemStack> ret = new LinkedList();
		ItemStack[] il = this.container.getOutputItems();
		for (int n = 0; n < getOutputNum(); n++) {
			if (il == null || n >= il.length)
				break;
			ret.add(il[n]);
		}
		return ret;
	}
	    
	public List<List<FluidStack>> getFluidInputs() {
		List<List<FluidStack>> ret = new LinkedList<List<FluidStack>>();
		FluidStack[] fsl = this.container.getInputFluids();
		for (int n = 0; n < getFInputNum(); n++) {
			List<FluidStack> al = new LinkedList();
			if (fsl != null && n < fsl.length)
				if (fsl[n] != null)
					al.add(fsl[n]);
			ret.add(al);
		}
		return ret;
	}
	    
	public List<FluidStack> getFluidOutputs() {
		LinkedList<FluidStack> ret = new LinkedList();
		FluidStack[] il = this.container.getOutputFluids();
		if (il != null)
			for (int n = 0; n < getFOutputNum(); n++)
				if (n < il.length && il[n] != null)
					ret.add(il[n]);
		return ret;
	}
	
	public int getInputNum() {
		return 8;
	}
	
	public int getOutputNum() {
		return 8;
	}
	
	public int getFInputNum() {
		return 4;
	}
	
	public int getFOutputNum() {
		return 4;
	}

	public static List<GenericRecipeWrapper> make(@Nonnull RecipeList rl) {
	    List<GenericRecipeWrapper> ret = new LinkedList();
	    for (RecipeContainer rec : rl.list)
	    	ret.add(new GenericRecipeWrapper(rec));
	    return ret;
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		FontRenderer fr = minecraft.fontRenderer;
		long e = this.container.getEnergyPerTick() * this.container.getDuration();
		String str = I18n.format("jst.compat.jei.total") + " " + e + " EU / " +  (e * JSTCfg.RFPerEU) + " RF";
	    fr.drawString(str, -8, 60, 0);
		
	    e = container.getEnergyPerTick();
		str = I18n.format("jst.tooltip.energy.volt") + " " + e + " EU / " +  (e * JSTCfg.RFPerEU) + " RF";
	    fr.drawString(str, -8, 70, 0);
	    
	    e = container.getDuration();
	    str = I18n.format("jst.compat.jei.time", Long.valueOf(e / 1200).toString(), Float.valueOf(e % 1200 / 20.0F).toString());
	    fr.drawString(str, -8, 80, 0);
	    
	    int t = JSTUtils.getTierFromVolt(this.container.getEnergyPerTick());
		str = I18n.format("jst.tooltip.energy.tier") + " " + t + " (" + JSTUtils.getTierName(t) + ")";
	    fr.drawString(str, -8, 90, 0);
	}
}
