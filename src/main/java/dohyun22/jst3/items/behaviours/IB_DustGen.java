package dohyun22.jst3.items.behaviours;

import dohyun22.jst3.evhandler.EvHandler;
import dohyun22.jst3.utils.JSTChunkData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class IB_DustGen extends ItemBehaviour {

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (!w.isRemote) {
			if (!pl.isSneaking())
				JSTChunkData.addFineDust(w, new ChunkPos(pl.getPosition()), 10000, true);
			else
				JSTChunkData.setFineDust(w, new ChunkPos(pl.getPosition()), 0, true);
		}
		return new ActionResult(EnumActionResult.PASS, st);
	}
	
	@Override
    public int getItemStackLimit(ItemStack st) {
    	return 1;
    }

	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz, EnumHand h) {
		if (w.isRemote) return EnumActionResult.PASS;
		EvHandler.makeMachineGoHaywire(w, p);
		return EnumActionResult.SUCCESS;
	}
}
