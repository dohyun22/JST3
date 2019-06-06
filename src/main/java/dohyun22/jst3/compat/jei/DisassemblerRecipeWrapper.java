package dohyun22.jst3.compat.jei;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTUtils;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class DisassemblerRecipeWrapper extends GenericRecipeWrapper {
	
	public DisassemblerRecipeWrapper(RecipeContainer rec) {
		super(rec);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		FontRenderer fr = minecraft.fontRenderer;
		long e = this.container.getEnergyPerTick() * this.container.getDuration();
		String str = I18n.format("jst.compat.jei.total") + " " + e + " EU / " +  (e * JSTCfg.RFPerEU) + " RF";
	    fr.drawString(str, -10, 10, 0);
		
	    e = this.container.getEnergyPerTick();
		str = I18n.format("jst.tooltip.energy.volt") + " " + e + " EU / " +  (e * JSTCfg.RFPerEU) + " RF";
	    fr.drawString(str, -10, 20, 0);
	    
	    e = container.getDuration();
	    str = I18n.format("jst.compat.jei.time", Long.valueOf(e / 1200).toString(), Float.valueOf(e % 1200 / 20.0F).toString());
	    fr.drawString(str, -10, 107, 0);
	    
	    int t = JSTUtils.getTierFromVolt(this.container.getEnergyPerTick());
		str = I18n.format("jst.tooltip.energy.tier") + " " + t + " (" + JSTUtils.getTierName(t) + ")";
	    fr.drawString(str, -10, 117, 0);
	}
	
	@Override
	public int getOutputNum() {
		return 9;
	}
	
	public static List<DisassemblerRecipeWrapper> make() {
	    List<DisassemblerRecipeWrapper> ret = new LinkedList();
	    for (RecipeContainer rec : MRecipes.DisassemblerRecipes.list)
	    	ret.add(new DisassemblerRecipeWrapper(rec));
	    return ret;
	}
}
