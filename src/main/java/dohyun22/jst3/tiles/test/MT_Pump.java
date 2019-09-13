package dohyun22.jst3.tiles.test;

import javax.annotation.Nullable;

import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.machine.MT_Machine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.oredict.OreDictionary;

public class MT_Pump extends MT_Machine {
	private FluidTank tank;
	private int cnt;

	public MT_Pump(int tier) {
		super(tier);
	}

	@Override
	protected boolean checkCanWork() {
		if (tank.getFluidAmount() + 2000 <= tank.getCapacity()) {
			energyUse = 30;
			mxprogress = 20;
			return true;
		}
		return false;
	}

	@Override
	protected void finishWork() {
		if (tank.getFluid() == null || tank.getFluid().amount + 1000 <= tank.getCapacity()) {
			
		}
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MT_Pump r = new MT_Pump(tier);
		r.tank = new MTETank(12000 + tier * 4000, true, false, r, 2);
		return r;
	}
	
	@Override
	public int getInvSize() {
		return 4;
	}

	@Override
	public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
		if (c == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) this.tank;
		return null;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tank.writeToNBT(tag);
	}
}
