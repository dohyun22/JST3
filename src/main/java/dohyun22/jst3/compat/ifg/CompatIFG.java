package dohyun22.jst3.compat.ifg;

import com.buuz135.industrial.api.IndustrialForegoingHelper;
import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.api.recipe.BioReactorEntry;
import com.buuz135.industrial.api.recipe.SludgeEntry;
import com.buuz135.industrial.api.straw.StrawHandler;

import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.compat.ifg.CSH;
import dohyun22.jst3.compat.ifg.PlantIC2Crop;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.loader.RecipeLoader;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public class CompatIFG extends Loadable {

	@Override
	public String getRequiredMod() {
		return "industrialforegoing";
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void postInit() {
		ItemStack st = JSTUtils.getModItemStack("industrialforegoing:fertilizer");
		MRecipes.addFertilizer(st);
		FluidStack fs = JSTCfg.ic2Loaded ? FluidRegistry.getFluidStack("ic2biogas", 250) : new FluidStack(JSTFluids.lng, 50);
		MRecipes.addSeparatorRecipe(null, null, FluidRegistry.getFluidStack("sewage", 2000), null, fs, 10, 200);
		MRecipes.addRefineryRecipe(FluidRegistry.getFluidStack("sewage", 8000), new FluidStack[] {FluidRegistry.getFluidStack("biofuel", 100), JSTUtils.modFStack(fs, fs.amount * 5)}, new ItemStack[] {new ItemStack(Blocks.DIRT), st}, 40, 200);
		RecipeLoader.addShapedRecipe(JSTUtils.getModItemStack("industrialforegoing:plastic", 12), "PP", "PP", 'P', new ItemStack(JSTItems.item1, 1, 105));
		addSludgeItem(new ItemStack(JSTBlocks.block2, 1, 0), 1);
		addSludgeItem(new ItemStack(JSTBlocks.block2, 1, 1), 1);
		String s;
		if (JSTCfg.ic2Loaded) {
			s = "ic2:crafting";
			addBioReactorItem(JSTUtils.getModItemStack(s, 1, 20));
			addBioReactorItem(JSTUtils.getModItemStack(s, 1, 21));
			addSludgeItem(JSTUtils.getModItemStack(s, 1, 23), 6);
			OreDictionary.registerOre("fertilizer", JSTUtils.getModItemStack("ic2:crop_res", 1, 2));
		}
		if (JSTCfg.tfLoaded) {
			s = "thermalfoundation:fertilizer";
			OreDictionary.registerOre("fertilizer", JSTUtils.getModItemStack(s, 1, 0));
			OreDictionary.registerOre("fertilizer", JSTUtils.getModItemStack(s, 1, 1));
			OreDictionary.registerOre("fertilizer", JSTUtils.getModItemStack(s, 1, 2));
		}
	}

	@Method(modid="industrialforegoing")
	@SubscribeEvent
	public void regPlant(RegistryEvent.Register<PlantRecollectable> ev) {
		IForgeRegistry<PlantRecollectable> reg = ev.getRegistry();
		if (JSTCfg.ic2Loaded) reg.register(new PlantIC2Crop());
	}

	@Method(modid="industrialforegoing")
	@SubscribeEvent
	public void regStraw(RegistryEvent.Register<StrawHandler> ev) {
		IForgeRegistry<StrawHandler> reg = ev.getRegistry();
		regStrawEffect(reg, "nitrofuel", 2);
		regStrawEffect(reg, "fuel", 1);
		regStrawEffect(reg, "heavyfuel", 1);
		regStrawEffect(reg, "mercury", new PotionEffect(MobEffects.POISON, 1200, 2), new PotionEffect(MobEffects.SLOWNESS, 1200, 2));
		regStrawEffect(reg, "acid", new PotionEffect(MobEffects.POISON, 1200, 3));
		regStrawEffect(reg, "helium", new PotionEffect(MobEffects.LEVITATION, 600));
		regStrawEffect(reg, "hydrogen", new PotionEffect(MobEffects.LEVITATION, 300), 1);
		regStrawEffect(reg, "bio.ethanol", new PotionEffect(MobEffects.NAUSEA, 1200));
		regStrawEffect(reg, "biomass", new PotionEffect(MobEffects.SLOWNESS, 600));
		if (JSTCfg.ic2Loaded) {
			regStrawEffect(reg, "ic2biogas", new PotionEffect(MobEffects.NAUSEA, 1200), new PotionEffect(MobEffects.LEVITATION, 100));
			regStrawEffect(reg, "ic2weed_ex", new PotionEffect(MobEffects.POISON, 1200, 3));
			regStrawEffect(reg, "ic2distilled_water", 5);
			regStrawEffect(reg, "ic2coolant", 5);
			regStrawEffect(reg, "ic2hot_coolant", 2);
			regStrawEffect(reg, "ic2pahoehoe_lava", 2, new Object[] {DamageSource.LAVA, 7.0F});
			regStrawEffect(reg, "ic2uu_matter", new PotionEffect(MobEffects.REGENERATION, 30000, 5), new PotionEffect(MobEffects.ABSORPTION, 30000, 4));
		}
	}

	public static void addBioReactorItem(ItemStack in) {
		if (in != null && !in.isEmpty()) try {IndustrialForegoingHelper.addBioReactorEntry(new BioReactorEntry(in));} catch (Throwable t) {}
	}

	public static void addSludgeItem(ItemStack in, int w) {
		if (in != null && !in.isEmpty()) try {IndustrialForegoingHelper.addSludgeRefinerEntry(new SludgeEntry(in, w));} catch (Throwable t) {}
	}

	@Method(modid="industrialforegoing")
	private void regStrawEffect(IForgeRegistry<StrawHandler> reg, String fl, Object... eff) {
		if (!FluidRegistry.isFluidRegistered(fl)) return;
		reg.register(new CSH(fl, eff).setRegistryName("jst_" + fl));
	}
}
