package dohyun22.jst3.compat.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.compat.jei.GenericCategory.GenericTooltipCallback;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTUtils;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.gui.elements.DrawableResource;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;

public class RefineryCategory extends GenericCategory {
	private final IDrawable alt;

	public RefineryCategory() {
		super(MRecipes.RefineryRecipes);
		alt = new DrawableResource(JEISupport.TEX_LOC, 166, 0, 72, 36, 0, 50, 94, 0, 256, 256);
	}

	@Override
	public void setRecipe(IRecipeLayout rlo, GenericRecipeWrapper rw, IIngredients in) {
        IGuiItemStackGroup gis = rlo.getItemStacks();
        IGuiFluidStackGroup gfs = rlo.getFluidStacks();
		List<List<FluidStack>> fa = in.getInputs(FluidStack.class);
		List<FluidStack> fs = JEISupport.getFluidFromList(fa, 0);
		if (fs != null && !fs.isEmpty() && fs.get(0) != null)
			gfs.init(0, true, 1, 1, 16, 16, fs.get(0).amount, false, null);
        if (rw instanceof RefineryRecipeWrapper) {
    		fa = in.getOutputs(FluidStack.class);
    		int cnt = 0;
   			for (int y = 0; y < 2; y++) {
	    		for (int x = 0; x < 4; x++) {
	    			if ((((RefineryRecipeWrapper)rw).enabled >> (x + y * 4) & 1) == 1) {
	    				fs = JEISupport.getFluidFromList(fa, cnt);
	    				if (fs != null && !fs.isEmpty() && fs.get(0) != null)
	    					gfs.init(cnt + 1, false, x * 18 + 95, y * 18 + 1, 16, 16, fs.get(0).amount, false, null);
	    				cnt++;
	    			}
	            }
   			}
        }
        gis.init(0, true, 0, 39);
        for (int x = 0; x < 4; x++)
        	gis.init(x + 1, false, x * 18 + 94, 38);
        gis.set(in);
        gfs.set(in);
        gis.addTooltipCallback(new GenericTooltipCallback(rw.container, 0));
	}

	@Override
	public void drawExtras(Minecraft mc) {
		alt.draw(mc);
	}
}
