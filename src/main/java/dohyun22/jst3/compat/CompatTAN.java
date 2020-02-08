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
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CompatTAN extends Loadable {

	@Override
	public void postInit() {
		ItemStack st, st2, st3;
		if (JSTCfg.ic2Loaded) {
			st = JSTUtils.getModItemStack("toughasnails:purified_water_bottle");
			if (!st.isEmpty()) {
				st2 = new ItemStack(Items.POTIONITEM);
				JSTUtils.getOrCreateNBT(st2).setString("Potion", "minecraft:water");
				CompatIC2.addExtRec(st2, st);
			}
			CompatIC2.addExtRec(new ItemStack(JSTItems.item1, 1, 10024), new ItemStack(JSTItems.item1, 1, 10025));
		}
		for (int n = 1; n <= 4; n++)
			RecipeLoader.addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 330 + n), "ImI", "CMC", "IcI", 'I', ItemList.baseMaterial[n], 'm', ItemList.motors[n], 'C', ItemList.circuits[n], 'M', ItemList.machineBlock[n], 'c', ItemList.coils[1]);
		st = JSTUtils.getValidOne("toughasnails:charcoal_filter", "dustCarbon");
		RecipeLoader.addSHWRecycle(new ItemStack(JSTBlocks.blockTile, 1, 6050), "FmF", "FMF", "FCF", 'F', st, 'm', ItemList.motors[1], 'M', ItemList.machineBlock[1], 'C', ItemList.circuits[1]);
		RecipeLoader.addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 10021), new ItemStack(JSTBlocks.blockTile, 1, 6050), new ItemStack(JSTItems.item1, 1, 12007));
		RecipeLoader.addShapelessRecipe(new ItemStack(JSTItems.item1, 1, 10022), new ItemStack(JSTBlocks.blockTile, 1, 331), new ItemStack(JSTItems.item1, 1, 12010));
		RecipeLoader.addShapedRecipe(new ItemStack(JSTItems.item1, 20, 10023), " P ", "P P", 'P', new ItemStack(JSTItems.item1, 1, 105));
		st2 = new ItemStack(JSTItems.item1, 1, 10024); st3 = new ItemStack(JSTItems.item1, 1, 10025);
		RecipeLoader.addShapelessRecipe(st3, st2, st);
		GameRegistry.addSmelting(st2, st3, 0.0F);
	}
}
