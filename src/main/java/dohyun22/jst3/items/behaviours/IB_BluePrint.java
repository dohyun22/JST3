package dohyun22.jst3.items.behaviours;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class IB_BluePrint extends ItemBehaviour {
	protected int consumeLead;
	
	public IB_BluePrint() {
		//consumeLead = consume;
	}
	@Override
	@Nullable
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		int size = getSizeOfComsumedLead(st);
		List<String> ret = new ArrayList();
		ret.addAll(JSTUtils.getListFromTranslation("jst.tooltip.blueprint", size));
		return ret;
	}	
	
	public int getSizeOfComsumedLead(ItemStack st) {
		return st.hasTagCompound() ? st.getTagCompound().getInteger("consumeSize") : 0;
	}
	
	public void setSizeOfConsumedLead(ItemStack st, long e) {
		NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
		nbt.setInteger("consumeSize", (int) Math.max(0, e));
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
