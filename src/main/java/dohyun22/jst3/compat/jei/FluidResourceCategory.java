package dohyun22.jst3.compat.jei;

import java.util.List;

import dohyun22.jst3.JustServerTweak;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.elements.DrawableResource;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.FluidStack;

public class FluidResourceCategory implements IRecipeCategory<IRecipeWrapper> {
	private final DrawableResource bg;

	public FluidResourceCategory() {
	    this.bg = new DrawableResource(JEISupport.TEX_LOC, 0, 74, 30, 18, 50, 50, 0, 136, 256, 256);
	}

	@Override
	public String getUid() {
		return JustServerTweak.MODID + ".fluidresource";
	}

	@Override
	public String getTitle() {
		return I18n.format("jst.compat.jei.fluidresource");
	}

	@Override
	public String getModName() {
		return JustServerTweak.NAME;
	}

	@Override
	public IDrawable getBackground() {
		return bg;
	}

	@Override
	public void setRecipe(IRecipeLayout rl, IRecipeWrapper rw, IIngredients in) {
        if (!(rw instanceof FluidResourceWrapper))
            return;

        IGuiFluidStackGroup gfs = rl.getFluidStacks();
        List<FluidStack> fs = JEISupport.getFluidFromList((List)in.getOutputs(FluidStack.class), 0);
        int amt = fs == null || fs.isEmpty() || fs.get(0) == null ? 1 : Math.min(fs.get(0).amount, 1);
        gfs.init(0, false, 1, 51, 16, 16, amt, false, null);
        gfs.set(0, fs);
	}
}
