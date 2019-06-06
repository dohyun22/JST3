package dohyun22.jst3.items.behaviours;

import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class IB_MTS extends ItemBehaviour {
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public boolean interactEntity(ItemStack st, EntityPlayer p, EntityLivingBase e, EnumHand h) {
		if (!p.world.isRemote && !p.isSneaking() && !e.isDead && !(e instanceof EntityPlayer)) {
			NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
			boolean flag = false;
			if (nbt.hasKey("tgt")) {
				Entity tgt = e.world.getEntityByID(nbt.getInteger("tgt"));
				if (tgt instanceof EntityLivingBase && !tgt.isDead && tgt.world.provider.getDimension() == e.world.provider.getDimension()) {
					e.world.playSound(null, p.getPosition(), JSTSounds.SWITCH2, SoundCategory.PLAYERS, 1.0F, 1.0F);
					((EntityLivingBase)e).setRevengeTarget((EntityLivingBase)tgt);
					if (e instanceof EntityCreature)
						((EntityCreature)e).setAttackTarget((EntityLivingBase)tgt);
				} else {
					flag = true;
				}
			} else {
				flag = true;
			}
			if (flag && e instanceof EntityLivingBase) {
				nbt.setInteger("tgt", e.getEntityId());
				e.world.playSound(null, p.getPosition(), JSTSounds.SWITCH2, SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		}
        return true;
    }

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (!w.isRemote && st.hasTagCompound() && pl.isSneaking())
			st.getTagCompound().removeTag("tgt");
		return new ActionResult(EnumActionResult.PASS, st);
	}
}
