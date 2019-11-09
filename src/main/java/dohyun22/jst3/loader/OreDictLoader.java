package dohyun22.jst3.loader;

import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictLoader extends Loadable {

	@Override
	public String getRequiredMod() {
		return null;
	}
	
	@Override
	public void preInit() {
		OreDictionary.registerOre("gemCoal", new ItemStack(Items.COAL, 1, 0));
		OreDictionary.registerOre("gemCharcoal", new ItemStack(Items.COAL, 1, 1));

		OreDictionary.registerOre("blockNikolite", new ItemStack(JSTBlocks.block1, 1, 11));
		OreDictionary.registerOre("blockNeutronium", new ItemStack(JSTBlocks.block1, 1, 12));
		OreDictionary.registerOre("logWood", new ItemStack(JSTBlocks.block1, 1, 13));
		OreDictionary.registerOre("plankWood", new ItemStack(JSTBlocks.block1, 1, 14));
		
		OreDictionary.registerOre("stoneBasalt", new ItemStack(JSTBlocks.block2, 1, 2));
		OreDictionary.registerOre("stoneMarble", new ItemStack(JSTBlocks.block2, 1, 6));
		
		OreDictionary.registerOre("oreCoal", new ItemStack(JSTBlocks.blockNO, 1, 0));
		OreDictionary.registerOre("oreDiamond", new ItemStack(JSTBlocks.blockNO, 1, 1));
		OreDictionary.registerOre("oreGold", new ItemStack(JSTBlocks.blockNO, 1, 2));
		OreDictionary.registerOre("oreIron", new ItemStack(JSTBlocks.blockNO, 1, 3));
		OreDictionary.registerOre("oreLapis", new ItemStack(JSTBlocks.blockNO, 1, 4));
		OreDictionary.registerOre("oreRedstone", new ItemStack(JSTBlocks.blockNO, 1, 5));
		OreDictionary.registerOre("oreCopper", new ItemStack(JSTBlocks.blockNO, 1, 6));
		OreDictionary.registerOre("oreTin", new ItemStack(JSTBlocks.blockNO, 1, 7));
		OreDictionary.registerOre("oreEmerald", new ItemStack(JSTBlocks.blockNO, 1, 8));
		OreDictionary.registerOre("oreSilver", new ItemStack(JSTBlocks.blockNO, 1, 9));
		OreDictionary.registerOre("oreLead", new ItemStack(JSTBlocks.blockNO, 1, 10));
		OreDictionary.registerOre("oreUranium", new ItemStack(JSTBlocks.blockNO, 1, 11));
		OreDictionary.registerOre("oreNikolite", new ItemStack(JSTBlocks.blockNO, 1, 12));
		OreDictionary.registerOre("oreRuby", new ItemStack(JSTBlocks.blockNO, 1, 13));
		OreDictionary.registerOre("oreSulfur", new ItemStack(JSTBlocks.blockNO, 1, 14));
		OreDictionary.registerOre("oreNickel", new ItemStack(JSTBlocks.blockNO, 1, 15));
		
		OreDictionary.registerOre("oreNaquadah", new ItemStack(JSTBlocks.blockOre, 1, 0));
		OreDictionary.registerOre("oreRhenium", new ItemStack(JSTBlocks.blockOre, 1, 1));
		OreDictionary.registerOre("oreUnobtainium", new ItemStack(JSTBlocks.blockOre, 1, 2));
		OreDictionary.registerOre("oreNikolite", new ItemStack(JSTBlocks.blockOre, 1, 4));
		OreDictionary.registerOre("oreOilsand", new ItemStack(JSTBlocks.blockOre, 1, 5));
		OreDictionary.registerOre("oreIridium", new ItemStack(JSTBlocks.blockOre, 1, 6));
		OreDictionary.registerOre("oreZinc", new ItemStack(JSTBlocks.blockOre, 1, 7));
		OreDictionary.registerOre("oreOilsand", new ItemStack(JSTBlocks.blockOre, 1, 8));
		OreDictionary.registerOre("oreGasHydrate", new ItemStack(JSTBlocks.blockOre, 1, 9));
		OreDictionary.registerOre("oreBauxite", new ItemStack(JSTBlocks.blockOre, 1, 10));
		OreDictionary.registerOre("oreTungsten", new ItemStack(JSTBlocks.blockOre, 1, 11));
		OreDictionary.registerOre("oreNiobium", new ItemStack(JSTBlocks.blockOre, 1, 12));
		OreDictionary.registerOre("oreRuby", new ItemStack(JSTBlocks.blockOre, 1, 13));
		OreDictionary.registerOre("orePeridot", new ItemStack(JSTBlocks.blockOre, 1, 14));
		OreDictionary.registerOre("oreSapphire", new ItemStack(JSTBlocks.blockOre, 1, 15));
		
		OreDictionary.registerOre("oreTungsten", new ItemStack(JSTBlocks.blockEO, 1, 0));
		OreDictionary.registerOre("oreDiamond", new ItemStack(JSTBlocks.blockEO, 1, 1));
		OreDictionary.registerOre("oreGold", new ItemStack(JSTBlocks.blockEO, 1, 2));
		OreDictionary.registerOre("oreIron", new ItemStack(JSTBlocks.blockEO, 1, 3));
		OreDictionary.registerOre("oreLapis", new ItemStack(JSTBlocks.blockEO, 1, 4));
		OreDictionary.registerOre("oreRedstone", new ItemStack(JSTBlocks.blockEO, 1, 5));
		OreDictionary.registerOre("oreCopper", new ItemStack(JSTBlocks.blockEO, 1, 6));
		OreDictionary.registerOre("oreTin", new ItemStack(JSTBlocks.blockEO, 1, 7));
		OreDictionary.registerOre("oreEmerald", new ItemStack(JSTBlocks.blockEO, 1, 8));
		OreDictionary.registerOre("oreSilver", new ItemStack(JSTBlocks.blockEO, 1, 9));
		OreDictionary.registerOre("oreLead", new ItemStack(JSTBlocks.blockEO, 1, 10));
		OreDictionary.registerOre("oreUranium", new ItemStack(JSTBlocks.blockEO, 1, 11));
		OreDictionary.registerOre("oreNikolite", new ItemStack(JSTBlocks.blockEO, 1, 12));
		OreDictionary.registerOre("orePeridot", new ItemStack(JSTBlocks.blockEO, 1, 13));
		OreDictionary.registerOre("orePlatinum", new ItemStack(JSTBlocks.blockEO, 1, 14));
		OreDictionary.registerOre("oreIridium", new ItemStack(JSTBlocks.blockEO, 1, 15));
		
		regOre("ingotTitanium", 1);
		regOre("dustTitanium", 2);
		regOre("plateTitanium", 3);
		regOre("circuitPowerElectronic", 4);
		regOre("circuitPowerControl", 5);
		regOre("plateIridium", 14);
		regOre("gemRuby", 16);
		regOre("gemPeridot", 17);
		regOre("gemSapphire", 18);
		regOre("gemBedrock", 20);
		regOre("dustBedrock", 21);
		regOre("ingotNeutronium", 24);
		regOre("ingotRedAlloy", 25);
		regOre("ingotBlueAlloy", 26);
		regOre("dustNikolite", 27);
		regOre("circuitHighTech", 28);
		regOre("ingotIridium", 29);
		regOre("dustIridium", 30);
		regOre("ingotZinc", 31);
		regOre("dustZinc", 32);
		regOre("ingotSilicon", 33);
		regOre("itemSilicon", 33);
		regOre("dustRedAlloy", 34);
		regOre("dustBlueAlloy", 35);
		regOre("ingotBrass", 36);
		regOre("dustBrass", 37);
		regOre("ingotNaquadah", 38);
		regOre("dustNaquadah", 39);
		regOre("dustUnobtainium", 41);
		regOre("plateZinc", 43);
		regOre("plateRedAlloy", 44);
		regOre("plateBlueAlloy", 45);
		regOre("plateSilicon", 53);
		regOre("plateBrass", 54);
		regOre("plateNaquadah", 55);
		regOre("dustRuby", 56);
		regOre("dustPeridot", 57);
		regOre("dustSapphire", 58);
		regOre("dustSilicon", 59);
		regOre("dustChrome", 65);
		regOre("dustLithium", 67);
		regOre("plateIndustrialAlloy", 68);
		regOre("plateIndustrialAlloyAdv", 69);
		regOre("ingotAluminum", 70);
		regOre("dustAluminum", 71);
		regOre("plateAluminum", 72);
		regOre("dustTungsten", 74);
		regOre("ingotEinsteinium", 78);
		regOre("dustEinsteinium", 79);
		regOre("plateEinsteinium", 80);
		regOre("circuitBasic", 86);
		regOre("circuitGood", 87);
		regOre("circuitAdvanced", 88);
		regOre("dustRhenium", 90);
		regOre("ingotNiobium", 95);
		regOre("dustNiobium", 96);
		regOre("plateNiobium", 97);
		regOre("gemThorium", 100);
		regOre("dustBauxite", 104);
		regOre("dustSalt", 107);
		regOre("dustSodium", 108);
		regOre("dustCarbon", 109);
		regOre("circuitPrimitive", 152);
		regOre("electronTube", 152);
		regOre("ingotElectricalSteel", 157);
		regOre("dustElectricalSteel", 158);
		regOre("plateElectricalSteel", 159);
		regOre("ingotSolder", 183);
		regOre("dustSolder", 184);
		regOre("wireSolder", 185);

		if (JSTCfg.customMat) {
			String[] str = {"ingotCopper","dustCopper", "plateCopper", "ingotTin", "dustTin", "plateTin", "ingotLead", "dustLead", "plateLead"};
			for (int n = 0; n < str.length; n++) regOre(str[n], 170 + n);
		}

		String s = "circuitBoardAny";
		regOre(s, 190);
		regOre(s, 191);
		regOre(s, 192);
		regOre(s, 193);

		OreDictionary.registerOre("craftingSolarPanel", new ItemStack(JSTBlocks.blockTile, 1, 40));
		OreDictionary.registerOre("craftingSCWire", new ItemStack(JSTBlocks.blockTile, 1, 4022));
	}
	
	/** To prevent Tech-Breaking recipes being added by other MODs, it registers some material to OreDict later.*/
	@Override
	public void postInit() {
		OreDictionary.registerOre("ingotUnobtainium", new ItemStack(JSTItems.item1, 1, 40));
		OreDictionary.registerOre("plateUnobtainium", new ItemStack(JSTItems.item1, 1, 42));
		OreDictionary.registerOre("ingotTungsten", new ItemStack(JSTItems.item1, 1, 73));
		OreDictionary.registerOre("plateTungsten", new ItemStack(JSTItems.item1, 1, 75));
		OreDictionary.registerOre("ingotRhenium", new ItemStack(JSTItems.item1, 1, 89));
		OreDictionary.registerOre("plateRhenium", new ItemStack(JSTItems.item1, 1, 91));
		OreDictionary.registerOre("ingotChrome", new ItemStack(JSTItems.item1, 1, 64));
		OreDictionary.registerOre("plateChrome", new ItemStack(JSTItems.item1, 1, 66));
	}

	private static void regOre(String ore, int meta) {
		OreDictionary.registerOre(ore, new ItemStack(JSTItems.item1, 1, meta));
	}
}
