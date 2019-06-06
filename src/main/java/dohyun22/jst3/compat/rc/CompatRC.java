package dohyun22.jst3.compat.rc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.fuel.FluidFuelManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class CompatRC extends Loadable {

	@Override
	public boolean canLoad() {
		return JSTCfg.rcLoaded;
	}

	@Override
	public String getRequiredMod() {
		return null;
	}

	@Override
	public void postInit() {
		addFluidFuel(JSTFluids.nitrofuel, 200000);
		addFluidFuel(JSTFluids.heavyfuel, 100000);
		addFluidFuel(JSTFluids.lpg, 40000);
		addFluidFuel(JSTFluids.lng, 32000);
		ItemStack st = JSTUtils.getModItemStack("railcraft:concrete", 4), st2 = new ItemStack(Blocks.GRAVEL, 2);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(Items.QUARTZ, 2), st2}, null, st, null, null, 8, 100);
		MRecipes.addChemMixerRecipe(new Object[] {new OreDictStack("itemSlag", 2), st2}, null, st, null, null, 8, 100);
	}

	public static List<EntityMinecart> getCartsInTrain(EntityMinecart c) {
		List<EntityMinecart> r = new ArrayList();
		if (c == null) return r;
		if (JSTCfg.rcLoaded)
			try {
				return CartToolsAPI.linkageManager().streamTrain(c).collect(Collectors.toList());
			} catch (Throwable t) {}
		r.add(c);
		return r;
	}

	public static void addFluidFuel(Fluid f, int h) {
		if (f != null && h > 0) try {FluidFuelManager.addFuel(f, h);} catch (Throwable t) {};
	}
}
