package dohyun22.jst3.recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.api.recipe.AdvRecipeItem;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.compat.ic2.CompatIC2;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.item.ItemStack;

public class ItemList {
	private ItemList() {}
	
	public static final Object[] circuits = new Object[10];
	public static final Object[] baseMaterial = new Object[10];
	public static final ItemStack[] machineBlock = new ItemStack[10];
	public static final ItemStack[] cables = new ItemStack[10];
	public static final ItemStack[] uninsCables = new ItemStack[10];
	public static final ItemStack[] coils = new ItemStack[10];
	public static final ItemStack[] motors = new ItemStack[10];
	public static final ItemStack[] sensors = new ItemStack[10];
	public static final ItemStack[] raygens = new ItemStack[10];
	public static final AdvRecipeItem[] molds = new AdvRecipeItem[3];
	
	
	public static void init() {
		machineBlock[0] = new ItemStack(JSTBlocks.blockTile, 1, 1);
		machineBlock[1] = new ItemStack(JSTBlocks.blockTile, 1, 2);
		machineBlock[2] = new ItemStack(JSTBlocks.blockTile, 1, 3);
		machineBlock[3] = new ItemStack(JSTBlocks.blockTile, 1, 4);
		machineBlock[4] = new ItemStack(JSTBlocks.blockTile, 1, 5);
		machineBlock[5] = new ItemStack(JSTBlocks.blockTile, 1, 6);
		machineBlock[6] = new ItemStack(JSTBlocks.blockTile, 1, 7);
		machineBlock[7] = new ItemStack(JSTBlocks.blockTile, 1, 8);
		machineBlock[8] = new ItemStack(JSTBlocks.blockTile, 1, 9);
		machineBlock[9] = new ItemStack(JSTBlocks.blockTile, 1, 10);
		
		coils[0] = new ItemStack(JSTBlocks.blockTile, 1, 5066);
		coils[1] = coils[0];
		coils[2] = new ItemStack(JSTBlocks.blockTile, 1, 5067);
		coils[3] = new ItemStack(JSTBlocks.blockTile, 1, 5068);
		coils[4] = new ItemStack(JSTBlocks.blockTile, 1, 5069);
		coils[5] = new ItemStack(JSTBlocks.blockTile, 1, 5070);
		coils[6] = new ItemStack(JSTBlocks.blockTile, 1, 5071);
		coils[7] = new ItemStack(JSTBlocks.blockTile, 1, 5072);
		coils[8] = new ItemStack(JSTBlocks.blockTile, 1, 5073);
		coils[9] = new ItemStack(JSTBlocks.blockTile, 1, 5074);
		
		motors[0] = new ItemStack(JSTItems.item1, 1, 111);
		motors[1] = motors[0];
		for (int n = 2; n <= 9; n++) 
			motors[n] = new ItemStack(JSTItems.item1, 1, 110 + n);
		
		sensors[0] = new ItemStack(JSTItems.item1, 1, 121);
		sensors[1] = sensors[0];
		for (int n = 2; n <= 9; n++) 
			sensors[n] = new ItemStack(JSTItems.item1, 1, 120 + n);
		
		raygens[0] = new ItemStack(JSTItems.item1, 1, 131);
		raygens[1] = raygens[0];
		for (int n = 2; n <= 9; n++) 
			raygens[n] = new ItemStack(JSTItems.item1, 1, 130 + n);
		
		circuits[0] = "circuitPrimitive";
		circuits[1] = "circuitBasic";
		circuits[2] = "circuitGood";
		circuits[3] = "circuitAdvanced";
		circuits[4] = "circuitHighTech";
		circuits[5] = "circuitPowerElectronic";
		circuits[6] = "circuitPowerControl";
		circuits[7] = new ItemStack(JSTItems.item1, 1, 51);
		circuits[8] = new ItemStack(JSTItems.item1, 1, 52);
		circuits[9] = new ItemStack(JSTItems.item1, 1, 153);
		
		String pf = JSTCfg.ic2Loaded ? "plate" : "ingot";
		baseMaterial[0] = "ingotBrass";
		baseMaterial[1] = "ingotIron";
		baseMaterial[2] = pf + "Aluminum";
		baseMaterial[3] = "plateIndustrialAlloy";
		baseMaterial[4] = "plateIndustrialAlloyAdv";
		baseMaterial[5] = pf + "Tungsten";
		baseMaterial[6] = pf + "Chrome";
		baseMaterial[7] = pf + "Iridium";
		baseMaterial[8] = pf + "Rhenium";
		baseMaterial[9] = "ingotNeutronium";
		
		cables[0] = new ItemStack(JSTBlocks.blockTile, 1, 4013);
		cables[1] = JSTCfg.RIC2C ? CompatIC2.getIC2Cable(4, 1) : null;
		cables[2] = JSTCfg.RIC2C ? CompatIC2.getIC2Cable(0, 1) : null;
		cables[3] = JSTCfg.RIC2C ? CompatIC2.getIC2Cable(2, 2) : null;
		cables[4] = JSTCfg.RIC2C ? CompatIC2.getIC2Cable(3, 3) : null;
		cables[5] = JSTCfg.RIC2C ? CompatIC2.getIC2Cable(1, 0) : null;
		cables[6] = new ItemStack(JSTBlocks.blockTile, 1, 4021);
		cables[7] = new ItemStack(JSTBlocks.blockTile, 1, 4027);
		cables[8] = new ItemStack(JSTBlocks.blockTile, 1, 4029);
		cables[9] = new ItemStack(JSTBlocks.blockTile, 1, 4012);
		if (cables[1] == null)
			cables[1] = JSTUtils.oreValid("ingotTin") ? new ItemStack(JSTBlocks.blockTile, 1, 4001) : new ItemStack(JSTBlocks.blockTile, 1, 4023); 
		if (cables[2] == null)
			cables[2] = JSTUtils.oreValid("ingotCopper") ? new ItemStack(JSTBlocks.blockTile, 1, 4003) : new ItemStack(JSTBlocks.blockTile, 1, 4025);
		if (cables[3] == null)
			cables[3] = new ItemStack(JSTBlocks.blockTile, 1, 4005); 
		if (cables[4] == null)
			cables[4] = new ItemStack(JSTBlocks.blockTile, 1, 4007); 
		if (cables[5] == null)
			cables[5] = new ItemStack(JSTBlocks.blockTile, 1, 4009); 
		
		uninsCables[0] = new ItemStack(JSTBlocks.blockTile, 1, 4014);
		uninsCables[1] = JSTCfg.RIC2C ? CompatIC2.getIC2Cable(4, 0) : null;
		uninsCables[2] = JSTCfg.RIC2C ? CompatIC2.getIC2Cable(0, 0) : null;
		uninsCables[3] = JSTCfg.RIC2C ? CompatIC2.getIC2Cable(2, 0) : null;
		uninsCables[4] = JSTCfg.RIC2C ? CompatIC2.getIC2Cable(3, 0) : null;
		uninsCables[5] = JSTCfg.RIC2C ? CompatIC2.getIC2Cable(1, 0) : null;
		uninsCables[6] = new ItemStack(JSTBlocks.blockTile, 1, 4022);
		uninsCables[7] = new ItemStack(JSTBlocks.blockTile, 1, 4028);
		uninsCables[8] = new ItemStack(JSTBlocks.blockTile, 1, 4030);
		uninsCables[9] = new ItemStack(JSTBlocks.blockTile, 1, 4012);
		if (uninsCables[1] == null)
			uninsCables[1] = JSTUtils.oreValid("ingotTin") ? new ItemStack(JSTBlocks.blockTile, 1, 4002) : new ItemStack(JSTBlocks.blockTile, 1, 4024); 
		if (uninsCables[2] == null)
			uninsCables[2] = JSTUtils.oreValid("ingotCopper") ? new ItemStack(JSTBlocks.blockTile, 1, 4004) : new ItemStack(JSTBlocks.blockTile, 1, 4026);
		if (uninsCables[3] == null)
			uninsCables[3] = new ItemStack(JSTBlocks.blockTile, 1, 4006); 
		if (uninsCables[4] == null)
			uninsCables[4] = new ItemStack(JSTBlocks.blockTile, 1, 4008); 
		if (uninsCables[5] == null)
			uninsCables[5] = new ItemStack(JSTBlocks.blockTile, 1, 4010);

		molds[0] = new AdvRecipeItem(JSTItems.item1, 0, 160);
		molds[1] = new AdvRecipeItem(JSTItems.item1, 0, 161);
		molds[2] = new AdvRecipeItem(JSTItems.item1, 0, 162);
	}
	
	@Nullable
	public static Object copy(Object obj, int cnt, boolean ODS) {
		if (obj instanceof String) {
			return ODS ? new OreDictStack((String)obj, cnt) : new String((String)obj);
		} else if (obj instanceof ItemStack) {
			ItemStack ret = ((ItemStack)obj).copy();
			ret.setCount(cnt);
			return ret;
		} else if (obj instanceof OreDictStack) {
			return new OreDictStack(((OreDictStack)obj).name, cnt);
		}
		return null;
	}
	
	public static ItemStack getStackFromObj(Object in) {
		if (in instanceof ItemStack)
			return (ItemStack) in;
		if (in instanceof String)
			return JSTUtils.getFirstItem((String) in);
		if (in instanceof OreDictStack)
			return JSTUtils.getFirstItem(((OreDictStack)in).name, ((OreDictStack)in).count);
		return ItemStack.EMPTY;
	}
}
