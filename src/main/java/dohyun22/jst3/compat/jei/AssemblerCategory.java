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
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.gui.elements.DrawableResource;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class AssemblerCategory extends GenericCategory {
	
	public AssemblerCategory() {
		super(MRecipes.AssemblerRecipes);
	}

	@Override
	protected int[] getValues() {
		return new int[] {54, 92, 122, 74, 30, 30, 0, 0};
	}

	@Override
	public void setRecipe(IRecipeLayout rlo, GenericRecipeWrapper rw, IIngredients in) {
        if (!(rw instanceof AssemblerRecipeWrapper))
            return;

        IGuiItemStackGroup gis = rlo.getItemStacks();
        IGuiFluidStackGroup gfs = rlo.getFluidStacks();
        
        for (int y = 0; y < 3; y++) {
        	for (int x = 0; x < 3; x++) {
        		int ix = x + y * 3;
        		gis.init(ix, true, x * 18, y * 18 + 30);
        	}
        }

        List<FluidStack> fs = JEISupport.getFluidFromList(in.getInputs(FluidStack.class), 0);
        if (fs != null && !fs.isEmpty() && fs.get(0) != null && fs.get(0).amount > 0)
        	gfs.init(0, true, 19, 87, 16, 16, fs.get(0).amount, false, null);
        
        gis.init(9, false, 86, 48);
        gis.init(10, false, 104, 48);
        
        if (rw instanceof ICustomCraftingRecipeWrapper) {
        	ICustomCraftingRecipeWrapper customWrapper = (ICustomCraftingRecipeWrapper)rw;
        	customWrapper.setRecipe(rlo, in);
        	return;
        }
        gis.set(in);
        gfs.set(in);
        gis.addTooltipCallback(new GenericTooltipCallback(rw.container, 0));
	}
}
