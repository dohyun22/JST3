package dohyun22.jst3.compat.ic2;

import java.util.List;

import dohyun22.jst3.utils.JSTUtils;
import ic2.api.reactor.IReactor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ReactorItemBehaviour {
	public static final ReactorItemBehaviour DEFAULT = new ReactorItemBehaviour();
	protected int maxDamage;

	public boolean canBePlaced(ItemStack st, IReactor re) {
		return false;
	}

	public void processChamber(ItemStack st, IReactor re, int x, int y, boolean hr) {}

	public boolean acceptPulse(ItemStack st, IReactor re, ItemStack ps, int yX, int yY, int pX, int pY, boolean hr) {
		return false;
	}

	public boolean canStoreHeat(ItemStack st, IReactor re, int x, int y) {
		return false;
	}

	public int getMaxHeat(ItemStack st, IReactor re, int x, int y) {
		return 0;
	}

	public int getCurrentHeat(ItemStack st, IReactor re, int x, int y) {
		return 0;
	}

	public int alterHeat(ItemStack st, IReactor re, int x, int y, int h) {
		return h;
	}

	public float influenceExplosion(ItemStack st, IReactor re) {
		return 0;
	}
	
	public int getRadiation(ItemStack st) {
		return 0;
	}

	public int getMaxDamage(ItemStack st) {
		return this.maxDamage;
	}
	
	public int getDamage(ItemStack st) {
		return st.hasTagCompound() ? st.getTagCompound().getInteger("damage") : 0;
	}

	public void setDamage(ItemStack st, int amt) {
		if (amt < 0) return;
		if (amt == 0) {
			NBTTagCompound tag = st.getTagCompound();
			if (tag == null)
				return;
			tag.removeTag("damage");
			if (tag.hasNoTags())
				st.setTagCompound(null);
			return;
		}
		NBTTagCompound tag2 = JSTUtils.getOrCreateNBT(st);
    	tag2.setInteger("damage", amt);
	}
	
	public double getDisplayDamage(ItemStack st) {
		int dmg = getMaxDamage(st);
		if (dmg > 0)
			return (double)getDamage(st) / (double)dmg;
		return 1.0D;
	}
	
	public boolean showDurability(ItemStack st) {
		return getMaxDamage(st) > 0 && getDamage(st) > 0;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack st, World w, List ls, ITooltipFlag adv) {
	}
}
