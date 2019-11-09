package dohyun22.jst3.tiles.multiblock;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.tiles.interfaces.IMultiBlockIO;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_EnergyPort extends MetaTileEnergyInput implements IGenericGUIMTE, IMultiBlockIO {
	private long mxengy;
	private int mxtrans;
	private String displayTexture;
	public final boolean isOutput;

	public MT_EnergyPort(boolean out) {
		isOutput = out;
	}
	
	@Override
	public int getInvSize() {
		return 1;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_EnergyPort(isOutput);
	}

	@Override
	public boolean canProvideEnergy() {
		return isOutput;
	}

	@Override
	public boolean canAcceptEnergy() {
		return !isOutput;
	}
	
	@Override
	public long getMaxEnergy() {
		return mxengy;
	}

	public void setMaxEnergy(long val) {
		mxengy = Math.max(val, 0L);
		baseTile.energy = Math.min(baseTile.energy, mxengy);
	}
	
	public void setMaxTransfer(int val) {
		mxtrans = Math.max(val, 0);
	}
	
	@Override
	public int maxEUTransfer() {
		return mxtrans;
	}
	
	@Override
	public boolean isEnergyInput(EnumFacing side) {
		if (this.baseTile == null) return false;
		return !isOutput && this.getFacing() == side;
	}

	@Override
	public boolean isEnergyOutput(EnumFacing side) {
		if (this.baseTile == null) return false;
		return isOutput && this.getFacing() == side;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("t1_side");
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex(isOutput ? "e1dout" : "e1din")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) ret[n] = getTETex(getFacing() == JSTUtils.getFacingFromNum(n) ? isOutput ? "e1dout" : "e1din" : displayTexture == null ? "t1_side" : displayTexture);
		return ret;
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, false);
	}
	
	@Override
	public boolean setFacing(EnumFacing f, EntityPlayer pl) {
		return doSetFacing(f, false);
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (getWorld().isRemote)
			return;
		if (isOutput) {
			if (baseTile.energy > 0L)
				injectEnergyToSide(getFacing());
	    }
		
    	if (!inv.get(0).isEmpty()) {
    		if (isOutput) {
    			if (baseTile.energy > 0L)
    				baseTile.energy -= JSTUtils.chargeItem(inv.get(0), Math.min(baseTile.energy, maxEUTransfer()), JSTUtils.getTierFromVolt(mxtrans), false, false);
    		} else if (baseTile.energy < getMaxEnergy()) {
    			baseTile.energy += JSTUtils.dischargeItem(inv.get(0), Math.min(getMaxEnergy() - baseTile.energy, maxEUTransfer()), JSTUtils.getTierFromVolt(mxtrans), false, false);
    		}
    	}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		mxengy = tag.getLong("mxengy");
		mxtrans = tag.getInteger("mxtrans");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setLong("mxengy", mxengy);
		tag.setInteger("mxtrans", mxtrans);
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
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile != null && !isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}
	
	@Override
	public boolean canInsertItem(int sl, ItemStack st, EnumFacing dir) {
		return sl == 0 && (isOutput && JSTUtils.chargeItem(st, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) > 0) || (!isOutput && JSTUtils.dischargeItem(st, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) > 0);
	}
	
	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		return sl == 0 && (isOutput && JSTUtils.chargeItem(st, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) <= 0) || (!isOutput && JSTUtils.dischargeItem(st, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) <= 0);
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		ContainerGeneric ret = new ContainerGeneric(te);
		ret.addSlot(new BatterySlot(te, 0, 52, 35, isOutput, !isOutput));
		ret.addPlayerSlots(inv);
		return ret;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		GUIGeneric ret = new GUIGeneric((ContainerGeneric) getServerGUI(id, inv, te));
		ret.addSlot(52, 35, 2);
		ret.addPwr2(93, 34);
		return ret;
	}
}
