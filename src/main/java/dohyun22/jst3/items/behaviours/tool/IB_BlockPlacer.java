package dohyun22.jst3.items.behaviours.tool;

import java.util.List;

import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class IB_BlockPlacer extends ItemBehaviour {

	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz, EnumHand h) {
		if (w.isRemote) return EnumActionResult.PASS;
		if (JSTUtils.isOPorSP(pl)) {
			NBTTagCompound tag = st.getTagCompound();
			IBlockState bs;
			if (pl.isSneaking()) {
				bs = w.getBlockState(p);
				Block b = bs.getBlock();
				byte m = (byte)bs.getBlock().getMetaFromState(bs);
				if (tag == null) tag = JSTUtils.getOrCreateNBT(st);
				boolean flag = false;
				if (JSTUtils.getRegName(b).equals(tag.getString("_bl")) && tag.getByte("_me") == m) {
					if (tag.getBoolean("_cl")) {
						flag = true;
						b = Blocks.AIR;
						m = 0;
						tag.removeTag("_cl");
					}
				} else {
					flag = true;
					tag.removeTag("_cl");
				}
				if (!flag) tag.setBoolean("_cl", true);
				tag.setString("_bl", JSTUtils.getRegName(b));
				tag.setByte("_me", m);
			} else {
				Block b = null;
				int m = 0;
				if (tag != null) {
					b = JSTUtils.getModBlock(tag.getString("_bl"));
					m = tag.getByte("_me");
					tag.removeTag("_cl");
				}
				if (b == null) b = Blocks.AIR;
				w.setBlockState(b == Blocks.AIR ? p : p.offset(s), b.getStateFromMeta(m), 18);
			}
			w.playSound(null, p, JSTSounds.SWITCH2, SoundCategory.PLAYERS, 1.0F, 1.0F);
		} else {
			TextComponentTranslation tr = new TextComponentTranslation("commands.generic.permission");
			tr.getStyle().setColor(TextFormatting.RED);
			pl.sendMessage(tr);
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.add(I18n.format("jst.msg.com.op"));
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.placer"));
		NBTTagCompound t = st.getTagCompound();
		Block b = Blocks.AIR;
		if (t != null && t.hasKey("_bl")) b = JSTUtils.getModBlock(t.getString("_bl"));
		ls.add(I18n.format("jst.tooltip.mover.filled", b.getLocalizedName()));
	}

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
}
