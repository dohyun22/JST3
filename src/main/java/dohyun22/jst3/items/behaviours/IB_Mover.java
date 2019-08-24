package dohyun22.jst3.items.behaviours;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class IB_Mover extends ItemBehaviour {

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (!w.isRemote) {
		}
		return new ActionResult(EnumActionResult.PASS, st);
	}

	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return !st.hasTagCompound() || st.getTagCompound().getCompoundTag("TEData").hasNoTags();
	}

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
}
