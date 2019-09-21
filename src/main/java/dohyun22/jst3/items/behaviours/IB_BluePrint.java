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
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.blueprint", getCircuitTier(st), getSolder(st)));
	}	
	
	public static int getSolder(ItemStack st) {
		return st.hasTagCompound() ? st.getTagCompound().getInteger("consumeSize") : 0;
	}
	
	public static int getCircuitTier(ItemStack st) {
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
}