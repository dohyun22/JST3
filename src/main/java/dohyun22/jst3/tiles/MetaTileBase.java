package dohyun22.jst3.tiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class MetaTileBase {
	public static final LinkedHashMap<Integer, MetaTileBase> tes = new LinkedHashMap();
	public TileEntityMeta baseTile;
	public NonNullList<ItemStack> inv = NonNullList.<ItemStack>withSize(getInvSize(), ItemStack.EMPTY);
	public static TextureAtlasSprite ErrorTex;
	protected static final AxisAlignedBB SLAB_BOTTOM_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
	protected static final List<AxisAlignedBB> def = Arrays.asList(new AxisAlignedBB[] {Block.FULL_BLOCK_AABB});
	public static final int[] EMPTY_LIST = new int[0];
	
	public void readFromNBT(NBTTagCompound tag) {}

	public void writeToNBT(NBTTagCompound tag) {}
	
	public void writeSyncableDataToNBT(NBTTagCompound tag) {}
	
	public void readSyncableDataFromNBT(NBTTagCompound tag) {}

	public int getInvSize() {
		return 0;
	}
	
	public ItemStack decrStackSize(int sl, int amt) {
        ItemStack st = ItemStackHelper.getAndSplit(inv, sl, amt);
        if (!st.isEmpty()) markDirty();
        return st;
	}
	
	public ItemStack removeStackFromSlot(int sl) {
        ItemStack st = inv.get(sl);
        if (st.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
        	inv.set(sl, ItemStack.EMPTY);
            return st;
        }
	}

	public ItemStack getStackInSlot(int sl) {
        return sl >= 0 && sl < getInvSize() ? inv.get(sl) : ItemStack.EMPTY;
	}
	
	public void setInventorySlotContents(int sl, ItemStack st) {
		inv.set(sl, st);
		if (st != null && !st.isEmpty() && st.getCount() > getStackLimit())
			st.setCount(baseTile.getInventoryStackLimit());
		markDirty();
	}
	
	public int getStackLimit() {
		return 64;
	}
	
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return sl >= 0 && sl < inv.size();
	}
	
	public boolean canInsertItem(int sl, ItemStack st, EnumFacing dir) {
		return isItemValidForSlot(sl, st);
	}
	
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		return sl >= 0 && sl < inv.size();
	}

	public void invalidate() {
	}
	
	public void validate() {
	}

	public abstract MetaTileBase newMetaEntity(TileEntityMeta tem);

	public void onMarkDirty() {
	}

	@Nullable
	public ITextComponent getDisplayName() {
		return new TextComponentString("JST MetaTileEntity");
	}

	public void onChunkUnload() {
	}

	public void onLoad() {
	}

	public void onPreTick() {
	}
	
	public void onFirstTick() {
	}
	
	public void onPostTick() {
	}

	@Nullable
	public EnumFacing getFacing() {
		if (baseTile == null) return null;
		return baseTile.facing;
	}

	public boolean setFacing(EnumFacing f, EntityPlayer pl) {
		return false;
	}

	public boolean wrenchCanRemove(EntityPlayer player) {
		return true;
	}

	public float getHardness() {
		return 2.0F;
	}
	
	public float getResistance(@Nullable Entity ee, Explosion ex) {
		return 10.0F;
	}

	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing s, float hX, float hY, float hZ) {
		return false;
	}

	public boolean canRightclickIfSneaking(EntityPlayer pl, ItemStack st, EnumFacing s, float hX, float hY, float hZ) {
		return false;
	}
	
	/** only called on server side. */
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
	}

	@Nullable
	public static final MetaTileBase getTE(int id) {
		return tes.get(id);
	}

	public static final void registerTE(int id, MetaTileBase te) {
		if (id <= 0)
			throw new IllegalArgumentException("Negative values are not allowed!");
		if (tes.containsKey(id))
			throw new IllegalArgumentException("MetaTileEntity slot #" + id + " is already occupied!");
		tes.put(id, te);
	}
	
	public boolean canUpdate() {
		return true;
	}

	public void markDirty() {
		if (baseTile != null) baseTile.markDirty();
	}

	/** Do not use this method when base TileEntity can be null. */
	public final World getWorld() {
		return baseTile.getWorld();
	}
	
	/** Do not use this method when base TileEntity can be null. */
	public final BlockPos getPos() {
		return baseTile.getPos();
	}

	public int[] getSlotsForFace(EnumFacing dir) {
		int[] ret = new int[this.inv.size()];
		for (int n = 0; n < ret.length; n++) ret[n] = n;
		return ret;
	}

	public void onBlockUpdate() {
	}

	@Nullable
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return null;
	}
	
	@Nullable
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return null;
	}

	public boolean isSideSolid(EnumFacing s) {
		return true;
	}
	
	public boolean isSideOpaque(EnumFacing f) {
		return true;
	}
	
	/* Client-only methods */
	@SideOnly(Side.CLIENT)
	protected static final TextureAtlasSprite getTex(String str) {
		TextureMap tm = FMLClientHandler.instance().getClient().getTextureMapBlocks();
		TextureAtlasSprite ret = tm.getAtlasSprite(str);
		return tm.getMissingSprite() == ret ? ErrorTex : ret;
	}
	
	@SideOnly(Side.CLIENT)
	protected static final TextureAtlasSprite getTETex(String str) {
		return getTex(JustServerTweak.MODID + ":blocks/tileentity/" + str);
	}
	
	@SideOnly(Side.CLIENT)
	protected static final TextureAtlasSprite getTieredTex(int tier) {
		return getTETex("t" + tier + "_side");
	}
	
	@SideOnly(Side.CLIENT)
	protected static final TextureAtlasSprite[] getSingleTex(String str) {
		TextureAtlasSprite t = getTex(str);
		return new TextureAtlasSprite[] {t, t, t, t, t, t};
	}
	
	@SideOnly(Side.CLIENT)
	protected static final TextureAtlasSprite[] getSingleTETex(String str) {
		return getSingleTex(JustServerTweak.MODID + ":blocks/tileentity/" + str);
	}
	
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		return getDefaultTexture();
	}
	
	/** Used for item rendering */
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return getErrorTex();
	}
	
	@SideOnly(Side.CLIENT)
	public static final TextureAtlasSprite[] getErrorTex() {
		return new TextureAtlasSprite[] {ErrorTex, ErrorTex, ErrorTex, ErrorTex, ErrorTex, ErrorTex};
	}
	
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {}
	
	@Nullable
	public AxisAlignedBB getBoundingBox() {
		return Block.FULL_BLOCK_AABB;
	}
	
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox() {
		return getBoundingBox();
	}
	
	public void addCollisionBoxesToList(AxisAlignedBB ab, List<AxisAlignedBB> ls, Entity e) {
	    AxisAlignedBB p1 = ab.offset(-getPos().getX(), -getPos().getY(), -getPos().getZ());
	    for (AxisAlignedBB p2 : getBox()) {
	    	if (p2.intersects(p1)) {
	    		ls.add(p2.offset(getPos()));
	    	}
	    }
	}
	
	public List<AxisAlignedBB> getBox() {
		return def;
	}

	public SoundType getSoundType(Entity e) {
		return SoundType.METAL;
	}

	public void onStructureUpdate() {
	}

	public boolean isMultiBlockPart() {
		return false;
	}

	public void getDrops(ArrayList<ItemStack> ls) {
		if (baseTile == null) return;
	    ls.add(new ItemStack(JSTBlocks.blockTile, 1, baseTile.getID()));
	}
	
	/** Utility method for wrench set facing */
	protected boolean doSetFacing(EnumFacing f, boolean side) {
		if (baseTile == null || f == null || baseTile.facing == f || (side && (f == EnumFacing.UP || f == EnumFacing.DOWN))) return false;
		baseTile.facing = f;
		World w = getWorld();
		BlockPos p = getPos();
		w.notifyNeighborsOfStateChange(p , baseTile.getBlockType(), true);
		w.notifyBlockUpdate(p, w.getBlockState(p), w.getBlockState(p), 3);
		markDirty();
		return true;
	}
	
	protected boolean getRSInput (@Nullable EnumFacing d) {
		if (d == null || !canConnectRedstone(d)) {
			return false;
		}
		d = d.getOpposite();
		BlockPos p = getPos().offset(d);
		World w = getWorld();
		IBlockState bs = w.getBlockState(p);
		return  w.getRedstonePower(p, d) > 0 || (bs.getBlock() == Blocks.REDSTONE_WIRE && ((Integer) bs.getValue(BlockRedstoneWire.POWER)).intValue() > 0);
	}
	
	protected boolean isRSPowered() {
		for (EnumFacing f : EnumFacing.VALUES) {
			if (getRSInput(f))
				return true;
		}
		return false;
	}

	public boolean isOpaque() {
		return true;
	}
	
	public int getLightValue() {
		return 0;
	}

	public boolean canConnectRedstone(EnumFacing f) {
		return false;
	}

	public int getWeakRSOutput(EnumFacing f) {
		return 0;
	}
	
	public int getStrongRSOutput(EnumFacing f) {
		return getWeakRSOutput(f);
	}

	@Nullable
	public <T> T getCapability(Capability<T> c, EnumFacing f) {
		return null;
	}
	
	public final boolean isClient() {
		return baseTile.getWorld().isRemote;
	}
	
	/** return false for virtual or secured item slots (which is useful for display) */
	public boolean canSlotDrop(int num) {
		return true;
	}

	public void onEntityCollided(Entity e) {
	}

	public boolean recolorTE(EnumFacing f, EnumDyeColor col) {
		return false;
	}

	/** if true, The location data of TileEntity will be saved. which is used to check MultiBlock structures */
	public boolean isLocationSensitive() {
		return false;
	}
	
	public boolean removedByPlayer(EntityPlayer pl, boolean wh) {
		return true;
	}
	
	@Nullable
	public static MetaTileBase getMTE(IBlockAccess w, BlockPos p) {
		if (w == null || p == null) return null;
		TileEntity te = w.getTileEntity(p);
		if (te instanceof TileEntityMeta && ((TileEntityMeta)te).hasValidMTE())
			return ((TileEntityMeta)te).mte;
		return null;
	}

	@Nullable
	public static <T extends MetaTileBase> T getMTE(@Nonnull Class<T> cl, IBlockAccess w, BlockPos p) {
		MetaTileBase ret = getMTE(w, p);
		if (cl.isInstance(ret))
			return (T) ret;
		return null;
	}
	
	public static int getMTEId(World w, BlockPos p) {
		TileEntity te = w.getTileEntity(p);
		if (te instanceof TileEntityMeta)
			return ((TileEntityMeta)te).getID();
		return 0;
	}

	/** Get key of the MTE Model.
	 * should return state value for optimization.
	 * */
	@SideOnly(value=Side.CLIENT)
	public String getModelKey() {
		return getKeyFromTex(getTexture());
	}

	@SideOnly(value=Side.CLIENT)
	public List<BakedQuad> getModel(boolean isItem) {
		return JSTUtils.makeFullCube(checkTextures(isItem ? getDefaultTexture() : getTexture()));
	}
	
	@SideOnly(value=Side.CLIENT)
	public static final String getKeyFromTex(TextureAtlasSprite[] tex) {
		String ret = "";
		for (int n = 0; n < tex.length; n++) ret = ret + (n == 0 ? "" : ",") + tex[n].getIconName();
		return ret;
	}
	
	public static final List<BakedQuad> makePipeModel(TextureAtlasSprite[] tx, float rad, byte c, boolean isItem) {
		if (rad >= 8) return JSTUtils.makeFullCube(tx);
		
		List<BakedQuad> ret = new ArrayList();
		float rd = rad * 0.0625F;
		if (isItem) {
			ret.addAll(JSTUtils.makeCube(tx, 0.0F, 0.5F - rd, 0.5F - rd, 1.0F, 0.5F + rd, 0.5F + rd));
			return ret;
		}
		
		if (c == 3) {
			ret.addAll(JSTUtils.makeCube(tx , 0.5F - rd, 0.0F, 0.5F - rd, 0.5F + rd, 1.0F, 0.5F + rd));
			return ret;
		} else if (c == 12) {
			ret.addAll(JSTUtils.makeCube(tx, 0.5F - rd, 0.5F - rd, 0.0F, 0.5F + rd, 0.5F + rd, 1.0F));
			return ret;
		} else if (c == 48) {
			ret.addAll(JSTUtils.makeCube(tx, 0.0F, 0.5F - rd, 0.5F - rd, 1.0F, 0.5F + rd, 0.5F + rd));
			return ret;
		}
		
		//E-W-S-N-U-D
		boolean cd = (c & 1) != 0;
		boolean cu = (c & 1 << 1) != 0;
		boolean cn = (c & 1 << 2) != 0;
		boolean cs = (c & 1 << 3) != 0;
		boolean cw = (c & 1 << 4) != 0;
		boolean ce = (c & 1 << 5) != 0;
		
		if (!(cd && cu && cn && cs && cw && ce))
			ret.addAll(JSTUtils.makeCube(tx, 0.5F - rd, 0.5F - rd, 0.5F - rd, 0.5F + rd, 0.5F + rd, 0.5F + rd));
		if (cd)
			ret.addAll(JSTUtils.makeCube(tx, 0.5F - rd, 0.0F, 0.5F - rd, 0.5F + rd, 0.5F - rd, 0.5F + rd));
		if (cu)
			ret.addAll(JSTUtils.makeCube(tx, 0.5F - rd, 0.5F + rd, 0.5F - rd, 0.5F + rd, 1.0F, 0.5F + rd));
		if (cn)
			ret.addAll(JSTUtils.makeCube(tx, 0.5F - rd, 0.5F - rd, 0.0F, 0.5F + rd, 0.5F + rd, 0.5F - rd));
		if (cs)
			ret.addAll(JSTUtils.makeCube(tx, 0.5F - rd, 0.5F - rd, 0.5F + rd, 0.5F + rd, 0.5F + rd, 1.0F));
		if (cw)
			ret.addAll(JSTUtils.makeCube(tx, 0.0F, 0.5F - rd, 0.5F - rd, 0.5F - rd, 0.5F + rd, 0.5F + rd));
		if (ce)
			ret.addAll(JSTUtils.makeCube(tx, 0.5F + rd, 0.5F - rd, 0.5F - rd, 1.0F, 0.5F + rd, 0.5F + rd));
		return ret;
	}
	
	public boolean isUsable(EntityPlayer pl) {
		return true;
	}
	
	public int getComparatorInput() {
		return 0;
	}

	public boolean canEntityDestroy(Entity e) {
		return true;
	}

	/** if true, the block can be destroyed by Explosion */
	public boolean onBlockExploded(Explosion ex) {
		return true;
	}

	@Nonnull
	public static final TextureAtlasSprite[] checkTextures(TextureAtlasSprite[] tx) {
		if (tx == null || tx.length != 6) {
			tx = getErrorTex();
		} else {
			for (TextureAtlasSprite t : tx) {
				if (t == null) {
					tx = getErrorTex(); 
					break;
				}
			}
		}
		return tx;
	}

	/** Return true if this block is energy storage device. Also used for IC2 Teleporter Compatibility check */
	public boolean isEnergyStorage() {
		return false;
	}

	public static void causeMTEUpdate(World w, BlockPos p) {
		try {
			doMTEUpdate(w, p, new ArrayList());
		} catch (Throwable t) {}
	}

	public static void doMTEUpdate(World w, BlockPos p, ArrayList<BlockPos> ls) {
		if (ls.contains(p) || ls.size() > 1500) return;
		ls.add(p);
		TileEntity te = w.getTileEntity(p);
		boolean flag = te instanceof TileEntityMeta && ((TileEntityMeta)te).isMultiBlockPart();
		if (flag) ((TileEntityMeta)te).mte.onStructureUpdate();
		if (ls.size() < 5 || flag) {
			for (EnumFacing f : EnumFacing.VALUES) {
				BlockPos o = p.offset(f);
				doMTEUpdate(w, o, ls);
			}
		}
	}

	public boolean isInvalid() {
		return baseTile == null || baseTile.isInvalid();
	}

	public void getSubBlocks(int id, NonNullList<ItemStack> list) {
		list.add(new ItemStack(JSTBlocks.blockTile, 1, id));
	}

	// ** Energy Related Stuff **
	public long injectEnergy(@Nullable EnumFacing dir, long v, boolean sim) {
		if (baseTile == null || !canAcceptEnergy() || !isEnergyInput(dir))
			return 0L;
		long tr = maxEUTransfer();
		long eu = Math.min(getMaxEnergy() - baseTile.energy, Math.min(tr, v));
		if (!sim) {
			if (JSTCfg.ovExplosion && tr > 0 && v > tr * 2L) {
				BlockPos p = getPos();
				getWorld().setBlockToAir(p);
				getWorld().createExplosion(null, p.getX() + 0.5F, p.getY() + 0.5F, p.getZ() + 0.5F, 2.0F, false);
				return eu;
			}
			baseTile.energy += eu;
		}
		return eu;
	}
	
	public boolean canProvideEnergy() {
		return false;
	}

	public boolean canAcceptEnergy() {
		return false;
	}

	public int maxEUTransfer() {
		return 0;
	}

	public boolean isEnergyInput(EnumFacing f) {
		return false;
	}

	public boolean isEnergyOutput(EnumFacing f) {
		return false;
	}

	public long getMaxEnergy() {
		return 0L;
	}
	
	protected void injectEnergyToSide(@Nullable EnumFacing f, long transfer) {
		if (f != null && isEnergyOutput(f)) {
			EnumFacing opp = f.getOpposite();
			baseTile.energy -= JSTUtils.sendEnergy(getWorld(), getPos().offset(f), opp, Math.min(transfer, baseTile.energy), false);
		}
	}
	  
    protected void injectEnergyToSide(EnumFacing f) {
    	injectEnergyToSide(f, maxEUTransfer());
    }

	public boolean tryUpgrade(String id) {
		return false;
	}

	public void onBreak() {
		for (int i = 0; i < baseTile.getSizeInventory(); i++) {
			if (!canSlotDrop(i)) continue;
			ItemStack st = baseTile.getStackInSlot(i);
			if (!st.isEmpty()) {
				JSTUtils.dropEntityItemInPos(getWorld(), getPos(), st);
				baseTile.setInventorySlotContents(i, ItemStack.EMPTY);
			}
		}
	}
	
	public IBlockState doBlockUpdate() {
		IBlockState bs = getWorld().getBlockState(getPos());
		getWorld().notifyBlockUpdate(getPos(), bs, bs, 3);
		return bs;
	}
	
	public void updateLight() {
		baseTile.cancelUpdate();
		World w = getWorld();
		IBlockState bs = doBlockUpdate();
		w.notifyNeighborsOfStateChange(getPos(), bs.getBlock(), true);
	    w.checkLightFor(EnumSkyBlock.BLOCK, getPos());
	    w.addBlockEvent(getPos(), bs.getBlock(), 100, 0);
	}
	
	/** NOTE: id 100 is used for updating light value. */
	public void sendEvent(int id, int arg) {
		getWorld().addBlockEvent(getPos(), getWorld().getBlockState(getPos()).getBlock(), id, arg);
	}
	
	/** NOTE: id 100 is used for updating light value. */
	public boolean receiveClientEvent(int id, int arg) {
		if (id == 100) {
			IBlockState bs = doBlockUpdate();
			getWorld().notifyNeighborsOfStateChange(getPos(), bs.getBlock(), true);
			getWorld().checkLightFor(EnumSkyBlock.BLOCK, getPos());
			return true;
		}
		return false;
	}

	public boolean showDurability(ItemStack st) {
		return false;
	}

	public double getDurability(ItemStack st) {
		return 1.0;
	}

	public int getRGBDurability(ItemStack st) {
		return 0x64C8FF;
	}

	@Nullable
	public MapColor getMapColor() {
		return MapColor.IRON;
	}
}
