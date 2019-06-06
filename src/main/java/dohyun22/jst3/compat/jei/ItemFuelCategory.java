package dohyun22.jst3.compat.jei;

import java.util.HashMap;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemFuelCategory implements IRecipeCategory<IRecipeWrapper> {
	private final IDrawable bg;
	private final String name;
	private IRecipeWrapper wrapper;

	public ItemFuelCategory(String name) {
		this.bg = new DrawableResource(JEISupport.TEX_LOC, 0, 74, 30, 18, 4, 4, 0, 136, 256, 256);
	    this.name = name;
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
        if (!(rw instanceof ItemFuelRecipeWrapper))
            return;

        IGuiItemStackGroup gis = rl.getItemStacks();
        gis.init(0, true, 0, 4);
        
        if (rw instanceof ICustomCraftingRecipeWrapper) {
        	ICustomCraftingRecipeWrapper customWrapper = (ICustomCraftingRecipeWrapper)rw;
        	customWrapper.setRecipe(rl, in);
        	return;
        }
        
        gis.set(in);
	}

}
