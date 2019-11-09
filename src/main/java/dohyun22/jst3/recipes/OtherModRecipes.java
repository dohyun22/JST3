package dohyun22.jst3.recipes;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipeResult;
import ic2.api.recipe.Recipes;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IOutputEntry;
import mods.railcraft.api.crafting.IRockCrusherCrafter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public final class OtherModRecipes {
	private static Method PMngCache, CMngCache;

	@Nullable
	public static RecipeContainer getIC2Maceration(ItemStack in) {
		if (JSTCfg.ic2Loaded)
		try {
			MachineRecipeResult<IRecipeInput, Collection<ItemStack>, ItemStack> res = Recipes.macerator.apply(in, false);
			if (res != null) {
				Collection<ItemStack> out = res.getOutput();
				if (out != null && !out.isEmpty()) {
					ItemStack s = in.copy();
					s.setCount(in.getCount() - res.getAdjustedInput().getCount());
					return RecipeContainer.newContainer(new Object[] {s}, null, new ItemStack[] {out.toArray(new ItemStack[0])[0].copy()}, null, 5, 150);
				}
			}
		} catch (Throwable t) {}
		return null;
	}

	@Nullable
	public static RecipeContainer getTEMaceration(ItemStack in) {
		if (Loader.isModLoaded("thermalexpansion"))
		try {
			if (PMngCache == null)
				PMngCache = Class.forName("cofh.thermalexpansion.util.managers.machine.PulverizerManager").getMethod("getRecipe", ItemStack.class);
			Object obj = PMngCache.invoke(null, in);
			if (obj != null) {
				int dur = 150;
				ItemStack s = in.copy();
				s.setCount(((ItemStack)ReflectionUtils.callMethod(obj, "getInput")).getCount());
				ItemStack s2 = (ItemStack)ReflectionUtils.callMethod(obj, "getPrimaryOutput");
				dur = Math.max(1, ((int)ReflectionUtils.callMethod(obj, "getEnergy")) / 20);
				int cnc = (int) ReflectionUtils.callMethod(obj, "getSecondaryOutputChance");
				ItemStack s3 = cnc >= 100 || new Random().nextInt(100) < cnc ? (ItemStack)ReflectionUtils.callMethod(obj, "getSecondaryOutput") : null;
				return RecipeContainer.newContainer(new Object[] {s}, null, new ItemStack[] {s2, s3}, null, 5, dur);
			}
		} catch (Throwable t) {}
		return null;
	}

	//TODO: make it work
	/*@Nullable
	public static RecipeContainer getRCMaceration(ItemStack in) {
		if (JSTCfg.rcLoaded)
		try {
			Optional<IRockCrusherCrafter.IRecipe> o = Crafters.rockCrusher().getRecipe(in);
			if (o.isPresent()) {
				IRockCrusherCrafter.IRecipe r = o.get();
				List<IOutputEntry> ls = r.getOutputs();
				ItemStack st = ls.get(0).getOutput();
				ItemStack st2 = ItemStack.EMPTY;
				if (ls.size() > 1)
					st2 = ls.get(1).getOutput();
				else {
					List<ItemStack> ls2 = r.pollOutputs(new Random());
					if (ls2.size() > 0) st2 = ls2.get(0).copy();
				}
				in = in.copy();
				in.setCount(r.getInput().getMatchingStacks()[0].getCount());
				return RecipeContainer.newContainer(new Object[] {in}, null, new ItemStack[] {st, st2}, null, 5, 150);
			}
		} catch (Throwable t) {}
		return null;
	}*/

	@Nullable
	public static RecipeContainer getTECrucible(ItemStack in) {
		if (JSTCfg.teLoaded)
		try {
			if (CMngCache == null)
				CMngCache = Class.forName("cofh.thermalexpansion.util.managers.machine.CrucibleManager").getMethod("getRecipe", ItemStack.class);
			Object obj = CMngCache.invoke(null, in);
			if (obj != null) {
				int dur = 200;
				ItemStack s = in.copy();
				s.setCount(((ItemStack)ReflectionUtils.callMethod(obj, "getInput")).getCount());
				FluidStack fs = (FluidStack)ReflectionUtils.callMethod(obj, "getOutput");
				dur = Math.max(1, ((int)ReflectionUtils.callMethod(obj, "getEnergy")) / 96);
				return RecipeContainer.newContainer(new Object[] {s}, null, null, new FluidStack[] {fs}, 24, dur);
			}
		} catch (Throwable t) {}
		return null;
	}

	@Nullable
	public static RecipeContainer getTicMelting(ItemStack st) {
		if (JSTCfg.ticLoaded)
		try {
			MeltingRecipe r = TinkerRegistry.getMelting(st);
			if (r != null) {
				int dur = r.getTemperature() / 4;
				return RecipeContainer.newContainer(new Object[] {JSTUtils.modStack(st, 1, -1)}, null, null, new FluidStack[] {r.getResult()}, 8, dur);
			}
		} catch (Throwable t) {}
		return null;
	}
}
