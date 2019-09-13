package dohyun22.jst3.compat;

import blusunrize.immersiveengineering.api.energy.ThermoelectricHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler.ChemthrowerEffect;
import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;
import blusunrize.immersiveengineering.api.tool.RailgunHandler;
import blusunrize.immersiveengineering.common.util.IEDamageSources;
import blusunrize.immersiveengineering.common.util.IEDamageSources.IEDamageSource;
import blusunrize.immersiveengineering.common.util.IEPotions;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.loader.RecipeLoader;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.compat.ic2.CompatIC2;
import dohyun22.jst3.recipes.ItemList;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTDamageSource.EnumHazard;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTPotions;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.info.Info;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import blusunrize.immersiveengineering.api.energy.DieselHandler;

public class CompatIE extends Loadable {

	@Override
	public String getRequiredMod() {
		return "immersiveengineering";
	}

	@Override
	@Method(modid = "immersiveengineering")
	public void init() {
		ThermoelectricHandler.registerSourceInKelvin("blockNaquadah", 4000);
		ThermoelectricHandler.registerSourceInKelvin("blockNaquadahAlloy", 3000);
		ThermoelectricHandler.registerSourceInKelvin("blockNaquadahEnriched", 5000);
		ThermoelectricHandler.registerSourceInKelvin("blockNaquadria", 6000);
		ThermoelectricHandler.registerSourceInKelvin("blockPlutonium241", 4500);
		ThermoelectricHandler.registerSourceInKelvin("blockUranium235", 4000);
		ThermoelectricHandler.registerSourceInKelvin("blockThorium", 650);
		ThermoelectricHandler.registerSourceInKelvin("blockPromethium", 1500);
		ThermoelectricHandler.registerSourceInKelvin("blockPolonium", 2000);
		ThermoelectricHandler.registerSourceInKelvin("blockTechnetium", 2000);

		DieselHandler.registerFuel(JSTFluids.nitrofuel, 750);
		DieselHandler.registerFuel(JSTFluids.heavyfuel, 75);
		DieselHandler.registerDrillFuel(JSTFluids.nitrofuel);
		if (JSTCfg.gtceLoaded) {
			Fluid f = FluidRegistry.getFluid("sulfuric_light_fuel");
			DieselHandler.registerFuel(f, JSTCfg.BuffIEDieselGen ? 125 : 50);
			DieselHandler.registerDrillFuel(f);
			f = FluidRegistry.getFluid("light_fuel");
			DieselHandler.registerFuel(f, JSTCfg.BuffIEDieselGen ? 500 : 200);
			DieselHandler.registerDrillFuel(f);
		}
		if (JSTCfg.ic2Loaded) {
			ChemthrowerHandler.registerFlammable("ic2hydrogen");
			ChemthrowerHandler.registerFlammable("ic2biogas");
			ChemthrowerEffect eff = new ChemthrowerHandler.ChemthrowerEffect_Extinguish();
			ChemthrowerHandler.registerEffect("ic2distilled_water", eff);
			ChemthrowerHandler.registerEffect("ic2heavy_water", eff);
			ChemthrowerHandler.registerEffect("ic2coolant", eff);
			ChemthrowerHandler.registerEffect("ic2uu_matter", new ChemthrowerHandler.ChemthrowerEffect_Potion(null, 0.0F, MobEffects.REGENERATION, 100, 2));
			ChemthrowerHandler.registerEffect("ic2hot_water", new ChemthrowerHandler.ChemthrowerEffect_Potion(null, 0.0F, MobEffects.REGENERATION, 80, 2));
			ChemthrowerHandler.registerEffect("ic2construction_foam", new ChemthrowerHandler.ChemthrowerEffect_Potion(null, 0.0F, MobEffects.SLOWNESS, 100, 2));
			ChemthrowerHandler.registerEffect("ic2weed_ex", new ChemthrowerHandler.ChemthrowerEffect_Potion(null, 0.0F, MobEffects.POISON, 100, 1));
			ChemthrowerHandler.registerEffect("ic2steam", new ChemthrowerHandler.ChemthrowerEffect_Damage(DamageSource.ON_FIRE, 1.0F));
			ChemthrowerHandler.registerEffect("ic2superheated_steam", new ChemthrowerHandler.ChemthrowerEffect_Damage(DamageSource.ON_FIRE, 4.0F));
		}
		if (JSTCfg.bcLoaded) {
			ChemthrowerHandler.registerFlammable("fuel_light");
			ChemthrowerHandler.registerFlammable("fuel_light_heat_1");
			ChemthrowerHandler.registerFlammable("fuel_light_heat_2");
			ChemthrowerHandler.registerFlammable("fuel_mixed_light");
			ChemthrowerHandler.registerFlammable("fuel_mixed_light_heat_1");
			ChemthrowerHandler.registerFlammable("fuel_mixed_light_heat_2");
			ChemthrowerHandler.registerFlammable("fuel_mixed_heavy");
			ChemthrowerHandler.registerFlammable("fuel_mixed_heavy_heat_1");
			ChemthrowerHandler.registerFlammable("fuel_mixed_heavy_heat_2");
			ChemthrowerHandler.registerFlammable("fuel_dense");
			ChemthrowerHandler.registerFlammable("fuel_dense_heat_1");
			ChemthrowerHandler.registerFlammable("fuel_dense_heat_2");
			ChemthrowerHandler.registerFlammable("oil_dense");
			ChemthrowerHandler.registerFlammable("oil_dense_heat_1");
			ChemthrowerHandler.registerFlammable("oil_dense_heat_2");
			ChemthrowerHandler.registerFlammable("oil_heat_1");
			ChemthrowerHandler.registerFlammable("oil_heat_2");
			ChemthrowerHandler.registerFlammable("oil_heavy");
			ChemthrowerHandler.registerFlammable("oil_heavy_heat_1");
			ChemthrowerHandler.registerFlammable("oil_heavy_heat_2");
			ChemthrowerHandler.registerFlammable("oil_distilled");
			ChemthrowerHandler.registerFlammable("oil_distilled_heat_1");
			ChemthrowerHandler.registerFlammable("oil_distilled_heat_2");
			ChemthrowerHandler.registerFlammable("fuel_gaseous");
			ChemthrowerHandler.registerFlammable("fuel_gaseous_heat_1");
			ChemthrowerHandler.registerFlammable("fuel_gaseous_heat_2");
		}
		Fluid f = FluidRegistry.getFluid("rocketfuel");
		ChemthrowerHandler.registerEffect(f, new ChemthrowerHandler.ChemthrowerEffect_Potion(null, 0.0F, IEPotions.flammable, 100, 2));
		ChemthrowerHandler.registerFlammable(f);
		ChemthrowerHandler.registerFlammable("hydrogen");
		ChemthrowerHandler.registerEffect(JSTFluids.mercury, new ChemthrowerHandler.ChemthrowerEffect_Potion(null, 0.0F, new PotionEffect(MobEffects.POISON, 300, 1), new PotionEffect(IEPotions.conductive, 100, 1)));
		ChemthrowerHandler.registerEffect(JSTFluids.chlorine, new ChemthrowerHandler.ChemthrowerEffect_Potion(null, 0.0F, new PotionEffect(MobEffects.POISON, 150, 2)));
		ChemthrowerHandler.registerFlammable(JSTFluids.deuterium);
		DamageSource dmg = null;
		try { if (JSTCfg.ic2Loaded) dmg = Info.DMG_RADIATION;} catch (Throwable t) {}
		ChemthrowerHandler.registerEffect(JSTFluids.tritium, new ChemthrowerHandler.ChemthrowerEffect_Potion(dmg, dmg == null ? 0.0F : 0.5F, new PotionEffect(MobEffects.POISON, 100, 0)));
		ChemthrowerHandler.registerFlammable(JSTFluids.tritium);
		ChemthrowerHandler.registerFlammable(JSTFluids.naturalGas);
		ChemthrowerHandler.registerFlammable(JSTFluids.lng);
		ChemthrowerHandler.registerFlammable(JSTFluids.lpg);
		ChemthrowerHandler.registerGas(JSTFluids.lng);
		ChemthrowerHandler.registerGas(JSTFluids.lpg);
		ChemthrowerHandler.registerEffect(JSTFluids.nitrofuel, new ChemthrowerHandler.ChemthrowerEffect_Potion(null, 0.0F, IEPotions.flammable, 100, 3));
		ChemthrowerHandler.registerFlammable(JSTFluids.nitrofuel);
		ChemthrowerHandler.registerFlammable(JSTFluids.lithium);
		ChemthrowerHandler.registerFlammable(JSTFluids.sodium);
		ChemthrowerHandler.registerFlammable(JSTFluids.heavyfuel);
		ChemthrowerEffect eff = new ChemthrowerHandler.ChemthrowerEffect_Potion(DamageSource.ON_FIRE, 0.5F, new PotionEffect(IEPotions.conductive, 100, 1), new PotionEffect(IEPotions.flammable, 100, 0)) {
			@Override		
			public void applyToBlock(World w, RayTraceResult mop, @Nullable EntityPlayer pl, ItemStack st, Fluid f) {
				if (w.rand.nextInt(8) == 0) {
					BlockPos p = mop.getBlockPos().offset(mop.sideHit);
					if (w.getBlockState(p).getMaterial() == Material.WATER)
						w.createExplosion(pl, p.getX() + 0.5F, p.getY() + 0.5F, p.getZ() + 0.5F, 0.5F + w.rand.nextFloat(), false);
				}
			}
			
			@Override
			public void applyToEntity(EntityLivingBase elb, @Nullable EntityPlayer pl, ItemStack st, Fluid f) {
				super.applyToEntity(elb, pl, st, f);
				World w = elb.world;
				if (w.rand.nextInt(3) == 0 && elb.isWet())
					w.createExplosion(pl, elb.posX + 0.5F, elb.posY + 0.5F, elb.posZ + 0.5F, 0.5F + w.rand.nextFloat(), false);
			}
		};
		ChemthrowerHandler.registerEffect(JSTFluids.lithium, eff);
		ChemthrowerHandler.registerEffect(JSTFluids.sodium, eff);
		ChemthrowerHandler.registerEffect("potassium", eff);
		ChemthrowerHandler.registerEffect("rubidium", eff);
		ChemthrowerHandler.registerEffect("cesium", eff);
		ChemthrowerHandler.registerEffect("acid", new ChemthrowerHandler.ChemthrowerEffect_Damage(IEDamageSources.acid, 4.0F));
		
		RailgunHandler.registerProjectileProperties(new IngredientStack("ingotTungsten"), 40.0, 2.0).setColourMap(new int[][] { { 0x5A5A64, 0x5A5A64, 0x5A5A64, 0x464650, 0x32323C, 0x32323C } });
		RailgunHandler.registerProjectileProperties(new IngredientStack("ingotTitanium"), 35.0, 1.1).setColourMap(new int[][] { { 0xBE78C8, 0xBE78C8, 0xBE78C8, 0x963CA0, 0x5A2864, 0x5A2864 } });
		RailgunHandler.registerProjectileProperties(new IngredientStack("ingotIridium"), 60.0, 2.5).setColourMap(new int[][] { { 0xC8FAFA, 0xC8FAFA, 0xC8FAFA, 0xB4E6E6, 0x96D2D2, 0x96D2D2 } });
	
		ExcavatorHandler.addMineral("Nikolite", 25, 0.2F, new String[] { "oreNikolite", "oreDiamond" }, new float[] { 0.97F, 0.03F });
		ExcavatorHandler.addMineral("Tungsten", 5, 0.35F, new String[] { "oreTungsten", "oreIron", "dustLithium" }, new float[] { 0.3F, 0.6F, 0.1F });
		ExcavatorHandler.addMineral("Sphalerite", 20, 0.2F, new String[] { "oreZinc", "oreSulfur" }, new float[] { 0.5F, 0.5F });
		ExcavatorHandler.addMineral("Sapphire", 10, 0.4F, new String[] { "oreSapphire", "oreAluminum" }, new float[] { 0.8F, 0.2F });
	}
	
