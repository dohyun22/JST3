package dohyun22.jst3.blocks;

import dohyun22.jst3.JustServerTweak;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemBlockBase extends ItemBlock {
	
	public ItemBlockBase(Block b) {
		super(b);
		this.setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
	}

	@Override
	public int getMetadata(int dmg) {
		return dmg;
	}
}