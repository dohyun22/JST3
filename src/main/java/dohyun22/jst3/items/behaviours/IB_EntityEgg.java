package dohyun22.jst3.items.behaviours;

import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IB_EntityEgg extends ItemBehaviour {
	private final String en, tt;

	public IB_EntityEgg(String id, @Nullable String tip) {
		en = id; tt = tip;
	}

	@Override
	public EnumActionResult onRightClickBlock(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumHand h, EnumFacing f, float hx, float hy, float hz) {
		if (!w.isRemote) {
			Entity e = EntityList.createEntityByIDFromName(new ResourceLocation(en), w);
			if (e != null) {
				if (st.hasTagCompound()) {
					NBTTagCompound nbt = st.getTagCompound().getCompoundTag("EntityNBT");
					if (!nbt.hasNoTags()) e.readFromNBT(nbt);
				}
				p = p.offset(f);
				e.setLocationAndAngles(p.getX() + 0.5F, p.getY(), p.getZ() + 0.5F, pl.rotationYaw, e.rotationPitch);
				w.spawnEntity(e);
				if (!pl.capabilities.isCreativeMode) st.shrink(1);
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		if (tt != null) ls.addAll(JSTUtils.getListFromTranslation(tt));
	}
}
