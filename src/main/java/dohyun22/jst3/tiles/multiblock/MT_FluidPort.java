package dohyun22.jst3.tiles.multiblock;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIFluidTank;
import dohyun22.jst3.container.ContainerFluidPort;
import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MetaTileFluidGen;
import dohyun22.jst3.tiles.interfaces.IMultiBlockIO;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_FluidPort extends MetaTileBase implements IMultiBlockIO {
	private String displayTexture;
	public FluidTank tank;
	public final boolean isOutput;
	private int transfer = 1000;
	
	public MT_FluidPort(boolean out) {
		isOutput = out;
	}
	
	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MT_FluidPort ret = new MT_FluidPort(isOutput);
		ret.tank = new MTETank(32000, true, true, ret, 2);
		return ret;
	}

	@Override
	public void setTexture(String tex) {
		if (tex == null) {
			if (displayTexture != null) {
				displayTexture = null;
				baseTile.issueUpdate();
			}
			return;
		}
		if (!tex.equals(displayTexture)) {
			displayTexture = tex;
			baseTile.issueUpdate();
		}
	}

	@Override
	public boolean isMBOutput() {
		return isOutput;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		transfer = tag.getInteger("trans");
		tank.setCapacity(tag.getInteger("cap"));
		tank.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("trans", transfer);
		tag.setInteger("cap", tank.getCapacity());
		tank.writeToNBT(tag);
	}
	
	@Override
	public void readSyncableDataFromNBT(NBTTagCompound tag) {
		if (tag.hasKey("dt"))
			displayTexture = tag.getString("dt");
	}

	@Override
	public void writeSyncableDataToNBT(NBTTagCompound tag) {
		if (displayTexture != null)
			tag.setString("dt", displayTexture);
	}
	
	@Override
	public boolean isMultiBlockPart() {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("t1_side");
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex(isOutput ? "fl_out" : "fl_in")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) ret[n] = getTETex(getFacing() == JSTUtils.getFacingFromNum(n) ? isOutput ? "fl_out" : "fl_in" : displayTexture == null ? "t1_side" : displayTexture);
		return ret;
	}
	
	@Override
	public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
		if (c == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && f != null && f == getFacing())
			return (T) tank;
		return null;
	}
	
	@Override
	public void onPostTick() {
		if (isClient()) return;
		
		ItemStack st = inv.get(0);
		if (!st.isEmpty()) {
			IFluidHandlerItem fh = FluidUtil.getFluidHandler(st);
			if (fh != null) {
				if (FluidUtil.getFluidContained(st) == null) {
					JSTUtils.fillFluidItemInv(tank, 1000, baseTile, 0, 1);
				} else {
					JSTUtils.drainFluidItemInv(tank, 1000, baseTile, 0, 1);
				}
			}
		}

		EnumFacing f = getFacing();
		if (f != null) {
			if (isOutput) {
				if (tank.getFluidAmount() > 0) {
					int amt = JSTUtils.fillTank(getWorld(), getPos(), f, new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), transfer)));
					if (amt > 0) tank.drain(amt, true);
				}
			} else if (tank.getFluidAmount() < tank.getCapacity()) {
				IFluidHandler fh = FluidUtil.getFluidHandler(getWorld(), getPos().offset(f), f.getOpposite());
				if (fh != null) {
					FluidStack fs = fh.drain(Math.min(tank.getCapacity() - tank.getFluidAmount(), transfer), false);
					if (fs != null) fh.drain(tank.fill(fs, true), true);
				}
			}
		}
	}
	
	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return super.isItemValidForSlot(sl, st) && sl == 0;
	}
	
    @Override
    public boolean canExtractItem(int sl, ItemStack st, EnumFacing f) {
    	return sl == 1;
    }
	
	@Override
	public int getInvSize() {
		return 3;
	}
	
	@Override
	public boolean canSlotDrop(int num) {
		return num != 2;
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new ContainerFluidPort(inv, te);
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new GUIFluidTank(new ContainerFluidPort(inv, te), null);
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile != null && !isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, false);
	}
	
	@Override
	public boolean setFacing(EnumFacing f, EntityPlayer pl) {
		return doSetFacing(f, false);
	}
	
	public void setCapacity(int amt) {
		tank.setCapacity(amt);
	}

	public void setTransfer(int amt) {
		transfer = Math.max(0, amt);
	}
}
