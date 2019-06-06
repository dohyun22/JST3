package dohyun22.jst3.compat.jei;

import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.IRecipeItem;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.utils.JSTUtils;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.gui.elements.DrawableResource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class GenericCategory implements IRecipeCategory<GenericRecipeWrapper> {
	private final IDrawable bg;
	private final RecipeList recipes;
	private IRecipeWrapper wrapper;
	  
	public GenericCategory(RecipeList rl) {
		int[] arr = getValues();
	    this.bg = new DrawableResource(JEISupport.TEX_LOC, arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], 256, 256);
	    this.recipes = rl;
	}
	
	/** @return return value's length must be 8<BR>u, v, w, h, pT, pB, pL, pR */
	protected int[] getValues() {
		return new int[] {0, 0, 166, 56, 0, 50, 0, 0};
	}
	  
	@Override
	public String getTitle() {
	    return I18n.format("jst.compat.jei." + recipes.name);
	}
	  
	@Override
	public IDrawable getBackground() {
	    return this.bg;
	}
	  
	@Override
	public void setRecipe(IRecipeLayout rlo, GenericRecipeWrapper rw, IIngredients in) {
        IGuiItemStackGroup gis = rlo.getItemStacks();
        IGuiFluidStackGroup gfs = rlo.getFluidStacks();
        
        for (int x = 0; x < 4; x++) {
        	for (int y = 0; y < 2; y++) {
        		int ix = x + y * 4;
        		gis.init(ix, true, x * 18, y * 18);
        		gis.init(ix + 8, false, x * 18 + 94, y * 18);
        	}
        }
        
        for (int x = 0; x < 4; x++) {
            List<FluidStack> fs = JEISupport.getFluidFromList((List)in.getInputs(FluidStack.class), x);
            if (fs != null && !fs.isEmpty() && fs.get(0) != null && fs.get(0).amount > 0)
            	gfs.init(x, true, x * 18 + 1, 39, 16, 16, fs.get(0).amount, false, null);
            
            fs = JEISupport.getFluidFromList((List)in.getOutputs(FluidStack.class), x);
            if (fs != null && !fs.isEmpty() && fs.get(0) != null && fs.get(0).amount > 0)
            	gfs.init(x + 4, false, x * 18 + 95, 39, 16, 16, fs.get(0).amount, false, null);
        }
        
        if (rw instanceof ICustomCraftingRecipeWrapper) {
        	ICustomCraftingRecipeWrapper customWrapper = (ICustomCraftingRecipeWrapper)rw;
        	customWrapper.setRecipe(rlo, in);
        	return;
        }
        
        gis.set(in);
        gfs.set(in);
        
        gis.addTooltipCallback(new GenericTooltipCallback(rw.container, 0));
	}

	@Override
	public String getModName() {
	    return JustServerTweak.NAME;
	}
	
	@Override
	public String getUid() {
		return JustServerTweak.MODID + "." + recipes.name;
	}
	
	public static class GenericTooltipCallback implements ITooltipCallback<ItemStack> {
		private final RecipeContainer container;
		private final int sIdx;
		
		public GenericTooltipCallback(RecipeContainer rc, int start) {
			container = rc;
			sIdx = start;
		}

		@Override
		public void onTooltip(int idx, boolean inp, ItemStack in, List<String> tt) {
			if (!JSTUtils.isClient()) return;
			Object[] obj = container.getInputItems();
			idx += sIdx;
			if (obj == null || idx < 0 || idx >= obj.length || !(obj[idx] instanceof IRecipeItem)) return;
			String str = ((IRecipeItem)obj[idx]).getJEITooltip();
			if (str != null) tt.add(str);
		}
	}
}
