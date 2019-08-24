package dohyun22.jst3.compat.ic2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.compat.ic2.BehaviorNeutDoubler;
import dohyun22.jst3.compat.ic2.BehaviourDepleted;
import dohyun22.jst3.compat.ic2.BehaviourEnrichable;
import dohyun22.jst3.compat.ic2.BehaviourFuelRod;
import dohyun22.jst3.compat.ic2.BehaviourHeatExc;
import dohyun22.jst3.compat.ic2.BehaviourHeatStorage;
import dohyun22.jst3.compat.ic2.BehaviourVent;
import dohyun22.jst3.compat.ic2.BehaviourVentComponent;
import dohyun22.jst3.compat.ic2.CropElecMushroom;
import dohyun22.jst3.compat.ic2.CropJST;
import dohyun22.jst3.compat.ic2.ItemReactorComponent;
import dohyun22.jst3.compat.ic2.CropJSTBase;
import dohyun22.jst3.compat.ic2.ReactorItemBehaviour;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.loader.RecipeLoader;
import dohyun22.jst3.api.recipe.AdvRecipeItem;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.recipes.ItemList;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import dohyun22.jst3.utils.JSTDamageSource.EnumHazard;
import ic2.api.crops.BaseSeed;
import ic2.api.crops.CropSoilType;
import ic2.api.crops.Crops;
import ic2.api.crops.ICropTile;
import ic2.api.recipe.IBasicMachineRecipeManager;
import ic2.api.recipe.ICannerBottleRecipeManager;
import ic2.api.recipe.ILiquidHeatExchangerManager.HeatExchangeProperty;
import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import ic2.api.recipe.Recipes;
import ic2.api.tile.ExplosionWhitelist;
import ic2.core.item.armor.ItemArmorHazmat;
import ic2.core.uu.UuIndex;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class CompatIC2 extends Loadable {
	private static Field crop_dirty;
	public static Item jstn;

	@Override
	public boolean canLoad() {
		return JSTCfg.ic2Loaded;
	}

	@Override
	public String getRequiredMod() {
		return null;
	}

	@Override
	@Method(modid = "ic2")
	public void preInit() {
		jstn = new ItemReactorComponent();
		ForgeRegistries.ITEMS.register(jstn);

		ItemReactorComponent rc = (ItemReactorComponent)jstn;
		rc.registerBehaviour(1, new BehaviourFuelRod(1, 50000, 0.4F, 0.25F, new ItemStack(rc, 1, 4), false, 0));
		rc.registerBehaviour(2, new BehaviourFuelRod(2, 50000, 0.4F, 0.25F, new ItemStack(rc, 1, 5), false, 0));
		rc.registerBehaviour(3, new BehaviourFuelRod(4, 50000, 0.4F, 0.25F, new ItemStack(rc, 1, 6), false, 0));
		ReactorItemBehaviour d = new BehaviourDepleted(10);
		rc.registerBehaviour(4, d);
		rc.registerBehaviour(5, d);
		rc.registerBehaviour(6, d);
		rc.registerBehaviour(7, new BehaviourFuelRod(1, 20000, 2.5F, 2.5F, new ItemStack(rc, 1, 10), false, 60));
		rc.registerBehaviour(8, new BehaviourFuelRod(2, 20000, 2.5F, 2.5F, new ItemStack(rc, 1, 11), false, 60));
		rc.registerBehaviour(9, new BehaviourFuelRod(4, 20000, 2.5F, 2.5F, new ItemStack(rc, 1, 12), false, 60));
		d = new BehaviourDepleted(60);
		rc.registerBehaviour(10, d);
		rc.registerBehaviour(11, d);
		rc.registerBehaviour(12, d);
		rc.registerBehaviour(13, new BehaviourFuelRod(1, 25000, 3.0F, 3.0F, new ItemStack(rc, 1, 16), false, 80));
		rc.registerBehaviour(14, new BehaviourFuelRod(2, 25000, 3.0F, 3.0F, new ItemStack(rc, 1, 17), false, 80));
		rc.registerBehaviour(15, new BehaviourFuelRod(4, 25000, 3.0F, 3.0F, new ItemStack(rc, 1, 18), false, 80));
		rc.registerBehaviour(16, d);
		rc.registerBehaviour(17, d);
		rc.registerBehaviour(18, d);
		rc.registerBehaviour(19, new BehaviourVent(2000, 80, 144));
		rc.registerBehaviour(20, new BehaviourVent(2000, 100, 0));
		rc.registerBehaviour(21, new BehaviourVentComponent(16));
		rc.registerBehaviour(22, new BehaviourVent(2000, 0, 100));
		rc.registerBehaviour(23, new BehaviourHeatStorage(60000));
		rc.registerBehaviour(24, new BehaviourHeatStorage(180000));
		rc.registerBehaviour(25, new BehaviourHeatStorage(360000));
		rc.registerBehaviour(26, new BehaviourVent(120000, 20, 0));
		rc.registerBehaviour(27, new BehaviourVent(360000, 60, 0));
		rc.registerBehaviour(28, new BehaviourVent(720000, 120, 0));
		rc.registerBehaviour(29, new BehaviourEnrichable(2000, new ItemStack(rc, 1, 30)));
		rc.registerBehaviour(30, new BehaviourDepleted(0));
		rc.registerBehaviour(31, new BehaviourEnrichable(1000, new ItemStack(rc, 1, 30)));
		rc.registerBehaviour(32, new BehaviourFuelRod(1, 100000, 1.5F, 1.0F, new ItemStack(rc, 1, 35), true, 120));
		rc.registerBehaviour(33, new BehaviourFuelRod(2, 100000, 1.5F, 1.0F, new ItemStack(rc, 1, 36), true, 120));
		rc.registerBehaviour(34, new BehaviourFuelRod(4, 100000, 1.5F, 1.0F, new ItemStack(rc, 1, 37), true, 120));
		rc.registerBehaviour(35, d);
		rc.registerBehaviour(36, d);
		rc.registerBehaviour(37, d);
		rc.registerBehaviour(38, new BehaviourHeatExc(40000, 144, 24));
		rc.registerBehaviour(39, new BehaviourHeatExc(40000, 100, 0));
		rc.registerBehaviour(40, new BehaviourHeatExc(40000, 0, 100));
		rc.registerBehaviour(41, new BehaviorNeutDoubler());
		rc.registerBehaviour(42, new BehaviourFuelRod(1, 30000, 5.0F, 3.0F, new ItemStack(rc, 1, 45), false, 100));
		rc.registerBehaviour(43, new BehaviourFuelRod(2, 30000, 5.0F, 3.0F, new ItemStack(rc, 1, 46), false, 100));
		rc.registerBehaviour(44, new BehaviourFuelRod(4, 30000, 5.0F, 3.0F, new ItemStack(rc, 1, 47), false, 100));
		rc.registerBehaviour(45, d);
		rc.registerBehaviour(46, d);
		rc.registerBehaviour(47, d);

		if (JSTUtils.isClient()) registerItemModel(rc);
		crop_dirty = ReflectionUtils.getField("ic2.core.crop.TileEntityCrop", "dirty");
		if (JSTCfg.noSuddenHoes) ReflectionUtils.setFieldValue("ic2.core.IC2", "suddenlyHoes", null, false);
	}

	@Override
	@Method(modid = "ic2")
	public void init() {
		ExplosionWhitelist.addWhitelistedBlock(Blocks.END_PORTAL);
		ExplosionWhitelist.addWhitelistedBlock(Blocks.END_PORTAL_FRAME);
		ExplosionWhitelist.addWhitelistedBlock(Blocks.END_GATEWAY);
		ExplosionWhitelist.addWhitelistedBlock(Blocks.BARRIER);
		ExplosionWhitelist.addWhitelistedBlock(Blocks.COMMAND_BLOCK);
		ExplosionWhitelist.addWhitelistedBlock(Blocks.CHAIN_COMMAND_BLOCK);
		ExplosionWhitelist.addWhitelistedBlock(Blocks.REPEATING_COMMAND_BLOCK);
		ExplosionWhitelist.addWhitelistedBlock(Blocks.STRUCTURE_BLOCK);
		ExplosionWhitelist.addWhitelistedBlock(Blocks.STRUCTURE_VOID);
		ExplosionWhitelist.addWhitelistedBlock(JSTBlocks.block1);
		ExplosionWhitelist.addWhitelistedBlock(JSTBlocks.blockOre);

		addUURecipe(new ItemStack(JSTItems.item1, 1, 16), 9950);
		addUURecipe(new ItemStack(JSTItems.item1, 1, 17), 6025);
		addUURecipe(new ItemStack(JSTItems.item1, 1, 18), 6025);
		addUURecipe(new ItemStack(JSTItems.item1, 1, 27), 710);
		addUURecipe(new ItemStack(JSTItems.item1, 1, 100), 16700);
		addUURecipe(new ItemStack(Items.QUARTZ), 600);
		addUURecipe("dustTungsten", 19500);
		addUURecipe("dustLithium", 3000);
		addUURecipe("dustSodium", 1000);
		addUURecipe("dustRhenium", 1500000);
		addUURecipe("dustUnobtainium", 8000000);
		addUURecipe("dustTitanium", 22000);
		addUURecipe("ingotTitanium", 22000);
		addUURecipe("dustChrome", 62100);
		addUURecipe("ingotChrome", 62100);
		addUURecipe("dustAluminum", 821);
		addUURecipe("ingotAluminum", 821);
		addUURecipe("dustZinc", 950);
		addUURecipe("ingotZinc", 950);
		addUURecipe("dustNiobium", 21000);
		addUURecipe("ingotNiobium", 21000);
		addUURecipe("dustPlatinum", 64000);
		addUURecipe("ingotPlatinum", 64000);
		addUURecipe("dustNickel", 1120);
		addUURecipe("ingotNickel", 1120);
		addUURecipe("dustIridium", 120000);
		addUURecipe("ingotIridium", 120000);
		addUURecipe("oreZinc", 2400);
		addUURecipe("oreTungsten", 40000);
		addUURecipe("oreNiobium", 50000);
		addUURecipe("gemRuby", 12000);
		addUURecipe("gemSapphire", 8000);
		addUURecipe("gemPeridot", 8000);
		addUURecipe("crystalCertusQuartz", 1200);

		if (JSTUtils.isClient()) CropJSTBase.tex = new ArrayList();

		new CropJST("bluewheat", "Eloraam", 7, 1, 100, null, new Object[] {"oreNikolite", "blockNikolite"}, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 27), new ItemStack(JSTItems.item1, 1, 59)}, new String[] {"Blue", "Nikolite", "Wheat"}, 6, 2, 1, 0, 3, 0);
		new CropJST("coalplant", null, 5, 3, 0, null, new Object[] {"oreCoal", "blockCoal"}, new ItemStack[] {new ItemStack(Items.COAL), new ItemStack(Items.COAL, 1)}, new String[] {"Coal", "Fuel", "Carbon"}, 6, 1, 0, 1, 0, 0);
		new CropJST("blazewood", null, 5, 3, 0, null, null, new ItemStack[] {new ItemStack(Items.BLAZE_POWDER), new ItemStack(Items.BLAZE_ROD)}, new String[] {"Blaze", "Wood"}, 7, 4, 0, 2, 2, 0);
		new CropJST("thorwood", "GregoriusT", 4, 1, 250, null, new Object[] {"oreCoal", "blockCoal", "oreThorium", "blockThorium"}, new ItemStack[] {null, new ItemStack(JSTItems.item1, 1, 100)}, new String[] {"Thorium", "Radioactive", "Coal"}, 8, 5, 0, 0, 1, 1);
		new CropJST("urania", "Alblaka", 4, 1, 250, null, new Object[] {"oreUranium", "blockUranium"}, new ItemStack[] {JSTUtils.getModItemStack("ic2:nuclear", 1, 6), JSTUtils.getModItemStack("ic2:nuclear", 1, 2)}, new String[] {"Uranium", "Radioactive", "Danger"}, 10, 5, 0, 0, 2, 1);
		new CropJST("irium", "Alblaka", 4, 1, 400, null, new Object[] {"oreIridium", "blockIridium"}, new ItemStack[] {JSTUtils.getModItemStack("ic2:misc_resource", 1, 2)}, new String[] {"Iridium", "Rare", "Quantum"}, 13, 1, 0, 4, 0, 2);
		new CropJST("diaplant", null, 5, 1, 0, null, new Object[] {"oreDiamond", "blockDiamond"}, new ItemStack[] {null, new ItemStack(Items.DIAMOND)}, new String[] {"Diamond", "Carbon", "Rare", "Crystal"}, 12, 0, 0, 0, 1, 1);
		new CropJST("chorus", "jeb_", 4, 3, 0, new ItemStack(Items.CHORUS_FRUIT), null, new ItemStack[] {new ItemStack(Items.CHORUS_FRUIT)}, new String[] {"Chorus", "Ender"}, 6, 2, 0, 1, 2, 0);
		new CropElecMushroom();
		new CropJST("emeria", "jeb_", 5, 1, 0, null, new Object[] {"oreEmerald", "blockEmerald"}, new ItemStack[] {null, new ItemStack(Items.EMERALD)}, new String[] {"Emerald", "Green", "Crystal"}, 11, 0, 0, 0, 2, 1);
		new CropJST("lapoleaf", null, 4, 1, 0, null, new Object[] {"oreLapis", "blockLapis"}, new ItemStack[] {null, new ItemStack(Items.DYE, 1, 4)}, new String[] {"Lapis Lazuli", "Blue", "Dye"}, 6, 2, 0, 1, 5, 1);
		new CropJST("glowood", null, 4, 1, 0, null, new Object[] {Blocks.GLOWSTONE}, new ItemStack[] {null, new ItemStack(Items.GLOWSTONE_DUST)}, new String[] {"Glowstone", "Bright", "Nether"}, 6, 3, 0, 2, 4, 1);
		new CropJST("nickelum", "KingLemming", 4, 2, 0, null, new Object[] {"oreNickel", "blockNickel"}, getNuggetOrDust("Nickel"), new String[] {"Shiny", "Leaves", "Metal", "Nickel"}, 6, 3, 0, 3, 1, 2);
		new CropJST("alumia", "GregoriusT", 4, 2, 0, null, new Object[] {"oreBauxite", "oreAluminum", "blockAluminum"}, getNuggetOrDust("Aluminum"), new String[] {"Shiny", "Leaves", "Metal", "Aluminium"}, 6, 4, 0, 2, 0, 1);
		new CropJST("platina", "KingLemming", 4, 2, 0, null, new Object[] {"orePlatinum", "blockPlatinum"}, getNuggetOrDust("Platinum"), new String[] {"Shiny", "Leaves", "Metal", "Platinum"}, 11, 1, 0, 5, 2, 0);
		new CropJST("niobis", "dohyun22", 4, 2, 0, null, new Object[] {"oreNiobium", "blockNiobium"}, getNuggetOrDust("Niobium"), new String[] {"Shiny", "Leaves", "Metal", "Niobium"}, 8, 2, 0, 3, 1, 1);
		new CropJST("zincium", "GregoriusT", 4, 2, 0, null, new Object[] {"oreZinc", "blockZinc"}, getNuggetOrDust("Zinc"), new String[] {"Shiny", "Leaves", "Metal", "Zinc"}, 6, 2, 0, 3, 1, 1);
		new CropJST("naquadium", "GregoriusT", 4, 2, 0, null, new Object[] {"oreNaquadah", "blockNaquadah"}, getNuggetOrDust("Naquadah"), new String[] {"Leaves", "Metal", "Alien", "Naquadah"}, 13, 6, 0, 6, 1, 3);
		ItemStack st = JSTUtils.getModItemStack("immersiveengineering:seed");
		new CropJST("hemp", "BluSunrize", 4, 1, -350, st, null, st.isEmpty() ? new ItemStack[] {new ItemStack(Items.STRING)} : new ItemStack[] {JSTUtils.getModItemStack("immersiveengineering:material", 1, 4), st, st}, new String[] {"Industrial", "Hemp", "Addictive"}, 3, 1, 0, 1, 2, 2);
		new CropJST("oilplant", "SpaceToad", 4, 2, 0, null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 151)}, new String[] {"Berries", "Oil", "Fuel"}, 6, 4, 1, 0, 0, 0);
		new CropJST("saltgrass", "dohyun22", 4, 1, 0, null, null, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 107), JSTUtils.getValidOne("dustRockSalt", "foodRockSalt")}, new String[] {"Food", "Salt"}, 4, 2, 1, 1, 2, 2);
		new CropCactus();
		new CropJST("nitrowood", "Creepers", 3, 1, 0, null, null, new ItemStack[] {new ItemStack(Items.GUNPOWDER)}, new String[] {"Creeper", "Sulfur", "Saltpeter"}, 6, 5, 0, 2, 1, 2);

		new CropJST("oak", "Notch", 3, 1, 0, new ItemStack(Blocks.SAPLING, 1, 0), null, new ItemStack[] {new ItemStack(Blocks.LOG, 1, 0), new ItemStack(Blocks.SAPLING, 1, 0), new ItemStack(Items.APPLE)}, new String[] {"Oak", "Wood", "Apple", "Bonsai"}, 3, 0, 1, 1, 1, 0);
		new CropJST("spruce", "Notch", 3, 1, 0, new ItemStack(Blocks.SAPLING, 1, 1), null, new ItemStack[] {new ItemStack(Blocks.LOG, 1, 1), new ItemStack(Blocks.SAPLING, 1, 1)}, new String[] {"Spruce", "Wood", "Bonsai"}, 3, 0, 0, 1, 1, 0);
		new CropJST("birch", "Notch", 3, 1, 0, new ItemStack(Blocks.SAPLING, 1, 2), null, new ItemStack[] {new ItemStack(Blocks.LOG, 1, 2), new ItemStack(Blocks.SAPLING, 1, 2)}, new String[] {"Birch", "Wood", "Bonsai"}, 3, 0, 0, 1, 1, 0);
		new CropJST("jungle", "Jeb_", 3, 1, 0, new ItemStack(Blocks.SAPLING, 1, 3), null, new ItemStack[] {new ItemStack(Blocks.LOG, 1, 3), new ItemStack(Blocks.SAPLING, 1, 3)}, new String[] {"Jungle", "Wood", "Bonsai"}, 3, 0, 0, 1, 1, 0);
		new CropJST("acacia", "Jeb_", 3, 1, 0, new ItemStack(Blocks.SAPLING, 1, 4), null, new ItemStack[] {new ItemStack(Blocks.LOG2, 1, 0), new ItemStack(Blocks.SAPLING, 1, 4)}, new String[] {"Acacia", "Wood", "Bonsai"}, 3, 0, 0, 1, 1, 0);
		new CropJST("roofedoak", "Jeb_", 3, 1, 0, new ItemStack(Blocks.SAPLING, 1, 5), null, new ItemStack[] {new ItemStack(Blocks.LOG2, 1, 1), new ItemStack(Blocks.SAPLING, 1, 5)}, new String[] {"Dark Oak", "Wood", "Apple", "Bonsai"}, 3, 0, 1, 1, 1, 0);
		new CropJST("rubwood", "Alblaka", 3, 1, -600, JSTUtils.getModItemStack("ic2:sapling"), null, new ItemStack[] {JSTUtils.getModItemStack("ic2:rubber_wood"), JSTUtils.getModItemStack("ic2:sapling"), JSTUtils.getModItemStack("ic2:misc_resource", 1, 4)}, new String[] {"Rubber", "Wood", "Bonsai"}, 4, 2, 0, 2, 1, 0);

		st = JSTUtils.getValidOne("cropBarley", "plantBarley");
		if (!st.isEmpty()) new CropJST("barley", "mDiyo", 3, 1, 0, st, null, new ItemStack[] {st}, new String[] {"Barley", "Crop", "Edible"}, 2, 0, 3, 1, 2, 1);
		st = JSTUtils.getValidOne("cropRice", "plantRice", "plantWildrice");
		if (!st.isEmpty()) new CropJST("rice", "glitchfiend", 3, 1, 0, st, null, new ItemStack[] {st}, new String[] {"Rice", "Crop", "Edible"}, 2, 0, 4, 0, 2, 1);
		st = JSTUtils.getModItemStack("biomesoplenty:bamboo");
		if (!st.isEmpty()) new CropJST("bamboo", "glitchfiend", 3, 1, -200, st, null, new ItemStack[] {st}, new String[] {"Bamboo", "Stick"}, 3, 0, 1, 1, 1, 2);

		if (JSTUtils.isClient()) {
			registerTex();
			CropJSTBase.tex = null;
		}

		addCropSoilType("FOR_HUMUS", JSTUtils.getModBlock("forestry:humus"));
		addCropSoilType("BOP_FARMLANDA", JSTUtils.getModBlock("biomesoplenty:farmland_0"));
		addCropSoilType("BOP_FARMLANDB", JSTUtils.getModBlock("biomesoplenty:farmland_1"));

		if (JSTCfg.nerfCS) {
			try {
				Class c = CropSoilType.class;
				Field f = null;
				for (Field f2 : c.getDeclaredFields()) {
					if (f2.getName().equals("block")) {
						f2.setAccessible(true);
						ReflectionUtils.removeFinal(f2);
						f = f2;
						break;
					}
				}
				String[] names = new String[] {"MYCELIUM", "SAND", "SOULSAND"};
				for (String str : names)
					f.set(c.getField(str).get(null), Blocks.FARMLAND);
			} catch (Throwable t) {
				JSTUtils.LOG.error("Failed to modify CropSoilType");
				JSTUtils.LOG.catching(t);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static void registerTex() {
		Map<ResourceLocation, TextureAtlasSprite> map = new HashMap();
		for (ResourceLocation rl : CropJST.tex)
			map.put(rl, FMLClientHandler.instance().getClient().getTextureMapBlocks().getAtlasSprite(rl.toString()));
		Crops.instance.registerCropTextures(map);
	}

	@Override
	@Method(modid = "ic2")
	public void postInit() {
		Object obj = new ItemStack[] {JSTUtils.getModItemStack("ic2:hazmat_helmet", 1, 32767), JSTUtils.getModItemStack("ic2:hazmat_chestplate", 1, 32767), JSTUtils.getModItemStack("ic2:hazmat_leggings", 1, 32767), JSTUtils.getModItemStack("ic2:rubber_boots", 1, 32767)};
	    for (ItemStack st : (ItemStack[]) obj)
	    	for (EnumHazard hz : EnumHazard.values())
	    		JSTDamageSource.addHazmat(hz, st);

		addScrapDrop("dustPlatinum", 0.075F);
		addScrapDrop("dustLithium", 0.4F);
		addScrapDrop("dustTitanium", 0.075F);
		addScrapDrop("dustChrome", 0.04F);
		addScrapDrop("dustIridium", 0.005F);
		addScrapDrop("dustAluminum", 0.8F);
		addScrapDrop("dustZinc", 0.8F);
		addScrapDrop("dustNikolite", 0.8F);
		addScrapDrop("dustSilicon", 0.8F);
		addScrapDrop("dustSulfur", 0.8F);
		addScrapDrop("dustLead", 0.8F);
		addScrapDrop("dustSilver", 0.7F);
		addScrapDrop("dustNickel", 0.7F);
		addScrapDrop("dustNiobium", 0.2F);
		addScrapDrop("dustSalt", 1.0F);
		addScrapDrop("oreLead", 0.4F);
		addScrapDrop("oreAluminum", 0.4F);
		addScrapDrop("oreSilver", 0.35F);
		addScrapDrop("oreNickel", 0.35F);
		addScrapDrop(new ItemStack(Blocks.TALLGRASS, 1, 1), 2.5F);
		addScrapDrop(new ItemStack(Blocks.DEADBUSH), 2.5F);
		addScrapDrop(new ItemStack(Blocks.WOOL), 1.0F);
		addScrapDrop(new ItemStack(Blocks.MYCELIUM), 0.2F);
		addScrapDrop(new ItemStack(Items.GOLDEN_APPLE), 0.05F);
		addScrapDrop(new ItemStack(JSTItems.item1, 1, 16), 0.1F);
		addScrapDrop(new ItemStack(JSTItems.item1, 1, 17), 0.1F);
		addScrapDrop(new ItemStack(JSTItems.item1, 1, 18), 0.1F);
		addScrapDrop(new ItemStack(JSTItems.item1, 1, 83), 0.1F);
		addScrapDrop(new ItemStack(JSTItems.item1, 1, 85), 0.5F);
		addScrapDrop(new ItemStack(JSTItems.item1, 1, 9000), 0.6F);
		addScrapDrop(new ItemStack(JSTItems.item1, 1, 9001), 0.3F);
		addScrapDrop(new ItemStack(JSTItems.item1, 1, 9002), 0.15F);
		addScrapDrop(new ItemStack(JSTItems.item1, 1, 9003), 0.15F);
		addScrapDrop(new ItemStack(JSTItems.item1, 1, 9019), 0.15F);

		addAmplifier("dustRedstone", 15000);
		addAmplifier("dustNikolite", 30000);
		addAmplifier("dustRuby", 400000);
		addAmplifier("dustSapphire", 300000);
		addAmplifier("dustPeridot", 300000);
		addAmplifier("dustDiamond", 1000000);
		addAmplifier("dustTungsten", 1000000);
		addAmplifier("dustUranium", 2500000);
		addAmplifier(new ItemStack(JSTItems.item1, 1, 49), 50000000);
		addAmplifier(new ItemStack(JSTItems.item1, 1, 50), 450000000);
		addAmplifier(new ItemStack(JSTItems.item1, 1, 100), 1750000);

		MRecipes.addSteam("ic2steam", 1);
		MRecipes.addSteam("ic2superheated_steam", 2);
		MRecipes.addHeatFuel("ic2pahoehoe_lava", 5);
		MRecipes.addGasFuel("ic2hydrogen", 24);
		MRecipes.addGasFuel("ic2biogas", 60);

		MRecipes.addFertilizer(JSTUtils.getModItemStack("ic2:crop_res", 1, 2));

		ItemStack st = JSTUtils.getModItemStack("ic2:nuclear", 1, 6), st2 = JSTUtils.getModItemStack("ic2:crafting", 1, 9);
		if (!st.isEmpty()) {
			Item i = st.getItem();
			st = JSTUtils.getFirstItem("dustUranium", 8);
			if (st.isEmpty()) st = new ItemStack(i, 8, 2);
			obj = new Object[] {new OreDictStack("oreUranium", 4)};
			MRecipes.addOreProcessRecipe((Object[]) obj, new FluidStack(FluidRegistry.WATER, 4000), new ItemStack[] {st, new ItemStack(i, 2, 1)}, 64, 400);
			MRecipes.addOreProcessRecipe((Object[]) obj, new FluidStack(JSTFluids.acid, 4000), new ItemStack[] {JSTUtils.modStack(st, 12, -1), new ItemStack(i, 3, 1)}, 64, 400);
			obj = "ingotUranium";
			if (JSTUtils.oreValid((String)obj)) {
				removeIC2RecipeByInput(JSTUtils.getModItemStack("ic2:crushed", 1, 6), Recipes.centrifuge);
				removeIC2RecipeByInput(JSTUtils.getModItemStack("ic2:purified", 1, 6), Recipes.centrifuge);
				addCentrifugeRecipe(3000, new OreDictStack("crushedUranium"), new ItemStack(i, 1, 5), new ItemStack(i, 1, 2));
				addCentrifugeRecipe(3000, new OreDictStack("crushedPurifiedUranium"), new ItemStack(i, 1, 5), new ItemStack(i, 1, 2));
				st = new ItemStack(i, 1, 2);
				addExtRec(new OreDictStack((String)obj), st);
				addCompRec(st, JSTUtils.getFirstItem((String)obj));
			}
			st = new ItemStack(i, 1, 6);
			RecipeLoader.addShapelessRecipe(new ItemStack(i, 1, 2), st, st, st, st, st, st, st, st, st);
			RecipeLoader.addShapelessRecipe(new ItemStack(i, 9, 6), new ItemStack(i, 1, 2));
			MRecipes.addFusionBreederRecipe(new ItemStack(i, 1, 6), new ItemStack(i, 1, 7));
			MRecipes.addFusionBreederRecipe(new ItemStack(JSTItems.item1, 1, 100), new ItemStack(i, 6, 5));
			MRecipes.addSeparatorRecipe(new ItemStack(i, 10, 2), null, null, new ItemStack[] {new ItemStack(i, 1, 5)}, null, 100, 1500);
			addFuelRecipe(new ItemStack(JSTItems.item1, 3, 100), st2, 1, new ItemStack(JSTItems.item1, 1, 100), new ItemStack(i, 1, 5));
			addFuelRecipe(new ItemStack(i, 3, 1), st2, 7, new ItemStack(i, 2, 1), new ItemStack(i, 2, 7));
			addFuelRecipe(new ItemStack(i, 3, 3), st2, 13, new ItemStack(i, 2, 3));
			addFuelRecipe(new ItemStack(JSTItems.item1, 1, 60), st2, 32, new ItemStack(JSTItems.item1, 1, 39));
			st = JSTUtils.getModItemStack("ic2:depleted_isotope_fuel_rod");
			if (!st.isEmpty()) {
				addCannerRecipe(JSTUtils.getModItemStack("ic2:crafting", 1, 9), new ItemStack(i, 3, 2), st);
				addCentrifugeRecipe(4000, new ItemStack(i, 1, 18), new ItemStack(i, 2, 7), JSTUtils.getFirstItem("dustIron"));
			}
			RecipeLoader.addShapedRecipe(new ItemStack(JSTItems.item1, 1, 12030), "LCL", "TRT", "LRL", 'C', ItemList.circuits[3], 'L', "ingotLead", 'R', new ItemStack(i, 1, 10), 'T', new ItemStack(JSTBlocks.blockTile, 1, 5020));
		}

		MRecipes.addAssemblerRecipe(new Object[] {new ItemStack(jstn, 1, 30), new ItemStack(JSTItems.item1, 1, 9000)}, null, new ItemStack(JSTItems.item1, 1, 9011), st2, 8, 20);
		RecipeLoader.addShapelessRecipe(new ItemStack(jstn, 1, 29), st2, new ItemStack(JSTItems.item1, 1, 9010));
		RecipeLoader.addShapelessRecipe(new ItemStack(jstn, 1, 31), st2, "dustLithium");

		FluidStack fs = FluidRegistry.getFluidStack("ic2heavy_water", 3000);
		if (fs != null)
			MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 1, 9000), null, fs, new ItemStack[] {new ItemStack(JSTItems.item1, 1, 9017)}, new FluidStack(JSTFluids.deuterium, 2000), 30, 2000);
		fs = FluidRegistry.getFluidStack("ic2air", 5000);
		if (fs != null)
			MRecipes.addSeparatorRecipe(new ItemStack(JSTItems.item1, 5, 9000), null, fs, new ItemStack[] {new ItemStack(JSTItems.item1, 4, 9016), new ItemStack(JSTItems.item1, 1, 9017)}, null, 30, 800);

		fs = FluidRegistry.getFluidStack("cryotheum", 1000);
		OreDictStack os = new OreDictStack("plateTin");
		if (fs != null && JSTUtils.oreValid(os)) {
			MRecipes.addAssemblerRecipe(new Object[] {null, os, null, os, null, os, null, os, null}, fs, new ItemStack(jstn, 1, 26), null, 10, 200);
			MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 1, 9004), new ItemStack(JSTItems.item1, 1, 9019)}, null, JSTUtils.getModItemStack("ic2:crafting", 32), new ItemStack(JSTItems.item1, 2, 9000), null, 24, 400);
			MRecipes.addChemMixerRecipe(new Object[] {new ItemStack(JSTItems.item1, 1, 9019)}, new FluidStack(JSTFluids.fuel, 1000), JSTUtils.getModItemStack("ic2:crafting", 32), new ItemStack(JSTItems.item1, 1, 9000), null, 24, 400);
		}
		String[] str = {
				"uranium_fuel_rod",
				"dual_uranium_fuel_rod",
				"quad_uranium_fuel_rod",
				"mox_fuel_rod",
				"dual_mox_fuel_rod",
				"quad_mox_fuel_rod",
				"heat_storage",
				"tri_heat_storage",
				"hex_heat_storage",
				"plating",
				"heat_plating",
				"containment_plating",
				"heat_exchanger",
				"reactor_heat_exchanger",
				"component_heat_exchanger",
				"advanced_heat_exchanger",
				"heat_vent",
				"reactor_heat_vent",
				"overclocked_heat_vent",
				"component_heat_vent",
				"advanced_heat_vent",
				"neutron_reflector",
				"thick_neutron_reflector",
				"iridium_reflector",
				"rsh_condensator",
				"lzh_condensator",
				"depleted_isotope_fuel_rod",
				"heatpack"
		};
		for (String s : str) {
			st = JSTUtils.getModItemStack("ic2:" + s);
			if (!st.isEmpty())
				MRecipes.NuclearItems.add(st);
		}
		Item it = CompatIC2.jstn;
		for (Entry<Integer, ReactorItemBehaviour> rb : ((ItemReactorComponent)jstn).behaviours.entrySet())
			if (!(rb.getValue() instanceof BehaviourDepleted))
				MRecipes.NuclearItems.add(new ItemStack(it, 1, rb.getKey()));
		
		st = JSTUtils.getModItemStack("ic2:misc_resource", 1, 1);
		if (!st.isEmpty()) {
			GameRegistry.addSmelting(st, new ItemStack(JSTItems.item1, 1, 29), 0.0F);
			addCompRec(new OreDictStack("ingotIridium"), st);
			addCompRec(new OreDictStack("dustIridium"), new ItemStack(JSTItems.item1, 1, 29));
		}
		it = JSTUtils.getModItem("ic2:crafting");
		MRecipes.addPressRecipe(new OreDictStack("dustCoal", 8), new ItemStack(Items.FLINT), new ItemStack(it, 1, 17), null, 4, 100);
		MRecipes.addPressRecipe(new ItemStack(it, 8, 17), null, new ItemStack(it, 1, 18), null, 4, 100);
		MRecipes.addPressRecipe(new ItemStack(it, 1, 18), null, new ItemStack(Items.DIAMOND), null, 4, 200);
		MRecipes.addPressRecipe(new OreDictStack("blockIron"), new AdvRecipeItem(JSTItems.item1, 0, 162), new ItemStack(it, 1, 29), null, 30, 200);
		MRecipes.addPressRecipe(new OreDictStack("blockSteel"), new AdvRecipeItem(JSTItems.item1, 0, 162), new ItemStack(it, 1, 30), null, 30, 200);
		it = JSTUtils.getModItem("ic2:casing");
		String[] sf = new String[] {"Bronze", "Copper", "Gold", "Iron", "Lead", "Steel", "Tin"};
		for (int n = 0; n < sf.length; n++)
			MRecipes.addPressRecipe(new OreDictStack("plate" + sf[n]), new AdvRecipeItem(JSTItems.item1, 0, 161), new ItemStack(it, 2, n), null, 4, 64);
		obj = new AdvRecipeItem(JSTItems.item1, 0, 160);
		MRecipes.addPressRecipe(new OreDictStack("ingotTin"), obj, getIC2Cable(4, 0, 3), null, 16, 64);
		MRecipes.addPressRecipe(new OreDictStack("ingotCopper"), obj, getIC2Cable(0, 0, 3), null, 16, 64);
		MRecipes.addPressRecipe(new OreDictStack("ingotGold"), obj, getIC2Cable(2, 0, 4), null, 16, 64);
		MRecipes.addPressRecipe(new OreDictStack("ingotIron"), obj, getIC2Cable(3, 0, 4), null, 16, 64);
		st = getIC2Cable(3, 0, 5);
		MRecipes.addPressRecipe(new OreDictStack("ingotAluminum"), obj, st, null, 16, 64);
		MRecipes.addPressRecipe(new OreDictStack("ingotSteel"), obj, st, null, 16, 64);

		st = JSTUtils.getModItemStack("ic2:resource");
		if (!st.isEmpty()) {
			st2 = new ItemStack(JSTBlocks.block2, 1, 2);
			GameRegistry.addSmelting(st, st2, 0.0F);
			addMacRec(st2, st);
		}
		addMacRec(new ItemStack(JSTItems.item1, 1, 20), new ItemStack(JSTItems.item1, 1, 21));

		RecipeLoader.addShapedRecipe(getIC2Cable(1, 0, 8), "III", "CDC", "III", 'I', Blocks.GLASS, 'C', JSTUtils.getModItemStack("ic2:dust", 1, 6), 'D', "dustNiobium");

		st = getIC2Cable(3, 0, 5);
		if (JSTUtils.oreValid("ingotAluminum"))
			addFormerRecipe(new OreDictStack("ingotAluminum"), st, 1);

		if (JSTUtils.oreValid("plateAluminum"))
			addFormerRecipe(new OreDictStack("plateAluminum"), st, 0);

		if (JSTUtils.oreValid("ingotSteel"))
			addFormerRecipe(new OreDictStack("ingotSteel"), st, 1);

		if (JSTUtils.oreValid("plateSteel"))
			addFormerRecipe(new OreDictStack("plateSteel"), st, 0);

		st = JSTUtils.getModItemStack("ic2:crafting", 1, 4);
		if (!st.isEmpty()) {
			RecipeLoader.addShapedRecipe(st, 
					"IAI", "ADA", "IAI",
					'I', "ingotIridium",
					'A', JSTUtils.getModItemStack("ic2:crafting", 1, 3),
					'D', "gemDiamond"
					);
		}

		RecipeLoader.addShapedRecipe(JSTUtils.getModItemStack("ic2:dust", 9, 6), 
				"rRr", "RrR", "rRr",
				'r', "dustRedstone",
				'R', "dustRuby"
				);
		
		st = JSTUtils.getModItemStack("ic2:energy_crystal", 1, 32767);
		if (!st.isEmpty()) {
			RecipeLoader.addShapedRecipe(JSTUtils.getModItemStack("ic2:lapotron_crystal"), 
					"LSL", "LEL", "LSL",
					'L', "dustLapis",
					'S', "gemSapphire",
					'E', st
					);
		}

		st = JSTUtils.getModItemStack("ic2:te", 1, 8);
		if (!st.isEmpty()) {
			OreDictionary.registerOre("craftingSolarPanel", st);
			RecipeLoader.addShapelessRecipe(new ItemStack(JSTBlocks.blockTile, 1, 40), st);
			RecipeLoader.addShapelessRecipe(st, new ItemStack(JSTBlocks.blockTile, 1, 40));
		}

		if (JSTUtils.oreValid("dustPlatinum")) {
			if (JSTUtils.oreValid("dustNickel") && JSTUtils.oreValid("dustGold") && JSTUtils.oreValid("dustIron")) {
				addCentrifugeRecipe(4500, new OreDictStack("dustNickel", 16), JSTUtils.getFirstItem("dustPlatinum"), JSTUtils.getFirstItem("dustGold"), JSTUtils.getFirstItem("dustIron"));
			}
			addCentrifugeRecipe(4500, new OreDictStack("dustPlatinum", 8), new ItemStack(JSTItems.item1, 1, 30));
		}

		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, 23), 
				" P ", "PHP", " P ",
				'P', "plateTin",
				'H', new ItemStack(JSTItems.item1, 1, 9013)
				);

		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, 24), 
				"PPP", "CCC", "PPP",
				'P', "plateTin",
				'C', new ItemStack(jstn, 1, 23)
				);

		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, 25), 
				"PCP", "PcP", "PCP",
				'P', "plateTin",
				'C', new ItemStack(jstn, 1, 24),
				'c', "plateCopper"
				);

		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, 27), 
				"PPP", "CCC", "PPP",
				'P', "plateTin",
				'C', new ItemStack(jstn, 1, 26)
				);

		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, 28), 
				"PCP", "PcP", "PCP",
				'P', "plateTin",
				'C', new ItemStack(jstn, 1, 27),
				'c', "plateCopper"
				);

		st = JSTUtils.getModItemStack("ic2:overclocked_heat_vent", 1, Short.MAX_VALUE);
		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, 19), 
				" I ", "PVP", " I ",
				'P', "plateChrome",
				'V', st,
				'I', ItemList.circuits[3]
				);

		st = JSTUtils.getModItemStack("ic2:advanced_heat_vent", 1, Short.MAX_VALUE);
		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, 20), 
				" I ", "PVP", " I ",
				'P', "plateTitanium",
				'V', st,
				'I', ItemList.circuits[3]
				);

		st = JSTUtils.getModItemStack("ic2:component_heat_vent", 1, Short.MAX_VALUE);
		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, 21), 
				" I ", "PVP", " I ",
				'P', "plateChrome",
				'V', st,
				'I', ItemList.circuits[3]
				);

		st = JSTUtils.getModItemStack("ic2:reactor_heat_vent", 1, Short.MAX_VALUE);
		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, 22), 
				" I ", "PVP", " I ",
				'P', "plateChrome",
				'V', st,
				'I', ItemList.circuits[3]
				);

		st = JSTUtils.getModItemStack("ic2:advanced_heat_exchanger", 1, Short.MAX_VALUE);
		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, 38), 
				" C ", "PEP",
				'C', ItemList.circuits[3],
				'P', "plateTitanium",
				'E', st
				);

		st = JSTUtils.getModItemStack("ic2:component_heat_exchanger", 1, Short.MAX_VALUE);
		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, 39), 
				" C ", "PEP",
				'C', ItemList.circuits[3],
				'P', "plateChrome",
				'E', st
				);

		st = JSTUtils.getModItemStack("ic2:reactor_heat_exchanger", 1, Short.MAX_VALUE);
		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, 40), 
				" C ", "PEP",
				'C', ItemList.circuits[3],
				'P', "plateChrome",
				'E', st
				);

		st = JSTUtils.getModItemStack("ic2:neutron_reflector");
		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, 41), 
				"cpa", "psp", "apc",
				'c', "dustTin",
				'p', Items.ENDER_EYE,
				'a', "dustAluminum",
				's', st
				);

		obj = "dustCarbon";
		RecipeLoader.addShapelessRecipe(JSTUtils.getModItemStack("ic2:crafting", 1, 13), obj, obj, obj, obj, obj, obj, obj, obj);

		if (JSTCfg.ExpensiveMassFab) {
			st = JSTUtils.getModItemStack("ic2:te", 1, 61);
			RecipeLoader.removeRecipeByOutput(st, true, true);
			RecipeLoader.addShapedRecipe(st, "ICI", "MLM", "ICI", 'I', "plateChrome", 'C', "circuitPowerControl", 'M', JSTUtils.getModItemStack("ic2:resource", 1, 13), 'L', JSTUtils.getModItemStack("ic2:crafting", 1, 4));
		}

		if (JSTCfg.CheaperIC2) {
			RecipeLoader.addShapedRecipe(getIC2Cable(0, 0, 6), "CCC", 'C', "ingotCopper");
			RecipeLoader.addShapedRecipe(getIC2Cable(0, 1, 6), "III", "CCC", "III", 'C', "ingotCopper", 'I', "itemRubber");
			RecipeLoader.addShapedRecipe(getIC2Cable(2, 0, 12), "CCC", 'C', "ingotGold");
			RecipeLoader.addShapedRecipe(getIC2Cable(3, 0, 12), "CCC", 'C', "ingotIron");
			RecipeLoader.addShapedRecipe(getIC2Cable(4, 0, 9), "CCC", 'C', "ingotTin");

			st = JSTUtils.getModItemStack("ic2:crafting", 1, 1);
			RecipeLoader.addShapedRecipe((ItemStack)st, "CCC", "RIR", "CCC", 'C', getIC2Cable(0, 1), 'R', "dustRedstone", 'I', "ingotIron");
			RecipeLoader.addShapedRecipe((ItemStack)st, "CRC", "CIC", "CRC", 'C', getIC2Cable(0, 1), 'R', "dustRedstone", 'I', "ingotIron");

			st = JSTUtils.getModItemStack("ic2:re_battery");
			RecipeLoader.removeRecipeByOutput((ItemStack)st, true, true);
			RecipeLoader.addShapedRecipe((ItemStack)st, " C ", "TRT", " R ", 'C', getIC2Cable(4, 1), 'T', "ingotTin", 'R', "dustRedstone");

			st = JSTUtils.getModItemStack("ic2:advanced_re_battery");
			RecipeLoader.removeRecipeByOutput((ItemStack)st, true, true);
			RecipeLoader.addShapedRecipe((ItemStack)st, "CSC", "BLB", 'C', getIC2Cable(0, 1), 'S', "dustSulfur", 'B', "ingotBronze", 'L', "dustLead");

			st = JSTUtils.getModItemStack("ic2:resource", 1, 12);
			RecipeLoader.removeRecipeByOutput((ItemStack)st, true, true);
			RecipeLoader.addShapedRecipe((ItemStack)st, "III", "I I", "III", 'I', "ingotIron");

			st = JSTUtils.getModItemStack("ic2:ingot", 2);
			RecipeLoader.removeRecipeByOutput((ItemStack)st, true, true);
			RecipeLoader.addShapedRecipe((ItemStack)st, "III", "BBB", "TTT", 'I', "ingotIron", 'B', "ingotBronze", 'T', "ingotTin");

			st = JSTUtils.getModItemStack("ic2:te", 1, 46);
			RecipeLoader.removeRecipeByOutput((ItemStack)st, true, true);
			RecipeLoader.addShapedRecipe((ItemStack)st, " I ", "I I", "IFI", 'I', "ingotIron", 'F', Blocks.FURNACE);

			st = JSTUtils.getModItemStack("ic2:mining_pipe", 16);
			RecipeLoader.removeRecipeByOutput((ItemStack)st, true, true);
			RecipeLoader.addShapedRecipe((ItemStack)st, "I I", "I I", "ITI", 'I', "ingotIron", 'T', JSTUtils.getModItemStack("ic2:treetap"));

			RecipeLoader.addShapedRecipe(JSTUtils.getModItemStack("ic2:fence", 12), " H ", "III", "III", 'H', "craftingToolForgeHammer", 'I', "ingotIron");
		}

		obj = new FluidStack[] {new FluidStack(FluidRegistry.WATER, 2), FluidRegistry.getFluidStack("ic2distilled_water", 2), FluidRegistry.getFluidStack("steam", 16000), FluidRegistry.getFluidStack("ic2steam", 16000)};
		MRecipes.HeatExcFakeRecipes.add(RecipeContainer.newContainer(null, new FluidStack[] {((FluidStack[])obj)[1], new FluidStack(FluidRegistry.LAVA, 333)}, null, new FluidStack[] {((FluidStack[])obj)[3], FluidRegistry.getFluidStack("ic2pahoehoe_lava", 16000)}, 0, 0));
		for (Entry<String, HeatExchangeProperty> en : Recipes.liquidCooldownManager.getHeatExchangeProperties().entrySet()) {
			if ("lava".equals(en.getKey())) continue;
			FluidStack res = new FluidStack(en.getValue().outputFluid, Math.max((int) Math.ceil(16000 / (double)en.getValue().huPerMB), 1));
			FluidStack inp = FluidRegistry.getFluidStack(en.getKey(), res.amount); if (inp == null) continue;
			MRecipes.HeatExcFakeRecipes.add(RecipeContainer.newContainer(null, new FluidStack[] {((FluidStack[])obj)[0], inp}, null, new FluidStack[] {((FluidStack[])obj)[2], res}, 0, 0));
			MRecipes.HeatExcFakeRecipes.add(RecipeContainer.newContainer(null, new FluidStack[] {((FluidStack[])obj)[1], inp}, null, new FluidStack[] {((FluidStack[])obj)[3], res}, 0, 0));
		}

		try {
			Map<ItemStack, BaseSeed> baseSeeds = (Map<ItemStack, BaseSeed>) ReflectionUtils.getFieldValue("ic2.core.crop.IC2Crops", "baseSeeds", Crops.instance);
			for (ItemStack st3 : baseSeeds.keySet()) {
				if (st3 != null && !st3.isEmpty()) {
					it = st3.getItem();
					if ((it == Item.getItemFromBlock(Blocks.RED_FLOWER) || it == Item.getItemFromBlock(Blocks.YELLOW_FLOWER)) && st3.getItemDamage() == 32767)
						st3.setItemDamage(0);
				}
			}
		} catch (Throwable t) {}
	}

	/**addMaceratorRecipe
	 * @param in input 
	 * @param out output
	 * */
	public static void addMacRec(Object in, ItemStack out) {
		try {
			if (in instanceof ItemStack)
				Recipes.macerator.addRecipe(Recipes.inputFactory.forStack((ItemStack)in), null, false, new ItemStack[] { out });
			else if (in instanceof OreDictStack)
				Recipes.macerator.addRecipe(Recipes.inputFactory.forOreDict(((OreDictStack)in).name, ((OreDictStack)in).count), null, false, new ItemStack[] { out });
		} catch (Throwable t) {
		}
	}

	/**addExtractorRecipe
	 * @param in input 
	 * @param out output
	 * */
	public static void addExtRec(Object in, ItemStack out) {
		try {
			if (in instanceof ItemStack)
				Recipes.extractor.addRecipe(Recipes.inputFactory.forStack((ItemStack)in), null, false, new ItemStack[] { out });
			else if (in instanceof OreDictStack)
				Recipes.extractor.addRecipe(Recipes.inputFactory.forOreDict(((OreDictStack)in).name, ((OreDictStack)in).count), null, false, new ItemStack[] { out });
		} catch (Throwable t) {
		}
	}

	/**addCompressorRecipe
	 * @param in input 
	 * @param out output
	 * */
	public static void addCompRec(Object in, ItemStack out) {
		try {
			if (in instanceof ItemStack)
				Recipes.compressor.addRecipe(Recipes.inputFactory.forStack((ItemStack)in), null, false, new ItemStack[] { out });
			else if (in instanceof OreDictStack)
				Recipes.compressor.addRecipe(Recipes.inputFactory.forOreDict(((OreDictStack)in).name, ((OreDictStack)in).count), null, false, new ItemStack[] { out });
		} catch (Throwable t) {
		}
	}

	/** Metal Former Recipe Adder
	 * @param mode 0 = Cutting, 1 = Extruding, 2 = Rolling
	 * */
	public static void addFormerRecipe(Object in, ItemStack out, int mode) {
		try {
			IBasicMachineRecipeManager rm = null;
			if (mode == 0)
				rm = Recipes.metalformerCutting;
			else if (mode == 1)
				rm = Recipes.metalformerExtruding;
			else if (mode == 2)
				rm = Recipes.metalformerRolling;
			
			if (rm != null) {
				if (in instanceof ItemStack)
					rm.addRecipe(Recipes.inputFactory.forStack((ItemStack)in), null, false, new ItemStack[] { out });
				else if (in instanceof OreDictStack)
					rm.addRecipe(Recipes.inputFactory.forOreDict(((OreDictStack)in).name, ((OreDictStack)in).count), null, false, new ItemStack[] { out });
			}
		} catch (Throwable t) {
		}
	}

	public static void addCentrifugeRecipe(int temp, Object in, ItemStack... out) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setShort("minHeat", (short) Math.max(1, temp));
			if (in instanceof ItemStack)
				Recipes.centrifuge.addRecipe(Recipes.inputFactory.forStack((ItemStack)in), tag, false, out);
			else if (in instanceof OreDictStack)
				Recipes.centrifuge.addRecipe(Recipes.inputFactory.forOreDict(((OreDictStack)in).name, ((OreDictStack)in).count), tag, false, out);
		} catch (Throwable a_1) {
		}
	}

	public static void addCannerRecipe(ItemStack can, ItemStack in, ItemStack out) {
		try {
			Recipes.cannerBottle.addRecipe(Recipes.inputFactory.forStack(can), Recipes.inputFactory.forStack(in), out, false);
		} catch (Throwable t) {}
	}

	public static void addUURecipe(Object obj, int uB) {
		try {
			if (obj instanceof ItemStack) {
				UuIndex.instance.add((ItemStack) obj, uB / 10.0D);
			} else if (obj instanceof String) {
				List<ItemStack> ls = OreDictionary.getOres((String)obj);
				for (ItemStack st : ls)
					UuIndex.instance.add(st.copy(), uB / 10.0D);
			}
		} catch (Throwable t) {}
	}

	public static void addAmplifier(Object in, int amp) {
		try {
			IRecipeInput ir = null;
			if (in instanceof ItemStack)
				ir = Recipes.inputFactory.forStack((ItemStack)in);
			else if (in instanceof String)
				ir = Recipes.inputFactory.forOreDict((String) in, 1);
			else if (in instanceof OreDictStack)
				ir = Recipes.inputFactory.forOreDict(((OreDictStack)in).name, ((OreDictStack)in).count);
			if (ir != null)
				Recipes.matterAmplifier.addRecipe(ir, Integer.valueOf(amp), null, false);
		} catch (Throwable t) {
		}
	}

	public static void addScrapDrop(Object obj, float cnc) {
		try {
			ItemStack st = ItemStack.EMPTY;
			if (obj instanceof ItemStack)
				st = (ItemStack)obj;
			else if (obj instanceof String)
				st = JSTUtils.getFirstItem((String)obj);
			else if (obj instanceof OreDictStack)
				st = JSTUtils.getFirstItem(((OreDictStack)obj).name);
			if (!st.isEmpty())
				Recipes.scrapboxDrops.addDrop(st, cnc);
		} catch (Throwable t) {}
	}

	/** @param t 0 = Copper, 1 = GFC, 2 = Gold, 3 = HV, 4 = Tin, 5 = Detector, 6 = Switch */
	@Nonnull
	public static ItemStack getIC2Cable(int t, int i, int a) {
		ItemStack st = JSTUtils.getModItemStack("ic2:cable", a, t);
		if (st.isEmpty()) return ItemStack.EMPTY;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("type", (byte) t);
		nbt.setByte("insulation", (byte) i);
		st.setTagCompound(nbt);
		return st;
	}

	/** @param t 0 = Copper, 1 = GFC, 2 = Gold, 3 = HV, 4 = Tin, 5 = Detector, 6 = Switch */
	@Nonnull
	public static ItemStack getIC2Cable(int t, int i) {
		return getIC2Cable(t, i, 1);
	}

	public static void banFromRecycle(ItemStack toBan) {
		if (!toBan.isEmpty())
			try {
				Recipes.recyclerBlacklist.add(Recipes.inputFactory.forStack(toBan));
			} catch (Throwable t) {}
	}

	public static boolean growCrop(TileEntity te, int n, boolean increaseSize) {
		if (!JSTCfg.ic2Loaded || n <= 0) return false;
		try {
			if (te instanceof ICropTile) {
				ICropTile cr = ((ICropTile)te);
				if (cr.getCrop() != null && cr.getCrop().canGrow(cr)) {
					int g = cr.getGrowthPoints() + n;
					int dur = cr.getCrop().getGrowthDuration(cr);
					if (increaseSize && g >= dur) {
						cr.setCurrentSize((byte) (cr.getCurrentSize() + 1));
						cr.setGrowthPoints(0);
						try {
							crop_dirty.setBoolean(te, true);
						} catch (Exception e) {}
					} else {
						cr.setGrowthPoints(Math.min(g, dur));
					}
					return true;
				}
			}
		} catch (Throwable t) {}
		return false;
	}

	public static void removeIC2RecipeByInput(ItemStack in, IBasicMachineRecipeManager ic) {
		try {
			if (!ic.isIterable()) return;
			Iterator<? extends MachineRecipe<IRecipeInput, Collection<ItemStack>>> it = ic.getRecipes().iterator();
			while (it.hasNext()) {
				MachineRecipe<IRecipeInput, Collection<ItemStack>> rec = it.next();
				if (rec.getInput().matches(in)) it.remove();
			}
		} catch (Throwable t) {}
	}

	public static void removeIC2RecipeByOutput(Collection<ItemStack> out, IBasicMachineRecipeManager ic) {
		try {
			if (!ic.isIterable()) return;
			ItemStack[] oa = out.toArray(new ItemStack[0]);
			Iterator<? extends MachineRecipe<IRecipeInput, Collection<ItemStack>>> it = ic.getRecipes().iterator();
			while (it.hasNext()) {
				MachineRecipe<IRecipeInput, Collection<ItemStack>> rec = it.next();
				ItemStack[] ro = rec.getOutput().toArray(new ItemStack[0]);
				if (oa.length != ro.length) continue;
				int cnt = 0;
				for (int n = 0; n < oa.length; n++) {
					ItemStack st1 = oa[n];
					ItemStack st2 = ro[n];
					if (st1 == null) st1 = ItemStack.EMPTY; if (st2 == null) st2 = ItemStack.EMPTY;
					if (OreDictionary.itemMatches(st1, st2, false)) cnt++;
				}
				if (cnt == oa.length)
					it.remove();
			}
		} catch (Throwable t) {}
	}

	public static void addCropSoilType(String n, Block b) {
		if (n == null || b == null || n.isEmpty() || b == Blocks.AIR) return;
		try { EnumHelper.addEnum(CropSoilType.class, n.toUpperCase(), new Class[] {Block.class}, b); } catch (Throwable t) {}
	}

	private static void addFuelRecipe(ItemStack mat, ItemStack er, int sID, ItemStack... cen) {
		if (mat.isEmpty() || er.isEmpty()) return;
		ItemStack rod = new ItemStack(jstn, 1, sID);
		addCannerRecipe(er, mat, rod);

		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, sID + 1), 
				"RIR",
				'R', rod,
				'I', "plateIron"
				);

		RecipeLoader.addShapedRecipe(new ItemStack(jstn, 1, sID + 2), 
				"RIR", "CIC", "RIR",
				'R', rod,
				'I', "plateIron",
				'C', "plateCopper"
				);

		er = JSTUtils.getFirstItem("dustIron");
		if (!er.isEmpty() && cen.length > 0 && cen.length <= 3) {
			for (int n = 0; n < 3; n++) {
				ItemStack[] result = new ItemStack[cen.length + 1];
				for (int m = 0; m < cen.length; m++) {
					ItemStack st2 = cen[m].copy();
					st2.setCount(cen[m].getCount() * (n == 0 ? 1 : n == 1 ? 2 : 4));
					result[m] = st2;
				}
				result[cen.length] = new ItemStack(er.getItem(), n == 0 ? 1 : n == 1 ? 3 : 7, er.getMetadata());
				addCentrifugeRecipe(4000, new ItemStack(jstn, 1, sID + 3 + n), result);
			}
		}
	}

	private static ItemStack[] getNuggetOrDust(String name) {
		ItemStack res = JSTUtils.getFirstItem("nugget" + name);
		if (res.isEmpty()) {
			res = JSTUtils.getFirstItem("dust" + name);
			if (!res.isEmpty())
				return new ItemStack[] {null, null, null, res};
		} else {
			return new ItemStack[] {res};
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private static void registerItemModel(ItemReactorComponent b) {
		ResourceLocation l = b.getRegistryName();
		for (Integer i : b.behaviours.keySet()) {
			if (i == null) continue;
			try {
				ModelLoader.setCustomModelResourceLocation(b, i, new ModelResourceLocation(l.getResourceDomain() + ":compat/ic2/ni" + i, "inventory"));
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
