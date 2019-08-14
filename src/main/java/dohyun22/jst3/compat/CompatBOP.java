package dohyun22.jst3.compat;

import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.compat.ic2.CompatIC2;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.loader.RecipeLoader;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

public class CompatBOP extends Loadable {

	@Override
	public String getRequiredMod() {
		return "biomesoplenty";
	}

	@Override
	public void postInit() {
		MRecipes.addLEDCrop(JSTUtils.getModBlock("biomesoplenty:bamboo"));
		MRecipes.addCokeOvenRecipe(JSTUtils.getModItemStack("biomesoplenty:bamboo", 20, 0), new ItemStack(Items.COAL, 32, 1), FluidRegistry.getFluidStack("creosote", 4000), 64, 400);
		RecipeLoader.addShapedRecipe(new ItemStack(Items.BREAD), "BBB", 'B', "plantBarley");
		if (JSTCfg.ic2Loaded) CompatIC2.addMacRec(new OreDictStack("plantBarley"), JSTUtils.getValidOne("foodFlour", "dustFlour"));
	}
}
