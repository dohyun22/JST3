package dohyun22.jst3.loader;

import java.util.Arrays;

import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.api.recipe.AdvRecipeItem;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.recipes.ItemList;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTDamageSource.EnumHazard;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class MRecipeLoader extends Loadable {

	@Override
	public String getRequiredMod() {
		return null;
	}

	@Override
	public void postInit() {
		MRecipes.addDieselFuel("oil", 100);
		MRecipes.addDieselFuel("crude_oil", 100);
		MRecipes.addDieselFuel("oil_heat_1", 100);
		MRecipes.addDieselFuel("oil_heat_2", 100);
		MRecipes.addDieselFuel("oil_heavy", 200);
		MRecipes.addDieselFuel("oil_heavy_heat_1", 200);
		MRecipes.addDieselFuel("oil_heavy_heat_2", 200);
		MRecipes.addDieselFuel("oilgc", 100);
		MRecipes.addDieselFuel("liquid_light_oil", 25);
		MRecipes.addDieselFuel("liquid_medium_oil", 50);
		MRecipes.addDieselFuel("liquid_heavy_oil", 100);
		MRecipes.addDieselFuel("liquid_extra_heavy_oil", 200);
		MRecipes.addDieselFuel("fuel_light", 512);
		MRecipes.addDieselFuel("fuel_light_heat_1", 512);
		MRecipes.addDieselFuel("fuel_light_heat_2", 512);
		MRecipes.addDieselFuel("fuel_mixed_light", 600);
		MRecipes.addDieselFuel("fuel_mixed_light_heat_1", 600);
		MRecipes.addDieselFuel("fuel_mixed_light_heat_2", 600);
		MRecipes.addDieselFuel("fuel", 512);
		MRecipes.addDieselFuel("diesel", 512);
		MRecipes.addDieselFuel("biodiesel", 480);
		MRecipes.addDieselFuel("ethanol", 200);
		MRecipes.addDieselFuel("biofuel", 200);
		MRecipes.addDieselFuel("bio.ethanol", 200);
		MRecipes.addDieselFuel("nitrofuel", 1024);
		MRecipes.addDieselFuel("sulfuric_light_fuel", 128);
		MRecipes.addDieselFuel("light_fuel", 512);
		MRecipes.addDieselFuel("coal", 120);
		MRecipes.addDieselFuel("refined_oil", 400);
		MRecipes.addDieselFuel("refined_fuel", 512);
		MRecipes.addDieselFuel("creosote", 25);
		MRecipes.addDieselFuel("gasoline", 300);
		MRecipes.addDieselFuel("heavyfuel", 120);
		MRecipes.addDieselFuel("refined_biofuel", 200);
		MRecipes.addDieselFuel("tree_oil", 100);
		MRecipes.addDieselFuel("seed_oil", 20);
		MRecipes.addDieselFuel("canolaoil", 20);
		MRecipes.addDieselFuel("refinedcanolaoil", 100);
		MRecipes.addDieselFuel("crystaloil", 100);
		MRecipes.addDieselFuel("empoweredoil", 200);

		MRecipes.addGasFuel("gas.natural", 72);
		MRecipes.addGasFuel("hydrogen", 20);
		MRecipes.addGasFuel("methane", 45);
		MRecipes.addGasFuel("biogas", 60);
		MRecipes.addGasFuel("gas_natural_gas", 72);
		MRecipes.addGasFuel("aliengas", 16);
		MRecipes.addGasFuel("gas_sulfuricgas", 16);
		MRecipes.addGasFuel("gas_gas", 256);
		MRecipes.addGasFuel("lpg", 400);
		MRecipes.addGasFuel("liquid_lpg", 320);
		MRecipes.addGasFuel("lng", 360);
		MRecipes.addGasFuel("liquid_lng", 300);
		MRecipes.addGasFuel("Butane", 500);
		MRecipes.addGasFuel("propane", 500);
		MRecipes.addGasFuel("fuel_gaseous", 300);
		MRecipes.addGasFuel("fuel_gaseous_heat_1", 300);
		MRecipes.addGasFuel("fuel_gaseous_heat_2", 300);

		MRecipes.addHeatFuel("lava", 40);
		MRecipes.addHeatFuel("pyrotheum", 800);
		MRecipes.addHeatFuel("uranium", 3200);

		MRecipes.addSteam("steam", 1);
		
		MRecipes.allowedFuelerFuels.addAll(Arrays.asList("fuel", "biofuel", "nitrofuel", "gasoline", "ethanol", "bio.ethanol", "ic2biogas", "fuel_light", "fuel_light_heat_1", "fuel_light_heat_2", "diesel", "biodiesel", "rocketfuel"));

		MRecipes.addMagicFuel(new ItemStack(JSTItems.item1, 1, 6), 1000000);
		MRecipes.addMagicFuel(new ItemStack(JSTItems.item1, 1, 7), 1000000);
		MRecipes.addMagicFuel(new ItemStack(JSTItems.item1, 1, 8), 1000000);
		MRecipes.addMagicFuel(new ItemStack(JSTItems.item1, 1, 9), 1000000);
		MRecipes.addMagicFuel(new ItemStack(JSTItems.item1, 1, 10), 1000000);
		MRecipes.addMagicFuel(new ItemStack(Items.EXPERIENCE_BOTTLE), 50000);
		MRecipes.addMagicFuel(new ItemStack(Items.ENDER_PEARL), 16000);
		MRecipes.addMagicFuel(new ItemStack(Items.ENDER_EYE), 32000);
		MRecipes.addMagicFuel(new ItemStack(Items.BLAZE_ROD), 32000);
		MRecipes.addMagicFuel(new ItemStack(Items.NETHER_STAR), 10000000);
		MRecipes.addMagicFuel(new ItemStack(Items.GHAST_TEAR), 500000);
		MRecipes.addMagicFuel(new ItemStack(Items.SKULL, 1, 1), 800000);
		MRecipes.addMagicFuel(new ItemStack(Items.SHULKER_SHELL), 2000000);
		MRecipes.addMagicFuel(new ItemStack(Items.GOLDEN_APPLE, 1, 1), 3200000);
		MRecipes.addMagicFuel(new ItemStack(Items.DRAGON_BREATH), 24000);
		MRecipes.addMagicFuel("dustMana", 100000);
		MRecipes.addMagicFuel("crystalEnder", 16000);
		MRecipes.addMagicFuel("dustMithril", 320000);
		MRecipes.addMagicFuel("ingotMithril", 320000);
		MRecipes.addMagicFuel("ingotThaumium", 120000);
		
		MRecipes.addFertilizer(new ItemStack(Items.DYE, 1, 15));
		
		FluidStack[] fa = new FluidStack[] {new FluidStack(JSTFluids.deuterium, 125), null};
		MRecipes.addFusionRecipe(fa[0], new FluidStack(JSTFluids.tritium, 125), new FluidStack(JSTFluids.helium, 125), 2048, 100, 40000000, 24000, true);
		MRecipes.addFusionRecipe(fa[0], fa[0], new FluidStack(JSTFluids.helium, 125), 2048, 100, 60000000, 8000, true);
		MRecipes.addFusionRecipe(fa[0], new FluidStack(JSTFluids.helium3, 125), new FluidStack(JSTFluids.helium, 125), 3000, 100, 80000000, 32000, false);
		for (int n = 0; n < 2; n++) {
			if (n == 1 && !JSTCfg.ic2Loaded) break;
			String pf = n == 1 ? "ic2" : "";
			fa[0] = FluidRegistry.getFluidStack(pf + "hydrogen", 125);
			fa[1] = FluidRegistry.getFluidStack(pf + "oxygen", 125);
			MRecipes.addFusionRecipe(fa[0], fa[0], new FluidStack(JSTFluids.helium, 50), 4096, 100, 199000000, 20000, false);
			MRecipes.addFusionRecipe(fa[0], new FluidStack(JSTFluids.carbon, 16), new FluidStack(JSTFluids.nitrogen, 125), 8000, 100, 280000000, 36000, false);
			MRecipes.addFusionRecipe(fa[0], new FluidStack(JSTFluids.nitrogen, 125), new FluidStack(JSTFluids.oxygen, 125), 8000, 100, 280000000, 36000, false);
			MRecipes.addFusionRecipe(fa[0], fa[1], new FluidStack(JSTFluids.carbon, 16), 8000, 100, 280000000, 36000, false);
		}
		MRecipes.addFusionRecipe(FluidRegistry.getFluidStack("silicon", 16), FluidRegistry.getFluidStack("silicon", 16), FluidRegistry.getFluidStack("iron", 16), 25000, 200, 320000000, 80000, false);
		MRecipes.addFusionRecipe(new FluidStack(JSTFluids.tritium, 125), FluidRegistry.getFluidStack("iron", 16), FluidRegistry.getFluidStack("nickel", 16), 30000, 200, 380000000, 100000, true);
		
		MRecipes.addFusionBreederRecipe(new ItemStack(JSTItems.item1, 1, 9014), new ItemStack(JSTItems.item1, 1, 9011));
		
		MRecipes.addSHFurnaceRecipe(new Object[] {new OreDictStack("dustChrome")}, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 64)}, 120, 2000);
		MRecipes.addSHFurnaceRecipe(new Object[] {new OreDictStack("dustUnobtainium")}, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 40)}, 32000, 1000);
		MRecipes.addSHFurnaceRecipe(new Object[] {new OreDictStack("dustTungsten")}, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 73)}, 2000, 1000);
		MRecipes.addSHFurnaceRecipe(new Object[] {new OreDictStack("dustRhenium")}, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 89)}, 2000, 1600);
		MRecipes.addSHFurnaceRecipe(new Object[] {new OreDictStack("ingotIron", 4), new ItemStack(Items.COAL, 4, 32767)}, new ItemStack[] {JSTUtils.getFirstItem("ingotSteel", 4)}, 50, 250);
		MRecipes.addSHFurnaceRecipe(new Object[] {new OreDictStack("ingotIron", 4), new OreDictStack("fuelCoke", 2)}, new ItemStack[] {JSTUtils.getFirstItem("ingotSteel", 4)}, 50, 200);
		MRecipes.addSHFurnaceRecipe(new Object[] {new OreDictStack("oreIron", 1), new ItemStack(JSTItems.item1, 2, 9017)}, new ItemStack[] {new ItemStack(Items.IRON_INGOT, 3), new ItemStack(JSTItems.item1, 2, 9000)}, 80, 200);
		String pf = "plate";
		MRecipes.addSHFurnaceRecipe(new Object[] {new OreDictStack(pf + "Titanium", 2), new OreDictStack(pf + "Gold")}, new ItemStack[] {new ItemStack(JSTItems.item1, 2, 69)}, 500, 500);
		
		//AlloySmelting
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("dustRedstone", 4), new OreDictStack(JSTUtils.oreValid("ingotCopper") ? "ingotCopper" : "ingotIron", 1), new ItemStack(JSTItems.item1, 1, 25), 5, 100);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("dustNikolite", 4), new OreDictStack(JSTUtils.oreValid("ingotSilver") ? "ingotSilver" : "ingotIron", 1), new ItemStack(JSTItems.item1, 1, 26), 5, 100);
		boolean flag = JSTUtils.oreValid("ingotCopper");
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack(flag ? "ingotCopper" : "ingotGold", flag ? 3 : 1), new OreDictStack("ingotZinc", flag ? 1 : 3), new ItemStack(JSTItems.item1, 4, 36), 5, 100);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("ingotCopper", 3), new OreDictStack("ingotTin"), JSTUtils.getFirstItem("ingotBronze", 4), 5, 100);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("ingotGold"), new OreDictStack("ingotSilver"), JSTUtils.getFirstItem("ingotElectrum", 2), 5, 100);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("ingotIron", 2), new OreDictStack("ingotNickel"), JSTUtils.getFirstItem("ingotInvar", 3), 5, 100);
		Object obj = new OreDictStack(JSTUtils.oreValid("ingotSteel") ? "ingotSteel" : "ingotIron");
		MRecipes.addAlloyFurnaceRecipe(obj, new OreDictStack("ingotSilicon"), JSTUtils.getFirstItem("ingotElectricalSteel"), 10, 200);
		MRecipes.addAlloyFurnaceRecipe(new ItemStack(Blocks.SAND, 16, Short.MAX_VALUE), new ItemStack(Items.COAL, 8, Short.MAX_VALUE), new ItemStack(JSTItems.item1, 1, 33), 20, 500);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("dustLithium"), new ItemStack(JSTItems.item1, 1, 9000), new ItemStack(JSTItems.item1, 1, 9014), 5, 100);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("dustSodium"), new ItemStack(JSTItems.item1, 1, 9000), new ItemStack(JSTItems.item1, 1, 9018), 5, 100);
		ItemStack st = JSTUtils.getFirstItem("ingotSolder", 10);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("ingotTin", 6), new OreDictStack("ingotLead", 4), st, 5, 200);
		obj = new OreDictStack("ingotBismuth", 6);
		if (JSTUtils.oreValid((OreDictStack)obj)) MRecipes.addAlloyFurnaceRecipe(new OreDictStack("ingotTin", 4), obj, st, 5, 200);

		//Press
		obj = new Object[] {new AdvRecipeItem(JSTItems.item1, 0, 160)};
		MRecipes.addPressRecipe(new OreDictStack("ingotSolder"), ((Object[])obj)[0], new ItemStack(JSTItems.item1, 2, 185), null, 10, 64);

		//Assembler
		obj = new OreDictStack(JSTCfg.ic2Loaded && JSTUtils.oreValid("platePlatinum") ? "platePlatinum" : JSTUtils.oreValid("ingotPlatinum") ? "ingotPlatinum" : "gemDiamond");
		Object obj2 = new OreDictStack("circuitHighTech");
		for (int n = 0; n < 2; n++) {
			OreDictStack ods = new OreDictStack(n == 0 ? "gemPeridot" : "gemEmerald", 2);
			MRecipes.addAssemblerRecipe(new Object[] {obj, ods , obj, obj2, new OreDictStack(pf + "Silicon", 2), obj2, obj, ods, obj}, null, new ItemStack(JSTItems.item1, 2, 4), null, 1000, 100);
		}
		MRecipes.addAssemblerRecipe(new Object[] {obj, new OreDictStack("gemSapphire", 2), obj, obj2, new OreDictStack("circuitAdvanced"), obj2, obj, new OreDictStack("gemSapphire", 2), obj}, null, new ItemStack(JSTItems.item1, 2, 5), null, 3000, 100);
		
		obj = new OreDictStack(pf + "Iridium");
		obj2 = new ItemStack(JSTBlocks.blockTile, 1, 4031);
		MRecipes.addAssemblerRecipe(new Object[] {obj, obj2, obj, obj2, new OreDictStack("circuitPowerControl", 2), obj2, obj, obj2, obj}, null, new ItemStack(JSTItems.item1, 2, 51), null, 20000, 1000);
		obj = new OreDictStack(pf + "Rhenium");
		obj2 = new ItemStack(JSTBlocks.blockTile, 1, 4012);
		MRecipes.addAssemblerRecipe(new Object[] {obj, obj2, obj, obj2, new ItemStack(JSTItems.item1, 2, 51), obj2, obj, obj2, obj}, null, new ItemStack(JSTItems.item1, 2, 52), null, 40000, 600);
		obj = new OreDictStack(pf + "Unobtainium");
		obj2 = new ItemStack(JSTItems.item1, 1, 49);
		MRecipes.addAssemblerRecipe(new Object[] {obj, new OreDictStack("circuitPowerControl", 1), obj, obj2, new ItemStack(JSTItems.item1, 1, 52), obj2, obj, new OreDictStack("circuitPowerControl", 1), obj}, null, new ItemStack(JSTItems.item1, 1, 153), null, 200000, 500);
		if (JSTCfg.hardEG) {
			obj = new ItemStack(Blocks.OBSIDIAN, 2);
			MRecipes.addAssemblerRecipe(new Object[] {obj, new OreDictStack(pf + "Iridium"), obj, new ItemStack (Items.ENDER_EYE, 6), new ItemStack(JSTItems.item1, 1, 11), new ItemStack (Items.ENDER_EYE, 6), obj, new OreDictStack(JSTUtils.oreValid(pf + "Enderium") ? pf + "Enderium" : "gemDiamond"), obj}, null, new ItemStack(JSTBlocks.block1, 1, 6), null, 200, 800);
		}
		obj = new OreDictStack("blockGold");
		MRecipes.addAssemblerRecipe(new Object[] {obj, obj, obj, obj, new ItemStack(Items.APPLE), obj, obj, obj, obj}, null, new ItemStack(Items.GOLDEN_APPLE, 1, 1), ItemStack.EMPTY, 30, 800);
		obj = new ItemStack(Items.LEATHER, 2);
		obj2 = new OreDictStack(pf + "Aluminum", 2);
		st = new ItemStack(Blocks.END_ROD, 4);
		MRecipes.addAssemblerRecipe(new Object[] {obj, obj2, obj, obj, obj2, obj, st, new ItemStack(Items.SHULKER_SHELL), st}, null, new ItemStack(Items.ELYTRA), ItemStack.EMPTY, 50, 1200);
		
		obj = new ItemStack(JSTItems.item1, 1, 105);
		MRecipes.addAssemblerRecipe(new Object[] {null, new OreDictStack("dustRedstone", 2), null, null, new OreDictStack(pf + "Silicon"), null, null, obj, null}, null, new ItemStack(JSTItems.item1, 8, 85), null, 10, 250);
		MRecipes.addAssemblerRecipe(new Object[] {null, new OreDictStack("dustNikolite"), null, null, new OreDictStack(pf + "Silicon"), null, null, obj, null}, null, new ItemStack(JSTItems.item1, 8, 85), null, 10, 250);
		MRecipes.addAssemblerRecipe(new Object[] {null, new OreDictStack(pf + "BlueAlloy"), null, null, new OreDictStack(pf + "Silicon", 2), null, null, obj, null}, null, new ItemStack(JSTItems.item1, 6, 83), null, 40, 100);
		obj = new OreDictStack(pf + "Steel");
		if (!JSTUtils.oreValid((OreDictStack) obj)) obj = new OreDictStack(pf + "Aluminum");
		MRecipes.addAssemblerRecipe(new Object[] {null, new OreDictStack(pf + "Aluminum"), null, null, new OreDictStack(pf + "Brass"), null, null, obj, null}, null, new ItemStack(JSTItems.item1, 2, 68), null, 120, 200);
		
		obj = ItemList.copy(ItemList.baseMaterial[8], 2, true);
		MRecipes.addAssemblerRecipe(new Object[] {null, ItemList.copy(ItemList.cables[7], 4, true), null, obj, ItemList.copy(ItemList.coils[9], 10, true), obj, obj, new OreDictStack("circuitPowerControl", 10), obj}, null, new ItemStack(JSTItems.item1, 1, 12020), null, 10000, 400);
		obj = new OreDictStack("ingotNeutronium", 2);
		MRecipes.addAssemblerRecipe(new Object[] {ItemList.cables[9], new OreDictStack("ingotUnobtainium", 4), ItemList.cables[9], obj, new ItemStack(JSTItems.item1, 1, 12021), obj, obj, obj, obj}, null, new ItemStack(JSTItems.item1, 1, 12022), null, 200000, 1200);
		obj = new Object[] {new ItemStack(Blocks.GLASS, 4), new OreDictStack(pf + "Silicon", 8), new OreDictStack(pf + "BlueAlloy", 2)};
		MRecipes.addAssemblerRecipe(new Object[] {((Object[])obj)[0], new OreDictStack("circuitAdvanced", 2), ((Object[])obj)[0], ((Object[])obj)[1], ItemList.machineBlock[0], ((Object[])obj)[1], ((Object[])obj)[2], ((Object[])obj)[2], ((Object[])obj)[2]}, null, new ItemStack(JSTBlocks.blockTile, 1, 5006), null, 30, 600);
		obj = new Object[] {new OreDictStack("gemSapphire", 4), new OreDictStack(pf + "Silicon", 16), new ItemStack(JSTItems.item1, 2, 150)};
		MRecipes.addAssemblerRecipe(new Object[] {((Object[])obj)[0], new OreDictStack("circuitHighTech", 4), ((Object[])obj)[0], ((Object[])obj)[1], ItemList.machineBlock[1], ((Object[])obj)[1], ((Object[])obj)[2], ((Object[])obj)[2], ((Object[])obj)[2]}, null, new ItemStack(JSTBlocks.blockTile, 1, 5007), null, 30, 1200);
		obj = new Object[] {new ItemStack(Blocks.DIAMOND_BLOCK, 1), new OreDictStack(pf + "Silicon", 64), new ItemStack(JSTItems.item1, 1, 103)};
		MRecipes.addAssemblerRecipe(new Object[] {((Object[])obj)[0], new OreDictStack("circuitPowerElectronic", 4), ((Object[])obj)[0], ((Object[])obj)[1], ItemList.machineBlock[2], ((Object[])obj)[1], ((Object[])obj)[2], ((Object[])obj)[2], ((Object[])obj)[2]}, null, new ItemStack(JSTBlocks.blockTile, 1, 5008), null, 30, 2400);
		MRecipes.addAssemblerRecipe(new Object[] {new ItemStack(Items.STRING, 4)}, null, new ItemStack(Blocks.WOOL), null, 5, 50);
		
		//Separator
		MRecipes.addSeparatorRecipe(new OreDictStack("dustBauxite", 8), new ItemStack(JSTItems.item1, 2, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 7, 71), new ItemStack(JSTItems.item1, 1, 2), new ItemStack(JSTItems.item1, 2, 9009)}, null, 100, 800);
		MRecipes.addSeparatorRecipe(null, null, new FluidStack(FluidRegistry.LAVA, 16000), new ItemStack[] {new ItemStack(Items.GOLD_INGOT), JSTUtils.getFirstItem("ingotSilver"), JSTUtils.getFirstItem("ingotTin", 2), JSTUtils.getFirstItem("ingotCopper", 4)}, null, 30, 2400);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 16, 9002), null, null, new ItemStack[] {new ItemStack(Items.GOLD_INGOT), JSTUtils.getFirstItem("ingotSilver"), JSTUtils.getFirstItem("ingotTin", 2), JSTUtils.getFirstItem("ingotCopper", 4), new ItemStack(JSTItems.item1, 16, 9000)}, null, 30, 2400);
		MRecipes.addSeparatorRecipe(new ItemStack(Blocks.SOUL_SAND, 16), new ItemStack(JSTItems.item1, 1, 9000), null, new ItemStack[] {new ItemStack(Blocks.SAND, 15), new ItemStack(Items.COAL), new ItemStack(JSTItems.item1, 1, 9003), JSTUtils.getFirstItem("dustSaltpeter")}, null, 30, 1600);
		MRecipes.addSeparatorRecipe(new ItemStack(Blocks.SAND, 16, 1), null, null, new ItemStack[] {new ItemStack(Blocks.SAND, 15), new ItemStack(Items.GOLD_NUGGET), JSTUtils.getFirstItem("dustIron")}, null, 30, 1200);
		MRecipes.addSeparatorRecipe(null, null, new FluidStack(JSTFluids.hydrogen, 8000), null, new FluidStack(JSTFluids.deuterium, 1000), 20, 2400);
		MRecipes.addSeparatorRecipe(null, null, new FluidStack(JSTFluids.helium, 16000), null, new FluidStack(JSTFluids.helium3, 1000), 20, 3200);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 8, 9009), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 9010), new ItemStack(JSTItems.item1, 7, 9000)}, null, 20, 1600);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 16, 9013), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 9012), new ItemStack(JSTItems.item1, 15, 9000)}, null, 20, 3000);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustRuby", 6), new ItemStack(JSTItems.item1, 3, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 2, 71), new ItemStack(JSTItems.item1, 1, 65), new ItemStack(JSTItems.item1, 3, 9017)}, null, 100, 400);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustSapphire", 6), new ItemStack(JSTItems.item1, 3, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 2, 71), new ItemStack(JSTItems.item1, 3, 9017)}, null, 80, 400);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustPeridot", 7), new ItemStack(JSTItems.item1, 4, 9000), null, new ItemStack[] {JSTUtils.getFirstOrSecond("dustIron", 2, new ItemStack(Items.IRON_INGOT, 2)), new ItemStack(JSTItems.item1, 1, 59), new ItemStack(JSTItems.item1, 4, 9017)}, null, 80, 400);
		if (JSTUtils.oreValid("dustClay"))
			MRecipes.addSeparatorRecipe(new OreDictStack("dustClay", 8), new ItemStack(JSTItems.item1, 2, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 3, 59), new ItemStack(JSTItems.item1, 2, 71), new ItemStack(JSTItems.item1, 1, 67), new ItemStack(JSTItems.item1, 2, 9017)}, null, 40, 300);
		MRecipes.addSeparatorRecipe(new ItemStack(Blocks.CLAY, 4), new ItemStack(JSTItems.item1, 2, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 3, 59), new ItemStack(JSTItems.item1, 2, 71), new ItemStack(JSTItems.item1, 1, 67), new ItemStack(JSTItems.item1, 2, 9017)}, null, 40, 600);
		st = JSTUtils.getFirstItem("dustIron", 2);
		if (st.isEmpty()) st = new ItemStack(Items.IRON_INGOT, 2);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustRedstone", 10), new ItemStack(JSTItems.item1, 1, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 56), new ItemStack(JSTItems.item1, 2, 59), st, new ItemStack(JSTItems.item1, 1, 9022)}, null, 60, 300);
		st = JSTUtils.getFirstItem("dustSilver", 1);
		if (st.isEmpty()) st = JSTUtils.getFirstItem("ingotSilver", 1);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustNikolite", 10), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 58), new ItemStack(JSTItems.item1, 1, 67), st}, null, 60, 300);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 1, 9000), null, new FluidStack(FluidRegistry.WATER, 3000), new ItemStack[] {new ItemStack(JSTItems.item1, 1, 9017)}, new FluidStack(JSTFluids.hydrogen, 2000), 30, 2000);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 3, 9001), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 9017), new ItemStack(JSTItems.item1, 2, 9009)}, null, 30, 2000);
		st = JSTUtils.getFirstItem("dustSulfur");
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 5, 9003), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 9008), new ItemStack(JSTItems.item1, 4, 9004), st}, null, 30, 2000);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 1, 9000), null, new FluidStack(JSTFluids.oil, 5000), new ItemStack[] {new ItemStack(JSTItems.item1, 1, 9008), st}, new FluidStack(JSTFluids.fuel, 4000), 30, 2000);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 8, 9006), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 2, 9009), new ItemStack(JSTItems.item1, 6, 9007), st}, null, 20, 800);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 2, 9000), null, new FluidStack(JSTFluids.naturalGas, 8000), new ItemStack[] {new ItemStack(JSTItems.item1, 2, 9009), st}, new FluidStack(JSTFluids.lng, 6000), 20, 800);
		MRecipes.addSeparatorRecipe(new ItemStack(Blocks.SAND, 16), new ItemStack(JSTItems.item1, 2, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 59), new ItemStack(JSTItems.item1, 2, 9017)}, null, 30, 500);
		MRecipes.addSeparatorRecipe(new ItemStack(Items.QUARTZ, 16), new ItemStack(JSTItems.item1, 6, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 12, 59), new ItemStack(JSTItems.item1, 6, 9017), new ItemStack(JSTItems.item1, 1, 67)}, null, 50, 500);
		MRecipes.addSeparatorRecipe(new ItemStack(Blocks.NETHERRACK, 16), null, null, new ItemStack[] {new ItemStack(Items.REDSTONE), new ItemStack(Items.COAL), JSTUtils.getFirstItem("dustSulfur")}, null, 30, 1200);
		MRecipes.addSeparatorRecipe(new ItemStack(Blocks.END_STONE, 16), new ItemStack(JSTItems.item1, 2, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 9012), new ItemStack(JSTItems.item1, 1, 9013), JSTUtils.getFirstItem("dustIron")}, null, 30, 2400);
		MRecipes.addSeparatorRecipe(new ItemStack(Blocks.OBSIDIAN, 8), new ItemStack(JSTItems.item1, 2, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 4, 59), new ItemStack(JSTItems.item1, 2, 9017), JSTUtils.getFirstItem("dustIron")}, null, 30, 800);
		MRecipes.addSeparatorRecipe(new ItemStack(Blocks.DIRT, 16), null, null, new ItemStack[] {new ItemStack(Blocks.SAND, 15), new ItemStack(Items.CLAY_BALL, 1)}, null, 20, 800);
		st = JSTUtils.getModItemStack("ic2:crafting", 1, 21);
		if (st.isEmpty()) st = new ItemStack(Items.WHEAT_SEEDS, 1);
		MRecipes.addSeparatorRecipe(new ItemStack(Blocks.GRASS, 16), null, null, new ItemStack[] {new ItemStack(Blocks.SAND, 15), new ItemStack(Items.CLAY_BALL, 1), st}, null, 20, 800);
		st = new ItemStack(JSTItems.item1, 50, 27);
		MRecipes.addSeparatorRecipe(new OreDictStack("oreNikolite", 10), null, null, new ItemStack[] {st, st, new ItemStack(JSTItems.item1, 2, 67), new ItemStack(Items.DIAMOND)}, null, 30, 2500);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTBlocks.blockOre, 1, 5), null, null, null, new FluidStack(JSTFluids.oil, 1000), 16, 100);
		MRecipes.addSeparatorRecipe(new ItemStack(Items.GUNPOWDER, 4), null, null, new ItemStack[] {JSTUtils.getFirstItem("dustSaltpeter"), JSTUtils.getFirstItem("dustSulfur")}, null, 20, 400);
		MRecipes.addSeparatorRecipe(new ItemStack(Blocks.SANDSTONE, 16), null, null, new ItemStack[] {new ItemStack(Blocks.SAND, 16), JSTUtils.getFirstItem("dustSaltpeter")}, null, 20, 800);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustTin", 8), null, null, new ItemStack[] {JSTUtils.getFirstItem("dustIron"), new ItemStack(JSTItems.item1, 1, 32)}, null, 32, 400);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustCopper", 12), null, null, new ItemStack[] {JSTUtils.getFirstItem("dustGold"), JSTUtils.getFirstItem("dustCobalt")}, null, 32, 400);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustIridium", 10), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 90)}, null, 512, 200);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustNaquadah", 4), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 60)}, null, 512, 200);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustSalt", 2), new ItemStack(JSTItems.item1, 1, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 108), new ItemStack(JSTItems.item1, 1, 9019)}, null, 20, 200);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 5, 9020), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 9017), new ItemStack(JSTItems.item1, 4, 9016)}, null, 30, 800);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 1, 9000), null, new FluidStack(JSTFluids.air, 5000), new ItemStack[] {new ItemStack(JSTItems.item1, 1, 9017)}, new FluidStack(JSTFluids.nitrogen, 4000), 30, 800);
		MRecipes.addSeparatorRecipe(new ItemStack(Items.COAL, 1, 0), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 2, 109)}, null, 30, 100);
		MRecipes.addSeparatorRecipe(new ItemStack(Items.COAL, 1, 1), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 109)}, null, 30, 100);
		st = new ItemStack(JSTItems.item1, 64, 109);
		obj = new ItemStack[] {st, st};
		MRecipes.addSeparatorRecipe(new OreDictStack("gemDiamond"), null, null, (ItemStack[]) obj, null, 30, 600);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustDiamond"), null, null, (ItemStack[]) obj, null, 30, 500);
		MRecipes.addSeparatorRecipe(new ItemStack(Items.SUGAR, 8), new ItemStack(JSTItems.item1, 4, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 4, 109), new ItemStack(JSTItems.item1, 4, 9001)}, null, 30, 200);
		MRecipes.addSeparatorRecipe(new OreDictStack("dustSodium", 16), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 67)}, null, 50, 400);
		MRecipes.addSeparatorRecipe(new ItemStack(Items.ROTTEN_FLESH, 32), new ItemStack(JSTItems.item1, 1, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 9007)}, null, 20, 1200);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 20, 21), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 49), new ItemStack(JSTItems.item1, 1, 39), new ItemStack(JSTItems.item1, 16, 74)}, null, 1000, 1200);
		MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 1, 151), null, null, null, new FluidStack(JSTFluids.oil, 500), 5, 200);
		obj = FluidRegistry.getFluidStack("bio.ethanol", 1000);
		if (obj != null) {
			MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 1, 9028), null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 9030)}, null, 10, 10);
			MRecipes.addSeparatorRecipe(null, null, (FluidStack) obj, null, FluidRegistry.getFluidStack("ethanol", 1000), 10, 10);
		}
		MRecipes.addSeparatorRecipe(new OreDictStack("dustNatron", 12), new ItemStack(JSTItems.item1, 3, 9000), null, new ItemStack[] {new ItemStack(JSTItems.item1, 2, 108), new ItemStack(JSTItems.item1, 1, 109), new ItemStack(JSTItems.item1, 3, 9017)}, null, 16, 300);
		
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 8, 9004), new ItemStack(JSTItems.item1, 2, 9001), new ItemStack(JSTItems.item1, 2, 9016), new OreDictStack("dustCarbon")}, null, new ItemStack(JSTItems.item1, 12, 9005), null, null, 30, 800);
		obj = FluidRegistry.getFluidStack("ic2distilled_water", 3000);
		if (obj == null) obj = new FluidStack(FluidRegistry.WATER, 3000);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 1, 9017), new ItemStack(JSTItems.item1, 2, 9009)}, null, new ItemStack(JSTItems.item1, 3, 9000), null, (FluidStack) obj, 16, 100);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 1, 9017)}, new FluidStack(JSTFluids.hydrogen, 2000), new ItemStack(JSTItems.item1, 1, 9000), null, (FluidStack) obj, 16, 100);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 2, 9004), new ItemStack(JSTItems.item1, 1, 9017)}, null, new ItemStack(JSTItems.item1, 8, 105), new ItemStack(JSTItems.item1, 3, 9000), null, 24, 400);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 1, 9017)}, new FluidStack(JSTFluids.fuel, 2000), new ItemStack(JSTItems.item1, 8, 105), new ItemStack(JSTItems.item1, 1, 9000), null, 24, 400);
		st = new ItemStack(Blocks.SAND, 2);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 4, 105), st, new ItemStack(JSTItems.item1, 1, 9008)}, null, new ItemStack(JSTItems.item1, 4, 106), new ItemStack(JSTItems.item1, 1, 9000), null, 300, 250);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 4, 105), st}, new FluidStack(JSTFluids.lpg, 1000), new ItemStack(JSTItems.item1, 4, 106), null, null, 300, 250);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(Items.COAL, 8, 32767), new ItemStack(Items.BLAZE_POWDER)}, null, new ItemStack(JSTItems.item1, 1, 98), null, null, 10, 300);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(Items.COAL, 8, 32767)}, new FluidStack(JSTFluids.fuel, 250), new ItemStack(JSTItems.item1, 4, 98), null, null, 10, 500);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 2, 98), new ItemStack(JSTItems.item1, 1, 9004)}, null, new ItemStack(JSTItems.item1, 8, 99), new ItemStack(JSTItems.item1, 1, 9000), null, 50, 400);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 4, 99), new ItemStack(JSTItems.item1, 1, 9005), new ItemStack(Items.ENDER_EYE)}, null, new ItemStack(JSTItems.item1, 4, 101), new ItemStack(JSTItems.item1, 1, 9000), null, 500, 100);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 1, 9034), new ItemStack(JSTItems.item1, 1, 9035)}, null, new ItemStack(JSTItems.item1, 2, 9004), null, null, 10, 100);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 1, 9009), new ItemStack(JSTItems.item1, 5, 9037)}, null, new ItemStack(JSTItems.item1, 1, 9008), new ItemStack(JSTItems.item1, 5, 9004), null, 10, 100);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 1, 9009)}, new FluidStack(JSTFluids.heavyfuel, 5000), new ItemStack(JSTItems.item1, 1, 9008), null, new FluidStack(JSTFluids.fuel, 5000), 30, 250);
		obj =  new OreDictStack("dustSulfur");
		if (JSTUtils.oreValid((OreDictStack)obj))
			MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 2, 9001), obj}, null, new ItemStack(JSTItems.item1, 2, 9021), null, null, 15, 400);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 1, 9001), new ItemStack(JSTItems.item1, 1, 9019)}, null, new ItemStack(JSTItems.item1, 2, 9021), null, null, 15, 400);
		st = JSTUtils.getFirstItem("dustSaltpeter", 5);
		if (!st.isEmpty()) {
			MRecipes.addChemMixerRecipe(new Object[] {new OreDictStack("dustSodium"), new ItemStack(JSTItems.item1, 1, 9016), new ItemStack(JSTItems.item1, 3, 9017)}, null, st, new ItemStack(JSTItems.item1, 4, 9000), null, 5, 100);
			if (JSTUtils.oreValid("dustSulfur"))
				MRecipes.addChemMixerRecipe(new Object[] {new OreDictStack("dustSaltpeter", 2), new OreDictStack("dustCarbon"), new OreDictStack("dustSulfur")}, null, new ItemStack(Items.GUNPOWDER, 5), null, null, 5, 100);
		}
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 1, 9005), new ItemStack(JSTItems.item1, 1, 9016)}, null, new ItemStack(Items.GUNPOWDER, 32), new ItemStack(JSTItems.item1, 2, 9000), null, 5, 100);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(Items.SUGAR, 16), new ItemStack(JSTItems.item1, 1, 9021)}, null, new ItemStack(JSTItems.item1, 16, 109), new ItemStack(JSTItems.item1, 1, 9000), null, 10, 400);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(Items.COAL, 24), new ItemStack(JSTItems.item1, 1, 9009)}, null, new ItemStack(JSTItems.item1, 1, 100), new ItemStack(JSTItems.item1, 1, 9003), null, 30, 1200);
		obj = new FluidStack(JSTFluids.chlorine, 125);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(Blocks.WOOL, 1, 32767)}, (FluidStack) obj, new ItemStack(Blocks.WOOL), null, null, 10, 200);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(Blocks.CARPET, 1, 32767)}, (FluidStack) obj, new ItemStack(Blocks.CARPET), null, null, 10, 200);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, 32767)}, (FluidStack) obj, new ItemStack(Blocks.HARDENED_CLAY), null, null, 10, 200);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(Blocks.STAINED_GLASS, 1, 32767)}, (FluidStack) obj, new ItemStack(Blocks.GLASS), null, null, 10, 200);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 32767)}, (FluidStack) obj, new ItemStack(Blocks.GLASS_PANE), null, null, 10, 200);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTBlocks.block2, 8, 0)}, (FluidStack) obj, new ItemStack(Blocks.DIRT, 8), null, null, 10, 200);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTBlocks.block2, 8, 1)}, (FluidStack) obj, new ItemStack(Blocks.SAND, 8), null, null, 10, 200);
		obj = new FluidStack(FluidRegistry.WATER, 1000);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTBlocks.block2, 8, 0)}, (FluidStack) obj, new ItemStack(Blocks.DIRT, 8), null, null, 8, 100);
		MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTBlocks.block2, 8, 1)}, (FluidStack) obj, new ItemStack(Blocks.SAND, 8), null, null, 8, 100);
		
		st = ItemList.getStackFromObj(ItemList.baseMaterial[1]);
		MRecipes.addDisassemblerRecipe(ItemList.machineBlock[1], new ItemStack[] {st, st, st, null, null, null, st, st, st}, 30, 450);
		MRecipes.addDisassemblerRecipe(new ItemStack(JSTBlocks.blockTile, 1, 21), new ItemStack[] {null, ItemList.cables[1], null, null, null, null, null, ItemList.machineBlock[1]}, 30, 450);

		MRecipes.addCokeOvenRecipe(new OreDictStack("logWood", 16), new ItemStack(Items.COAL, 32, 1), FluidRegistry.getFluidStack("creosote", 4000), 64, 400);
		MRecipes.addCokeOvenRecipe(new ItemStack(Items.COAL, 16), JSTUtils.getFirstItem("fuelCoke", 16), FluidRegistry.getFluidStack("creosote", 8000), 64, 400);
		MRecipes.addCokeOvenRecipe(new ItemStack(Blocks.COAL_BLOCK), JSTUtils.getFirstItem("blockFuelCoke"), FluidRegistry.getFluidStack("creosote", 4500), 64, 200);
		MRecipes.addCokeOvenRecipe(new OreDictStack("fuelCoke", 10), new ItemStack(JSTItems.item1, 10, 109), new FluidStack(JSTFluids.oil, 1000), 100, 200);

		fa = new FluidStack[] {FluidRegistry.getFluidStack("bio.ethanol", 2000), FluidRegistry.getFluidStack("ic2biogas", 16000)};
		MRecipes.addRefineryRecipe(FluidRegistry.getFluidStack("biomass", 4000), fa, null, 50, 200);
		MRecipes.addRefineryRecipe(FluidRegistry.getFluidStack("ic2biomass", 4000), fa, null, 50, 200);
		fa = new FluidStack[] {FluidRegistry.getFluidStack("lubricant", 500), new FluidStack(JSTFluids.heavyfuel, 1000), FluidRegistry.getFluidStack("diesel", 1500), new FluidStack(JSTFluids.fuel, 2000), FluidRegistry.getFluidStack("gasoline", 2000), new FluidStack(JSTFluids.lpg, 1500)};
		if (fa[2] == null) fa[3].amount += 1500;
		if (fa[4] == null) fa[3].amount += 2000;
		int sum = 0;
		for (FluidStack f : fa) if (f != null) sum += f.amount;
		st = JSTUtils.getFirstItem("dustSulfur");
		obj = new ItemStack[] {st, JSTUtils.getModItemStack("immersivepetroleum:material")};
		MRecipes.addRefineryRecipe(new FluidStack(JSTFluids.oil, sum), fa, (ItemStack[])obj, 120, sum / 20);
		MRecipes.addRefineryRecipe(FluidRegistry.getFluidStack("crude_oil", sum), fa, (ItemStack[])obj, 120, sum / 20);
		MRecipes.addRefineryRecipe(FluidRegistry.getFluidStack("oilgc", sum), fa, (ItemStack[])obj, 120, sum / 20);
		MRecipes.addRefineryRecipe(FluidRegistry.getFluidStack("creosote", 2000), new FluidStack[] {FluidRegistry.getFluidStack("lubricant", 1000), new FluidStack(JSTFluids.heavyfuel, 500), FluidRegistry.getFluidStack("bio.ethanol", 500)}, null, 120, 100);
		MRecipes.addRefineryRecipe(new FluidStack(JSTFluids.naturalGas, 8000), new FluidStack[] {new FluidStack(JSTFluids.lng, 6000), new FluidStack(JSTFluids.helium, 100), new FluidStack(JSTFluids.hydrogen, 2000)}, new ItemStack[] {st}, 50, 100);

		fa = new FluidStack[] {new FluidStack(FluidRegistry.WATER, 4000), new FluidStack(JSTFluids.acid, 4000), new FluidStack(JSTFluids.mercury, 4000)};
		addGrind("Iron", 4, null, false, "Nickel", false, fa);
		addGrind("Gold", 4, null, true, "Copper", false, fa);
		addGrind("Silver", 4, null, true, "Lead", false, fa);
		addGrind("Tin", 4, null, false, "Iron", false, fa);
		addGrind("Copper", 4, null, false, "Gold", true, fa);
		addGrind("Lead", 4, null, false, "Silver", true, fa);
		if (!JSTCfg.ic2Loaded)
			addGrind("Uranium", 4, null, false, "Lead", false, fa);
		addGrind("Aluminum", 4, null, false, "Bauxite", false, fa);
		addGrind("Bauxite", 4, null, false, "Titanium", false, fa);
		addGrind("Tungsten", 4, null, false, "Lithium", false, fa);
		addGrind("Redstone", 20, null, false, "Ruby", false, fa);
		addGrind("Nikolite", 20, null, false, "Diamond", false, fa);
		addGrind("Coal", 6, "gem", false, new ItemStack(JSTItems.item1, 1, 100), false, fa);
		addGrind("Diamond", 4, "gem", false, "Coal", false, fa);
		addGrind("Emerald", 4, "gem", false, "Aluminum", false, fa);
		addGrind("Ruby", 4, "gem", false, "Chrome", false, fa);
		addGrind("Sapphire", 4, "gem", false, "Aluminum", false, fa);
		addGrind("Peridot", 4, "gem", false, "Magnesium", false, fa);
		addGrind("Zinc", 4, null, false, "Tin", false, fa);
		addGrind("Niobium", 4, null, false, "Zinc", false, fa);
		addGrind("Iridium", 4, null, true, "Platinum", true, fa);
		addGrind("Platinum", 4, null, true, "Iridium", true, fa);
		addGrind("Osmium", 4, null, true, "Platinum", true, fa);
		addGrind("Naquadah", 4, null, false, new ItemStack(JSTItems.item1, 1, 60), false, fa);
		addGrind("Nickel", 4, null, false, "Platinum", true, fa);
		addGrind("Titanium", 4, null, false, "Iron", false, fa);
		addGrind("Nickel", 4, null, false, "Platinum", true, fa);
		addGrind("Rhenium", 4, null, true, "Iridium", true, fa);
		addGrind("Unobtainium", 4, null, false, "Naquadah", false, fa);
		addGrind("Lapis", 24, "gem", false, "Lapis", false, fa);
		addGrind("Amber", 4, "gem", false, null, false, fa);
		addGrind("Sulfur", 6, null, false, null, false, fa);
		addGrind("Saltpeter", 8, null, false, null, false, fa);
		addGrind("Salt", 4, null, false, null, false, fa);
		addGrind("Natron", 8, null, false, null, false, fa);
		addGrind("Quartz", 6, "gem", false, "Silicon", false, fa);
		obj = "CertusQuartz";
		addGrind(obj.toString(), 4, JSTUtils.oreValid("gem" + obj) ? "gem" : "crystal", false, "Silicon", false, fa);
		obj = "Dilithium";
		addGrind(obj.toString(), 4, JSTUtils.oreValid("gem" + obj) ? "gem" : "crystal", false, "Platinum", true, fa);

		fa[0] = JSTUtils.modFStack(fa[0], 1000);
		MRecipes.addCrystalRecipe(new OreDictStack("dustDiamond"), null, fa[0], new ItemStack(Items.DIAMOND), 64, 600);
		MRecipes.addCrystalRecipe(new OreDictStack("dustEmerald"), null, fa[0], new ItemStack(Items.EMERALD), 24, 400);
		MRecipes.addCrystalRecipe(new OreDictStack("dustCoal"), null, fa[0], new ItemStack(Items.COAL), 24, 400);
		MRecipes.addCrystalRecipe(new OreDictStack("dustRuby"), null, fa[0], new ItemStack(JSTItems.item1, 1, 16), 24, 400);
		MRecipes.addCrystalRecipe(new OreDictStack("dustPeridot"), null, fa[0], new ItemStack(JSTItems.item1, 1, 17), 24, 400);
		MRecipes.addCrystalRecipe(new OreDictStack("dustSapphire"), null, fa[0], new ItemStack(JSTItems.item1, 1, 18), 24, 400);
		MRecipes.addCrystalRecipe(new OreDictStack("dustLapis"), null, fa[0], new ItemStack(Items.DYE, 1, 4), 24, 400);
		MRecipes.addCrystalRecipe(new OreDictStack("dustNetherQuartz"), null, fa[0], new ItemStack(Items.QUARTZ), 24, 400);
		MRecipes.addCrystalRecipe(new OreDictStack("dustCertusQuartz"), null, fa[0], JSTUtils.getValidOne("gemCertusQuartz", "crystalCertusQuartz"), 24, 400);
		MRecipes.addCrystalRecipe(new OreDictStack("dustFluix"), null, fa[0], JSTUtils.getValidOne("gemFluix", "crystalFluix"), 24, 400);
		MRecipes.addCrystalRecipe(new OreDictStack("dustAmethyst"), null, fa[0], JSTUtils.getFirstItem("gemAmethyst"), 24, 400);
		MRecipes.addCrystalRecipe(new OreDictStack("dustAmber"), null, fa[0], JSTUtils.getFirstItem("gemAmber"), 24, 400);
		MRecipes.addCrystalRecipe(new OreDictStack("dustDilithium"), null, fa[0], JSTUtils.getValidOne("gemDilithium", "crystalDilithium"), 24, 400);
		MRecipes.addCrystalRecipe(new OreDictStack("dustCarbon", 32), new OreDictStack("dustNiobium"), null, new ItemStack(JSTItems.item1, 1, 150), 250, 400);
		MRecipes.addCrystalRecipe(new OreDictStack("dustCarbon", 32), JSTUtils.getValidOne("dustGold", "ingotGold"), null, new ItemStack(JSTItems.item1, 1, 150), 250, 400);
		MRecipes.HeatExcFakeRecipes.add(RecipeContainer.newContainer(null, new FluidStack[] {new FluidStack(FluidRegistry.WATER, 2), new FluidStack(FluidRegistry.LAVA, 333)}, null, new FluidStack[] {new FluidStack(JSTFluids.steam, 16000), FluidRegistry.getFluidStack("ic2pahoehoe_lava", 333)}, 0, 0));
	}

	private static void addGrind(String ore, int mu, String pf, boolean m, Object s1, boolean m1, FluidStack[] pr) {
		Object[] in = new Object[] {new OreDictStack("ore" + ore, 4)};
		if (pf == null)
			pf = "dust";
		ItemStack out = ItemStack.EMPTY;
		if (s1 instanceof ItemStack)
			out = (ItemStack) s1;
		else if (s1 instanceof String)
			out = JSTUtils.getFirstItem("dust" + s1, 1);
		MRecipes.addOreProcessRecipe(in , pr[0], new ItemStack[] {JSTUtils.getFirstItem(pf + ore, Math.min(mu * 2, 64)), out}, 64, 400);
		MRecipes.addOreProcessRecipe(in, pr[1], new ItemStack[] {JSTUtils.getFirstItem(pf + ore, Math.min(mu * 3, 64)), JSTUtils.modStack(out, 2, -1)}, 64, 400);
		if (m || m1)
			MRecipes.addOreProcessRecipe(in, pr[2], new ItemStack[] {JSTUtils.getFirstItem(pf + ore, Math.min(mu * (m ? 4 : 2), 64)), JSTUtils.modStack(out, m1 ? 4 : 1, -1)}, 64, 400);
	}
}

