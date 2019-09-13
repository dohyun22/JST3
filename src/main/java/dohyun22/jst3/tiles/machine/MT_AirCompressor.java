package dohyun22.jst3.tiles.machine;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_AirCompressor extends MT_Machine {
	private FluidTank tank;
	private boolean isBlocked;

	public MT_AirCompressor() {
		super(1);
	}
	
	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MT_AirCompressor ret = new MT_AirCompressor();
		ret.tank = new MTETank(32000, true, true, ret, 2);
		return ret;
	}

	@Override
	protected boolean checkCanWork() {
		if (!isBlocked && tank.getFluidAmount() + 2000 <= tank.getCapacity() && JSTChunkData.getFineDust(getWorld(), new ChunkPos(getPos())) <= 100000) {
			energyUse = 20;
			mxprogress = 100;
			return true;
		}
		return false;
	}

	@Override
	protected void finishWork() {
		tank.fillInternal(new FluidStack(JSTFluids.air, 2000), true);
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
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return super.isItemValidForSlot(sl, st) && sl == 0;
	}
	
    @Override
    public boolean canExtractItem(int sl, ItemStack st, EnumFacing f) {
    	return sl == 1;
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
		isBlocked = tag.getBoolean("isBlocked");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tank.writeToNBT(tag);
		if (isBlocked) tag.setBoolean("isBlocked", true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite ws = getTETex("vent");
		return new TextureAtlasSprite[] {getTieredTex(1), getTieredTex(1), ws, ws, ws, ws};
	}
	
	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new Slot(te, 0, 128, 17));
		cg.addSlot(new JSTSlot(te, 1, 128, 52, false, true, 64, true));
		cg.addSlot(new JSTSlot(te, 2, 100, 35, false, false, 64, false));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(128, 17, 0);
		gg.addSlot(128, 52, 0);
		gg.addSlot(100, 35, 3);
		gg.addPrg(56, 35);
		gg.addPwr(12, 31);
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile != null && !isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (getWorld().isRemote) return;
		ItemStack st = inv.get(0);
		if (!st.isEmpty() && FluidUtil.getFluidHandler(st) != null)
			JSTUtils.fillFluidItemInv(tank, 1000, baseTile, 0, 1);
	}
	
	@Override
	protected void onStartWork() {
		getWorld().playSound(null, getPos(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.8F, 0.25F);
	}
	
	@Override
	public void onBlockUpdate() {
		if (isClient()) return;
		byte n = 0;
		for (EnumFacing f : EnumFacing.HORIZONTALS) {
			BlockPos p = getPos().offset(f);
			if (getWorld().isAirBlock(p)) n++;
		}
		isBlocked = n == 0;
	}
}
