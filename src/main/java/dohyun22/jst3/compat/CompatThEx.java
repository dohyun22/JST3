package dohyun22.jst3.compat;

import java.util.List;

import cofh.api.util.ThermalExpansionHelper;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.EffectBlocks;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.oredict.OreDictionary;

public class CompatThEx extends Loadable {

	@Override
	public String getRequiredMod() {
		return null;
	}

	@Override
	public boolean canLoad() {
		return JSTCfg.tfLoaded;
	}
	
	@Override
	public void init() {
		if (!Loader.isModLoaded("thermalexpansion")) return;
		try {
			ThermalExpansionHelper.addCompressionFuel("fuel", 2000000);
			ThermalExpansionHelper.addCompressionFuel("nitrofuel", 4000000);
			ThermalExpansionHelper.addCompressionFuel("liquid_lng", 1000000);
			ThermalExpansionHelper.addCompressionFuel("liquid_lpg", 1000000);
			ThermalExpansionHelper.addCompressionFuel("heavyfuel", 480000);
			ThermalExpansionHelper.addPulverizerRecipe(2000, new ItemStack(JSTItems.item1, 1, 73), new ItemStack(JSTItems.item1, 1, 74), ItemStack.EMPTY, 0);
			ThermalExpansionHelper.addPulverizerRecipe(2000, new ItemStack(JSTItems.item1, 1, 89), new ItemStack(JSTItems.item1, 1, 90), ItemStack.EMPTY, 0);
			
			ItemStack st = new ItemStack(Items.REDSTONE, 4);
			ThermalExpansionHelper.addSmelterRecipe(4000, st, JSTUtils.getFirstItem("ingotCopper", 1), new ItemStack(JSTItems.item1, 1, 25));
			ThermalExpansionHelper.addSmelterRecipe(4000, st, JSTUtils.getFirstItem("dustCopper", 1), new ItemStack(JSTItems.item1, 1, 25));
			st = JSTUtils.getFirstItem("dustNikolite", 4);
			ThermalExpansionHelper.addSmelterRecipe(4000, st, JSTUtils.getFirstItem("ingotSilver", 1), new ItemStack(JSTItems.item1, 1, 26));
			ThermalExpansionHelper.addSmelterRecipe(4000, st, JSTUtils.getFirstItem("dustSilver", 1), new ItemStack(JSTItems.item1, 1, 26));
			st = JSTUtils.getFirstItem("ingotCopper", 3);
			ThermalExpansionHelper.addSmelterRecipe(4000, st, JSTUtils.getFirstItem("ingotZinc", 1), new ItemStack(JSTItems.item1, 4, 36));
			ThermalExpansionHelper.addSmelterRecipe(4000, st, JSTUtils.getFirstItem("dustZinc", 1), new ItemStack(JSTItems.item1, 4, 36));
			st = JSTUtils.getFirstItem("dustCopper", 3);
			ThermalExpansionHelper.addSmelterRecipe(4000, st, JSTUtils.getFirstItem("ingotZinc", 1), new ItemStack(JSTItems.item1, 4, 36));
			ThermalExpansionHelper.addSmelterRecipe(4000, st, JSTUtils.getFirstItem("dustZinc", 1), new ItemStack(JSTItems.item1, 4, 36));
			st = JSTUtils.getFirstItem("ingotTin", 6);
			ThermalExpansionHelper.addSmelterRecipe(4000, st, JSTUtils.getFirstItem("ingotLead", 4), new ItemStack(JSTItems.item1, 10, 183));
			ThermalExpansionHelper.addSmelterRecipe(4000, st, JSTUtils.getFirstItem("dustLead", 4), new ItemStack(JSTItems.item1, 10, 183));
			st = JSTUtils.getFirstItem("dustTin", 6);
			ThermalExpansionHelper.addSmelterRecipe(4000, st, JSTUtils.getFirstItem("ingotLead", 4), new ItemStack(JSTItems.item1, 10, 183));
			ThermalExpansionHelper.addSmelterRecipe(4000, st, JSTUtils.getFirstItem("dustLead", 4), new ItemStack(JSTItems.item1, 10, 183));
		} catch (Throwable t) {}
	}

	@Override
	public void postInit() {
		String str = "thermalfoundation:fertilizer";
		MRecipes.addFertilizer(JSTUtils.getModItemStack(str));
		MRecipes.addFertilizer(JSTUtils.getModItemStack(str, 1, 1));
		MRecipes.addFertilizer(JSTUtils.getModItemStack(str, 1, 2));

		for (String s : new String[] {"oreFluidCrudeOilSand", "oreFluidCrudeOilShale", "oreClathrateOilSand", "oreClathrateOilShale"})
			MRecipes.addSeparatorRecipe(new OreDictStack(s), null, null, null, new FluidStack(JSTFluids.oil, 1000), 16, 100);
		FluidStack fs = FluidRegistry.getFluidStack("crude_oil", 5000);
		if (fs != null)
			MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 2, 9000), null, fs, new ItemStack[] {new ItemStack(JSTItems.item1, 2, 9008), JSTUtils.getFirstItem("dustSulfur")}, new FluidStack(JSTFluids.fuel, 4000), 30, 2000);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("dustObsidian", 4), new OreDictStack("ingotLead"), JSTUtils.getModItemStack("thermalfoundation:glass", 2, 3), 10, 100);
		MRecipes.addChemMixerRecipe(new Object[] {JSTUtils.getModItemStack("thermalfoundation:rockwool", 1, 32767)}, new FluidStack(JSTFluids.chlorine, 125), JSTUtils.getModItemStack("thermalfoundation:rockwool", 1, 15), null, null, 10, 200);
		MRecipes.addRefineryRecipe(FluidRegistry.getFluidStack("biocrude", 4000), new FluidStack[] {FluidRegistry.getFluidStack("refined_biofuel", 2000), FluidRegistry.getFluidStack("ic2biogas", 4000)}, null, 60, 100);
		MRecipes.addRefineryRecipe(FluidRegistry.getFluidStack("resin", 4000), new FluidStack[] {FluidRegistry.getFluidStack("tree_oil", 2000), FluidRegistry.getFluidStack("ic2biogas", 4000)}, new ItemStack[] {JSTUtils.getModItemStack("thermalfoundation:material", 5, 832)}, 60, 100);
		MRecipes.addRefineryRecipe(FluidRegistry.getFluidStack("coal", 6000), new FluidStack[] {new FluidStack(JSTFluids.oil, 5000), new FluidStack(JSTFluids.heavyfuel, 500), FluidRegistry.getFluidStack("creosote", 500)}, null, 120, 200);
		ItemStack st = JSTUtils.getModItemStack("thermalfoundation:material", 1, 866);
		str = "dustSulfur";
		MRecipes.addSeparatorRecipe(st, null, null, new ItemStack[] {JSTUtils.getFirstItem(str)}, new FluidStack(JSTFluids.mercury, 1000), 24, 150);
		MRecipes.addChemMixerRecipe(new Object[] {new OreDictStack(str), new ItemStack(JSTItems.item1, 1, 9022)}, null, st, new ItemStack(JSTItems.item1, 1, 9000), null, 24, 300);

		Block b = JSTUtils.getModBlock("thermalfoundation:ore_fluid");
		EffectBlocks.addOre(b, false);
		EffectBlocks.addOre(b, true);
	}
}
