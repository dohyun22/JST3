package dohyun22.jst3.items.behaviours;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.device.MT_WaterPurifier;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.ThirstHelper;

public class IB_WaterPurifier extends ItemBehaviour {

	public IB_WaterPurifier() {
		maxEnergy = 120000;
	}

	@Override
	public int getTier(ItemStack st) {
		return 1;
	}
	
	@Override
	public boolean canCharge(ItemStack st) {
		return true;
	}
	
	@Override
	public boolean canDischarge(ItemStack st) {
		return false;
	}
	
	@Override
	public long transferLimit(ItemStack st) {
		return 256;
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public void update(ItemStack st, World w, Entity e, int sl, boolean select) {
		if (!w.isRemote && w.getTotalWorldTime() % 100L == 0 && e instanceof EntityPlayer && getEnergy(st) >= 100) {
			FluidStack fs = FluidUtil.getFluidContained(st);
			if (fs != null) {
				try {
					IThirst td = ThirstHelper.getThirstData((EntityPlayer)e);
					int ct = td.getThirst();
					if (ct < 20) {
						int r = 20 - ct;
						if (fs.amount >= r * 30) {
							td.setThirst(20);
							td.setHydration(10.0f);
							td.setExhaustion(0.0f);
							setEnergy(st, getEnergy(st) - r * 5);
							st.getTagCompound().getCompoundTag("Fluid").setInteger("Amount", Math.max(0, fs.amount - r * 30));
						}
					}
				} catch (Throwable t) {}
			}
		}
	}

	@Override
	public EnumActionResult onRightClickBlock(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumHand h, EnumFacing f, float hx, float hy, float hz) {
		if (!w.isRemote && st.getCount() == 1) {
			TileEntity te = w.getTileEntity(p);
			if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f)) {
				IFluidHandler fh = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f);
				IFluidHandlerItem fh2 = FluidUtil.getFluidHandler(st);
				if (fh2 != null && fh.drain(fh2.fill(fh.drain(1000, false), true), true) != null)
					w.playSound(null, p, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 0.25F, 1.0F);
				return EnumActionResult.SUCCESS;
			}
			Fluid fl = FluidRegistry.lookupFluidForBlock(w.getBlockState(p.offset(f)).getBlock());
			if (fl != null) {
				FluidStack fs = new FluidStack(fl, 1000);
				IFluidHandlerItem fh = FluidUtil.getFluidHandler(st);
				if (fh != null && fh.fill(fs, false) == 1000) {
					fh.fill(fs, true);
					w.setBlockState(p, Blocks.AIR.getDefaultState(), 11);
					w.playSound(null, p, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 0.25F, 1.0F);
				}
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		setEnergy(st, this.maxEnergy);
		JSTUtils.getOrCreateNBT(st).setInteger("watr", 16000);
		sub.add(st);
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		addEnergyTip(st, ls);
		addFluidTip(st, ls);
    }

	@Override
	public boolean hasCapability(Capability<?> cap, ItemStack st) {
		return cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> cap, ItemStack st) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
			return (T) new InternalTank(st);
		return null;
	}

	private static class InternalTank extends FluidHandlerItemStack {
		InternalTank(ItemStack c) {
			super(c, 16000);
		}

		@Override
		public int fill(FluidStack fs, boolean fill) {
			if (fs != null) {
				String s = fs.getFluid().getName();
				for (String s2 : MT_WaterPurifier.supported)
					if (s2.equals(s))
						return super.fill(fs, fill);
			}
			return 0;
		}
		
		@Override
		protected void setContainerToEmpty() {
			super.setContainerToEmpty();
			if (container.getTagCompound().hasNoTags()) container.setTagCompound(null);
		}
	}
}
