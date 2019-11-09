package dohyun22.jst3.tiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.utils.JSTUtils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

@Optional.Interface(iface="ic2.api.tile.IEnergyStorage", modid="ic2")
public class TileEntityMeta extends TileEntity implements ITickable, ISidedInventory, ic2.api.tile.IEnergyStorage {
	public static final int maxID = Short.MAX_VALUE;
	/** MetaTileEntity id */
	private int ID;
	public byte errorCode;
	public long energy;
	private boolean active;
	private boolean needsUpdate = true;
	@Nullable
	public EnumFacing facing;
	@Nullable 
	private EnumFacing facing2;
	private long timer;
	public MetaTileBase mte;

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readSyncableDataFromNBT(tag);
		if (tag == null) {
			tag = new NBTTagCompound();
		}
		this.energy = tag.getLong("JEU");
		if (this.energy == 0) this.energy = tag.getLong("Energy");

		if (this.hasValidMTE()) {
			try {
				ItemStackHelper.loadAllItems(tag, this.mte.inv);
				this.mte.readFromNBT(tag);
			} catch (Throwable e) {
				System.err.println("An error occurred while loading MetaTileEntity data. Please report this log to JustServer developer team!");
				e.printStackTrace();
			}
			issueUpdate();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.writeSyncableDataToNBT(tag);
		if (this.energy > 0)
			tag.setLong("JEU", this.energy);
		
		if (this.hasValidMTE()) {
			try {
				if (this.mte.inv.size() > 0)
					ItemStackHelper.saveAllItems(tag, this.mte.inv);
				this.mte.writeToNBT(tag);
			} catch (Throwable e) {
				System.err.println("An error occurred while saving MetaTileEntity data. Please report this log to JustServer developer team!");
				e.printStackTrace();
			}
		}
		return tag;
	}
	
	protected void readSyncableDataFromNBT(NBTTagCompound tag) {
		this.ID = tag.getInteger("ID");
		if (!this.hasValidMTE()) createNewMetatileEntity(this.ID);
		this.facing = tag.hasKey("Facing") ? JSTUtils.getFacingFromNum(tag.getByte("Facing")) : null;
		this.active = tag.getBoolean("IsActive");
		if (this.hasValidMTE()) {
			try {
				mte.readSyncableDataFromNBT(tag);
			} catch (Throwable e) {
				System.err.println("An error occurred while reading MetaTileEntity sync data. Please report this log to JustServer developer team!");
				e.printStackTrace();
			}
		}
	}
	
