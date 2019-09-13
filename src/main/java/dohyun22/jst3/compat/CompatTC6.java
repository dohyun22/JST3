package dohyun22.jst3.compat;

import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.item.ItemStack;

public class CompatTC6 extends Loadable {

	@Override
	public String getRequiredMod() {
		return "thaumcraft";
	}

	@Override
	public void postInit() {
		ItemStack st = JSTUtils.getModItemStack("thaumcraft:quicksilver");
		ItemStack st2 = new ItemStack(JSTItems.item1, 1, 9000);
		MRecipes.addAlloyFurnaceRecipe(st, st2, new ItemStack(JSTItems.item1, 1, 9022), 5, 100);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 1, 9022), null, null, new ItemStack[] {st, st2}, null, 5, 100);
		MRecipes.addMagicFuel(st, 75000);
		MRecipes.addMagicFuel(JSTUtils.getModItemStack("thaumcraft:crystal_essence"), 50000);
	}
}