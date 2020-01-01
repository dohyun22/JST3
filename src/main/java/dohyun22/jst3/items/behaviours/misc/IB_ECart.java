package dohyun22.jst3.items.behaviours.misc;

import dohyun22.jst3.entity.EntityPoweredCart;
import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.loader.JSTCfg;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IB_ECart extends ItemBehaviour {

	@Override
	public EnumActionResult onRightClickBlock(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumHand h, EnumFacing f, float hx, float hy, float hz) {
		if (BlockRailBase.isRailBlock(w.getBlockState(p))) {
			if (!w.isRemote) {
				EntityMinecart c = new EntityPoweredCart(w, p.getX() + 0.5F, p.getY() + 0.5F, p.getZ() + 0.5F);
				if (st.hasDisplayName())
					c.setCustomNameTag(st.getDisplayName());
				w.spawnEntity(c);
			}
			st.shrink(1);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return JSTCfg.rcLoaded ? 3 : 1;
	}
}
