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
		
		OreDictionary.registerOre("ingotTitanium", new ItemStack(JSTItems.item1, 1, 1));
		OreDictionary.registerOre("dustTitanium", new ItemStack(JSTItems.item1, 1, 2));
		OreDictionary.registerOre("plateTitanium", new ItemStack(JSTItems.item1, 1, 3));
		OreDictionary.registerOre("circuitPowerElectronic", new ItemStack(JSTItems.item1, 1, 4));
		OreDictionary.registerOre("circuitPowerControl", new ItemStack(JSTItems.item1, 1, 5));
		OreDictionary.registerOre("plateIridium", new ItemStack(JSTItems.item1, 1, 14));
		OreDictionary.registerOre("gemRuby", new ItemStack(JSTItems.item1, 1, 16));
		OreDictionary.registerOre("gemPeridot", new ItemStack(JSTItems.item1, 1, 17));
		OreDictionary.registerOre("gemSapphire", new ItemStack(JSTItems.item1, 1, 18));
		OreDictionary.registerOre("ingotNeutronium", new ItemStack(JSTItems.item1, 1, 24));
		OreDictionary.registerOre("ingotRedAlloy", new ItemStack(JSTItems.item1, 1, 25));
		OreDictionary.registerOre("ingotBlueAlloy", new ItemStack(JSTItems.item1, 1, 26));
		OreDictionary.registerOre("dustNikolite", new ItemStack(JSTItems.item1, 1, 27));
		OreDictionary.registerOre("circuitHighTech", new ItemStack(JSTItems.item1, 1, 28));
		OreDictionary.registerOre("ingotIridium", new ItemStack(JSTItems.item1, 1, 29));
		OreDictionary.registerOre("dustIridium", new ItemStack(JSTItems.item1, 1, 30));
		OreDictionary.registerOre("ingotZinc", new ItemStack(JSTItems.item1, 1, 31));
		OreDictionary.registerOre("dustZinc", new ItemStack(JSTItems.item1, 1, 32));
		ItemStack st = new ItemStack(JSTItems.item1, 1, 33);
		OreDictionary.registerOre("ingotSilicon", st);
		OreDictionary.registerOre("itemSilicon", st);
		OreDictionary.registerOre("dustRedAlloy", new ItemStack(JSTItems.item1, 1, 34));
		OreDictionary.registerOre("dustBlueAlloy", new ItemStack(JSTItems.item1, 1, 35));
		OreDictionary.registerOre("ingotBrass", new ItemStack(JSTItems.item1, 1, 36));
		OreDictionary.registerOre("dustBrass", new ItemStack(JSTItems.item1, 1, 37));
		OreDictionary.registerOre("ingotNaquadah", new ItemStack(JSTItems.item1, 1, 38));
		OreDictionary.registerOre("dustNaquadah", new ItemStack(JSTItems.item1, 1, 39));
		OreDictionary.registerOre("dustUnobtainium", new ItemStack(JSTItems.item1, 1, 41));
		OreDictionary.registerOre("plateZinc", new ItemStack(JSTItems.item1, 1, 43));
		OreDictionary.registerOre("plateRedAlloy", new ItemStack(JSTItems.item1, 1, 44));
		OreDictionary.registerOre("plateBlueAlloy", new ItemStack(JSTItems.item1, 1, 45));
		OreDictionary.registerOre("plateSilicon", new ItemStack(JSTItems.item1, 1, 53));
		OreDictionary.registerOre("plateBrass", new ItemStack(JSTItems.item1, 1, 54));
		OreDictionary.registerOre("plateNaquadah", new ItemStack(JSTItems.item1, 1, 55));
		OreDictionary.registerOre("dustRuby", new ItemStack(JSTItems.item1, 1, 56));
		st = new ItemStack(JSTItems.item1, 1, 57);
		OreDictionary.registerOre("dustPeridot", st);
		OreDictionary.registerOre("dustSapphire", new ItemStack(JSTItems.item1, 1, 58));
		OreDictionary.registerOre("dustSilicon", new ItemStack(JSTItems.item1, 1, 59));
		OreDictionary.registerOre("dustChrome", new ItemStack(JSTItems.item1, 1, 65));
		OreDictionary.registerOre("dustLithium", new ItemStack(JSTItems.item1, 1, 67));
		OreDictionary.registerOre("plateIndustrialAlloy", new ItemStack(JSTItems.item1, 1, 68));
		OreDictionary.registerOre("plateIndustrialAlloyAdv", new ItemStack(JSTItems.item1, 1, 69));
		OreDictionary.registerOre("ingotAluminum", new ItemStack(JSTItems.item1, 1, 70));
		OreDictionary.registerOre("dustAluminum", new ItemStack(JSTItems.item1, 1, 71));
		OreDictionary.registerOre("plateAluminum", new ItemStack(JSTItems.item1, 1, 72));
		OreDictionary.registerOre("dustTungsten", new ItemStack(JSTItems.item1, 1, 74));
		OreDictionary.registerOre("ingotEinsteinium", new ItemStack(JSTItems.item1, 1, 78));
		OreDictionary.registerOre("dustEinsteinium", new ItemStack(JSTItems.item1, 1, 79));
		OreDictionary.registerOre("plateEinsteinium", new ItemStack(JSTItems.item1, 1, 80));
		OreDictionary.registerOre("circuitBasic", new ItemStack(JSTItems.item1, 1, 86));
		OreDictionary.registerOre("circuitGood", new ItemStack(JSTItems.item1, 1, 87));
		OreDictionary.registerOre("circuitAdvanced", new ItemStack(JSTItems.item1, 1, 88));
		OreDictionary.registerOre("dustRhenium", new ItemStack(JSTItems.item1, 1, 90));
		OreDictionary.registerOre("ingotNiobium", new ItemStack(JSTItems.item1, 1, 95));
		OreDictionary.registerOre("dustNiobium", new ItemStack(JSTItems.item1, 1, 96));
		OreDictionary.registerOre("plateNiobium", new ItemStack(JSTItems.item1, 1, 97));
		OreDictionary.registerOre("dustBauxite", new ItemStack(JSTItems.item1, 1, 104));
		OreDictionary.registerOre("dustSalt", new ItemStack(JSTItems.item1, 1, 107));
		OreDictionary.registerOre("dustSodium", new ItemStack(JSTItems.item1, 1, 108));
		OreDictionary.registerOre("dustCarbon", new ItemStack(JSTItems.item1, 1, 109));
		OreDictionary.registerOre("ingotElectricalSteel", new ItemStack(JSTItems.item1, 1, 157));
		OreDictionary.registerOre("dustElectricalSteel", new ItemStack(JSTItems.item1, 1, 158));
		OreDictionary.registerOre("plateElectricalSteel", new ItemStack(JSTItems.item1, 1, 159));
		if (JSTCfg.customMat) {
			String[] str = {"ingotCopper","dustCopper", "plateCopper", "ingotTin", "dustTin", "plateTin", "ingotLead", "dustLead", "plateLead"};
			for (int n = 0; n < str.length; n++) OreDictionary.registerOre(str[n], new ItemStack(JSTItems.item1, 1, 170 + n));
		}
		
		st = new ItemStack(JSTItems.item1, 1, 152);
		OreDictionary.registerOre("circuitPrimitive", st);
		OreDictionary.registerOre("electronTube", st);
		
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
}
