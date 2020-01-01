package dohyun22.jst3.compat.ic2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import dohyun22.jst3.utils.WeightedItem;
import ic2.api.crops.Crops;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CropLoader {
	
	public static void load() {
		if (JSTUtils.isClient()) CropJSTBase.tex = new ArrayList();

		new CropElecMushroom();
		new CropCactus();
		new CropFishing();

		new CropJST("bluewheat", "Eloraam", 7, 1, -400, new String[] {"Blue", "Nikolite", "Wheat"}, 6, 2, 1, 0, 3, 0).block("oreNikolite", "blockNikolite").drop(1, "dustNikolite").fin();
		new CropJST("coalplant", null, 5, 3, -600, new String[] {"Coal", "Fuel", "Carbon"}, 5, 1, 0, 1, 0, 0).block("oreCoal", "blockCoal").drop(1, Items.COAL).fin();
		new CropJST("blazewood", null, 5, 3, 0, new String[] {"Blaze", "Wood"}, 7, 4, 0, 2, 2, 0).drop(2, Items.BLAZE_POWDER).drop(1, Items.BLAZE_ROD).glow(6).fin();
		new CropJST("thorwood", "GregoriusT", 4, 1, 250, new String[] {"Thorium", "Radioactive", "Coal"}, 8, 5, 0, 0, 1, 1).block("oreCoal", "blockCoal", "oreThorium", "blockThorium").drop(1, new ItemStack(JSTItems.item1, 1, 100)).fin();
		new CropJST("urania", "Alblaka", 4, 1, 250, new String[] {"Uranium", "Radioactive", "Danger"}, 10, 5, 0, 0, 2, 1).block("oreUranium", "blockUranium").mod(2, "ic2:nuclear", 6).drop(1, "ic2:nuclear", 2).fin();
		new CropJST("irium", "Alblaka", 4, 1, 400, new String[] {"Iridium", "Rare", "Quantum"}, 13, 1, 0, 4, 0, 2).block("oreIridium", "blockIridium").drop(2, ItemStack.EMPTY).drop(1, JSTUtils.getModItemStack("ic2:misc_resource", 1, 2)).fin();
		new CropJST("diaplant", null, 5, 1, 0, new String[] {"Diamond", "Carbon", "Rare", "Crystal"}, 11, 0, 0, 2, 1, 0).block("oreDiamond", "blockDiamond").drop(2, ItemStack.EMPTY).drop(1, Items.DIAMOND).fin();
		new CropJST("chorus", "jeb_", 4, 3, 0, new String[] {"Chorus", "Ender"}, 6, 1, 3, 0, 2, 0).drop(1, Items.CHORUS_FRUIT).seed(new ItemStack(Items.CHORUS_FRUIT)).fin();
		new CropJST("emeria", "jeb_", 5, 1, 0, new String[] {"Emerald", "Green", "Crystal"}, 10, 0, 0, 1, 3, 0).block("oreEmerald", "blockEmerald").drop(1, Items.EMERALD).fin();
		new CropJST("lapoleaf", null, 4, 1, 0, new String[] {"Lapis Lazuli", "Blue", "Dye"}, 6, 2, 0, 1, 5, 1).block("oreLapis", "blockLapis").drop(1, new ItemStack(Items.DYE, 1, 4)).fin();
		new CropJST("glowood", null, 4, 1, 0, new String[] {"Glowstone", "Bright", "Nether"}, 6, 3, 0, 2, 4, 1).block(Blocks.GLOWSTONE).drop(1, Items.GLOWSTONE_DUST).glow(12).fin();
		new CropJST("alumia", "GregoriusT", 4, 2, 0, new String[] {"Shiny", "Leaves", "Metal", "Aluminium"}, 6, 4, 0, 2, 0, 1).block("oreBauxite", "oreAluminum", "blockAluminum").drop(getNuggetOrDust("Aluminum")).fin();
		new CropJST("niobis", "dohyun22", 4, 2, 0, new String[] {"Shiny", "Leaves", "Metal", "Niobium"}, 8, 2, 0, 3, 1, 1).block("oreNiobium", "blockNiobium").drop(getNuggetOrDust("Niobium")).fin();
		new CropJST("zincium", "GregoriusT", 4, 2, 0, new String[] {"Shiny", "Leaves", "Metal", "Zinc"}, 6, 2, 0, 3, 1, 1).block("oreZinc", "blockZinc").drop(getNuggetOrDust("Zinc")).fin();
		new CropJST("naquadium", "GregoriusT", 4, 2, 0, new String[] {"Leaves", "Metal", "Alien", "Naquadah"}, 14, 6, 0, 6, 1, 3).block("oreNaquadah", "blockNaquadah").drop(getNuggetOrDust("Naquadah")).fin();
		new CropJST("oilplant", "SpaceToad", 4, 2, 0, new String[] {"Berries", "Oil", "Fuel"}, 7, 4, 1, 0, 0, 0).drop(1, new ItemStack(JSTItems.item1, 1, 151)).fin();
		new CropJST("saltgrass", "dohyun22", 4, 1, 0, new String[] {"Food", "Salt"}, 4, 2, 1, 1, 2, 2).drop(1, new ItemStack(JSTItems.item1, 1, 107)).fin();
		new CropJST("nitrowood", "Creepers", 3, 1, -600, new String[] {"Creeper", "Sulfur", "Saltpeter"}, 5, 3, 0, 5, 1, 1).drop(1, Items.GUNPOWDER).fin();
		new CropJST("milkberry", null, 4, 1, -600, new String[] {"Food", "Milk"}, 6, 0, 3, 0, 1, 0).drop(1, new ItemStack(JSTItems.item1, 1, 15)).fin();
		new CropJST("chickenbush", null, 3, 1, -600, new String[] {"Chicken", "Egg", "Food", "Feather"}, 6, 0, 4, 1, 0, 0).drop(2, Items.EGG, Items.FEATHER).drop(1, Items.CHICKEN).fin();
		new CropJST("meatgrass", null, 3, 1, -600, new String[] {"Grass", "Meat", "Food"}, 6, 0, 4, 1, 3, 0).drop(1, Items.CHICKEN, Items.BEEF, Items.PORKCHOP, Items.MUTTON, Items.RABBIT).fin();
		new CropJST("graveplant", null, 4, 1, 0, new String[] {"Undead", "Rotten"}, 5, 0, 1, 1, 0, 3).drop(1, Items.ROTTEN_FLESH, Items.BONE).fin();
		new CropJST("enderberry", null, 4, 1, 0, new String[] {"Ender", "Shiny"}, 9, 5, 0, 2, 1, 0).drop(3, Items.ENDER_PEARL).drop(1, Items.ENDER_EYE).fin();
		new CropJST("slimeberry", "MDiyo", 4, 3, 0, new String[] {"Slime", "Bouncy", "Sticky"}, 5, 3, 0, 0, 0, 2).drop(1, Items.SLIME_BALL).fin();
		new CropJST("ghastplant", null, 3, 1, -1000, new String[] {"Nether", "Ingredient", "Ghast"}, 8, 1, 2, 0, 0, 0).drop(1, Items.GHAST_TEAR).fin();
		new CropJST("leatherleaf", null, 3, 2, -600, new String[] {"Leather", "Ingredient", "Cow"}, 5, 0, 0, 3, 1, 0).drop(1, Items.LEATHER).fin();
		new CropJST("starflower", null, 3, 1, 0, new String[] {"Nether", "Wither", "Nether Star"}, 13, 2, 0, 0, 1, 0).drop(3, ItemStack.EMPTY).drop(1, Items.NETHER_STAR).fin();
		new CropJST("avataria", "Vaporware", 3, 1, 0, new String[] {"Rare", "Alien", "Unobtainium"}, 15, 4, 0, 1, 1, 1).block("oreUnobtainium", "blockUnobtainium").drop(getNuggetOrDust("Unobtainium")).glow(4).fin();
		new CropJST("quarkium", "dohyun22", 3, 1, 0, new String[] {"Rare", "Dense", "Alien"}, 16, 1, 0, 3, 1, 0).drop(2, ItemStack.EMPTY).drop(1, new ItemStack(JSTItems.item1, 1, 49)).glow(6).fin();
		new CropJST("rubium", "Eloraam", 4, 2, 0, new String[] {"Red", "Crystal", "Ruby"}, 7, 1, 0, 0, 3, 0).block("oreRuby", "blockRuby").drop(1, new ItemStack(JSTItems.item1, 1, 16)).fin();
		new CropJST("peridotia", "MrTJP", 4, 2, 0, new String[] {"Green", "Crystal", "Peridot"}, 6, 1, 0, 0, 3, 0).block("orePeridot", "blockPeridot").drop(1, new ItemStack(JSTItems.item1, 1, 17)).fin();
		new CropJST("sapphiria", "Eloraam", 4, 2, 0, new String[] {"Blue", "Crystal", "Sapphire"}, 6, 1, 0, 0, 3, 0).block("oreSapphire", "blockSapphire").drop(1, new ItemStack(JSTItems.item1, 1, 18)).fin();
		new CropJST("quartzint", "jeb_", 4, 2, 0, new String[] {"White", "Crystal", "Nether"}, 5, 0, 0, 2, 0, 1).block("oreQuartz", Blocks.QUARTZ_BLOCK).drop(1, Items.QUARTZ).fin();
		new CropJST("spiderbush", null, 3, 1, 0, new String[] {"Bush", "Spider", "Web"}, 5, 0, 1, 2, 0, 1).drop(2, Items.STRING).drop(1, Blocks.WEB, Items.SPIDER_EYE).fin();
		new CropJST("wolframia", "Eloraam", 4, 2, 0, new String[] {"Heavy", "Leaves", "Metal", "Tungsten"}, 9, 1, 0, 2, 0, 0).block("oreTungsten", "blockTungsten").drop(3, ItemStack.EMPTY).drop(1, new ItemStack(JSTItems.item1, 1, 74)).fin();
		new CropJST("titania", "GregoriusT", 4, 2, 0, new String[] {"Shiny", "Leaves", "Metal", "Titanium"}, 9, 1, 0, 3, 0, 0).block("oreBauxite", "oreTitaium", "blockTitaium", "oreRutile").drop(getNuggetOrDust("Titanium")).fin();
		new CropJST("oceanplant", "jeb_", 3, 1, 0, new String[] {"Ocean", "Prismarine", "Crystal"}, 4, 0, 0, 1, 2, 2).drop(2, Items.PRISMARINE_SHARD).drop(1, Items.PRISMARINE_CRYSTALS).fin();
		new CropJST("sulfonia", "jeb_", 4, 2, 0, new String[] {"Yellow", "Fire", "Sulfur"}, 5, 4, 0, 1, 1, 1).block(Blocks.LAVA, Blocks.FLOWING_LAVA, "oreSulfur", "blockSulfur").drop(1, "dustSulfur").fin();
		new CropJST("goldenapple", "Notch", 4, 2, 0, new String[] {"Gold", "Apple", "Magic"}, 12, 0, 4, 0, 3, 0).block("oreGold", "blockGold").drop(9, Items.GOLDEN_APPLE).drop(1, new ItemStack(Items.GOLDEN_APPLE, 1, 1)).fin();

		if (!JSTUtils.getValidOne("dustNickel", "nuggetNickel").isEmpty())
			new CropJST("nickelum", "KingLemming", 4, 2, 0, new String[] {"Shiny", "Leaves", "Metal", "Nickel"}, 6, 3, 0, 3, 1, 2).block("oreNickel", "blockNickel").drop(getNuggetOrDust("Nickel")).fin();
		if (!JSTUtils.getValidOne("dustPlatinum", "nuggetPlatinum").isEmpty())
			new CropJST("platina", "KingLemming", 4, 2, 0, new String[] {"Shiny", "Leaves", "Metal", "Platinum"}, 11, 1, 0, 5, 2, 0).block("orePlatinum", "blockPlatinum").drop(getNuggetOrDust("Platinum")).fin();
		if (JSTUtils.oreValid("gemApatite"))
			new CropJST("apatium", "SirSengir", 3, 1, 0, new String[] {"Blue", "Crystal", "Apatite"}, 5, 2, 0, 0, 2, 0).block("oreApatite", "blockApatite").drop(1, "gemApatite").fin();

		boolean hasOak = ReflectionUtils.checkFieldExists("ic2.core.crop.IC2Crops", "cropOakSapling");
		ItemStack st = hasOak ? ItemStack.EMPTY : new ItemStack(Blocks.SAPLING, 1, 0);
		new CropJST("oak", "Notch", 3, 1, 0, new String[] {"Oak", "Wood", "Apple", "Bonsai"}, 3, 0, 1, 1, 1, 0).drop(2, Blocks.LOG).drop(1, st, Items.APPLE).seed(st).migr("ic2:oak_sapling", hasOak).fin();
		new CropJST("spruce", "Notch", 3, 1, 0, new String[] {"Spruce", "Wood", "Bonsai"}, 3, 0, 1, 1, 1, 0).drop(2, new ItemStack(Blocks.LOG, 1, 1)).drop(1, JSTUtils.modStack(st, -1, 1)).seed(JSTUtils.modStack(st, -1, 1)).migr("ic2:spruce_sapling", hasOak).fin();
		new CropJST("birch", "Notch", 3, 1, 0, new String[] {"Birch", "Wood", "Bonsai"}, 3, 0, 1, 1, 1, 0).drop(2, new ItemStack(Blocks.LOG, 1, 2)).drop(1, JSTUtils.modStack(st, -1, 2)).seed(JSTUtils.modStack(st, -1, 2)).migr("ic2:birch_sapling", hasOak).fin();
		new CropJST("jungle", "Notch", 3, 1, 0, new String[] {"Jungle", "Wood", "Bonsai"}, 3, 0, 1, 1, 1, 0).drop(2, new ItemStack(Blocks.LOG, 1, 3)).drop(1, JSTUtils.modStack(st, -1, 3)).seed(JSTUtils.modStack(st, -1, 3)).migr("ic2:jungle_sapling", hasOak).fin();
		new CropJST("acacia", "Notch", 3, 1, 0, new String[] {"Acacia", "Wood", "Bonsai"}, 3, 0, 1, 1, 1, 0).drop(2, Blocks.LOG2).drop(1, JSTUtils.modStack(st, -1, 4)).seed(JSTUtils.modStack(st, -1, 4)).migr("ic2:acacia_sapling", hasOak).fin();
		new CropJST("roofedoak", "Notch", 3, 1, 0, new String[] {"Dark Oak", "Wood", "Apple", "Bonsai"}, 3, 0, 1, 1, 1, 0).drop(2, new ItemStack(Blocks.LOG2, 1, 1)).drop(1, JSTUtils.modStack(st, -1, 5), Items.APPLE).seed(JSTUtils.modStack(st, -1, 5)).migr("ic2:dark_oak_sapling", hasOak).fin();
		new CropJST("rubwood", "Alblaka", 3, 1, -1000, new String[] {"Rubber", "Wood", "Bonsai"}, 4, 2, 0, 2, 1, 0).drop(2, JSTUtils.getModItemStack("ic2:rubber_wood")).drop(1, JSTUtils.getModItemStack("ic2:sapling"), JSTUtils.getModItemStack("ic2:misc_resource", 1, 4)).seed(JSTUtils.getModItemStack("ic2:sapling")).fin();

		st = JSTUtils.getModItemStack("immersiveengineering:seed");
		if (!st.isEmpty()) new CropJST("hemp", "BluSunrize", 4, 1, -350, new String[] {"Industrial", "Hemp", "Addictive"}, 3, 1, 0, 1, 2, 2).drop(1, JSTUtils.getModItemStack("immersiveengineering:material", 1, 4), st).seed(st).fin();
		st = JSTUtils.getValidOne("cropBarley", "plantBarley");
		if (!st.isEmpty()) new CropJST("barley", "mDiyo", 3, 1, 0, new String[] {"Barley", "Crop", "Edible"}, 2, 0, 3, 1, 2, 1).drop(1, st).seed(st).fin();
		st = JSTUtils.getValidOne("cropRice", "plantRice", "plantWildrice");
		if (!st.isEmpty()) new CropJST("rice", "glitchfiend", 3, 1, 0, new String[] {"Rice", "Crop", "Edible"}, 2, 0, 4, 0, 2, 1).drop(1, st).seed(st).fin();
		st = JSTUtils.getModItemStack("biomesoplenty:bamboo");
		if (!st.isEmpty()) new CropJST("bamboo", "glitchfiend", 3, 1, -200, new String[] {"Bamboo", "Stick"}, 3, 0, 1, 1, 1, 2).drop(1, st).seed(st).fin();
		if (JSTCfg.tcLoaded) {
			st = JSTUtils.getModItemStack("thaumcraft:shimmerleaf");
			if (!st.isEmpty()) new CropJST("shimmerleaf", "Azanor", 3, 1, 0, new String[] {"Mercury", "Toxic", "Magic"}, 5, 2, 0, 1, 1, 0).drop(1, st).seed(st).biome(BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.FOREST).fin();
			st = JSTUtils.getModItemStack("thaumcraft:cinderpearl");
			if (!st.isEmpty()) new CropJST("cinderpearl", "Azanor", 3, 1, 0, new String[] {"Blaze", "Fire", "Magic"}, 6, 1, 0, 2, 2, 0).drop(1, st, Items.BLAZE_POWDER).seed(st).biome(BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SANDY).fin();
		}

		if (JSTUtils.isClient()) {
			registerTex();
			CropJSTBase.tex = null;
		}
	}

	@SideOnly(Side.CLIENT)
	private static void registerTex() {
		Map<ResourceLocation, TextureAtlasSprite> map = new HashMap();
		for (ResourceLocation rl : CropJST.tex)
			map.put(rl, FMLClientHandler.instance().getClient().getTextureMapBlocks().getAtlasSprite(rl.toString()));
		Crops.instance.registerCropTextures(map);
	}

	private static List<WeightedItem> getNuggetOrDust(String name) {
		ItemStack res = JSTUtils.getFirstItem("nugget" + name);
		if (res.isEmpty()) {
			res = JSTUtils.getFirstItem("dust" + name);
			if (!res.isEmpty())
				return Arrays.asList(new WeightedItem(ItemStack.EMPTY, 3), new WeightedItem(res, 1));
		} else {
			return Arrays.asList(new WeightedItem(res, 1));
		}
		return null;
	}
}
