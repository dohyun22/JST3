package dohyun22.jst3.items.behaviours;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IB_Battery extends ItemBehaviour {
	protected final int tier;
	protected final float mult;
	/** 0 = Rechargeable, 1 = Single use, 2 Can only be charged*/
	protected final byte mode;
	
	public IB_Battery(long storage, int tier) {
		this(storage, tier, 1.0F, (byte) 0);
	}
	
	public IB_Battery(long storage, int tier, int mode) {
		this(storage, tier, 1.0F, mode);
	}
	
	/** It's useful for batteries with custom transfer limits. (i.e high transfer battery) */
	public IB_Battery(long storage, int tier, float mult, int mode) {
		this.maxEnergy = storage;
		this.tier = tier;
		this.mult = mult;
		if (mode > 2 || mode < 0) throw new IllegalArgumentException("Invalid Mode: " + mode);
		this.mode = (byte) mode;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@Nullable
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		long e = st.hasTagCompound() ? st.getTagCompound().getLong("energy") : 0L;
		if (this.mode == 1 && e <= 0) {
			return JSTUtils.getListFromTranslation("jst.tooltip.energy.depleted");
		} else {
			List<String> ret = new ArrayList();
			ret.add(I18n.format("jst.tooltip.energy.eu", e, this.maxEnergy));
			BigInteger bi = BigInteger.valueOf(JSTCfg.RFPerEU);
			ret.add(I18n.format("jst.tooltip.energy.rf", BigInteger.valueOf(e).multiply(bi), BigInteger.valueOf(this.maxEnergy).multiply(bi)));
			ret.add(I18n.format("jst.tooltip.energy.tier") + " " + this.tier + " (" + JSTUtils.getTierName(this.tier) + ")");
			long tl = this.transferLimit(st);
			ret.add(I18n.format("jst.tooltip.energy.transfer") + " " + tl  + " EU / " + (tl * JSTCfg.RFPerEU) + " RF");
			if (this.maxEnergy > (Integer.MAX_VALUE / JSTCfg.RFPerEU)) ret.add(I18n.format("jst.tooltip.energy.highcapacity"));
			return ret;
		}
	}
	
	@Override
	public int getTier(ItemStack st) {
		return this.tier;
	}
	
	@Override
	public boolean canCharge(ItemStack st) {
		return st.getCount() == 1 && (mode == 0 || mode == 2);
	}
	
	@Override
	public boolean canDischarge(ItemStack st) {
		return st.getCount() == 1 && (mode == 0 || mode == 1);
	}
	
	@Override
	public long transferLimit(ItemStack st) {
		return (long) (JSTUtils.getVoltFromTier(tier) * mult);
	}
	
	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		if (mode != 1) sub.add(st.copy());
		setEnergy(st, this.maxEnergy);
		sub.add(st);
	}
}
