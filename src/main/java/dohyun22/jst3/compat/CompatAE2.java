package dohyun22.jst3.compat;

import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class CompatAE2 extends Loadable {

	@Override
	public String getRequiredMod() {
		return "appliedenergistics2";
	}

	@Override
	public void postInit() {
		String s = getRequiredMod() + ":";
		FluidStack fs = new FluidStack(FluidRegistry.WATER, 1000);
		Item it = JSTUtils.getModItem(s + "crystal_seed"), it2 = JSTUtils.getModItem(s + "material");
		MRecipes.addCrystalRecipe(new ItemStack(it), null, fs, new ItemStack(it2, 1, 10), 30, 750);
		MRecipes.addCrystalRecipe(new ItemStack(it, 1, 600), null, fs, new ItemStack(it2, 1, 11), 30, 750);
		MRecipes.addCrystalRecipe(new ItemStack(it, 1, 1200), null, fs, new ItemStack(it2, 1, 12), 30, 750);
		MRecipes.addChemMixerRecipe(new Object[] {JSTUtils.getModItemStack(s + "quartz_ore")}, null, JSTUtils.getModItemStack(s + "charged_quartz_ore"), null, null, 30, 260);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(it2)}, null, new ItemStack(it2, 1, 1), null, null, 30, 260);
		ItemStack[] sa = new ItemStack[] {new ItemStack(JSTItems.item1, 12, 59), new ItemStack(JSTItems.item1, 6, 9017), new ItemStack(JSTItems.item1, 1, 67)};
		ItemStack st = new ItemStack(JSTItems.item1, 6, 9000);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustCertusQuartz", 16), st, null, sa, null, 50, 500);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustNetherQuartz", 16), st, null, sa, null, 50, 500);
	}
}