	protected void writeSyncableDataToNBT(NBTTagCompound tag) {
		tag.setInteger("ID", this.ID);
		if (this.facing != null) tag.setByte("Facing", JSTUtils.getNumFromFacing(this.facing));
		if (this.active) tag.setBoolean("IsActive", this.active);
		if (this.hasValidMTE()) {
			try {
				mte.writeSyncableDataToNBT(tag);
			} catch (Throwable e) {
				System.err.println("An error occurred while writing MetaTileEntity data for server-client sync. Please report this log to JustServer developer team!");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (hasValidMTE()) mte.onMarkDirty();
	}

	@Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

	@Override
	public void invalidate() {
		super.invalidate();
		if (hasValidMTE())
			mte.invalidate();
	}

	@Override
	public void validate() {
		super.validate();
		this.timer = 0;
		if (hasValidMTE()) mte.validate();
	}

	@Override
	public void update() {
		if (isInvalid())
			return;
		if (!hasValidMTE()) {
			if (this.mte == null)
				return;
			this.mte.baseTile = this;
		}
		if (!this.mte.canUpdate() || this.errorCode != 0)
			return;
		try {
			this.timer++;
			if (this.timer == 1) {
				if (!this.world.isRemote)
					issueUpdate();
				this.mte.onFirstTick();
			}
			if (this.timer > 1 && hasValidMTE()) {
				this.mte.onPreTick();
				if (this.world.isRemote) {
					if (this.needsUpdate) this.needsUpdate = false;
				} else {
					if (this.facing != this.facing2) {
						this.facing2 = this.facing;
						issueUpdate();
					}
					if (this.needsUpdate) {
	                    this.markDirty();
	                    IBlockState bs = this.world.getBlockState(this.pos);
						this.world.notifyBlockUpdate(pos, bs, bs, 3);
						this.needsUpdate = false;
					}
				}
				this.mte.onPostTick();
			}
		} catch (Throwable e) {
			if (this.errorCode == 0) {
				System.err.println("An error occurred while ticking MetaTileEntity. Please report this log to JustServer developer team!");
				e.printStackTrace();
				this.errorCode = 1;
			}
		}
	}

	@Override
	public void addInfoToCrashReport(CrashReportCategory cr) {
		super.addInfoToCrashReport(cr);
		cr.addDetail("MetaTileEntityID", new ICrashReportDetail<String>() {
			public String call() throws Exception {
				return Integer.toString(ID);
			}
		});
	}

	@Nullable
	public ITextComponent getDisplayName() {
		if (hasValidMTE()) return mte.getDisplayName();
		return new TextComponentString("JST MetaTileEntity");
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound t = new NBTTagCompound();
		this.writeSyncableDataToNBT(t);
		return new SPacketUpdateTileEntity(pos, 1, t);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.getNbtCompound();
		this.readSyncableDataFromNBT(tag);
	    IBlockState state = getWorld().getBlockState(pos);
	    getWorld().markAndNotifyBlock(pos, null, state, state, 3);
	}

	@Override
	public void onChunkUnload() {
		if (hasValidMTE()) mte.onChunkUnload();
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public boolean canRenderBreaking() {
		return false;
	}

	@Override
	public void onLoad() {
		if (hasValidMTE()) {
			mte.onLoad();
			
			if (!mte.canUpdate()) {
				try {
					this.getWorld().tickableTileEntities.remove(this);
				} catch (Exception e) {}
			}
		}
	}

	@Override
	public int getSizeInventory() {
		if (hasValidMTE()) return mte.getInvSize();
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int sl) {
		if (hasValidMTE())
			return mte.getStackInSlot(sl);
        return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int sl, int amt) {
		if (hasValidMTE())
			return mte.decrStackSize(sl, amt);
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int sl) {
        if (hasValidMTE())
        	return mte.removeStackFromSlot(sl);
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int sl, ItemStack st) {
		if (!hasValidMTE() || sl < 0 || sl >= getSizeInventory()) return;
		mte.setInventorySlotContents(sl, st);
	}

	@Override
	public int getInventoryStackLimit() {
		if (!hasValidMTE()) return 64;
		return mte.getStackLimit();
	}

	@Override
	public boolean isEmpty() {
		if (!hasValidMTE() || getSizeInventory() <= 0) return false;
		for (ItemStack st : mte.inv)
			if (!st.isEmpty()) return false;
		return true;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer pl) {
		return !isInvalid() && hasValidMTE() && mte.isUsable(pl) && getDistanceSq(pl.posX, pl.posY, pl.posZ) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer pl) {}

	@Override
	public void closeInventory(EntityPlayer pl) {}

	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		if (hasValidMTE())
			return this.mte.isItemValidForSlot(sl, st);
		return false;
	}

	@Override
	public void clear() {
		if (hasValidMTE())
			for (int n = 0; n < mte.inv.size(); ++n)
				mte.inv.set(n, ItemStack.EMPTY);
	}

	@Override
	public String getName() {
		return "JST MetaTileEntity";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing dir) {
		if (hasValidMTE())
			return mte.getSlotsForFace(dir);
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int sl, ItemStack st, EnumFacing dir) {
		if (hasValidMTE())
			return mte.canInsertItem(sl, st, dir);
		return false;
	}

	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		if (hasValidMTE())
			return mte.canExtractItem(sl, st, dir);
		return false;
	}
	
	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}
	
	@Override
    @Optional.Method(modid = "ic2")
    public int getStored() {
        if (hasValidMTE())
            return JSTUtils.convLongToInt(energy);
        return 0;
    }
    
	@Override
    @Optional.Method(modid = "ic2")
    public void setStored(int e) {
        if (hasValidMTE())
           energy = e;
    }
    
	@Override
    @Optional.Method(modid = "ic2")
    public int addEnergy(int e) {
        if (!hasValidMTE())
            return 0;
        energy = Math.max(0L, energy + e);
        return JSTUtils.convLongToInt(energy);
    }
    
	@Override
    @Optional.Method(modid = "ic2")
    public int getCapacity() {
        if (hasValidMTE())
            return JSTUtils.convLongToInt(mte.getMaxEnergy());
        return 0;
    }
    
