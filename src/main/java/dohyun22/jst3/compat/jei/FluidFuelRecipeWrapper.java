package dohyun22.jst3.compat.jei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidFuelRecipeWrapper implements IRecipeWrapper {
	@Nonnull
	protected final FluidStack fluid;
	protected final int energy;

	private FluidFuelRecipeWrapper(@Nonnull FluidStack in, int energy) {
		this.fluid = in;
		this.energy = energy;
	}

	@Override
	public void getIngredients(IIngredients ing) {
		List<FluidStack> list = new ArrayList();
		if (energy > 0)
			list.add(fluid);
		ing.setInputs(FluidStack.class, list);
	}

	public static List<FluidFuelRecipeWrapper> make(HashMap<String, Integer> recipe) {
	    List<FluidFuelRecipeWrapper> ret = new LinkedList();
	    for (String str : recipe.keySet()) {
	    	FluidStack fs = FluidRegistry.getFluidStack(str, 1000);
	    	if (fs == null) continue;
	    	Integer e = recipe.get(str);
	    	if (e == null || e.intValue() <= 0) continue;
	    	ret.add(new FluidFuelRecipeWrapper(fs, e.intValue() * 1000));
	    }
	    return ret;
	}
	
	@Override
	public void drawInfo(Minecraft mc, int w, int h, int mX, int mY) {
		mc.fontRenderer.drawString(energy + " EU / " +  (energy * JSTCfg.RFPerEU) + " RF", 36, 9, 0);
	}
}
