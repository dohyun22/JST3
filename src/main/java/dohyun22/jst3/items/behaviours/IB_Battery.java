package dohyun22.jst3.items.behaviours;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

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
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		long e = st.hasTagCompound() ? st.getTagCompound().getLong("energy") : 0L;
		if (this.mode == 1 && e <= 0) {
			ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.energy.depleted"));
		} else {
			ls.add(I18n.format("jst.tooltip.energy.eu", JSTUtils.formatNum(e), JSTUtils.formatNum(maxEnergy)));
			BigInteger bi = BigInteger.valueOf(JSTCfg.RFPerEU);
			ls.add(I18n.format("jst.tooltip.energy.rf", JSTUtils.formatNum(BigInteger.valueOf(e).multiply(bi)), JSTUtils.formatNum(BigInteger.valueOf(maxEnergy).multiply(bi))));
			ls.add(I18n.format("jst.tooltip.energy.tier") + " " + tier + " (" + JSTUtils.getTierName(tier) + ")");
			long tl = this.transferLimit(st);
			ls.add(I18n.format("jst.tooltip.energy.transfer") + " " + JSTUtils.formatNum(tl)  + " EU / " + JSTUtils.formatNum(tl * JSTCfg.RFPerEU) + " RF");
			if (maxEnergy > (Integer.MAX_VALUE / JSTCfg.RFPerEU)) ls.add(I18n.format("jst.tooltip.energy.highcapacity"));
		}
	}
	
	@Override
	public int getTier(ItemStack st) {
		return tier;
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
		setEnergy(st, maxEnergy);
		sub.add(st);
	}
}
