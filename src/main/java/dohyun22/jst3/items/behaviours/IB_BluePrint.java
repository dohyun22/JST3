package dohyun22.jst3.items.behaviours;

import java.math.BigInteger;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class IB_BluePrint extends ItemBehaviour {	
	public IB_BluePrint() {}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		int size = getSizeOfComsumedLead(st);
		int tier = getSizeOfTier(st);
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.blueprint", tier, size));
	}	
	
	public static int getSizeOfComsumedLead(ItemStack st) {
		return st.hasTagCompound() ? st.getTagCompound().getInteger("consumeSize") : 0;
	}
	
	public static int getSizeOfTier(ItemStack st) {
		return st.hasTagCompound() ? st.getTagCompound().getInteger("tier") : 0;
	}
	
	public static void setSizeOfConsumedLeadAndTier(ItemStack st, int lead, int tier) {
		NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
		nbt.setInteger("consumeSize", lead);
		nbt.setInteger("tier", tier);
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
	
	@Override
	public boolean showDurability(ItemStack st) {
		return false;
	}
	
	@Override
	public double getDisplayDamage(ItemStack st) {
		return 1.0D;
	}
}