	@Override
	public void postInit() {
		Item i = JSTUtils.getModItem("immersiveengineering:material");
		ItemStack st;
		Object obj = ItemList.molds[0];
		if (i != null) {
			OreDictionary.registerOre("circuitBasic", new ItemStack(i, 1, 27));
			if (JSTCfg.ic2Loaded) {
				CompatIC2.addFormerRecipe(new OreDictStack("plateCopper"), new ItemStack(i, 3, 20), 1);
				CompatIC2.addFormerRecipe(new OreDictStack("plateElectrum"), new ItemStack(i, 3, 21), 1);
				st = new ItemStack(i, 3, 22);
				CompatIC2.addFormerRecipe(new OreDictStack("plateAluminum"), st, 1);
				CompatIC2.addFormerRecipe(new OreDictStack("plateAluminium"), st, 1);
				CompatIC2.addFormerRecipe(new OreDictStack("plateSteel"), new ItemStack(i, 3, 23), 1);
			}
			MRecipes.addPressRecipe(new OreDictStack("plateCopper"), obj, new ItemStack(i, 3, 20), null, 4, 50);
			MRecipes.addPressRecipe(new OreDictStack("plateElectrum"), obj, new ItemStack(i, 3, 21), null, 4, 50);
			st = new ItemStack(i, 3, 22);
			MRecipes.addPressRecipe(new OreDictStack("plateAluminum"), obj, st, null, 4, 50);
			MRecipes.addPressRecipe(new OreDictStack("plateAluminium"), obj, st, null, 4, 50);
			MRecipes.addPressRecipe(new OreDictStack("plateSteel"), obj, new ItemStack(i, 3, 23), null, 4, 50);
		}
		MRecipes.addPressRecipe(new OreDictStack("plateConstantan"), obj, JSTUtils.getFirstItem("wireConstantan", 3), null, 4, 50);
		MRecipes.addChemMixerRecipe(new Object[] {new OreDictStack("dustAluminum")}, new FluidStack(JSTFluids.nitrofuel, 2000), null, null, FluidRegistry.getFluidStack("napalm", 2000), 8, 200);

		st = new ItemStack(JSTItems.item1, 1, 105);
		i = JSTUtils.getModItem("immersiveengineering:connector");
		if (i != null) {
			RecipeLoader.addShapedRecipe(new ItemStack(i, 4), " I ", "PIP", "PIP", 'P', st, 'I', "ingotCopper");
			RecipeLoader.addShapedRecipe(new ItemStack(i, 8, 1), " I ", "PIP", 'P', st, 'I', "ingotCopper");
			RecipeLoader.addShapedRecipe(new ItemStack(i, 4, 2), " I ", "PIP", "PIP", 'P', st, 'I', "ingotIron");
			RecipeLoader.addShapedRecipe(new ItemStack(i, 8, 3), " I ", "PIP", 'P', st, 'I', "ingotIron");
			RecipeLoader.addShapedRecipe(new ItemStack(i, 4, 4), " I ", "PIP", "PIP", 'P', st, 'I', "ingotAluminum");
		}
		
		RecipeLoader.addShapedRecipe(JSTUtils.getModItemStack("immersiveengineering:stone_decoration", 8, 8), " G ", "GFG", " G ", 'G', "blockGlass", 'F', new ItemStack(JSTItems.item1, 1, 106));
	
		if (JSTCfg.BuffIEDieselGen) {
			DieselHandler.registerFuel(FluidRegistry.getFluid("biodiesel"), 480);
			DieselHandler.registerFuel(FluidRegistry.getFluid("fuel"), 500);
			DieselHandler.registerFuel(FluidRegistry.getFluid("diesel"), 500);
		}

		obj = FluidRegistry.getFluidStack("biodiesel", 5000);
		if (obj != null) {
			Object o2 = FluidRegistry.getFluidStack("plantoil", 4000);
			if (o2 != null) {
				MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 1, 9030)}, (FluidStack) o2, new ItemStack(JSTItems.item1, 1, 9000), null, (FluidStack) obj, 20, 400);
				MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 4, 9032), new ItemStack(JSTItems.item1, 1, 9030)}, null, new ItemStack(JSTItems.item1, 5, 9031), null, null, 20, 400);
			}
			o2 = FluidRegistry.getFluidStack("seed.oil", 4000);
			if (o2 != null) {
				MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 1, 9030)}, (FluidStack) o2, new ItemStack(JSTItems.item1, 1, 9000), null, (FluidStack) obj, 20, 400);
				MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 4, 9033), new ItemStack(JSTItems.item1, 1, 9030)}, null, new ItemStack(JSTItems.item1, 5, 9031), null, null, 20, 400);
			}
			o2 = new FluidStack[] {FluidRegistry.getFluidStack("tree_oil", 4000), FluidRegistry.getFluidStack("seed_oil", 4000), FluidRegistry.getFluidStack("fish_oil", 4000)};
			for (FluidStack fs : (FluidStack[])o2) if (fs != null) MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 1, 9030)}, fs, new ItemStack(JSTItems.item1, 1, 9000), null, (FluidStack) obj, 20, 400);
		}
		
		JSTDamageSource.addHazmat(EnumHazard.ELECTRIC, JSTUtils.getModItemStack("immersiveengineering:faraday_suit_head", 1, 32767));
		JSTDamageSource.addHazmat(EnumHazard.ELECTRIC, JSTUtils.getModItemStack("immersiveengineering:faraday_suit_chest", 1, 32767));
		JSTDamageSource.addHazmat(EnumHazard.ELECTRIC, JSTUtils.getModItemStack("immersiveengineering:faraday_suit_legs", 1, 32767));
		JSTDamageSource.addHazmat(EnumHazard.ELECTRIC, JSTUtils.getModItemStack("immersiveengineering:faraday_suit_feet", 1, 32767));
	}
}
