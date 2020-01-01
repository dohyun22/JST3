package dohyun22.jst3.compat;

import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.recipes.ItemList;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class CompatPR extends Loadable {

	@Override
	public void postInit() {
		MRecipes.addPressRecipe(new OreDictStack("ingotRedAlloy"), ItemList.molds[0], JSTUtils.getModItemStack("projectred-transmission:wire", 4), null, 10, 64);
		OreDictionary.registerOre("dustElectrotine", new ItemStack(JSTItems.item1, 1, 27));
		OreDictionary.registerOre("ingotElectrotineAlloy", new ItemStack(JSTItems.item1, 1, 26));
		OreDictionary.registerOre("dustElectrotineAlloy", new ItemStack(JSTItems.item1, 1, 35));
		OreDictionary.registerOre("plateElectrotineAlloy", new ItemStack(JSTItems.item1, 1, 45));
	}
}
