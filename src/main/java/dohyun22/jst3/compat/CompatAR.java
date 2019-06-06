package dohyun22.jst3.compat;

import java.util.HashMap;
import java.util.Map.Entry;

import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.compat.ic2.CompatIC2;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.loader.RecipeLoader;
import dohyun22.jst3.recipes.ItemList;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;

public class CompatAR extends Loadable {

	@Override
	public String getRequiredMod() {
		return "advancedrocketry";
	}

	@Override
	public void init() {
		if (JSTCfg.ic2Loaded) {
			String s = "dustDilithium";
			CompatIC2.addAmplifier(s, 3000000);
			CompatIC2.addUURecipe(s, 20000);
			s = "crystalDilithium";
			CompatIC2.addUURecipe(s, 20000);
			CompatIC2.addScrapDrop(s, 0.1F);
		}
	}

	@Override
	public void postInit() {
		Item i = JSTUtils.getModItem("advancedrocketry:ic");
		OreDictionary.registerOre("circuitBasic", new ItemStack(i));
		OreDictionary.registerOre("circuitAdvanced", new ItemStack(i, 1, 2));
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("ingotAluminum", 7), new OreDictStack("ingotTitanium", 3), JSTUtils.getFirstItem("ingotTitaniumAluminide", 3), 80, 200);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("ingotTitanium"), new OreDictStack("ingotIridium"), JSTUtils.getFirstItem("ingotTitaniumIridium", 2), 80, 200);
		MRecipes.addCrystalRecipe(new OreDictStack("ingotSilicon"), new OreDictStack("nuggetSilicon"), new FluidStack(FluidRegistry.WATER, 1000), JSTUtils.getModItemStack("libvulpes:productboule", 1, 3), 5, 200);
		MRecipes.addMagicFuel("dustDilithium", 1000000);
		MRecipes.addMagicFuel("crystalDilithium", 1000000);
		RecipeLoader.addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 6045), "CDC", "LTL", "CDC", 'C', ItemList.circuits[3], 'D', "crystalDilithium", 'L', ItemList.coils[3], 'T', "blockTitanium");
		RecipeLoader.addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 6046), "SSS", "CMC", "BBB", 'S', JSTUtils.getModItem("advancedrocketry:solarpanel"), 'C', ItemList.sensors[2], 'M', ItemList.machineBlock[2], 'B', ItemList.baseMaterial[2]);
		RecipeLoader.addShapedRecipe(new ItemStack(JSTItems.item1, 1, 154), "SSS", "SCS", "SSS", 'S', JSTUtils.getModItemStack("advancedrocketry:satellitepowersource", 1, 1), 'C', ItemList.circuits[3]);
		HashMap<String,Float> h = new HashMap();
		h.put("fuel", 1.0F); h.put("kerosene", 1.0F); h.put("rocket_fuel", 1.0F);
		h.put("fuel_light", 1.0F); h.put("gasoline", 1.5F); h.put("nitrofuel", 4.0F);
		try {for (Entry<String, Float> e : h.entrySet()) {
			Fluid f = FluidRegistry.getFluid(e.getKey());
			if (f != null) FuelRegistry.instance.registerFuel(FuelType.LIQUID, f, e.getValue());
		}} catch (Throwable t) {}
		try {
			SatelliteRegistry.registerSatelliteProperty(new ItemStack(JSTItems.item1, 1, 12030), new SatelliteProperties().setPowerGeneration(5));
			SatelliteRegistry.registerSatelliteProperty(new ItemStack(JSTItems.item1, 1, 154), new SatelliteProperties().setPowerGeneration(80));
		} catch (Throwable t) {}
	}
}
