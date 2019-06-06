package dohyun22.jst3.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidItemWrapper implements ICapabilityProvider {
	protected ItemStack st;
	private final boolean canFill;
	private final boolean canDrain;
	private final int max;
	@Nullable
	private FluidStack fluid;
	

	public FluidItemWrapper(ItemStack st, int max) {
		this.st = st;
		this.canFill = true;
		this.canDrain = true;
		this.max = max;
	}

	public FluidItemWrapper(ItemStack st, boolean inject, boolean pull, int max) {
		this.st = st;
		this.canFill = inject;
		this.canDrain = pull;
		this.max = max;
	}
	
	@Nullable
	protected int fill(ItemStack st, FluidStack res, boolean notsim) {
		return 0;
	}

	@Nullable
	protected FluidStack drain(ItemStack st, int amt, boolean notsim) {
		return null;
	}
	
	@Nullable
	protected FluidStack getFluid(ItemStack st) {
		return null;
	}

	protected int getCapacity(ItemStack st) {
		return this.max;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing from) {
		return cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		if (!hasCapability(cap, f))
			return null;
		return (T)CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(new IFluidHandlerItem() {
			public IFluidTankProperties[] getTankProperties() {
				return new IFluidTankProperties[] { new FluidTankProperties(
						FluidItemWrapper.this.getFluid(FluidItemWrapper.this.st),
						FluidItemWrapper.this.getCapacity(FluidItemWrapper.this.st),
						FluidItemWrapper.this.canFill, FluidItemWrapper.this.canDrain) };
			}

			public int fill(FluidStack fs, boolean fill) {
				return FluidItemWrapper.this.fill(FluidItemWrapper.this.st, fs, fill);
			}

			@Nullable
			public FluidStack drain(FluidStack fs, boolean drain) {
				if (fs != null && fs.isFluidEqual(FluidItemWrapper.this.getFluid(FluidItemWrapper.this.st)))
					return FluidItemWrapper.this.drain(FluidItemWrapper.this.st, fs.amount, drain);
				return null;
			}

			@Nullable
			public FluidStack drain(int max, boolean drain) {
				return FluidItemWrapper.this.drain(FluidItemWrapper.this.st, max, drain);
			}

			@Nonnull
			public ItemStack getContainer() {
				return FluidItemWrapper.this.st;
			}
		});
	}
}
