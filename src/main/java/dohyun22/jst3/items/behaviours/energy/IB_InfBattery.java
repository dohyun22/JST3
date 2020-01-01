package dohyun22.jst3.items.behaviours.energy;

import java.math.BigInteger;
import java.util.List;

import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class IB_InfBattery extends ItemBehaviour {

	@Override
	public long charge(ItemStack st, long amt, int tier, boolean itl, boolean sim) {
		if (getMaxEnergy(st) <= 0 || getTier(st) > tier || st.getCount() != 1 || !canCharge(st)) return 0;
		if (!sim) {
			NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
			JSTUtils.bigIntToNBT(nbt, JSTUtils.nbtToBigInt(nbt, "BIG_EU").add(BigInteger.valueOf(amt)), "BIG_EU");
		}
		return amt;
	}

	@Override
	public long discharge(ItemStack st, long amt, int tier, boolean itl, boolean sim) {
		if (getMaxEnergy(st) <= 0 || getTier(st) > tier || !canDischarge(st) || !st.hasTagCompound()) return 0;
		NBTTagCompound nbt = st.getTagCompound();
		boolean flag = nbt.hasKey("energy");
		BigInteger bi = flag ? BigInteger.valueOf(nbt.getLong("energy")) : JSTUtils.nbtToBigInt(nbt, "BIG_EU");
		BigInteger use = bi.min(BigInteger.valueOf(amt));
		if (!sim) {
			if (flag) nbt.removeTag("energy");
			bi = bi.subtract(use);
			if (bi.compareTo(BigInteger.ZERO) <= 0) {
				nbt.removeTag("BIG_EU");
				if (nbt.hasNoTags())
					st.setTagCompound(null);
			} else
				JSTUtils.bigIntToNBT(nbt, bi, "BIG_EU");
		}
		return use.longValue();
	}

	@Override
	public long getEnergy(ItemStack st) {
		return discharge(st, Long.MAX_VALUE, Integer.MAX_VALUE, true, true);
	}
	
	@Override
	public long getMaxEnergy(ItemStack st) {
		return Long.MAX_VALUE;
	}

	@Override
	public int getTier(ItemStack st) {
		return 9;
	}

	@Override
	public boolean canCharge(ItemStack st) {
		return st.getCount() == 1;
	}

	@Override
	public boolean canDischarge(ItemStack st) {
		return st.getCount() == 1;
	}

	@Override
	public long transferLimit(ItemStack st) {
		return Long.MAX_VALUE;
	}

	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		JSTUtils.bigIntToNBT(JSTUtils.getOrCreateNBT(st), BigInteger.TEN.pow(100), "BIG_EU");
		sub.add(st);
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		BigInteger bi = st.hasTagCompound() ? JSTUtils.nbtToBigInt(st.getTagCompound(), "BIG_EU") : BigInteger.ZERO;
		ls.add(I18n.format("jst.tooltip.energy.eu", JSTUtils.formatNum(bi), "\u221E"));
		ls.add(I18n.format("jst.tooltip.energy.rf", JSTUtils.formatNum(bi.multiply(BigInteger.valueOf(JSTCfg.RFPerEU))), "\u221E"));
		int t = getTier(st);
		ls.add(I18n.format("jst.tooltip.energy.tier") + " " + t + " (" + JSTUtils.getTierName(t) + ")");
		long tl = transferLimit(st);
		ls.add(I18n.format("jst.tooltip.energy.transfer") + " " + JSTUtils.formatNum(tl)  + " EU / " + JSTUtils.formatNum(BigInteger.valueOf(tl).multiply(BigInteger.valueOf(JSTCfg.RFPerEU))) + " RF");
		ls.add(I18n.format("jst.tooltip.energy.highcapacity"));
	}
}
