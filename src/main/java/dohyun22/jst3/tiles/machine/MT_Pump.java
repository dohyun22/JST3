package dohyun22.jst3.tiles.machine;

import javax.annotation.Nullable;

import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.oredict.OreDictionary;

public class MT_Pump extends MT_Machine {
	private FluidTank tank;
	private byte cnt;

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
		/*if (tank.getFluid() == null || tank.getFluid().amount + 1000 <= tank.getCapacity()) {
    		boolean down = false;
    		
    		if (cnt % 10 == 0)
    			down = moveOneDown();
    		
    		if (mPumpedBlockID1 <= 0 || mPumpedBlockID2 <= 0) {
        		getFluidAt(getBaseMetaTileEntity().getXCoord(), getYOfPumpHead() - 1, getBaseMetaTileEntity().getZCoord());
        		if (mPumpedBlockID1 <= 0 || mPumpedBlockID2 <= 0)
        			getFluidAt(getBaseMetaTileEntity().getXCoord(), getYOfPumpHead(), getBaseMetaTileEntity().getZCoord() + 1);
        		if (mPumpedBlockID1 <= 0 || mPumpedBlockID2 <= 0)
        			getFluidAt(getBaseMetaTileEntity().getXCoord(), getYOfPumpHead(), getBaseMetaTileEntity().getZCoord() - 1);
        		if (mPumpedBlockID1 <= 0 || mPumpedBlockID2 <= 0)
        			getFluidAt(getBaseMetaTileEntity().getXCoord() + 1, getYOfPumpHead(), getBaseMetaTileEntity().getZCoord());
        		if (mPumpedBlockID1 <= 0 || mPumpedBlockID2 <= 0)
        			getFluidAt(getBaseMetaTileEntity().getXCoord() - 1, getYOfPumpHead(), getBaseMetaTileEntity().getZCoord());
            } else {
            	if (getYOfPumpHead() < getBaseMetaTileEntity().getYCoord()) {
		        	if (down || (mPumpList.isEmpty() && getBaseMetaTileEntity().getTimer() % 200 == 100) || getBaseMetaTileEntity().getTimer() % 72000 == 100) {
		        		mPumpList.clear();
		        		for (int y = getBaseMetaTileEntity().getYCoord() - 1, yHead = getYOfPumpHead(); mPumpList.isEmpty() && y >= yHead; y--) {
		        			scanForFluid(getBaseMetaTileEntity().getXCoord(), y, getBaseMetaTileEntity().getZCoord(), mPumpList, getBaseMetaTileEntity().getXCoord(), getBaseMetaTileEntity().getZCoord(), 64);
		        		}
		        	}
		        	if (!down && !mPumpList.isEmpty()) {
			        	consumeFluid(mPumpList.get(mPumpList.size()-1).x, mPumpList.get(mPumpList.size()-1).y, mPumpList.get(mPumpList.size()-1).z);
			        	mPumpList.remove(mPumpList.size()-1);
		        	}
            	}
    		}
		}*/
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MT_Pump ret = new MT_Pump(tier);
		tank = new MTETank(32000, true, false, ret, 2);
		return ret;
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
	
    /*private boolean moveOneDown() {
    	if (inv.get(3).isEmpty() || !OreDictionary.itemMatches(inv.get(3), new ItemStack(JSTBlocks.blockTile, 1, 5065), false)) return false;
    	int yHead = getYOfPumpHead();
    	if (yHead <= 0) return false;
    	if (!consumeFluid(getPos().getX(), yHead - 1, getPos().getZ()) && !getBaseMetaTileEntity().getAir(getBaseMetaTileEntity().getXCoord(), yHead - 1, getBaseMetaTileEntity().getZCoord())) return false;
    	if (!getBaseMetaTileEntity().getWorld().setBlock(getBaseMetaTileEntity().getXCoord(), yHead - 1, getBaseMetaTileEntity().getZCoord(), GT_ModHandler.getIC2Item("miningPipeTip", 1).itemID)) return false;
    	if (yHead != getBaseMetaTileEntity().getYCoord()) getBaseMetaTileEntity().getWorld().setBlock(getBaseMetaTileEntity().getXCoord(), yHead, getBaseMetaTileEntity().getZCoord(), GT_ModHandler.getIC2Item("miningPipe", 1).itemID);
    	getBaseMetaTileEntity().decrStackSize(0, 1);
    	return true;
    }
    
    private int getYOfPumpHead() {
    	int y = getPos().getY() - 1;
    	while (getBaseMetaTileEntity().getBlockID(getBaseMetaTileEntity().getXCoord(), y, getBaseMetaTileEntity().getZCoord()) == GT_ModHandler.getIC2Item("miningPipe", 1).itemID) y--;
    	if (y == getBaseMetaTileEntity().getYCoord() - 1) {
    		if (getBaseMetaTileEntity().getBlockID(getBaseMetaTileEntity().getXCoord(), y, getBaseMetaTileEntity().getZCoord()) != GT_ModHandler.getIC2Item("miningPipeTip", 1).itemID) {
        		return y + 1;
    		}
    	} else {
    		if (getBaseMetaTileEntity().getBlockID(getBaseMetaTileEntity().getXCoord(), y, getBaseMetaTileEntity().getZCoord()) != GT_ModHandler.getIC2Item("miningPipeTip", 1).itemID) {
    			getBaseMetaTileEntity().getWorld().setBlock(getBaseMetaTileEntity().getXCoord(), y, getBaseMetaTileEntity().getZCoord(), GT_ModHandler.getIC2Item("miningPipeTip", 1).itemID);
    		}
    	}
    	return y;
    }*/
}
