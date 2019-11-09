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
import net.minecraft.client.resources.I18n;
import net.minecraft.world.biome.Biome;
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
			int max = fr.min + fr.range;
			FluidStack fs = FluidRegistry.getFluidStack(fr.fluid, max);
			if (fs != null) ret.add(new FluidResourceWrapper(fs, fr));
		}
		return ret;
	}
	
	@Override
	public void drawInfo(Minecraft mc, int w, int h, int mX, int mY) {
		mc.fontRenderer.drawString(I18n.format("jst.compat.jei.fluid.min", frt.min), 36, 10, 0);
		mc.fontRenderer.drawString(I18n.format("jst.compat.jei.fluid.max", frt.min + frt.range), 36, 20, 0);
		mc.fontRenderer.drawString(I18n.format("jst.compat.jei.fluid.rand", frt.itemWeight), 36, 30, 0);
		List<String> ls = new ArrayList();
		if (frt.dims != null && frt.dims.length > 0) {
			ls.add(I18n.format("jst.compat.jei.fluid.dimw"));
			ls.add(convert(Arrays.toString(frt.dims)));
		}
		if (frt.dimsBL != null && frt.dimsBL.length > 0) {
			ls.add(I18n.format("jst.compat.jei.fluid.dimb"));
			ls.add(convert(Arrays.toString(frt.dimsBL)));
		}
		if (frt.biomes != null && frt.biomes.length > 0) {
			ls.add(I18n.format("jst.compat.jei.fluid.biow"));
			ls.add(convert(convBiome(frt.biomes)));
		}
		if (frt.biomesBL != null && frt.biomesBL.length > 0) {
			ls.add(I18n.format("jst.compat.jei.fluid.biob"));
			ls.add(convert(convBiome(frt.biomesBL)));
		}
		for (int n = 0; n < ls.size(); n++)
			mc.fontRenderer.drawString(ls.get(n), 36, 40 + n * 10, 0);
	}
	
	private static String convert(String in) {
		if (in.length() > 2)
			in = in.substring(1, in.length() - 1);
		return in;
	}

	private static String convBiome(Object[] in) {
		String[] r = new String[in.length];
		for (int n = 0; n < in.length; n++) {
			Object o = in[n];
			if (o instanceof String) {
				r[n] = (String)o;
			} else if (o instanceof Integer) {
				Biome b = Biome.getBiome((Integer)o);
				if (b != null) r[n] = b.getBiomeName();
			}
		}
		return Arrays.toString(r);
	}
}
