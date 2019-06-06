package dohyun22.jst3.compat.jei;

import java.util.List;

import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.recipes.MRecipes;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.gui.elements.DrawableResource;
import net.minecraftforge.fluids.FluidStack;

public class FusionCategory extends GenericCategory {
	
	public FusionCategory() {
		super(MRecipes.FusionRecipes);
	}

	@Override
	protected int[] getValues() {
		return new int[] {36, 56, 76, 18, 10, 70, 50, 50};
	}
	
	@Override
	public void setRecipe(IRecipeLayout rlo, GenericRecipeWrapper rw, IIngredients in) {
        if (!(rw instanceof GenericRecipeWrapper))
            return;
        
        IGuiItemStackGroup gis = rlo.getItemStacks();
        IGuiFluidStackGroup gfs = rlo.getFluidStacks();
        
        List<FluidStack> fs;
        int amt;
        for (int x = 0; x < 4; x++) {
        	fs = JEISupport.getFluidFromList((List)in.getInputs(FluidStack.class), x);
	        amt = fs == null || fs.isEmpty() || fs.get(0) == null ? 1 : Math.min(fs.get(0).amount, 1);
	        gfs.init(x, true, x * 18 + 51, 11, 16, 16, amt, false, null);
        }
        
        fs = JEISupport.getFluidFromList((List)in.getOutputs(FluidStack.class), 0);
        amt = fs == null || fs.isEmpty() || fs.get(0) == null ? 1 : Math.min(fs.get(0).amount, 1);
        gfs.init(4, false, 109, 11, 16, 16, amt, false, null);
        
        if (rw instanceof ICustomCraftingRecipeWrapper) {
        	ICustomCraftingRecipeWrapper customWrapper = (ICustomCraftingRecipeWrapper)rw;
        	customWrapper.setRecipe(rlo, in);
        	return;
        }
        
        gis.set(in);
        gfs.set(in);
	}
}
