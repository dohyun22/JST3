package dohyun22.jst3.compat.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTChunkData.FRType;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidResourceWrapper implements IRecipeWrapper {
	private final FluidStack resource;
	private final FRType frt;
	
	public FluidResourceWrapper(FluidStack res, FRType frt) {
		this.resource = res;
		this.frt = frt;
	}

	@Override
	public void getIngredients(IIngredients ing) {
		ing.setOutput(FluidStack.class, resource);
	}

	public static List<FluidResourceWrapper> make() {
		List<FluidResourceWrapper> ret = new LinkedList();
		for (FRType fr : JSTChunkData.FLUID_RESOURCES) {
			FluidStack fs = FluidRegistry.getFluidStack(fr.fluid, 1000);
			if (fs != null) ret.add(new FluidResourceWrapper(fs, fr));
		}
		return ret;
	}
	
	@Override
	public void drawInfo(Minecraft mc, int w, int h, int mX, int mY) {
		mc.fontRenderer.drawString("Min. " + frt.min + " mB", 36, 10, 0);
		mc.fontRenderer.drawString("Max. " + (frt.min + frt.range) + " mB", 36, 20, 0);
		mc.fontRenderer.drawString("Random Weight " + frt.itemWeight, 36, 30, 0);
		List<String> ls = new ArrayList();
		if (frt.dims != null && frt.dims.length > 0) {
			ls.add("Dimension Whitelist:");
			ls.add(convert(Arrays.toString(frt.dims)));
		}
		if (frt.dimsBL != null && frt.dimsBL.length > 0) {
			ls.add("DIM Blacklist:");
			ls.add(convert(Arrays.toString(frt.dimsBL)));
		}
		if (frt.biomes != null && frt.biomes.length > 0) {
			ls.add("Biome Whitelist:");
			ls.add(convert(Arrays.toString(frt.biomes)));
		}
		if (frt.biomesBL != null && frt.biomesBL.length > 0) {
			ls.add("Biome Blacklist:");
			ls.add(convert(Arrays.toString(frt.biomesBL)));
		}
		for (int n = 0; n < ls.size(); n++)
			mc.fontRenderer.drawString(ls.get(n), 36, 40 + n * 10, 0);
	}
	
	private static String convert(String in) {
		if (in.length() > 2)
			in = in.substring(1, in.length() - 1);
		return in;
	}
}
