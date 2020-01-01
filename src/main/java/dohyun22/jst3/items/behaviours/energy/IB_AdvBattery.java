package dohyun22.jst3.items.behaviours.energy;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.items.FluidItemWrapper;
import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.energy.MetaTileFluidGen;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class IB_AdvBattery extends ItemBehaviour {
	/** 0 = RTG, 1 = Fuel Cell */
	private final byte type;
	
	public IB_AdvBattery(int t) {
		type = (byte)t;
	}
	
	@Override
	public long charge(ItemStack st, long amt, int tier, boolean itl, boolean sim) {
		return 0;
	}

	@Override
	public long discharge(ItemStack st, long amt, int tier, boolean itl, boolean sim) {
		if (st.getCount() != 1) return 0;
		switch (type) {
		case 0:
			return Math.min(getOutput(), amt);
		case 1:
			IFluidHandlerItem fh = FluidUtil.getFluidHandler(st);
			if (fh != null) {
				NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
				int bf = nbt.getInteger("buff");
				if (bf <= 0) {
					int fv = MetaTileFluidGen.getFuelValue((byte) 1, fh.drain(1, false));
					if (fv > 0) {
						int m = Math.max((int) Math.ceil(32 / (double)fv), 1);
						FluidStack fs = fh.drain(m, !sim);
						bf = fv * (fs == null ? 0 : fs.amount);
					}
				}
				
				if (bf > 0) {
					int use = Math.min((int)Math.min(bf, amt), 32);
					if (!sim) {
						bf -= use;
						nbt.setInteger("buff", bf);
					}
					return use;
				}
			}
			break;
		}
		return 0;
	}
	
	@Override
	public long getEnergy(ItemStack st) {
		return discharge(st, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
	}
	
	@Override
	public long getMaxEnergy(ItemStack st) {
		return discharge(st, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
	}
	
	@Override
	public boolean hasCapability(Capability<?> cap, ItemStack st) {
		if (type == 1) return cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
		return super.hasCapability(cap, st);
	}
	
	@Override
	@Nullable
	public <T> T getCapability(Capability<T> cap, ItemStack st) {
		if (type == 1 && cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
			return (T) new BatteryFluidHandler(st);
		return super.getCapability(cap, st);
	}

	@Override
	public boolean canDischarge(ItemStack st) {
		return true;
	}
	
	@Override
	public int getTier(ItemStack st) {
		return 1;
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		if (type == 1) {
			ls.add(I18n.format("jst.tooltip.advbat.fuelcell"));
			addFluidTip(st, ls);
		}
		int e = getOutput();
		ls.add(I18n.format("jst.msg.com.out") + " " + e + " EU/t, " + (e * JSTCfg.RFPerEU) + " RF/t");
	}
	
	private int getOutput() {
		switch (type) {
		case 0:
			return 16;
		case 1:
			return 32;
		default:
			return 0;
		}
	}
	
	private class BatteryFluidHandler extends FluidHandlerItemStack {
		public BatteryFluidHandler(ItemStack st) {
			super(st, 4000);
		}
		
		@Override
		public int fill(FluidStack fs, boolean fill) {
			if (type == 1 && fs != null) {
				String s = FluidRegistry.getFluidName(fs);
				if (MetaTileFluidGen.getFuelValue((byte) 1, fs) > 0) return super.fill(fs, fill);
			}
			return 0;
		}
	}
}
