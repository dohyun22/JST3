package dohyun22.jst3.compat;

import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.compat.ic2.CompatIC2;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.loader.RecipeLoader;
import dohyun22.jst3.recipes.ItemList;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CompatTAN extends Loadable {

	@Override
	public String getRequiredMod() {
		return "toughasnails";
	}

	@Override
	public void postInit() {
		ItemStack st;
		if (JSTCfg.ic2Loaded) {
			st = JSTUtils.getModItemStack("toughasnails:purified_water_bottle");
			if (!st.isEmpty()) {
				ItemStack st2 = new ItemStack(Items.POTIONITEM);
				JSTUtils.getOrCreateNBT(st2).setString("Potion", "minecraft:water");
				CompatIC2.addExtRec(st2, st);
			}
		}
		for (int n = 1; n <= 4; n++)
			RecipeLoader.addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 330 + n), "ImI", "CMC", "IcI", 'I', ItemList.baseMaterial[n], 'm', ItemList.motors[n], 'C', ItemList.circuits[n], 'M', ItemList.machineBlock[n], 'c', ItemList.coils[1]);
		st = JSTUtils.getModItemStack("toughasnails:charcoal_filter");
		RecipeLoader.addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6050), "FmF", "FMF", "FCF", 'F', st.isEmpty() ? "dustCarbon" : st, 'm', ItemList.motors[1], 'M', ItemList.machineBlock[1], 'C', ItemList.circuits[1]);
		st = new ItemStack(JSTItems.item1, 1, 12007);
		RecipeLoader.addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 10021), new ItemStack(JSTBlocks.blockTile, 1, 6050), st);
		RecipeLoader.addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 10022), new ItemStack(JSTBlocks.blockTile, 1, 331), st);
	}
}
