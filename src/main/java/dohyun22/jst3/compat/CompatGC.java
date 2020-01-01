package dohyun22.jst3.compat;

import dohyun22.jst3.api.recipe.AdvRecipeItem;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.recipes.ItemList;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class CompatGC extends Loadable {

	@Override
	public void postInit() {
		Item it = JSTUtils.getModItem("galacticraftcore:basic_item");
		if (it != null) {
			AdvRecipeItem ar = new AdvRecipeItem(Blocks.PISTON, 0, 0);
			MRecipes.addPressRecipe(new OreDictStack("ingotCopper", 2), ar, new ItemStack(it, 1, 6), null, 16, 200);
			MRecipes.addPressRecipe(new OreDictStack("ingotTin", 2), ar, new ItemStack(it, 1, 7), null, 16, 200);
			MRecipes.addPressRecipe(new OreDictStack("ingotAluminum", 2), ar, new ItemStack(it, 1, 8), null, 16, 200);
			MRecipes.addPressRecipe(new OreDictStack("ingotSteel", 2), ar, new ItemStack(it, 1, 9), null, 16, 200);
			MRecipes.addPressRecipe(new OreDictStack("ingotBronze", 2), ar, new ItemStack(it, 1, 10), null, 16, 200);
			MRecipes.addPressRecipe(new OreDictStack("ingotIron", 2), ar, new ItemStack(it, 1, 11), null, 16, 200);
			MRecipes.addPressRecipe(new OreDictStack("ingotIron", 2), ar, new ItemStack(it, 1, 11), null, 16, 200);
			MRecipes.addPressRecipe(new OreDictStack("ingotMeteoricIron"), ar, JSTUtils.getModItemStack("galacticraftcore:item_basic_moon", 1, 1), null, 16, 200);
			MRecipes.addPressRecipe(new OreDictStack("ingotDesh"), ar, JSTUtils.getModItemStack("galacticraftplanets:item_basic_mars", 1, 5), null, 16, 200);
			MRecipes.addPressRecipe(new OreDictStack("ingotTitanium", 2), ar, JSTUtils.getModItemStack("galacticraftplanets:item_basic_asteroids", 1, 6), null, 16, 200);
		}
		ItemStack st = JSTUtils.getModItemStack("galacticraftcore:heavy_plating"), st2 = JSTUtils.getModItemStack("galacticraftplanets:item_basic_mars", 1, 3);
		MRecipes.addAssemblerRecipe(new Object[] {new OreDictStack("compressedSteel"), new OreDictStack("compressedAluminum"), new OreDictStack("compressedBronze")}, null, st, null, 12, 200);
		if (!st2.isEmpty()) {
			MRecipes.addPressRecipe(st, new OreDictStack("compressedMeteoricIron"), st2, null, 16, 200);
			MRecipes.addPressRecipe(st2, new OreDictStack("compressedDesh"), JSTUtils.getModItemStack("galacticraftplanets:item_basic_asteroids", 1, 5), null, 16, 200);
		}
		MRecipes.addWrench(JSTUtils.getModItemStack("galacticraftcore:standard_wrench", 1, 32767));
		st = JSTUtils.getModItemStack("galacticraftcore:item_basic_moon", 2, 2);
		MRecipes.addGrindingRecipe(JSTUtils.getModItemStack("galacticraftcore:basic_block_moon", 1, 6), st, 5, 150);
		MRecipes.addGrindingRecipe(JSTUtils.modStack(st, 1, -1), JSTUtils.getFirstItem("gemSapphire", 2), 5, 150);
		MRecipes.addGrindingRecipe(JSTUtils.getModItemStack("galacticraftcore:fallen_meteor"), JSTUtils.getModItemStack("galacticraftcore:meteoric_iron_raw", 2), 5, 150);
		MRecipes.addGrindingRecipe(new OreDictStack("oreDesh"), JSTUtils.getModItemStack("galacticraftplanets:item_basic_mars", 2), 5, 150);
		MRecipes.addSeparatorRecipe(null, null, FluidRegistry.getFluidStack("sulphuricacid", 1000), null, new FluidStack(JSTFluids.acid, 4000), 5, 100);
		MRecipes.addSeparatorRecipe(new OreDictStack("ingotMeteoricIron", 10), null, null, new ItemStack[] {new ItemStack(Items.IRON_INGOT, 8), JSTUtils.getValidOne("ingotPlatinum", "ingotGold"), JSTUtils.getFirstItem("ingotNickel")}, null, 12, 400);
		it = JSTUtils.getModItem("galacticraftcore:basic_block_moon");
		if (it != null) {
			ItemStack[] sa = new ItemStack[] {new ItemStack(JSTItems.item1, 1, 9012), new ItemStack(JSTItems.item1, 1, 9013), JSTUtils.getFirstItem("dustIron"), JSTUtils.getFirstItem("nuggetNickel")};
			MRecipes.addSeparatorRecipe(new ItemStack(it, 16, 3), new ItemStack(JSTItems.item1, 2, 9000), null, sa, null, 30, 1200);
			MRecipes.addSeparatorRecipe(new ItemStack(it, 8, 5), new ItemStack(JSTItems.item1, 2, 9000), null, sa, null, 30, 1200);
		}
	}
}