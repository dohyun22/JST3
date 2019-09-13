package dohyun22.jst3.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.compat.ic2.CompatIC2;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.recipes.ItemList;
import dohyun22.jst3.recipes.JSTCraftingRecipe;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;

public class RecipeLoader extends Loadable {
	private static int rID;
	
	@Override
	public boolean canLoad() {
		return true;
	}
	
	@Override
	public String getRequiredMod() {
		return null;
	}
	
	@Override
	public void init() {
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 0), new ItemStack(Items.COAL), 0.5F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 1), new ItemStack(Items.DIAMOND), 1.0F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 1), new ItemStack(Items.DIAMOND), 1.0F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 2), new ItemStack(Items.GOLD_INGOT), 1.0F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 2), new ItemStack(Items.GOLD_INGOT), 1.0F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 3), new ItemStack(Items.IRON_INGOT), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 3), new ItemStack(Items.IRON_INGOT), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 4), new ItemStack(Items.DYE, 5, 4), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 4), new ItemStack(Items.DYE, 5, 4), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 5), new ItemStack(Items.REDSTONE, 5), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 5), new ItemStack(Items.REDSTONE, 5), 0.7F);
		
		ItemStack st = JSTUtils.getFirstItem("ingotCopper");
		if (!st.isEmpty()) {
			GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 6), st, 0.7F);
			GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 6), st, 0.7F);
		}
		st = JSTUtils.getFirstItem("ingotTin");
		if (!st.isEmpty()) {
			GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 7), st, 0.7F);
			GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 7), st, 0.7F);
		}
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 8), new ItemStack(Items.EMERALD), 1.0F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 8), new ItemStack(Items.EMERALD), 1.0F);
		st = JSTUtils.getFirstItem("ingotSilver");
		if (!st.isEmpty()) {
			GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 9), st, 0.7F);
			GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 9), st, 0.7F);
		}
		st = JSTUtils.getFirstItem("ingotLead");
		if (!st.isEmpty()) {
			GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 10), st, 0.7F);
			GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 10), st, 0.7F);
		}
		st = JSTUtils.getFirstItem("ingotUranium");
		if (!st.isEmpty()) {
			GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 11), st, 0.7F);
			GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 11), st, 0.7F);
		}
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 12), new ItemStack(JSTItems.item1, 5, 27), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 12), new ItemStack(JSTItems.item1, 5, 27), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 13), new ItemStack(JSTItems.item1, 1, 16), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 13), new ItemStack(JSTItems.item1, 1, 17), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 15), new ItemStack(JSTItems.item1, 1, 29), 0.7F);
		st = JSTUtils.getFirstItem("dustSulfur");
		if (!st.isEmpty()) GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 14), st, 0.5F);
		st = JSTUtils.getFirstItem("ingotNickel");
		if (!st.isEmpty()) GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockNO, 1, 15), st, 0.7F);
		st = JSTUtils.getFirstItem("ingotPlatinum");
		if (!st.isEmpty()) GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockEO, 1, 14), st, 2.0F);
		
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 2), new ItemStack(JSTItems.item1, 1, 1), 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 34), new ItemStack(JSTItems.item1, 1, 25), 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 35), new ItemStack(JSTItems.item1, 1, 26), 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 30), new ItemStack(JSTItems.item1, 1, 29), 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 32), new ItemStack(JSTItems.item1, 1, 31), 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 59), new ItemStack(JSTItems.item1, 1, 33), 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 37), new ItemStack(JSTItems.item1, 1, 36), 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 71), new ItemStack(JSTItems.item1, 1, 70), 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 79), new ItemStack(JSTItems.item1, 1, 78), 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 96), new ItemStack(JSTItems.item1, 1, 95), 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 39), new ItemStack(JSTItems.item1, 1, 38), 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 104), new ItemStack(JSTItems.item1, 1, 70), 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 158), new ItemStack(JSTItems.item1, 1, 157), 0.0F);
		st = new ItemStack(Items.IRON_INGOT, 2);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 160), st, 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 161), st, 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 162), st, 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 184), new ItemStack(JSTItems.item1, 1, 183), 0.0F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockOre, 1, 0), new ItemStack(JSTItems.item1, 1, 38), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockOre, 1, 1), new ItemStack(JSTItems.item1, 1, 90), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockOre, 1, 4), new ItemStack(JSTItems.item1, 5, 27), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockOre, 1, 6), new ItemStack(JSTItems.item1, 1, 29), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockOre, 1, 7), new ItemStack(JSTItems.item1, 1, 31), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockOre, 1, 10), new ItemStack(JSTItems.item1, 1, 70), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockOre, 1, 11), new ItemStack(JSTItems.item1, 1, 74), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockOre, 1, 12), new ItemStack(JSTItems.item1, 1, 95), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockOre, 1, 13), new ItemStack(JSTItems.item1, 1, 16), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockOre, 1, 14), new ItemStack(JSTItems.item1, 1, 17), 0.7F);
		GameRegistry.addSmelting(new ItemStack(JSTBlocks.blockOre, 1, 15), new ItemStack(JSTItems.item1, 1, 18), 0.7F);
		if (JSTCfg.customMat) {
			GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 171), new ItemStack(JSTItems.item1, 1, 170), 0.0F);
			GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 174), new ItemStack(JSTItems.item1, 1, 173), 0.0F);
			GameRegistry.addSmelting(new ItemStack(JSTItems.item1, 1, 177), new ItemStack(JSTItems.item1, 1, 176), 0.0F);
		}
	}

	@Override
	public void postInit() {
		if (!JSTCfg.ic2Loaded || !JSTCfg.RIC2C) {
			addWireRecipe("Tin", 1, 4001, 4002, 2, 3);
			addWireRecipe("Copper", 2, 4003, 4004, 2, 3);
			addWireRecipe("Gold", 3, 4005, 4006, 3, 4);
			addWireRecipe("Iron", 4, 4007, 4008, 3, 4);
			addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 5, 4009), "WWW", "IDI", "WWW", 'W', new ItemStack(Blocks.GLASS), 'I', "ingotBlueAlloy", 'D', "gemDiamond");
			addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 4010), " I ", "CRC", 'R', "dustRedstone", 'C', new ItemStack(JSTBlocks.blockTile, 1, 4007), 'I', new ItemStack(Blocks.LEVER));
			addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 4011), " I ", "CRC", 'R', "dustRedstone", 'C', new ItemStack(JSTBlocks.blockTile, 1, 4007), 'I', "circuitBasic");
		}
		addWireRecipe("Lead", 0, 4013, 4014, 4, 6);
		addWireRecipe("Silver", 3, 4015, 4016, 3, 4);
		addWireRecipe("BlueAlloy", 3, 4017, 4018, 3, 4);
		addWireRecipe("Platinum", 4, 4019, 4020, 4, 6);
		addWireRecipe("Niobium", 6, 4021, 4022, 2, 3);
		addWireRecipe("Zinc", 1, 4023, 4024, 2, 3);
		addWireRecipe("Brass", 2, 4025, 4026, 2, 3);
		addWireRecipe("Iridium", 7, 4027, 4028, 3, 4);
		addWireRecipe("Rhenium", 8, 4029, 4030, 3, 4);
		
		//Items
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10000), "S S", "ZRZ", " S ", 'S', "ingot" + (JSTUtils.oreValid("ingotSteel") ? "Steel" : "Iron"), 'Z', "ingotZinc", 'R', "ingotRedAlloy");
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10001), "S", "T", 'S', "ingotSilver", 'T', Items.STICK);
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10003), " NG", "NGN", "GN ", 'N', "dustNikolite", 'G', Blocks.GLASS_PANE);
		addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 10005), new ItemStack(JSTItems.item1, 1, 9000), new ItemStack(JSTItems.item1, 1, 60));

		Object obj = JSTUtils.getModItemStack("ic2:iridium_drill");
		if (obj instanceof ItemStack && ((ItemStack)obj).isEmpty()) obj = ItemList.motors[5];
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10006), "RNR", "CDC", "JBJ", 'R', "ingotRhenium", 'N', "ingotNeutronium", 'C', ItemList.circuits[6], 'D', obj, 'J', new ItemStack(JSTItems.item1, 1, 11), 'B', new ItemStack(JSTItems.item1, 1, 12017));

		String pf = JSTCfg.ic2Loaded ? "plate" : "ingot";
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10007), "SSR", "NNT", " LL", 'S', "gemSapphire", 'R', ItemList.raygens[3], 'N', new ItemStack(JSTBlocks.blockTile, 1, 4022), 'T', new ItemStack(JSTItems.item1, 1, 12011), 'L', pf + "Titanium");
		for (int n = 0; n < 2; n++)
			addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10008), "ASA", "gGg", "ABA", 'A', pf + "Aluminum", 'S', ItemList.sensors[2], 'g', "dustGlowstone", 'G', Blocks.GLASS, 'B', new ItemStack(JSTItems.item1, 1, n == 0 ? 12003 : 12004));

		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10009), " B", "BI", 'I', "ingotIron", 'B', "ingotBrass");

		ItemStack st = new ItemStack(JSTItems.item1, 1, 10010);
		addShapedRecipe(st, "WCN", 'W', ItemList.uninsCables[1], 'C', ItemList.circuits[1], 'N', "dustNikolite");
		addShapelessRecipe(st, st);

		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10011), "S  ", "  I", 'S', "stickWood", 'I', "ingotIron");
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10012), "SGS", " S ", " S ", 'S', "stickWood", 'G', Blocks.GLASS_PANE);
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10013), "  I", "   ", "S  ", 'I', "ingotIron", 'S', "stickWood");
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10014), "  I", " M ", "B  ", 'I', "gemPeridot", 'M', ItemList.motors[1], 'B', new ItemStack(JSTItems.item1, 1, 12000));
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10015), "W W", " I ", " B ", 'W', ItemList.uninsCables[2], 'I', ItemList.circuits[1], 'B', new ItemStack(JSTItems.item1, 1, 12000));
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10016), "G", "I", "B", 'G', Items.GLOWSTONE_DUST, 'I', ItemList.circuits[2], 'B', new ItemStack(JSTItems.item1, 1, 12000));
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10018), " S ", "PDP", " B ", 'S', ItemList.sensors[3], 'P', ItemList.baseMaterial[3], 'D', new ItemStack(JSTItems.item1, 1, 10016), 'B', new ItemStack(JSTItems.item1, 1, 12010));

		st = new ItemStack(JSTItems.item1, 1, 10019);
		ItemStack st2 = st;
		addShapedRecipe(st2, "BB ", "FCB", " BC", 'B', "ingotBrass", 'C', new ItemStack(JSTItems.item1, 1, 9000), 'F', Items.FLINT_AND_STEEL);
		st = st.copy(); JSTUtils.getOrCreateNBT(st).setBoolean("UPG", true);
		addShapedRecipe(st, "BCB", "CFC", "BCB", 'B', Items.BLAZE_POWDER, 'C', Items.FIRE_CHARGE, 'F', st2);

		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10020), "CCR", "LLT", 'C', ItemList.coils[3], 'R', new ItemStack(JSTItems.item1, 1, 133), 'T', new ItemStack(JSTItems.item1, 1, 12011), 'L', pf + "Niobium");

		RecipeLoader.addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10030), "W", "C", "B", 'W', ItemList.cables[1], 'C', ItemList.circuits[1], 'B', Blocks.STONE_BUTTON);
		RecipeLoader.addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10031), "AAC", "mMm", "IRI", 'A', "ingotAluminum", 'C', Blocks.HARDENED_CLAY, 'm', ItemList.motors[1], 'M', ItemList.machineBlock[1], 'I', ItemList.circuits[1], 'R', Items.MINECART);
		RecipeLoader.addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10041), "C C", "CNC", "CIC", 'C', ItemList.cables[2], 'N', "dustNikolite", 'I', ItemList.circuits[1]);
		obj = JSTUtils.getValidOne("stickIron", "ingotIron");
		RecipeLoader.addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10042), "ABA", "AAA", "SMS", 'A', ItemList.baseMaterial[2], 'B', new ItemStack(JSTItems.item1, 1, 12004), 'S', obj, 'M', ItemList.motors[1]);
		RecipeLoader.addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10043), "AIA", "ABA", "SMS", 'A', ItemList.baseMaterial[2], 'I', ItemList.circuits[2], 'B', new ItemStack(JSTItems.item1, 1, 12011), 'S', obj, 'M', ItemList.motors[2]);
		RecipeLoader.addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10044), "NGN", " C ", 'N', "dustNikolite", 'G', Blocks.GLASS_PANE, 'C', ItemList.circuits[1]);
		RecipeLoader.addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10045), "  B", "  B", "IIM", 'B', "ingotIron", 'M', ItemList.motors[1], 'I', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
		st = new ItemStack(JSTItems.item1, 1, 9000);
		RecipeLoader.addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10046), "IR", " C", " C", 'I', "ingotIron", 'R', "dyeRed", 'C', st);
		RecipeLoader.addShapedRecipe(new ItemStack(JSTItems.item1, 1, 10050), "  N", " B ", "I  ", 'N', "dustNikolite", 'B', new ItemStack(JSTItems.item1, 1, 12000), 'I', "ingotIron");
		RecipeLoader.addSHWRecycle(new ItemStack(JSTItems.item1, 1, 10060), "III", "CCC", "CCC", 'I', "ingotIron", 'C', st, 'I', "ingotIron");
		RecipeLoader.addSHWRecycle(new ItemStack(JSTItems.item1, 1, 10061), "BIB", 'B', new ItemStack(JSTItems.item1, 1, 10060), 'I', "ingotIron");
		RecipeLoader.addSHWRecycle(new ItemStack(JSTItems.item1, 1, 10062), "BIB", 'B', new ItemStack(JSTItems.item1, 1, 10061), 'I', "plateTitanium");
		RecipeLoader.addSHWRecycle(new ItemStack(JSTItems.item1, 1, 10063), "BIB", 'B', new ItemStack(JSTItems.item1, 1, 10062), 'I', "plateTungsten");

		addToolRecipes(10100, "gemRuby", true);
		addToolRecipes(10106, "gemPeridot", true);
		addToolRecipes(10112, "gemSapphire", true);

		RecipeLoader.addSHWRecycle(new ItemStack(JSTItems.item1, 1, 13000), "CcC", "CFC", 'C', st, 'c', ItemList.circuits[1], 'F', Blocks.FURNACE);
		RecipeLoader.addSHWRecycle(new ItemStack(JSTItems.item1, 1, 13001), "GCG", "GTG", 'G', Blocks.GLASS, 'C', ItemList.circuits[2], 'T', new ItemStack(JSTBlocks.blockTile, 1, 192));
		RecipeLoader.addSHWRecycle(new ItemStack(JSTItems.item1, 1, 13002), " C ", "CWC", " C ", 'C', st, 'W', new ItemStack(JSTBlocks.blockTile, 1, 201));

		//Materials
		obj = Blocks.WOODEN_PRESSURE_PLATE;
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 86), "CSC", "SBS", "CSC", 'C', ItemList.cables[1], 'S', "dustRedstone", 'B', obj);
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 86), " C ", "VBV", " C ", 'C', ItemList.cables[1], 'V', "circuitPrimitive", 'B', obj);
		obj = new ItemStack(JSTItems.item1, 1, 85);
		addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 86), ItemList.cables[1], obj, obj, obj);
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 87), " R ", "NBN", " R ", 'R', "dustRedstone", 'N', "dustNikolite", 'B', "circuitBasic");
		addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 87), obj, obj, "circuitBasic");
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 88), "RGR", "NCN", "RGR", 'R', "dustRedstone", 'G', new ItemStack(Items.GLOWSTONE_DUST), 'N', "dustNikolite", 'C', "circuitBasic");
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 88), "RNR", "GCG", "RNR", 'R', "dustRedstone", 'G', new ItemStack(Items.GLOWSTONE_DUST), 'N', "dustNikolite", 'C', "circuitBasic");
		addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 88), "dustRedstone", "dustRedstone", new ItemStack(Items.GLOWSTONE_DUST), new ItemStack(Items.GLOWSTONE_DUST), "circuitGood");
		addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 88), new ItemStack(JSTItems.item1, 1, 83), new ItemStack(JSTItems.item1, 1, 83), "circuitBasic");
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 28), "BGB", "RCR", "BGB", 'G', new ItemStack(JSTItems.item1, 1, 106), 'B', pf + "BlueAlloy", 'R', pf + "RedAlloy", 'C', "circuitAdvanced");
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 28), "TRT", "BCB", 'T', new ItemStack(JSTItems.item1, 1, 83), 'R', pf + "RedAlloy", 'B', new ItemStack(JSTItems.item1, 1, 106), 'C', "circuitAdvanced");
		addSHWRecycle(new ItemStack(JSTItems.item1, 1, 103), "NSN", "SBS", "NSN", 'N', new ItemStack(JSTItems.item1, 1, 150), 'S', "gemSapphire", 'B', "gemDiamond");
		addShapedRecipe(new ItemStack(JSTItems.item1, 3, 152), " G ", "GWG", "RWR", 'G', new ItemStack(Blocks.GLASS), 'W', ItemList.uninsCables[1], 'R', "dustRedstone");

		st = new ItemStack(JSTItems.item1, 1, 10013);
		obj = Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE;
		addShapedRecipe(false, new ItemStack(JSTItems.item1, 1, 160), "P  ", " S ", "   ", 'P', obj, 'S', st);
		addShapedRecipe(false, new ItemStack(JSTItems.item1, 1, 161), " P ", " S ", "   ", 'P', obj, 'S', st);
		addShapedRecipe(false, new ItemStack(JSTItems.item1, 1, 162), "  P", " S ", "   ", 'P', obj, 'S', st);
		addShapedRecipe(new ItemStack(JSTItems.item1, 3, 185), "SSS", 'S', "ingotSolder");
		addShapedRecipe(new ItemStack(JSTItems.item1, 16, 9000), " I ", "IGI", " I ", 'I', "plateAluminum", 'G', Blocks.GLASS_PANE);
		addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 67), new ItemStack(JSTItems.item1, 1, 9014));
		addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 108), new ItemStack(JSTItems.item1, 1, 9018));
		
		obj = new OreDictStack(JSTCfg.ic2Loaded && JSTUtils.oreValid("platePlatinum") ? "platePlatinum" : JSTUtils.oreValid("ingotPlatinum") ? "ingotPlatinum" : "gemDiamond");
		addSHWRecycle(new ItemStack(JSTItems.item1, 1, 11), "AIB", "NCN", "DcE",
				'A', new ItemStack(JSTItems.item1, 1, 6), 'B', new ItemStack(JSTItems.item1, 1, 7),
				'C', new ItemStack(JSTItems.item1, 1, 8), 'D', new ItemStack(JSTItems.item1, 1, 9),
				'E', new ItemStack(JSTItems.item1, 1, 10), 'I', pf + "Iridium", 'N', pf + "Niobium", 'c', "circuitHighTech");

		//Machine blocks
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 1), "WIW", "I I", "WIW", 'W', "plankWood", 'I', "ingotIron");
		addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 2), "III", "B B", "III", 'I', "ingotIron", 'B', "ingotZinc");
		addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 2), "III", "B B", "III", 'I', "ingotIron", 'B', "ingotTin");
		addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 2), "III", "B B", "III", 'I', "ingotIron", 'B', "ingotBrass");

		obj = new ItemStack[] {new ItemStack(JSTItems.item1, 1, 105), new ItemStack(JSTItems.item1, 1, 106)};
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 9), "III", "BPB", "III", 'I', pf + "Rhenium", 'B', pf + "Naquadah", 'P', ((ItemStack[])obj)[1]);
		for (int n = 2; n <= 9; n++) {
			if (n == 8) continue;
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, n + 1), "III", "IBI", "III", 'I', ItemList.baseMaterial[n], 'B', ((ItemStack[]) obj)[n <= 3 ? 0 : 1]);
		}

		obj = new Object[] {ItemList.cables[4], Blocks.CHEST, new ItemStack(JSTItems.item1, 1, 9000)};
		for (int n = 0; n < 6; n++) {
			boolean f = n % 2 == 0;
			st = new ItemStack(JSTBlocks.blockTile, 1, 11 + n);
			addSHWRecycle(st, 
					f ? "m" : "M", f ? "M" : "m",
					'm', ((Object[])obj)[n / 2],
					'M', ItemList.machineBlock[1]
					);
			if (f) {
				st2 = new ItemStack(JSTBlocks.blockTile, 1, 12 + n);
				addShapelessRecipe(st, st2);
				addShapelessRecipe(st2, st);
			}
		}

		//Machines
		addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 21), " W ", "BBB", " M ", 'W', ItemList.cables[1], 'B', new ItemStack(JSTItems.item1, 1, 12000), 'M', ItemList.machineBlock[1]);
		addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 21), " W ", "BMB", 'W', ItemList.cables[1], 'B', new ItemStack(JSTItems.item1, 1, 12002), 'M', ItemList.machineBlock[1]);
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 22), " C ", "WBW", " M ", 'W', ItemList.cables[2], 'C', ItemList.circuits[2], 'B', new ItemStack(JSTItems.item1, 1, 12005), 'M', ItemList.machineBlock[2]);
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 23), " C ", "WBW", " M ", 'W', ItemList.cables[3], 'C', ItemList.circuits[3], 'B', new ItemStack(JSTItems.item1, 1, 12012), 'M', ItemList.machineBlock[3]);
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 24), " W ", "CBC", " M ", 'W', ItemList.cables[4], 'C', ItemList.circuits[4], 'B', new ItemStack(JSTItems.item1, 1, 12017), 'M', ItemList.machineBlock[4]);
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 25), "BWB", "CMC", 'W', ItemList.cables[5], 'C', ItemList.circuits[5], 'B', new ItemStack(JSTItems.item1, 1, 12018), 'M', ItemList.machineBlock[5]);
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 26), "CWC", "WBW", "CMC", 'W', ItemList.cables[6], 'C', ItemList.circuits[6], 'B', new ItemStack(JSTItems.item1, 1, 12019), 'M', ItemList.machineBlock[6]);
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 27), "CWC", "WBW", "CMC", 'W', ItemList.cables[7], 'C', ItemList.circuits[7], 'B', new ItemStack(JSTItems.item1, 1, 12020), 'M', ItemList.machineBlock[7]);
		
		for (int n = 0; n < 9; n++) {
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 181 + n), 
					"CHC", "IMI", "CIC",
					'C', ItemList.cables[n + 1],
					'H', new ItemStack(Blocks.CHEST),
					'I', ItemList.circuits[n + 1],
					'M', ItemList.machineBlock[n + 1]
					);
			
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 190 + n), 
					"mCm", "cMD",
					'm', n >= 3 ? "plateElectricalSteel" : "ingotIron",
					'C', ItemList.coils[n],
					'c', ItemList.cables[n],
					'M', ItemList.machineBlock[n],
					'D', ItemList.cables[n + 1]
					);
		}
		
		obj = new ItemStack(JSTBlocks.blockTile, 1, 5075);
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 101), 
				"CSC", "MFM", "CSC",
				'C', ItemList.circuits[5],
				'S', ItemList.sensors[6],
				'M', ItemList.machineBlock[6],
				'F', obj
				);
		
		addSHWRecycle((ItemStack)obj, 
				"TRT", "CSC", "TMT",
				'T', pf + "Tungsten",
				'R', ItemList.raygens[6],
				'C', ItemList.circuits[5],
				'S', ItemList.coils[9],
				'M', ItemList.machineBlock[6]
				);
		
		obj = new Object[] {new ItemStack(JSTItems.item1, 1, 9001), pf + "Tungsten", pf + "Iridium", pf + "Rhenium"};
		for (int n = 0; n < 8; n++) {
			if (n < 3) {
				addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 102 + n), 
						"cmc", "RMR", "cCc",
						'c', ItemList.circuits[6 + n],
						'R', ItemList.raygens[6 + n],
						'C', new ItemStack(JSTBlocks.blockTile, 1, 101 + n),
						'm', ItemList.machineBlock[6 + n],
						'M', new ItemStack(JSTBlocks.blockTile, 1, 5076 + n)
						);
				
				addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 5076 + n), 
						"pRp", "cFc", "pRp",
						'c', ItemList.circuits[6 + n],
						'p', ((Object[])obj)[1 + n],
						'R', ItemList.raygens[6 + n],
						'F', new ItemStack(JSTBlocks.blockTile, 1, 5075 + n)
						);
			}
			
			if (n < 5)
				addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 201 + n), 
					"EEE", "BMB", "CGC",
					'B', ((Object[])obj)[0],
					'G', new ItemStack(Blocks.GLASS),
					'E', ItemList.baseMaterial[1 + n],
					'M', ItemList.machineBlock[1 + n],
					'C', ItemList.circuits[1 + n]
					);
		
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 221 + n), 
					" C ", "cMc", " C ",
					'C', ItemList.coils[1 + n],
					'c', ItemList.circuits[1 + n],
					'M', ItemList.machineBlock[1 + n]
					);
			
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 231 + n), "oRo", "CMC", "CoC",
					'o', ItemList.motors[1 + n],
					'R', ItemList.raygens[1 + n],
					'C', ItemList.circuits[1 + n],
					'M', ItemList.machineBlock[1 + n]
					);
			
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 241 + n), "coc", "cMc", "CoC",
					'o', ItemList.motors[1 + n],
					'C', ItemList.circuits[1 + n],
					'M', ItemList.machineBlock[1 + n],
					'c', ItemList.uninsCables[1 + n]
					);
			
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 251 + n), "geg", "mMm", "CeC",
					'g', new ItemStack(Blocks.GLASS),
					'e', new ItemStack(JSTItems.item1, 1, 9000),
					'm', ItemList.motors[1 + n],
					'M', ItemList.machineBlock[1 + n],
					'C', ItemList.circuits[1 + n]
					);
			
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 261 + n), "CoC", "CMC", "oSo",
					'o', ItemList.motors[1 + n],
					'S', ItemList.sensors[1 + n],
					'C', ItemList.circuits[1 + n],
					'M', ItemList.machineBlock[1 + n]
					);

			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 271 + n), "PmP", "PmP", "CMC",
					'P', ItemList.baseMaterial[1 + n],
					'm', ItemList.motors[1 + n],
					'C', ItemList.circuits[1 + n],
					'M', ItemList.machineBlock[1 + n]
					);
			
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 281 + n), "c", "M", "C", 'c', ItemList.circuits[1 + n], 'M', ItemList.machineBlock[1 + n], 'C', ItemList.coils[1 + n]);
			
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 291 + n), "BmB", "GMG", "BCB",
					'B', ItemList.baseMaterial[1 + n],
					'm', ItemList.motors[1 + n],
					'G', n < 5 ? new ItemStack(Items.DIAMOND) : "ingotTungsten",
					'M', ItemList.machineBlock[1 + n],
					'C', ItemList.circuits[1 + n]
					);

			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 301 + n), "mLm", "CMC", "BLB",
					'm', ItemList.motors[1 + n],
					'L', ItemList.coils[1 + n],
					'C', ItemList.circuits[1 + n],
					'M', ItemList.machineBlock[1 + n],
					'B', ItemList.baseMaterial[1 + n]
					);
			
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 311 + n), " G ", "mMm", "ECE",
					'G', new ItemStack(Items.GLOWSTONE_DUST),
					'm', ItemList.motors[1 + n],
					'M', ItemList.machineBlock[1 + n],
					'E', ItemList.baseMaterial[1 + n],
					'C', ItemList.circuits[1 + n]
					);

			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 321 + n), " C ", "WMW", " c ",
					'C', ItemList.coils[1 + n],
					'W', ItemList.cables[1 + n],
					'M', ItemList.machineBlock[1 + n],
					'c', ItemList.circuits[1 + n]
					);
		}

		//#5000 multi-block structure parts
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 5000), 
				" C ", "TMT",
				'C', ItemList.circuits[2],
				'T', pf + "Titanium",
				'M', ItemList.machineBlock[3]
				);
		
		obj = pf + "Invar";
		if(!JSTUtils.oreValid((String)obj))
			obj = pf + "Iron";
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 5001), " I ", "IBI", " I ",
				'I', obj,
				'B', Blocks.NETHER_BRICK
				);

		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 4, 5002), "III", "CMC", "III", 'I', ItemList.baseMaterial[2], 'C', ItemList.circuits[1], 'M', ItemList.machineBlock[1]);
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 4, 5003), "III", "CMC", "III", 'I', ItemList.baseMaterial[3], 'C', ItemList.circuits[3], 'M', ItemList.machineBlock[3]);
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 4, 5083), "III", "CMC", "III", 'I', ItemList.baseMaterial[6], 'C', ItemList.circuits[5], 'M', ItemList.machineBlock[4]);

		addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 4, 5004), "WNW",
				'W', ItemList.cables[1],
				'N', "dustNikolite"
				);
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 5005), "SGS", "WMW",
				'S', pf + "Silicon",
				'G', new ItemStack(Blocks.GLASS),
				'W', new ItemStack(JSTBlocks.blockTile, 1, 5004),
				'M', ItemList.machineBlock[0]
				);
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 5010), " I ", " m ", "IMI",
				'I', pf + "Aluminum",
				'm', ItemList.motors[0],
				'M', ItemList.machineBlock[0]
				);
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 5015), "iIi", "ImI", "iMi",
				'i', new ItemStack(Blocks.IRON_BARS),
				'I', pf + "Aluminum",
				'm', ItemList.motors[0],
				'M', ItemList.machineBlock[0]
				);
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 5020), "ACA", "RMR", "BRB",
				'A', pf + "Aluminum",
				'C', new ItemStack(JSTBlocks.blockTile, 1, 5004),
				'R', pf + "RedAlloy",
				'M', ItemList.machineBlock[0],
				'B', pf + "BlueAlloy"
				);
		
		for (int n = 0; n < 2; n++) {
			addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, n == 0 ? 5011 : 5016), "WMW",
					'W', new ItemStack(JSTBlocks.blockTile, 1, n == 0 ? 5010 : 5015),
					'M', ItemList.machineBlock[0]
					);
			
			addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 5012 + n), " W ", "WMW", " W ",
					'W', new ItemStack(JSTBlocks.blockTile, 1, 5011 + n),
					'M', ItemList.machineBlock[1 + n]
					);
			
			addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 5017 + n), " W ", "WMW", " W ",
					'W', new ItemStack(JSTBlocks.blockTile, 1, 5016 + n),
					'M', ItemList.machineBlock[1 + n]
					);
		}
		
		for (int n = 1; n <= 9; n++) {
			if (n == 5) continue;
			addSHWRecycle(ItemList.coils[n], "WWW", "WSW", "WWW", 'W', ItemList.uninsCables[n], 'S', n <= 3 ? "plankWood" : "ingotElectricalSteel");
		}
		addSHWRecycle(ItemList.coils[5], "WwW", "wSw", "WwW", 'W', ItemList.uninsCables[5], 'w', ItemList.uninsCables[4], 'S', "ingotElectricalSteel");
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 2, 5080), "AIA", "I I", "AIA",
				'I', new ItemStack(Blocks.IRON_BARS),
				'A', pf + "Aluminum"
				);
		
		addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 4, 5082), "WwW",
				'W', ItemList.uninsCables[4],
				'w', ItemList.uninsCables[2]
				);
		
		//#6000 machines
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6001), "DCD", "BMB", "WCW",
				'D', new ItemStack(JSTBlocks.blockTile, 1, 5004),
				'C', "circuitAdvanced",
				'B', pf + "BlueAlloy",
				'M', ItemList.machineBlock[2],
				'W', ItemList.cables[4]
				);
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6002), " C ", "WMW", "LGL",
				'C', "circuitGood",
				'W', ItemList.cables[4],
				'M', ItemList.machineBlock[1],
				'G', new ItemStack(Blocks.GLASS_PANE),
				'L', new ItemStack(Items.GLOWSTONE_DUST)
				);
		
		addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 6003), "SBS", "F F", "SBS",
				'S', new ItemStack(Blocks.STONE),
				'F', new ItemStack(Blocks.FURNACE),
				'B', new ItemStack(Blocks.BRICK_BLOCK)
				);
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6004), "GSG", "C C", "CFC",
				'G', new ItemStack(Blocks.GLASS),
				'S', (JSTUtils.oreValid("ingotSilver") ? "ingotSilver" : "ingotGold"),
				'C', new ItemStack(Blocks.COBBLESTONE),
				'F', new ItemStack(Blocks.FURNACE)
				);
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6005), "CMC",
				'C', ItemList.cables[4],
				'M', ItemList.machineBlock[1]
				);
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6006), "mCm", "OMO", "mCm",
				'O', pf + "Rhenium",
				'C', ItemList.circuits[5],
				'M', ItemList.machineBlock[5],
				'm', ItemList.motors[5]
				);
		
		st = new ItemStack(JSTBlocks.blockTile, 1, 6007);
		addSHWRecycle(st, "IPI", "ImI",
				'I', pf + "Iron",
				'P', new ItemStack(Items.IRON_PICKAXE),
				'm', ItemList.motors[1]
				);
		
		st = st.copy();
		obj = st.copy();
		JSTUtils.setEnchant(Enchantments.SILK_TOUCH, 1, st);
		addShapedRecipe(st, " E ", "GMG",
				'E', "gemEmerald",
				'G', pf + "Gold",
				'M', obj
				);
		
		if (JSTCfg.ic2Loaded) {
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6008), "zzz", "cGc", "rMb",
					'z', pf + "Zinc",
					'c', ItemList.circuits[2],
					'G', new ItemStack(Blocks.GLASS_PANE),
					'r', "ingotRedAlloy",
					'M', ItemList.machineBlock[1],
					'b', "ingotBlueAlloy"
					);
			
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6009), "PcP", "dGd", "cMc",
					'P', ItemList.baseMaterial[3],
					'c', ItemList.circuits[3],
					'd', "gemPeridot",
					'G', new ItemStack(Blocks.GLASS_PANE),
					'M', ItemList.machineBlock[2]
					);
		}
		
		for (int n = 0; n < 4; n++)
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6010 + n), "CGC", "GTG", "CGC",
					'C', ItemList.circuits[6],
					'G', new ItemStack(JSTBlocks.blockTile, 1, 63 + n * 10),
					'T', new ItemStack(JSTBlocks.blockTile, 1, 196)
					);
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6020), "CcC", "H H", "CcC",
				'C', ItemList.coils[2],
				'c', ItemList.circuits[3],
				'H', new ItemStack(JSTBlocks.blockTile, 1, 5001)
				);
		
		st = new ItemStack(JSTBlocks.blockTile, 1, 6022);
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6021), "CCC", "MPM", "mmm",
				'C', ItemList.circuits[3],
				'M', ItemList.machineBlock[2],
				'P', st,
				'm', ItemList.motors[2]
				);
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6024), " W ", "CTC", " W ",
				'W', new ItemStack(JSTBlocks.blockTile, 1, 5082),
				'C', ItemList.circuits[2],
				'T', new ItemStack(JSTBlocks.blockTile, 1, 191)
				);
		
		addSHWRecycle(st, " F ", "CMC", "mmm",
				'F', new ItemStack(JSTBlocks.blockTile, 1, 5080),
				'C', ItemList.circuits[1],
				'M', ItemList.machineBlock[1],
				'm', ItemList.motors[1]
				);
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6025), "HCH", "CMC", "HCH",
				'H', new ItemStack(JSTBlocks.blockTile, 1, 5001),
				'C', ItemList.circuits[5],
				'M', ItemList.machineBlock[5]
				);
		
		if (JSTCfg.ticLoaded)
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6026), " c ", "CHC", " c ",
					'c', ItemList.circuits[2],
					'C', ItemList.coils[2],
					'H', new ItemStack(JSTBlocks.blockTile, 1, 5001)
					);
		
		st = new ItemStack(JSTItems.item1, 1, 9000);
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6027), "BIB", "CMC", "cOc",
				'B', ItemList.motors[1],
				'I', new ItemStack(Blocks.IRON_BARS),
				'C', ItemList.circuits[2],
				'M', ItemList.machineBlock[1],
				'c', st,
				'O', ItemList.coils[2]
				);
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6028), "BCB", "IMI", "CcC",
				'B', ItemList.motors[1],
				'C', ItemList.circuits[2],
				'I', new ItemStack(Blocks.IRON_BARS),
				'M', ItemList.machineBlock[1],
				'c', st
				);

		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6029), "WWW", "cTc", "MaM",
				'W', ItemList.uninsCables[3],
				'c', ItemList.circuits[3],
				'T', new ItemStack(JSTBlocks.blockTile, 1, 193),
				'M', ItemList.baseMaterial[3],
				'a', ItemList.cables[3]
				);

		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6030), "CPC", "LPL",
				'C', ItemList.circuits[1],
				'P', ItemList.baseMaterial[2],
				'L', Blocks.REDSTONE_LAMP
				);

		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6040), "PCP", "PMP",
				'P', ItemList.baseMaterial[1],
				'C', Blocks.CHEST,
				'M', ItemList.motors[1]
				);

		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6042), "PWP", "CMC", "mcm",
				'P', ItemList.baseMaterial[1],
				'W', Blocks.WOOL,
				'C', "dustCarbon",
				'M', ItemList.machineBlock[1],
				'm', ItemList.motors[1],
				'c', ItemList.circuits[3]
				);

		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6043), "MMM", "CBC", "DDD",
				'M', ItemList.motors[3],
				'C', ItemList.circuits[3],
				'B', ItemList.machineBlock[3],
				'D', "gemDiamond"
				);

		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6044), "LLL", "CMC", "LLL",
				'L', ItemList.coils[3],
				'C', ItemList.circuits[3],
				'M', new ItemStack(JSTBlocks.blockTile, 1, 243)
				);

		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6047), "AcA", "MMM", "AmA",
				'A', new ItemStack(JSTBlocks.blockTile, 1, 5003),
				'M', ItemList.motors[2],
				'c', new ItemStack(JSTBlocks.blockTile, 1, 252),
				'm', new ItemStack(JSTBlocks.blockTile, 1, 242)
				);

		if (JSTCfg.ic2Loaded)
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6031), "MSM", "CBC", "MSM",
					'M', ItemList.motors[3],
					'S', ItemList.sensors[3],
					'C', new ItemStack(JSTItems.item1, 1, 9000),
					'B', ItemList.machineBlock[3]
					);

		for (int n = 0; n < 3; n++)
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6033 + n), "CPC", "PLP",
					'C', ItemList.circuits[2 + n],
					'P', ItemList.baseMaterial[2 + n],
					'L', new ItemStack(JSTBlocks.blockTile, 1, n == 0 ? 6030 : 6032 + n)
					);

		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6041), "cCc", "CHC", "cCc",
				'C', ItemList.coils[2],
				'c', ItemList.circuits[2],
				'H', new ItemStack(JSTBlocks.blockTile, 1, 5001)
				);

		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6060), "ccc", "CMF",
				'c', new ItemStack(JSTItems.item1, 1, 9000),
				'C', ItemList.circuits[1],
				'M', ItemList.machineBlock[1],
				'F', new ItemStack(JSTItems.item1, 1, 10009)
				);

		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6062), " F ", "cMc", " C ",
				'c', new ItemStack(JSTItems.item1, 1, 9000),
				'C', ItemList.circuits[1],
				'M', ItemList.machineBlock[1],
				'F', new ItemStack(JSTItems.item1, 1, 10019)
				);

		//Generators
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 31), 
				"C", "M", "F",
				'C', ItemList.circuits[1],
				'M', ItemList.machineBlock[1],
				'F', new ItemStack(Blocks.FURNACE)
				);
		
		st = JSTUtils.getModItemStack("ic2:te", 1, 46);
		if (st.isEmpty()) st = new ItemStack(Blocks.FURNACE);
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 32), 
				" C ", " M ", "FFF",
				'C', ItemList.circuits[2],
				'M', ItemList.machineBlock[2],
				'F', st
				);
		
		addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 33), 
				"FCF", "FMF", "FFF",
				'C', ItemList.circuits[3],
				'M', ItemList.machineBlock[3],
				'F', st
				);
		
		obj = new Object[] {
				pf + "Iridium", pf + "Rhenium", pf + "Unobtainium",
				new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.GOLDEN_APPLE), new ItemStack(Items.SKULL, 1, 1),
				new ItemStack(Items.BLAZE_ROD), new ItemStack(Blocks.ENCHANTING_TABLE)
		};
		for (int n = 0; n < 3; n++) {
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 61 + n), 
					"PEP", "EME", "CEC",
					'P', ItemList.motors[1 + n],
					'E', ItemList.baseMaterial[1 + n],
					'M', ItemList.machineBlock[1 + n],
					'C', ItemList.circuits[1 + n]
					);
			
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 71 + n), 
					"PTP", "EME", "CmC",
					'P', new ItemStack(Blocks.IRON_BARS),
					'T', new ItemStack(Items.FLINT_AND_STEEL),
					'E', ItemList.baseMaterial[1 + n],
					'M', ItemList.machineBlock[1 + n],
					'C', ItemList.circuits[1 + n],
					'm', ItemList.motors[1 + n]
					);
		
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 81 + n), 
					"PmP", "EME", "CEC",
					'P', new ItemStack(Blocks.IRON_BARS),
					'E', ItemList.baseMaterial[1 + n],
					'M', ItemList.machineBlock[1 + n],
					'C', ItemList.circuits[1 + n],
					'm', ItemList.motors[1 + n]
					);
			
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 91 + n), 
					"PTP", "EME", "CmC",
					'P', new ItemStack(Blocks.NETHER_BRICK),
					'T', new ItemStack(Blocks.GLASS),
					'E', ItemList.baseMaterial[1 + n],
					'M', ItemList.machineBlock[1 + n],
					'C', ItemList.circuits[1 + n],
					'm', ItemList.motors[1 + n]
					);
			
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 111 + n), 
					"CPC", "EME", "CPC",
					'C', ItemList.circuits[2 + n],
					'P', ((Object[])obj)[3 + n],
					'E', ((Object[])obj)[Math.min(6 + n, 7)],
					'M', ItemList.machineBlock[1 + n]
					);
			
			addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 51 + n), 
					"CNC", "mMm", "CNC",
					'C', ItemList.circuits[4 + n],
					'N', new ItemStack(JSTItems.item1, 1, 103),
					'm', ((Object[])obj)[n],
					'M', ItemList.machineBlock[4 + n]
					);
		}
		
		for (int n = 0; n < 4; n++) {
			obj = ItemList.circuits[1 + n];
			addShapelessRecipe(new ItemStack(JSTBlocks.blockTile, 1, 40 + n), new ItemStack(JSTBlocks.blockTile, 1, 5005 + n), obj, obj);
		}
		
		addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 41), 
				"SSS", "SCS", "SSS",
				'S', "craftingSolarPanel",
				'C', "circuitGood"
				);
		
		for (int n = 0; n < 8; n++)
			addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 1, 42 + n), 
					" S ", "STS", " S ",
					'S', new ItemStack(JSTBlocks.blockTile, 1, 41 + n),
					'T', new ItemStack(JSTBlocks.blockTile, 1, 190 + n)
					);
		
		//Superconductor
		addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 4, 4012), 
				"CHC", "WWW", "CEC",
				'C', ItemList.baseMaterial[3],
				'H', ItemList.circuits[4],
				'W', "craftingSCWire",
				'E', new ItemStack(JSTItems.item1, 1, 9016)
				);
		
		addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 5, 4012), 
				"CHC", "WWW", "CEC",
				'C', ItemList.baseMaterial[3],
				'H', ItemList.circuits[4],
				'W', "craftingSCWire",
				'E', new ItemStack(JSTItems.item1, 1, 9013)
				);
		
		addShapedRecipe(new ItemStack(JSTBlocks.blockTile, 10, 4031), 
				"FFF", "DCD", "FFF",
				'F', new ItemStack(JSTItems.item1, 1, 106),
				'C', new ItemStack(JSTItems.item1, 1, 150),
				'D', "gemDiamond"
				);
		
		//Non-Tile device
		addShapelessRecipe(new ItemStack(JSTBlocks.block1, 4, 14), new ItemStack(JSTBlocks.block1, 1, 13));
		
		for (int n = 0; n < 2; n++)
			addShapedRecipe(new ItemStack(JSTBlocks.block1, 10, 1), 
					"BMB", "MBM", "BMB",
					'B', new ItemStack(Blocks.STONEBRICK),
					'M', "ingot" + (n == 0 ? "Zinc" : "Tin")
					);
		
		addSHWRecycle(new ItemStack(JSTBlocks.block1, 1, 5), 
				"NNN", "ICI", "NNN",
				'I', "ingotIridium",
				'N', "ingotNeutronium",
				'C', new ItemStack(JSTItems.item1, 1, 11)
				);

		if (!JSTCfg.hardEG)
			addShapedRecipe(new ItemStack(JSTBlocks.block1, 1, 6), 
					"ENE", "EDE", "ENE",
					'E', Items.ENDER_EYE,
					'D', Blocks.DIAMOND_BLOCK,
					'N', Items.NETHER_STAR
					);

		addShapedRecipe(new ItemStack(JSTBlocks.block1, 1, 8), 
				"II", "II",
				'I', new ItemStack(JSTBlocks.block2, 1, 1)
				);

		addShapedRecipe(new ItemStack(JSTBlocks.block1, 1, 11), 
				"III", "III", "III",
				'I', "dustNikolite"
				);

		addShapelessRecipe(new ItemStack(JSTItems.item1, 9, 27), new ItemStack(JSTBlocks.block1, 1, 11));

		addShapedRecipe(new ItemStack(JSTBlocks.block1, 1, 12), 
				"III", "III", "III",
				'I', "ingotNeutronium"
				);

		addShapelessRecipe(new ItemStack(JSTItems.item1, 9, 24), new ItemStack(JSTBlocks.block1, 1, 12));

		addShapedRecipe(new ItemStack(JSTBlocks.block2, 4, 3), 
				"BB", "BB",
				'B', new ItemStack(JSTBlocks.block2, 1, 2)
				);

		addShapedRecipe(new ItemStack(JSTBlocks.block2, 4, 4), 
				"BB", "BB",
				'B', new ItemStack(JSTBlocks.block2, 1, 3)
				);

		addShapedRecipe(new ItemStack(JSTBlocks.block2, 2, 5), 
				"BB",
				'B', new ItemStack(JSTBlocks.block2, 1, 2)
				);

		addShapedRecipe(new ItemStack(JSTBlocks.block2, 4, 7), 
				"BB", "BB",
				'B', new ItemStack(JSTBlocks.block2, 1, 6)
				);

		addShapedRecipe(new ItemStack(JSTBlocks.block2, 4, 8), "BB", "BB", 'B', new ItemStack(JSTBlocks.block2, 1, 7));

		addShapedRecipe(new ItemStack(JSTBlocks.block2, 2, 9), "BB", 'B', new ItemStack(JSTBlocks.block2, 1, 6));

		addShapedRecipe(new ItemStack(JSTBlocks.blockOHW, 32), "HHH", "CCC", 'H', ItemList.uninsCables[4], 'C', ItemList.uninsCables[2]);

		obj = new Object[] {new ItemStack(Blocks.GLASS), "gemPeridot", "gemSapphire", "gemRuby", "gemDiamond", new ItemStack(JSTItems.item1, 1, 103), "dustNaquadah", "dustUnobtainium",
				new ItemStack(JSTItems.item1, 1, 12004), new ItemStack(JSTItems.item1, 1, 12011), new ItemStack(JSTItems.item1, 1, 12012), new ItemStack(JSTItems.item1, 1, 12017), new ItemStack(JSTItems.item1, 1, 12018)
		};
		for (int n = 1; n <= 8; n++) {
			addSHWRecycle(ItemList.motors[n], 
					" b ", "cCc", " b ",
					'b', ItemList.baseMaterial[n],
					'c', ItemList.cables[n],
					'C', ItemList.coils[n]
					);
			
			addSHWRecycle(ItemList.sensors[n], 
					"cbc", "CiC", "cbc",
					'c', ItemList.cables[n],
					'b', ItemList.baseMaterial[n],
					'C', ItemList.circuits[n],
					'i', ((Object[])obj)[n - 1]
					);
			
			addSHWRecycle(ItemList.raygens[n], 
					"COC", "bib", "bib",
					'C', ItemList.circuits[n],
					'O', ItemList.coils[n],
					'b', ItemList.baseMaterial[n],
					'i', ((Object[])obj)[n - 1]
					);
			
			if (n <= 5) {
				st = new ItemStack(JSTItems.item1, 1, 12039 + n);
				addSHWRecycle(st, 
						"WWW", "CBC",
						'W', ItemList.uninsCables[n],
						'C', ItemList.circuits[n],
						'B', ((Object[])obj)[n + 7]
						);
			}
			
			if (n <= 3)
				addSLWRecycle(new ItemStack(JSTItems.item1, 1, 12049 + n), st, new ItemStack(JSTBlocks.blockTile, 1, 5005 + n));
		}
		obj = new ItemStack(JSTBlocks.blockTile, 1, 5008);
		addSLWRecycle(new ItemStack(JSTItems.item1, 1, 12053), new ItemStack(JSTItems.item1, 1, 12043), obj, obj, obj, obj);
		
		//Battery
		addBasicBatteryRecipe(new int[] {12000, 12001}, 0, ItemList.cables[1], new Object[] {"dustRedstone"}, null);
		
		addBasicBatteryRecipe(new int[] {12002, 12003}, 0, ItemList.cables[1], new Object[] {"dustNikolite"}, null);
		
		addBasicBatteryRecipe(new int[] {12004, 12005, 12006}, 0, ItemList.cables[0], new Object[] {pf + "Lead", "dustSulfur"}, null);
		
		addBasicBatteryRecipe(new int[] {12007, 12008, 12009}, 0, ItemList.cables[2], new Object[] {"dustNickel", new ItemStack(JSTItems.item1, 1, 9009)}, new ItemStack(JSTItems.item1, 1, 9000));
		
		addBasicBatteryRecipe(new int[] {12010, 12011, 12012}, 0, ItemList.cables[2], new Object[] {"dustLithium"}, null);
		
		addBasicBatteryRecipe(new int[] {12013, 12014, 12015, 12016}, 0, ItemList.cables[2], new Object[] {new ItemStack(JSTItems.item1, 1, 76)}, null);
		
		addBasicBatteryRecipe(new int[] {12023, 12024}, 15000, ItemList.cables[1], new Object[] {JSTUtils.oreValid("dustCoal") ? "dustCoal" : new ItemStack(Items.COAL), "dustRedstone"}, null);
		
		addBasicBatteryRecipe(new int[] {12025, 12026}, 50000, ItemList.cables[1], new Object[] {"dustSulfur", "dustNikolite"}, null);
		
		addBasicBatteryRecipe(new int[] {12027, 12028, 12029}, 0, ItemList.cables[2], new Object[] {"dustSodium"}, null);
		
		st = JSTUtils.getModItemStack("ic2:crafting", 1, 15);
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 12017), 
				"NCN", "NnN", "NCN",
				'N', (st.isEmpty() ? "plateNiobium" : st),
				'n', new ItemStack(JSTItems.item1, 1, 103),
				'C', "circuitAdvanced"
				);
		
		st = new ItemStack(JSTItems.item1, 1, 12017);
		addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 12018), st, st, st, st);
		st = new ItemStack(JSTItems.item1, 1, 12018);
		addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 12019), st, st, st, st);
		
		addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 76), 
				(JSTUtils.oreValid("gemApatite") ? "gemApatite" : new ItemStack(Items.BLAZE_POWDER)),
				(JSTUtils.oreValid("dustIron") ? "dustIron" : "ingotIron"),
				"dustLithium"
				);
		
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 12030), "CRC", "LRL", "CRC", 'C', ItemList.circuits[3], 'L', new ItemStack(JSTBlocks.blockTile, 1, 5020), 'R', "blockUranium");
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, 12031), "PCP", "GcG", "PcP", 'P', pf + "Aluminum", 'C', ItemList.circuits[2], 'c', new ItemStack(JSTItems.item1, 1, 9000), 'G', pf + "Gold");
		
		st = new ItemStack(JSTItems.item1, 1, 12020);
		addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 12021), st, st, st, st);
		addShapedRecipe(new ItemStack(JSTItems.mask), "SCS", "SCS", 'S', Items.STRING, 'C', new ItemStack(Blocks.CARPET));
		
		if (JSTCfg.NoElecEngine) removeRecipeByOutput(JSTUtils.getModItemStack("forestry:engine_electrical"), true, true);
		
		addShapedRecipe(new ItemStack(Items.STRING, 48), 
				"PPP",
				'P', new ItemStack(JSTItems.item1, 1, 105)
				);
	}

	private static void addToolRecipes(int sID, Object mat, boolean sickle) {
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, sID), 
				"G", "G", "S",
				'G', mat,
				'S', "stickWood"
				);
		
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, sID + 1), 
				"G", "S", "S",
				'G', mat,
				'S', "stickWood"
				);
		
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, sID + 2), 
				"GGG", " S ", " S ",
				'G', mat,
				'S', "stickWood"
				);
		
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, sID + 3), 
				"GG", "GS", " S",
				'G', mat,
				'S', "stickWood"
				);
		
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, sID + 4), 
				"GG", " S", " S",
				'G', mat,
				'S', "stickWood"
				);
		
		if (sickle)
		addShapedRecipe(new ItemStack(JSTItems.item1, 1, sID + 5), 
				" G ", "  G", "SG ",
				'G', mat,
				'S', "stickWood"
				);
	}

	private static void addWireRecipe(String name, int tier, int i, int ui, int min, int max) {
		if (!JSTUtils.oreValid("ingot" + name)) return;
		
		Object ins = JSTUtils.oreValid("itemRubber") ? "itemRubber" : new ItemStack(Blocks.WOOL, 1, Short.MAX_VALUE);
		if (JSTCfg.ic2Loaded) {
			addShapelessRecipe(new ItemStack(JSTBlocks.blockTile, min, ui), "plate" + name, "craftingToolWireCutter");
		    if (JSTUtils.oreValid("plate" + name))
		    	CompatIC2.addFormerRecipe(new OreDictStack("plate" + name), new ItemStack(JSTBlocks.blockTile, min, ui), 0);
		    if (JSTUtils.oreValid("ingot" + name))
		    	CompatIC2.addFormerRecipe(new OreDictStack("ingot" + name), new ItemStack(JSTBlocks.blockTile, max, ui), 1);
		}
		MRecipes.addPressRecipe(new OreDictStack("ingot" + name), ItemList.molds[0], new ItemStack(JSTBlocks.blockTile, max, ui), null, 16, 64);
		if (!JSTCfg.ic2Loaded || JSTCfg.CheaperIC2) {
			if (min * 3 == 6 && tier < 3)
				addShapedRecipe(new ItemStack(JSTBlocks.blockTile, min * 3, i), 
						"WWW", "III", "WWW",
						'W', ins, 'I', "ingot" + name
						);
			
			addShapedRecipe(new ItemStack(JSTBlocks.blockTile, min * 3, ui), 
					"III",
					'I', "ingot" + name
					);
		}
		Object[] inp = new Object[MathHelper.clamp(tier, 2, 5)];
		Arrays.fill(inp, ins);
		inp[0] = new ItemStack(JSTBlocks.blockTile, 1, ui);
		addShapelessRecipe(new ItemStack(JSTBlocks.blockTile, 1, i), inp);
	}
	
	/** For re-battery, set energy to zero. */
	private static void addBasicBatteryRecipe(int[] meta, long energy, ItemStack cable, Object[] ing, ItemStack aRet) {
		if (meta.length > 4 || (ing.length != 1 && ing.length != 2)) return;
		boolean flag = energy <= 0;
		for (Object obj : ing) {
			if (obj instanceof String && !JSTUtils.oreValid((String)obj))
				return;
		}
		ItemStack bat = new ItemStack(JSTItems.item1, 1, meta[0]);
		if (!flag) {
			ItemStack bat2 = bat.copy();
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setLong("energy", energy);
			bat.setTagCompound(nbt);
			addShapelessRecipe(bat, bat2, ing[0], ing.length == 1 ? ing[0] : ing[1]);
		}
		String mat = "ingotZinc";
		Object mat2 = (ing.length == 1 ? ing[0] : ing[1]);
		addShapedRecipe(bat, 
				" c ", "mom", " d ",
				'c', cable,
				'm', mat,
				'o', ing[0],
				'd', mat2 
				);
		
		for (int n = 1; n < meta.length; n++) {
			int cnt = (JSTUtils.getVoltFromTier(n + 1) / 32) / 4 * 3;
			ItemStack st = new ItemStack(JSTItems.item1, 1, meta[n]);
			ItemStack st3 = ItemList.cables[n + 1].copy();
			st3.setCount((int) Math.pow(2, n));
			OreDictStack od = new OreDictStack(mat, cnt);
			if (flag) {
				ItemStack pb = new ItemStack(JSTItems.item1, 1, meta[n - 1]);
				addSLWRecycle(st, pb, pb, pb, pb);
			} else {
				ItemStack st4 = st.copy();
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setLong("energy", energy * (JSTUtils.getVoltFromTier(n + 1) / 32));
				st.setTagCompound(nbt);
				MRecipes.addAssemblerRecipe(new Object[] {setCount(ing[0], cnt), st4, setCount(mat2, cnt)}, null, st, null, 5, 50);
			}
			ItemStack r = null;
			if (aRet != null && !aRet.isEmpty()) {
				r = aRet.copy();
				r.setCount(cnt);
			}
			MRecipes.addAssemblerRecipe(new Object[] {null, st3, null, od, setCount(ing[0], cnt), od, null, setCount(mat2, cnt), null}, null, st, r, 5, 50);
		}
		
	}
	
	@Nullable
	public static Object setCount(Object in, int cnt) {
		if (cnt <= 0) return null;
		if (in instanceof ItemStack) {
			in = ((ItemStack)in).copy();
			((ItemStack)in).setCount(cnt);
		}
		if (in instanceof OreDictStack) {
			OreDictStack od = (OreDictStack)in;
			in = new OreDictStack(od.name, cnt);
		}
		if (in instanceof String) {
			String s = (String)in;
			in = new OreDictStack(s, cnt);
		}
		return in;
	}
	
	@Nullable
	public static ShapelessOreRecipe addShapelessRecipe(ItemStack output, Object... input) {
		if (output == null || output.isEmpty()) return null;
		ShapelessOreRecipe r = null;
		try {
			r = new JSTCraftingRecipe.ShapelessRecipe(null, output, input);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		String nm = JustServerTweak.MODID + ":j" + rID;
		ForgeRegistries.RECIPES.register(r.setRegistryName(nm));
		rID++;
		return r;
	}
	
	@Nullable
	public static ShapedOreRecipe addShapedRecipe(ItemStack output, Object... input) {
		return addShapedRecipe(true, output, input);
	}

	@Nullable
	public static ShapedOreRecipe addShapedRecipe(boolean mirror, ItemStack output, Object... input) {
		if (output == null || output.isEmpty()) return null;
		ShapedOreRecipe r = null;
		try {
			r = new JSTCraftingRecipe.ShapedRecipe(null, output, input);
			r.setMirrored(mirror);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		String nm = JustServerTweak.MODID + ":j" + rID;
		ForgeRegistries.RECIPES.register(r.setRegistryName(nm));
		rID++;
		return r;
	}
	
	public static void addSLWRecycle(@Nonnull ItemStack output, Object... input) {
		ShapelessOreRecipe r = addShapelessRecipe(output, input);
		if (r != null) {
			NonNullList<Ingredient> list = r.getIngredients();
			if (list.size() >= 9) return;
			ItemStack[] out = new ItemStack[9];
			for (int n = 0; n < list.size(); n++)
				out[n] = getSt(list.get(n).getMatchingStacks());
			MRecipes.addDisassemblerRecipe(r.getRecipeOutput(), out, 30, 100 * list.size());
		}
	}
	
	public static void addSHWRecycle(@Nonnull ItemStack output, Object... input) {
		ShapedOreRecipe r = addShapedRecipe(output, input);
		if (r != null) {
			NonNullList<Ingredient> list = r.getIngredients();
			ItemStack[] out = new ItemStack[9];
			int cnt = 0;
			for (int w = 0; w < r.getRecipeWidth(); w++) {
				for (int h = 0; h < r.getRecipeHeight(); h++) {
					int idx = w + h * r.getRecipeWidth();
					if (idx >= 9) continue;
					ItemStack st = getSt(list.get(idx).getMatchingStacks());
					if (st != null && !st.isEmpty()) {
						if (st.getItem().hasContainerItem(st))
							st = null;
						else
							cnt++;
					}
					out[w + h * 3] = st;
				}
			}
			MRecipes.addDisassemblerRecipe(r.getRecipeOutput(), out, 30, cnt * 100);
		}
	}
	
	private static ItemStack getSt(ItemStack[] in) {
		if (in == null || in.length <= 0) return null;
		for (ItemStack st : in) {
			ResourceLocation rl = Item.REGISTRY.getNameForObject(st.getItem());
			if (rl == null) continue;
			String d = rl.getResourceDomain();
			if (d.equals(JustServerTweak.NAME) || d.equals("ic2"))
				return st;
		}
		return in[0];
	}
	
	/** Will return false if it did not recipe for ItemStack.
	 * @param st ItemStack
	 * @param isl will not remove shapeless recipes if true.
	 * @param inbt will ignore input item's NBT if true.
	 * */
	public static void removeRecipeByOutput(@Nonnull ItemStack st, boolean isl, boolean inbt) {
		if (st.isEmpty()) return;
		
		ForgeRegistry<IRecipe> reg = RegistryManager.ACTIVE.getRegistry(GameData.RECIPES);
        for (IRecipe rec : reg) {
        	try {
        		if (isl && rec instanceof ShapelessRecipes)
        			continue;
        		ItemStack out = rec.getRecipeOutput();
        		if (out != null && OreDictionary.itemMatches(out, st, false) && (inbt || ItemStack.areItemStackTagsEqual(st, out)))
        			reg.remove(rec.getRegistryName());
        	} catch (Exception e) {
        		e.printStackTrace();
        		return;
        	}
        }
	}
}
