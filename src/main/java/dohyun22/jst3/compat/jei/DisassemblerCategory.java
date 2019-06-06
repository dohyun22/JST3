package dohyun22.jst3.compat.jei;

import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.compat.jei.GenericCategory.GenericTooltipCallback;
import dohyun22.jst3.recipes.MRecipes;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.gui.elements.DrawableResource;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class DisassemblerCategory extends GenericCategory {
	
	public DisassemblerCategory() {
		super(MRecipes.DisassemblerRecipes);
	}

	@Override
	protected int[] getValues() {
		return new int[] {0, 92, 108, 54, 40, 40, 0, 0};
	}
	  
	@Override
	public void setRecipe(IRecipeLayout rlo, GenericRecipeWrapper rw, IIngredients in) {
        if (!(rw instanceof DisassemblerRecipeWrapper))
            return;

        IGuiItemStackGroup gis = rlo.getItemStacks();
        
		gis.init(0, true, 0, 58);
        
        for (int y = 0; y < 3; y++) {
        	for (int x = 0; x < 3; x++) {
        		int ix = x + y * 3 + 1;
        		gis.init(ix, false, x * 18 + 54, y * 18 + 40);
        	}
        }
        
        if (rw instanceof ICustomCraftingRecipeWrapper) {
        	ICustomCraftingRecipeWrapper customWrapper = (ICustomCraftingRecipeWrapper)rw;
        	customWrapper.setRecipe(rlo, in);
        	return;
        }
        
        gis.set(in);
        gis.addTooltipCallback(new GenericTooltipCallback(rw.container, 0));
	}
}