	@Override
    @Optional.Method(modid = "ic2")
    public int getOutput() {
        if (hasValidMTE())
            return mte.maxEUTransfer();
        return 0;
    }
    
	@Override
    @Optional.Method(modid = "ic2")
    public double getOutputEnergyUnitsPerTick() {
        if (hasValidMTE())
            return mte.maxEUTransfer();
        return 0.0;
    }
    
	@Override
    @Optional.Method(modid = "ic2")
    public boolean isTeleporterCompatible(EnumFacing side) {
        return hasValidMTE() && mte.isEnergyStorage();
    }
	
	public boolean createNewMetatileEntity(int id) {
		MetaTileBase te = MetaTileBase.getTE(id);
		if (id < 0 || id >= maxID || te == null)
			return false;
		if (id != 0) {
			if (hasValidMTE()) {
				mte.invalidate();
				mte.baseTile = null;
			}
			mte = te.newMetaEntity(this);
			mte.baseTile = this;
			timer = 0L;
			ID = id;
			return true;
		}
		return false;
	}

	public final boolean hasValidMTE() {
		return mte != null && mte.baseTile == this;
	}
	
	public void issueUpdate() {
		needsUpdate = true;
	}
	
	public void cancelUpdate() {
		needsUpdate = false;
	}

	public long getTimer() {
		return this.timer;
	}
	
	public boolean setActive(boolean val) {
		if (this.active == val) return false;
		this.active = val;
		this.issueUpdate();
		return true;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public boolean isMultiBlockPart() {
		if (this.hasValidMTE()) return this.mte.isMultiBlockPart();
		return false;
	}
	
	public int getID() {
		return this.ID;
	}
	
    @Override
    public boolean hasCapability(Capability<?> c, @Nullable EnumFacing f) {
        return this.getCapability(c, f) != null;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
        if (hasValidMTE()) {
        	T ret = mte.getCapability(c, f);
        	if (ret != null) return ret;
            if (c == CapabilityEnergy.ENERGY && ((mte.canAcceptEnergy() && mte.isEnergyInput(f)) || (mte.canProvideEnergy() && mte.isEnergyOutput(f))))
                return (T)CapabilityEnergy.ENERGY.cast(new EnergyHandler(f));
            if (c == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            	if (f != null) {
            		int[] arr = mte.getSlotsForFace(f);
            		if (arr != null && arr.length > 0)
            			return (T)CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(this, f));
            	}
            	return null;
            }
        }
        return super.getCapability(c, f);
    }
    
    protected class EnergyHandler implements net.minecraftforge.energy.IEnergyStorage {
        @Nullable
        private final EnumFacing f;
        
        public EnergyHandler(EnumFacing in) {
            this.f = in;
        }
        
        @Override
        public int receiveEnergy(int in, boolean sim) {
            if (hasValidMTE())
                return JSTUtils.convLongToInt(mte.injectEnergy(f, in / 4, sim) * 4L);
            return 0;
        }
        
        @Override
        public int extractEnergy(int out, boolean sim) {
    		if (hasValidMTE() && mte.canProvideEnergy() && mte.isEnergyOutput(f)) {
                int rf = JSTUtils.convLongToInt(Math.min(energy * 4L, Math.min(mte.maxEUTransfer() * 4L, out)));
                if (!sim) energy -= rf / 4;
                return rf;
            }
            return 0;
        }
        
        @Override
        public int getEnergyStored() {
            if (hasValidMTE() && (mte.isEnergyInput(f) || mte.isEnergyOutput(f)))
                return JSTUtils.convLongToInt(energy * 4L);
            return 0;
        }
        
        @Override
        public int getMaxEnergyStored() {
            if (hasValidMTE() && (mte.isEnergyInput(f) || mte.isEnergyOutput(f)))
                return JSTUtils.convLongToInt(mte.getMaxEnergy() * 4L);
            return 0;
        }
        
        @Override
        public boolean canExtract() {
            return hasValidMTE() && mte.canProvideEnergy() && mte.isEnergyOutput(this.f);
        }
        
        @Override
        public boolean canReceive() {
            return hasValidMTE() && mte.canAcceptEnergy() && mte.isEnergyInput(this.f);
        }
    }
}
