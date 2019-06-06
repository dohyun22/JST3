package dohyun22.jst3.compat.jei;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.multiblock.MT_Fusion;
import dohyun22.jst3.utils.JSTUtils;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

public class FusionRecipeWrapper extends GenericRecipeWrapper {

	public FusionRecipeWrapper(RecipeContainer rec) {
		super(rec);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		FontRenderer fr = minecraft.fontRenderer;
		Object[] obj = this.container.getObj();
		if (obj == null || obj.length != 3 || !(obj[0] instanceof Integer) || !(obj[1] instanceof Integer) || !(obj[2] instanceof Boolean)) {
			fr.drawString("<RECIPE ERROR>", 10, 34, 0xFF0000);
		} else {
			String str = I18n.format("jst.compat.jei.fusion." + (((Boolean)obj[2]).booleanValue() ? "neut" : "prot"));
		    fr.drawString(str, 0, 30, 0);
			
		    int e = ((Integer)obj[0]).intValue();
		    String pf = e > 1000000.0D ? "M " :" ";
		    double d = e > 1000000.0D ? e / 1000000.0D : e;
			str = I18n.format("jst.compat.jei.fusion.startup")  + " " + conv(d) + pf + "EU / " +  conv(d * JSTCfg.RFPerEU) + pf + "RF";
		    fr.drawString(str, 0, 40, 0);
			
		    e = this.container.getEnergyPerTick();
			str = I18n.format("jst.msg.com.in") + " " + e + " EU / " +  (e * JSTCfg.RFPerEU) + " RF";
		    fr.drawString(str, 0, 50, 0);
		    
		    e = ((Integer)obj[1]).intValue();
		    str = I18n.format("jst.msg.com.out") + " " + e + " EU / " +  (e * JSTCfg.RFPerEU) + " RF";
		    fr.drawString(str, 0, 60, 0);
		    
		    e = e * this.container.getDuration();
		    pf = e > 1000000.0D ? "M " :" ";
		    d = e > 1000000.0D ? e / 1000000.0D : e;
			str = I18n.format("jst.compat.jei.totalgen") + " " + conv(d) + pf + "EU / " +  conv(d * JSTCfg.RFPerEU) + pf + "RF";
		    fr.drawString(str, 0, 70, 0);
		    
		    e = container.getDuration();
		    str = I18n.format("jst.compat.jei.time", Long.valueOf(e / 1200).toString(), Float.valueOf(e % 1200 / 20.0F).toString());
		    fr.drawString(str, 0, 80, 0);
		    
		    e = ((Integer)obj[0]).intValue();
			str = I18n.format("jst.tooltip.energy.tier") + " " + (e / MT_Fusion.ES + (e % MT_Fusion.ES == 0 ? 0 : 1));
		    fr.drawString(str, 0, 90, 0);
		}
	}

	public static List<FusionRecipeWrapper> make() {
	    List<FusionRecipeWrapper> ret = new LinkedList();
	    for (RecipeContainer rec : MRecipes.FusionRecipes.list)
	    	ret.add(new FusionRecipeWrapper(rec));
	    return ret;
	}
	
	private static String conv(double d) {
		if (Double.isInfinite(d) || Double.isNaN(d) || d > Long.MAX_VALUE || d < Long.MIN_VALUE) return Double.toString(d);
		if ((long)d == d) return Long.toString((long)d);
		return Double.toString(d);
	}
}
