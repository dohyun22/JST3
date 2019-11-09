package dohyun22.jst3.tiles;

import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

public class MultiTankHandler implements IFluidHandler {
	private FluidTank[] tanks;
	
	public MultiTankHandler (@Nonnull FluidTank... tank) {
		this.tanks = tank;
	}

	public void writeToNBT(NBTTagCompound tag) {
		for (int n = 0; n < this.tanks.length; n++) {
			FluidTank t = this.tanks[n];
			NBTTagCompound tag2 = new NBTTagCompound();
			t.writeToNBT(tag2);
			tag.setTag("tank" + n, tag2);
		}
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		for (int n = 0; n < this.tanks.length; n++) {
			FluidTank t = this.tanks[n];
			t.readFromNBT(tag.getCompoundTag("tank" + n));
		}
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		if (tanks == null) return new IFluidTankProperties[0];
		IFluidTankProperties[] ret = new IFluidTankProperties[tanks.length];
		for (int n = 0; n < tanks.length; n++) {
			ret[n] = new FluidTankProperties(tanks[n].getFluid(), tanks[n].getCapacity());
		}
		return ret;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
	    for (FluidTank tank : tanks) {
	    	int ret = tank.fill(resource, doFill);
	    	if (ret > 0)
	    		return ret;
	    }
	    return EmptyFluidHandler.INSTANCE.fill(resource, doFill);
	}
	
	@Nullable
	public FluidStack drain(int maxDrain, boolean doDrain) {
		for (FluidTank tank : tanks) {
			FluidStack ret = tank.drain(maxDrain, doDrain);
	    	if (ret != null)
	    		return ret;
		}
		return EmptyFluidHandler.INSTANCE.drain(maxDrain, doDrain);
	}

	public FluidStack drain(FluidStack resource, boolean doDrain) {
		for (FluidTank tank : tanks) {
			FluidStack ret = tank.drain(resource, doDrain);
			if (ret != null)
	    		return ret;
		}
		return EmptyFluidHandler.INSTANCE.drain(resource, doDrain);
	}
	
	public FluidTank[] getTanks() {
		return tanks;
	}
	
	@Nullable
	public FluidTank getTank(int idx) {
		if (idx < 0 || idx > this.tanks.length) return null;
		return tanks[idx];
	}

	public int getSize() {
		return tanks.length;
	}
}
