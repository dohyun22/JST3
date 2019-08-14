package dohyun22.jst3.items.behaviours;

import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class IB_Ruler extends ItemBehaviour {

	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer ep, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz, EnumHand h) {
		if (w.isRemote)
			return EnumActionResult.PASS;

		NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
		byte mode = nbt.getByte("mode");
		if (ep.isSneaking()) {
			byte m = (byte) ((mode + 1) % 2);
			nbt.setByte("mode", m);
			JSTUtils.sendMessage(ep, "jst.tooltip.ruler.mode" + m);
			return EnumActionResult.SUCCESS;
		}

		NBTTagCompound tag = nbt.getCompoundTag("coord");
		w.playSound((EntityPlayer) null, ep.posX, ep.posY, ep.posZ, JSTSounds.SWITCH2, SoundCategory.PLAYERS, 1.0F, 1.0F);
		if (!nbt.hasKey("coord")) {
			tag.setInteger("x", p.getX());
			tag.setInteger("y", p.getY());
			tag.setInteger("z", p.getZ());
			nbt.setTag("coord", tag);
			ep.sendMessage(new TextComponentTranslation("jst.tooltip.ruler.start"));
		} else {
			int x = tag.getInteger("x");
			int y = tag.getInteger("y");
			int z = tag.getInteger("z");
			
			if (mode == 0)
				nbt.removeTag("coord");

			int distX = Math.abs(p.getX() - x);
			int distY = Math.abs(p.getY() - y);
			int distZ = Math.abs(p.getZ() - z);

			double dist = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2) + Math.pow(distZ, 2));

			ep.sendMessage(new TextComponentTranslation("jst.tooltip.ruler.distance", JSTUtils.getDist(dist, false), JSTUtils.getDist(dist, true)));
			
			if (dist > 0.0D) JSTUtils.clearAdvancement(ep, JustServerTweak.MODID, "main/ruler");
		}

		return EnumActionResult.SUCCESS;
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		NBTTagCompound nbt = st.getTagCompound();
		if (nbt == null || !nbt.hasKey("coord")) {
			ls.add(I18n.format("jst.tooltip.ruler.tip"));
		} else {
			NBTTagCompound tag = nbt.getCompoundTag("coord");
			int x = tag.getInteger("x");
			int y = tag.getInteger("y");
			int z = tag.getInteger("z");
			String c = x + ", " + y + ", " + z;
			ls.add(I18n.format("jst.tooltip.ruler.coord", c));
		}
		ls.add(I18n.format("jst.tooltip.ruler.mode" + (nbt == null ? 0 : nbt.getInteger("mode"))));
	}
	
	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
}
