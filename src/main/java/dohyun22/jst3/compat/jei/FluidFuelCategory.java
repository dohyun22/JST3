package dohyun22.jst3.compat.jei;

import java.util.HashMap;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.elements.DrawableResource;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class FluidFuelCategory implements IRecipeCategory<IRecipeWrapper> {
	private final IDrawable bg;
	private final String name;
	private IRecipeWrapper wrapper;

	public FluidFuelCategory(String name) {
		this.bg = new DrawableResource(JEISupport.TEX_LOC, 0, 74, 30, 18, 4, 4, 0, 136, 256, 256);
	    this.name= name;
	}
	  
	@Override
	public String getTitle() {
	    return I18n.format("jst.compat.jei." + name);
	}
	  
	@Override
	public IDrawable getBackground() {
	    return this.bg;
	}

	@Override
	public String getUid() {
		return JustServerTweak.MODID + "." + name;
	}

	@Override
	public String getModName() {
		return JustServerTweak.NAME;
	}

	@Override
	public void setRecipe(IRecipeLayout rl, IRecipeWrapper rw, IIngredients in) {
        if (!(rw instanceof FluidFuelRecipeWrapper)) {
            return;
        }

        IGuiFluidStackGroup gfs = rl.getFluidStacks();
        List<FluidStack> fs = JEISupport.getFluidFromList((List)in.getInputs(FluidStack.class), 0);
        int amt = fs == null || fs.isEmpty() || fs.get(0) == null ? 1 : Math.min(fs.get(0).amount, 1);
        gfs.init(0, true, 1, 5, 16, 16, amt, false, null);
        gfs.set(0, fs);
	}

}
